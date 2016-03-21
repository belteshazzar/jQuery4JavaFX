package com.belteshazzar.jquery.functions;

import org.w3c.dom.Element;

@FunctionalInterface
public interface IntElementVoidFunction {
	
	void apply(int index, Element el);

}
