package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.Offset;
import com.belteshazzar.jquery.functions.IntOffsetOffsetFunction;

import netscape.javascript.JSObject;

public class IntOffsetOffsetFunctionCaller {

	private IntOffsetOffsetFunction function;

	public IntOffsetOffsetFunctionCaller(IntOffsetOffsetFunction function) {
		this.function = function;
	}

	public Offset call(int i, Object el) {
		return function.apply(i,new Offset((JSObject)el));
	}

}
