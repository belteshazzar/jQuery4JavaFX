package com.belteshazzar.jquery;

import netscape.javascript.JSObject;

public class Deferred extends Promise {

	public Deferred(JSObject js) {
		super(js);
	}

	public Deferred notify(Object ... args) {
		js.call("notify", JQuery.createArray(args));
		return this;
	}
	
	public Deferred notifyWith(Object context) {
		js.call("notifyWith", context);
		return this;		
	}
	public Deferred notifyWith(Object context, Object ... args) {
		js.call("notifyWith", context, JQuery.createArray(args));
		return this;		
	}

	public Deferred reject() {
		js.call("reject");
		return this;
	}

	public Deferred reject(Object ... args) {
		js.call("reject", JQuery.createArray(args));
		return this;
	}

	public Deferred rejectWith(Object context) {
		js.call("rejectWith", context);
		return this;
	}

	public Deferred rejectWith(Object context, Object ... args) {
		js.call("rejectWith", context, JQuery.createArray(args));
		return this;
	}

	public Deferred resolve() {
		js.call("resolve");
		return this;
	}

	public Deferred resolve(Object ... args) {
		js.call("resolve", JQuery.createArray(args));
		return this;
	}
	
	public Deferred resolveWith(Object context) {
		js.call("resolveWith", context);
		return this;
	}

	public Deferred resolveWith(Object context, Object ... args) {
		js.call("resolveWith", context, JQuery.createArray(args));
		return this;
	}
	
	public Promise when(Deferred ... deferreds) {
		js.call("done", JQuery.createArray(deferreds));
		return this;		
	}
}
