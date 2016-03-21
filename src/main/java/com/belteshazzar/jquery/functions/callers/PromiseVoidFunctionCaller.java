package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.Promise;
import com.belteshazzar.jquery.functions.PromiseVoidFunction;

import netscape.javascript.JSObject;

public class PromiseVoidFunctionCaller {

	private PromiseVoidFunction function;

	public PromiseVoidFunctionCaller(PromiseVoidFunction function) {
		this.function = function;
	}

	public void call(Object o) {
		function.apply(new Promise((JSObject)o));
	}

}
