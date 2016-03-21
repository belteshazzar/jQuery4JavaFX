package com.belteshazzar.jquery.functions.callers;

import org.w3c.dom.Element;

import com.belteshazzar.jquery.functions.IntElementBooleanFunction;

public class IntElementBooleanFunctionCaller {

	private IntElementBooleanFunction function;

	public IntElementBooleanFunctionCaller(IntElementBooleanFunction function) {
		this.function = function;
	}

	public boolean call(int i, Object el) {
		return function.apply(i,(Element)el);
	}

}
