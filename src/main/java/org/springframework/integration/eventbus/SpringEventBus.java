package org.springframework.integration.eventbus;


import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.GenericMessage;
import com.google.common.collect.Maps;


public class SpringEventBus implements EventBus,ApplicationContextAware{
    
	private ApplicationContext applicationContext;
    private final Map<EventSubscriber, MessageHandler> subscribers = Maps.newHashMap();

    @Override
    public boolean subscribe(final String topic, final Object subscriber) {

    	PublishSubscribeChannel intChannel = getChannel(topic);
        
        EventSubscriberMethodSelectorUtil.loadSubscribeMethodsIntoCache(subscriber);
        
        MessageHandler messageHandler = new MessageHandler() {
 
            @Override
            public void handleMessage(Message<?> message)  {
            	EventSubscriberMethodSelectorUtil.selectSubcriberMethodAndInvoke(message, subscriber);
            }
        };
        EventSubscriber eventListener = new EventSubscriber(topic, subscriber);
        boolean success = intChannel.subscribe(messageHandler);
        if (success) {
            synchronized (subscribers) {
                subscribers.put(eventListener, messageHandler);
            }
        }
        return success;
    }
    

    @Override
    public boolean unsubscribe(String topic, Object subscriber) {
        PublishSubscribeChannel intChannel = getChannel(topic);
        EventSubscriber eventListener = new EventSubscriber(topic, subscriber);
        MessageHandler messageHandler = null;
        boolean success = true;
        synchronized (subscribers) {
            messageHandler = subscribers.get(eventListener);
            subscribers.remove(eventListener);
        }
        if (messageHandler != null) {
            success = intChannel.unsubscribe(messageHandler);
        }

        return success;
    }

	@Override
	public boolean publish(Event event) {
		PublishSubscribeChannel intChannel = getChannel(event.forTopic());
	    GenericMessage<Event> intMessage = new GenericMessage<Event>(event);
	    return intChannel.send(intMessage);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	protected PublishSubscribeChannel getChannel(String topic){
		return this.applicationContext.getBean(topic, PublishSubscribeChannel.class);
	}    
       
    
    private class EventSubscriber {
        final String topic;
        final Object subscriber;

        EventSubscriber(String topic, Object subscriber) {
            this.topic = topic;
            this.subscriber = subscriber;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + topic.hashCode();
            result = prime * result + subscriber.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EventSubscriber other = (EventSubscriber) obj;

            return topic.equals(other.topic) && subscriber.equals(other.subscriber);
        }

    }
}
