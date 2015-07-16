package com.foobar.domain.model;

import java.util.Date;


public class BarDomainEvent extends DomainEvent {

	protected BarDomainEvent() {
		super(new Date());
	}

}
