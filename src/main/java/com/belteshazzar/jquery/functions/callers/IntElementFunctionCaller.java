package com.belteshazzar.jquery.functions.callers;

import org.w3c.dom.Element;

import com.belteshazzar.jquery.functions.IntElementFunction;

public class IntElementFunctionCaller {

	private IntElementFunction function;

	public IntElementFunctionCaller(IntElementFunction function) {
		this.function = function;
	}

	public Element call(int i) {
		return function.apply(i);
	}

}
