package com.foobar.domain.model;

import java.util.Date;
import org.springframework.integration.eventbus.Event;

public abstract class DomainEvent implements Event {

	public int eventVersion;
	private Date createdOn;
	private transient String topic;
	
	protected DomainEvent(Date createdOn) {
		this.setCreatedOn(createdOn);
		this.setTopic("domainEventsChannel");
		this.setEventVersion(1);
	}
	
	private void setTopic(String topic) {
		this.topic = topic;
	}

	private void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date occurredOn() {
		return createdOn;
	}

	public String forTopic() {
		return topic;
	}

	private void setEventVersion(int eventVersion) {
		this.eventVersion = eventVersion;
	}
	
	public int eventVersion() {
		return eventVersion;
	}

}
