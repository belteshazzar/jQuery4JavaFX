package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.Promise;
import com.belteshazzar.jquery.functions.PromiseBooleanVoidFunction;

import netscape.javascript.JSObject;

public class PromiseBooleanVoidFunctionCaller {

	private PromiseBooleanVoidFunction function;

	public PromiseBooleanVoidFunctionCaller(PromiseBooleanVoidFunction function) {
		this.function = function;
	}

	public void call(Object o, boolean b) {
		function.apply(new Promise((JSObject)o),b);
	}

}
