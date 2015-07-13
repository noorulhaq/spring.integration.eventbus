package com.foobar.domain.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.eventbus.EventBus;

public class DomainEventPublisher implements ApplicationContextAware {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainEventPublisher.class);

	private static ApplicationContext applicationContext;
	
	private DomainEventPublisher(){}
	
	private static EventBus eventBus() {
		EventBus eventBus = null;
		if (DomainEventPublisher.applicationContext == null || 
				(eventBus = DomainEventPublisher.applicationContext.getBean(EventBus.class)) == null)
			throw new IllegalStateException("Eventbus not found.");
		else{
			return eventBus;
		}
	}

	
	public static boolean publish(DomainEvent event){
		LOGGER.debug("Eventbus publishing event: "+event.getClass());
		return eventBus().publish(event);
	}
	
	@Override
	public synchronized void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		DomainEventPublisher.applicationContext = applicationContext;
	}
}
