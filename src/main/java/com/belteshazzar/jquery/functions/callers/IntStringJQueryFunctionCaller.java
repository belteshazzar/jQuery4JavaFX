package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.JQuery;
import com.belteshazzar.jquery.functions.IntStringJQueryFunction;

public class IntStringJQueryFunctionCaller {

	private IntStringJQueryFunction function;

	public IntStringJQueryFunctionCaller(IntStringJQueryFunction function) {
		this.function = function;
	}

	public JQuery call(int i, String s) {
		return function.apply(i,s);
	}

}
