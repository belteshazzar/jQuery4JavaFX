package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.Event;
import com.belteshazzar.jquery.EventHandler;

import netscape.javascript.JSObject;

public class EventHandlerCaller {

	private EventHandler handler;

	public EventHandlerCaller(EventHandler handler) {
		this.handler = handler;
	}

	public void call(JSObject jQueryEvent) {
		handler.handleEvent(new Event(jQueryEvent));
	}

}
