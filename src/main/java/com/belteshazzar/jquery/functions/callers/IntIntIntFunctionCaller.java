package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.functions.IntIntIntFunction;

public class IntIntIntFunctionCaller {

	private IntIntIntFunction function;

	public IntIntIntFunctionCaller(IntIntIntFunction function) {
		this.function = function;
	}

	public int call(int i, int j) {
		return function.apply(i,j);
	}

}
