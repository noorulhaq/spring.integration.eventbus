package org.springframework.integration.eventbus;

public interface EventBus{
	
	public boolean publish(Event event);
	
    boolean subscribe(String topic, Object subscriber);

    boolean unsubscribe(String topic, Object subscriber);

}
