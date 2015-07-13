package org.springframework.integration.eventbus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

public class EventSubscriberBeanProcessor 
	   implements DestructionAwareBeanPostProcessor, ApplicationContextAware, InitializingBean, Ordered {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ApplicationContext applicationContext;
	private int order = 1000;

	// Temporary subscribers placeholder between postProcessBefore and postProcessAfter method calls.
	private final Map<String, Object> subscribers = new HashMap<String, Object>();
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (isAnnotationPresent(bean)) {
            subscribers.put(beanName, bean);
        }
        return bean;
    }

	@Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
    	Object subscriber = subscribers.get(beanName);
        if (subscriber != null) {
            EventBus eventBus = getEventBus(eventBusName(subscriber));
            eventBus.subscribe(topic(subscriber), subscriber);
        }
        return bean;
    }

	@Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (subscribers.containsKey(beanName)) {
            try {
            	Object subscriber = subscribers.get(beanName);
                EventBus eventBus = getEventBus(eventBusName(subscriber));
                eventBus.unsubscribe(topic(subscriber), subscriber);
            } catch (Exception e) {
                log.error("An exception occurred while unsubscribing an event listener", e);
            } finally {
                subscribers.remove(beanName);
            }
        }
    }

    private boolean isAnnotationPresent(Object bean) {
        return getAnnotation(bean) != null;
    }

    private SubscribeEvent getAnnotation(Object bean) {
        return getAnnotation(bean.getClass());
    }

    private SubscribeEvent getAnnotation(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return null;
        }
        Method methods[] = clazz.getMethods();  
        for (Method method : methods) {
        	boolean foundIt = method.isAnnotationPresent(SubscribeEvent.class);
        	if(foundIt)
        		return method.getAnnotation(SubscribeEvent.class);
		}
        // recursive call with super class
        return getAnnotation(clazz.getSuperclass());

    }

    private String eventBusName(Object bean) {
    	SubscribeEvent annotation = getAnnotation(bean);
        if (annotation != null) {
            return annotation.eventBus();
        } else {
            throw new IllegalArgumentException("Missing annotation");
        }
    }

    private String topic(Object bean) {
        SubscribeEvent annotation = getAnnotation(bean);
        if (annotation != null) {
            return annotation.topic();
        } else {
            throw new IllegalArgumentException("Missing annotation");
        }
    }

    protected EventBus getEventBus(String name) {
        Object bean = getApplicationContext().getBean(name);
        if (!(bean instanceof EventBus)) {
            throw new IllegalStateException("Wrong EventBus type, got: " + bean.getClass().getName());
        }
        return (EventBus) bean;
    }

    public void afterPropertiesSet() throws Exception {
    }


    private ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Processing order of this BeanPostProcessor, use last. Default is 1000.
     */
    public void setOrder(int order) {
        this.order = order;
    }
}
