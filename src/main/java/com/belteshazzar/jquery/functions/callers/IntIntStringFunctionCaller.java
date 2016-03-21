package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.functions.IntIntStringFunction;

public class IntIntStringFunctionCaller {

	private IntIntStringFunction function;

	public IntIntStringFunctionCaller(IntIntStringFunction function) {
		this.function = function;
	}

	public String call(int i, int j) {
		return function.apply(i,j);
	}

}
