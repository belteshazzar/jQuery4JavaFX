package com.belteshazzar.jquery.functions.callers;

import org.w3c.dom.Element;

import com.belteshazzar.jquery.functions.IntStringElementFunction;

public class IntStringElementFunctionCaller {

	private IntStringElementFunction function;

	public IntStringElementFunctionCaller(IntStringElementFunction function) {
		this.function = function;
	}

	public Element call(int i, String s) {
		return function.apply(i,s);
	}

}
