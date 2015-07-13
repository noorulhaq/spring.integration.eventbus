package com.foobar.domain.model;

public class FooDomainEvent extends DomainEvent {
	
	public String fooStr;
	public String barStr;
	
	public FooDomainEvent() {}
	
	public FooDomainEvent(String fooStr, String barStr) {
		super();
		this.fooStr = fooStr;
		this.barStr = barStr;
	}
	
	public String getFooStr() {
		return fooStr;
	}
	public String getBarStr() {
		return barStr;
	}
	public void setFooStr(String fooStr) {
		this.fooStr = fooStr;
	}
	public void setBarStr(String barStr) {
		this.barStr = barStr;
	}
	
	
	@Override
	public String toString() {
		return "{"+ fooStr +" : "+ barStr +"}";
	}
}
