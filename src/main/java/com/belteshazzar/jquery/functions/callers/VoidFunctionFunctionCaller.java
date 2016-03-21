package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.functions.VoidFunction;
import com.belteshazzar.jquery.functions.VoidFunctionFunction;

public class VoidFunctionFunctionCaller {

	private VoidFunctionFunction function;

	public VoidFunctionFunctionCaller(VoidFunctionFunction function) {
		this.function = function;
	}

	public VoidFunction call() {
		return function.apply();
	}

}
