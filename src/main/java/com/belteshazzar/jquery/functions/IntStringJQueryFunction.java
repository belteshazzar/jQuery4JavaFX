package com.belteshazzar.jquery.functions;

import com.belteshazzar.jquery.JQuery;

@FunctionalInterface
public interface IntStringJQueryFunction {

	JQuery apply(int i, String s);
}
