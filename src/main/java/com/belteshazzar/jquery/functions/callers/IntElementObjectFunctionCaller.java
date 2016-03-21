package com.belteshazzar.jquery.functions.callers;

import org.w3c.dom.Element;

import com.belteshazzar.jquery.functions.IntElementObjectFunction;

public class IntElementObjectFunctionCaller {

	private IntElementObjectFunction function;

	public IntElementObjectFunctionCaller(IntElementObjectFunction function) {
		this.function = function;
	}

	public Object call(int i, Object el) {
		return function.apply(i,(Element)el);
	}

}
