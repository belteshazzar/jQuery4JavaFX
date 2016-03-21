package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.functions.IntStringBooleanStringFunction;

public class IntStringBooleanStringFunctionCaller {

	private IntStringBooleanStringFunction function;

	public IntStringBooleanStringFunctionCaller(IntStringBooleanStringFunction function) {
		this.function = function;
	}

	public String call(int i, String s, boolean b) {
		return function.apply(i,s,b);
	}

}
