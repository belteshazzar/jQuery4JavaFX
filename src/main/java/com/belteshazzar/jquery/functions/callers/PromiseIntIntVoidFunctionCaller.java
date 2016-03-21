package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.Promise;
import com.belteshazzar.jquery.functions.PromiseIntIntVoidFunction;

import netscape.javascript.JSObject;

public class PromiseIntIntVoidFunctionCaller {

	private PromiseIntIntVoidFunction function;

	public PromiseIntIntVoidFunctionCaller(PromiseIntIntVoidFunction function) {
		this.function = function;
	}

	public void call(Object o, int i, int j) {
		function.apply(new Promise((JSObject)o),i,j);
	}

}
