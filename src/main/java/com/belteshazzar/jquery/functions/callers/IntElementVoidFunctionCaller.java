package com.belteshazzar.jquery.functions.callers;

import org.w3c.dom.Element;

import com.belteshazzar.jquery.functions.IntElementVoidFunction;

public class IntElementVoidFunctionCaller {

	private IntElementVoidFunction function;

	public IntElementVoidFunctionCaller(IntElementVoidFunction function) {
		this.function = function;
	}

	public void call(int i, Object el) {
		function.apply(i,(Element)el);
	}

}
