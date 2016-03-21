package com.belteshazzar.jquery.functions;

import org.w3c.dom.Element;

@FunctionalInterface
public interface IntElementBooleanFunction {

	boolean apply(int i, Element el);
}
