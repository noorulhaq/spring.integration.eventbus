package org.springframework.integration.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.integration.annotation.ServiceActivator;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ServiceActivator
public @interface SubscribeEvent {

	String eventBus() default "eventBus";
	
	String topic() default "domainEventsChannel";
	
}
