package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.functions.IntStringFunction;

public class IntStringFunctionCaller {

	private IntStringFunction function;

	public IntStringFunctionCaller(IntStringFunction function) {
		this.function = function;
	}

	public String call(int i) {
		return function.apply(i);
	}

}
