package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.JQuery;
import com.belteshazzar.jquery.functions.IntJQueryFunction;

public class IntJQueryFunctionCaller {

	private IntJQueryFunction function;

	public IntJQueryFunctionCaller(IntJQueryFunction function) {
		this.function = function;
	}

	public JQuery call(int i) {
		return function.apply(i);
	}

}
