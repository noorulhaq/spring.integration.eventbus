package com.foobar.application;

import org.springframework.integration.eventbus.SubscribeEvent;
import org.springframework.stereotype.Service;
import com.foobar.domain.model.BarDomainEvent;
import com.foobar.domain.model.DomainEvent;

@Service
public class FooBarApplicationService extends BaseFooBarApplicationService{

	@SubscribeEvent
	public void when(BarDomainEvent event){
		System.out.println(this.getClass().getSimpleName()+"  received event "+event.getClass().getSimpleName());	
	}
	
	@SubscribeEvent
	public void when(DomainEvent event) {
		System.out.println(this.getClass().getSimpleName()+"  received event "+event.getClass().getSimpleName());
	}
}
