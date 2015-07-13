package com.foobar.domain.model;


public class BarService {

	public void update(){
		
		DomainEventPublisher.publish(new BarDomainEvent());
			
	}

}
