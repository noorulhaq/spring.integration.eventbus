package com.foobar.infrastructure.eventstore;

import java.util.ArrayList;
import java.util.List;

import com.foobar.domain.eventstore.EventStore;
import com.foobar.domain.model.DomainEvent;

import org.springframework.integration.eventbus.SubscribeEvent;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryEventStore implements EventStore{
	
	private static final List<DomainEvent> IN_MEMORY_EVENT_STORE = new ArrayList<DomainEvent>();

	@Override
	@SubscribeEvent
	public void append(DomainEvent event) {
		System.out.println(this.getClass().getSimpleName()+"  received event "+event.getClass().getSimpleName());
		IN_MEMORY_EVENT_STORE.add(event);
	}

	@Override
	public List<DomainEvent> allDomainEventsSince(long stroredEventId) {
		return IN_MEMORY_EVENT_STORE;
	}
	
}
