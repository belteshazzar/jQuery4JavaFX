package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.functions.IntStringIntFunction;

public class IntStringIntFunctionCaller {

	private IntStringIntFunction function;

	public IntStringIntFunctionCaller(IntStringIntFunction function) {
		this.function = function;
	}

	public int call(int i, String s) {
		return function.apply(i,s);
	}

}
