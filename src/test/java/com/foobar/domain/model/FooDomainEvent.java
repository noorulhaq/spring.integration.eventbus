package com.foobar.domain.model;

import java.util.Date;

public class FooDomainEvent extends DomainEvent {

	public FooDomainEvent() {
		super(new Date());
	}

}
