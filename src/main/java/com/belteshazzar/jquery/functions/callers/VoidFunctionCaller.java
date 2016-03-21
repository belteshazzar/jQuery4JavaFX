package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.functions.VoidFunction;

public class VoidFunctionCaller {

	private VoidFunction function;

	public VoidFunctionCaller(VoidFunction function) {
		this.function = function;
	}

	public void call() {
		function.apply();
	}

}
