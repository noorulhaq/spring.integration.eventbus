package com.foobar.domain.model;

public class FooService {
	
	public void update(){
		
		DomainEventPublisher.publish(new FooDomainEvent());
			
	}
	

}
