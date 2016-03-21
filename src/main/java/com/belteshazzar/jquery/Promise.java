package com.belteshazzar.jquery;

import com.belteshazzar.jquery.functions.VoidFunction;

import netscape.javascript.JSObject;

public class Promise {
	protected JSObject js;
	
	public Promise(JSObject js) {
		this.js = js;
	}
	
	public Promise then(VoidFunction doneFilter) {
		js.call("then", JQuery.createFunction(doneFilter));
		return this;
	}

	public Promise then(VoidFunction doneFilter, VoidFunction failFilter) {
		js.call("then", JQuery.createFunction(doneFilter), JQuery.createFunction(failFilter));
		return this;
	}

	public Promise then(VoidFunction doneFilter, VoidFunction failFilter, VoidFunction progressFilter) {
		js.call("then", JQuery.createFunction(doneFilter), JQuery.createFunction(failFilter), JQuery.createFunction(progressFilter));
		return this;
	}

	public Promise done(VoidFunction ... doneFilters) {
		Object[] cbs = new Object[doneFilters.length];
		for (int i=0 ; i<cbs.length ; i++) {
			cbs[i] = JQuery.createFunction(doneFilters[i]);
		}
		js.call("done", cbs);
		return this;
	}

	public Promise fail(VoidFunction ... doneFilters) {
		Object[] cbs = new Object[doneFilters.length];
		for (int i=0 ; i<cbs.length ; i++) {
			cbs[i] = JQuery.createFunction(doneFilters[i]);
		}
		js.call("fail", cbs);
		return this;
	}
	
	public Promise always(VoidFunction ... doneFilters) {
		Object[] cbs = new Object[doneFilters.length];
		for (int i=0 ; i<cbs.length ; i++) {
			cbs[i] = JQuery.createFunction(doneFilters[i]);
		}
		js.call("always", cbs);
		return this;
	}

	public Promise pipe(VoidFunction doneFilter) {
		js.call("pipe", JQuery.createFunction(doneFilter));
		return this;
	}

	public Promise pipe(VoidFunction doneFilter, VoidFunction failFilter) {
		js.call("pipe", JQuery.createFunction(doneFilter), JQuery.createFunction(failFilter));
		return this;
	}

	public Promise pipe(VoidFunction doneFilter, VoidFunction failFilter, VoidFunction progressFilter) {
		js.call("pipe", JQuery.createFunction(doneFilter), JQuery.createFunction(failFilter), JQuery.createFunction(progressFilter));
		return this;
	}
	public Promise progress(VoidFunction ... doneFilters) {
		Object[] cbs = new Object[doneFilters.length];
		for (int i=0 ; i<cbs.length ; i++) {
			cbs[i] = JQuery.createFunction(doneFilters[i]);
		}
		js.call("progress", cbs);
		return this;
	}
	
	public static enum State {
		pending, resolved, rejected;
	}

	public State state() {
		String s = (String)js.call("state");
		return State.valueOf(s);
	}
	
	public Promise promise() {
		return new Promise((JSObject)js.call("promise"));
	}
	
	public Promise promise(Object o) {
		return new Promise((JSObject)js.call("promise",o));		
	}
}