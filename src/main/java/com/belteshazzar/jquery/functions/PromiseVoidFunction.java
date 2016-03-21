package com.belteshazzar.jquery.functions;

import com.belteshazzar.jquery.Promise;

@FunctionalInterface
public interface PromiseVoidFunction {

	void apply(Promise promise);
}
