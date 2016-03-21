package com.belteshazzar.jquery.functions.callers;

import org.w3c.dom.Element;

import com.belteshazzar.jquery.functions.IntElementStringFunction;

public class IntElementStringFunctionCaller {

	private IntElementStringFunction function;

	public IntElementStringFunctionCaller(IntElementStringFunction function) {
		this.function = function;
	}

	public String call(int i, Object el) {
		return function.apply(i,(Element)el);
	}

}
