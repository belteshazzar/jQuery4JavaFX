package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.functions.IntObjectObjectFunction;

public class IntObjectObjectFunctionCaller {

	private IntObjectObjectFunction function;

	public IntObjectObjectFunctionCaller(IntObjectObjectFunction function) {
		this.function = function;
	}

	public Object call(int i, Object o) {
		return function.apply(i, o);
	}

}
