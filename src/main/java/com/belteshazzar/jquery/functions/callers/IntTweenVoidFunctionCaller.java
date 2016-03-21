package com.belteshazzar.jquery.functions.callers;

import com.belteshazzar.jquery.Tween;
import com.belteshazzar.jquery.functions.IntTweenVoidFunction;

import netscape.javascript.JSObject;

public class IntTweenVoidFunctionCaller {

	private IntTweenVoidFunction function;

	public IntTweenVoidFunctionCaller(IntTweenVoidFunction function) {
		this.function = function;
	}

	public void call(int i, Object tween) {
		function.apply(i,new Tween((JSObject)tween));
	}

}
