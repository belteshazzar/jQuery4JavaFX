package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.functions.IntStringStringFunction;

public class IntStringStringFunctionCaller {

	private IntStringStringFunction function;

	public IntStringStringFunctionCaller(IntStringStringFunction function) {
		this.function = function;
	}

	public Object call(int i, String s) {
		return function.apply(i,s);
	}

}
