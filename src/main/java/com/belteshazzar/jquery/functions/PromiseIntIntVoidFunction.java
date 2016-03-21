package com.belteshazzar.jquery.functions;

import com.belteshazzar.jquery.Promise;

@FunctionalInterface
public interface PromiseIntIntVoidFunction {

	public void apply(Promise promise, int i, int j);
}
