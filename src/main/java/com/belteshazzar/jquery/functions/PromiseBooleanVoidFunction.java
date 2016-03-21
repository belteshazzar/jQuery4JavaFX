package com.belteshazzar.jquery.functions;

import com.belteshazzar.jquery.Promise;

@FunctionalInterface
public interface PromiseBooleanVoidFunction {

	void apply(Promise promise, boolean b);
}
