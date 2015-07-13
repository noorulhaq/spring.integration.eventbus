package com.foobar.application;

import com.foobar.domain.model.BarService;
import com.foobar.domain.model.FooDomainEvent;
import com.foobar.domain.model.FooService;
import org.springframework.integration.eventbus.SubscribeEvent;

public abstract class BaseFooBarApplicationService{

	private FooService fooService = new FooService();
	private BarService barService = new BarService();
	
	
	public void updateFoo(){
		fooService.update();
	}
	
	public void updateBar(){
		barService.update();
	}
	
	
	@SubscribeEvent
	public void when(FooDomainEvent event) {
		System.out.println(this.getClass().getSimpleName()+"  received event "+event.getClass().getSimpleName());
	}	
}
