package com.belteshazzar.jquery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLDocument;

import com.belteshazzar.jquery.functions.IntElementBooleanFunction;
import com.belteshazzar.jquery.functions.IntElementFunction;
import com.belteshazzar.jquery.functions.IntElementObjectFunction;
import com.belteshazzar.jquery.functions.IntElementVoidFunction;
import com.belteshazzar.jquery.functions.IntIntIntFunction;
import com.belteshazzar.jquery.functions.IntIntStringFunction;
import com.belteshazzar.jquery.functions.IntJQueryFunction;
import com.belteshazzar.jquery.functions.IntObjectObjectFunction;
import com.belteshazzar.jquery.functions.IntOffsetOffsetFunction;
import com.belteshazzar.jquery.functions.IntStringBooleanStringFunction;
import com.belteshazzar.jquery.functions.IntStringElementFunction;
import com.belteshazzar.jquery.functions.IntStringFunction;
import com.belteshazzar.jquery.functions.IntStringIntFunction;
import com.belteshazzar.jquery.functions.IntStringJQueryFunction;
import com.belteshazzar.jquery.functions.IntStringStringFunction;
import com.belteshazzar.jquery.functions.IntTweenVoidFunction;
import com.belteshazzar.jquery.functions.JQueryFunction;
import com.belteshazzar.jquery.functions.PromiseBooleanVoidFunction;
import com.belteshazzar.jquery.functions.PromiseIntIntVoidFunction;
import com.belteshazzar.jquery.functions.PromiseVoidFunction;
import com.belteshazzar.jquery.functions.VoidFunction;
import com.belteshazzar.jquery.functions.VoidFunctionFunction;
import com.belteshazzar.jquery.functions.callers.EventHandlerCaller;
import com.belteshazzar.jquery.functions.callers.IntElementBooleanFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntElementFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntElementObjectFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntIntIntFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntIntStringFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntJQueryFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntObjectObjectFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntOffsetOffsetFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntStringBooleanStringFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntStringElementFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntStringFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntStringIntFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntStringJQueryFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntStringStringFunctionCaller;
import com.belteshazzar.jquery.functions.callers.IntTweenVoidFunctionCaller;
import com.belteshazzar.jquery.functions.callers.PromiseBooleanVoidFunctionCaller;
import com.belteshazzar.jquery.functions.callers.PromiseIntIntVoidFunctionCaller;
import com.belteshazzar.jquery.functions.callers.PromiseVoidFunctionCaller;
import com.belteshazzar.jquery.functions.callers.VoidFunctionCaller;
import com.belteshazzar.jquery.functions.callers.VoidFunctionFunctionCaller;
import com.sun.webkit.dom.HTMLScriptElementImpl;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public class JQuery {
	
	public static final String DEFAULT_JQUERY_LOCAL = JQuery.class.getResource("jquery-2.2.1.min.js").toExternalForm();
	public static final String DEFAULT_JQUERY_REMOTE = "http://ajax.googleapis.com/ajax/libs/jquery/2.2.0/jquery.min.js";
	public static final boolean DEFAULT_CLEAR_READY_FUNCTIONS = true;

	private static class Config {
		String local;
		String remote;
		boolean clearReadyFunctions;
		
		Config() {
			this.local = DEFAULT_JQUERY_LOCAL;
			this.remote = DEFAULT_JQUERY_REMOTE;
			this.clearReadyFunctions = DEFAULT_CLEAR_READY_FUNCTIONS;
		}
	}

	private static final List<VoidFunction> readyFunctions = new ArrayList<VoidFunction>();
	private static final Map<Integer,JSObject> eventHandlerMap = new HashMap<Integer,JSObject>();

	private static WebEngine webEngine;
	private static JSObject window = null;
	private static Config config = new Config();
	
	public static void setEngine(WebEngine webEngine) {
		JQuery.webEngine = webEngine;
    	webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>() {
                @Override
                public void changed(ObservableValue<? extends State> ov,
                    State oldState, State newState) {
                    if (newState == State.SUCCEEDED) {
                    	documentLoaded();
                    } else {
                    	window = null;
                    }
                }
            }
        );
    	
    	if (webEngine.getLoadWorker().getState()== State.SUCCEEDED) {
    		documentLoaded();
    	}
	}
	
	private static void documentLoaded() {
		try {
			@SuppressWarnings("unused")
			Object jQuery = webEngine.executeScript("jQuery");
			window = (JSObject)webEngine.executeScript("window");
			documentReady();
		} catch (JSException jsex) {
			HTMLDocument doc = (HTMLDocument)webEngine.getDocument();
			HTMLScriptElementImpl script = (HTMLScriptElementImpl)doc.createElement("script");
			((EventTarget)script).addEventListener("load", new EventListener() {

				@Override
				public void handleEvent(org.w3c.dom.events.Event evt) {
					Object jQuery = webEngine.executeScript("jQuery");
					if (!(jQuery instanceof JSObject)) {
						throw new IllegalStateException("JQuery hasn't been loaded into web page");
					}
					window = (JSObject)webEngine.executeScript("window");
					documentReady();
				}
				
			}, false);
			((EventTarget)script).addEventListener("error", new EventListener() {

				@Override
				public void handleEvent(org.w3c.dom.events.Event evt) {
					if (script.getSrc().equals(JQuery.config.local)) {
						script.setSrc(JQuery.config.remote);
					}
				}
				
			}, false);
			script.setSrc(JQuery.config.local);
			doc.getBody().appendChild(script);
		}
	}
	
	private static void documentReady() {
		for (VoidFunction f : readyFunctions) {
			f.apply();
		}
		if (JQuery.config.clearReadyFunctions) readyFunctions.clear();
	}

	static JSObject createFunction(IntStringStringFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntStringStringFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,s) { return obj.java.call(i,s); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(EventHandler handler) {
		JSObject obj = createObject();
		obj.setMember("java", new EventHandlerCaller(handler));
		obj.eval("(function(obj) { obj.fn = function(ev) { obj.java.call(ev); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntElementObjectFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntElementObjectFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,el) { return obj.java.call(i,el); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntStringFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntStringFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i) { return obj.java.call(i); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntStringBooleanStringFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntStringBooleanStringFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,s,b) { return obj.java.call(i,s,b); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntElementFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntElementFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,s,b) { return obj.java.call(i,s,b); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntJQueryFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntJQueryFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,s,b) { return obj.java.call(i,s,b); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntStringJQueryFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntStringJQueryFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,s,b) { return obj.java.call(i,s,b); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntStringElementFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntStringElementFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,s,b) { return obj.java.call(i,s,b); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntIntStringFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntIntStringFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,j) { return obj.java.call(i,j); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntIntIntFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntIntIntFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,j) { return obj.java.call(i,j); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(VoidFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new VoidFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function() { obj.java.call(); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntObjectObjectFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntObjectObjectFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,o) { return obj.java.call(i,o); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntTweenVoidFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntTweenVoidFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,t) { return obj.java.call(i,t); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntElementBooleanFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntElementBooleanFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,el) { return obj.java.call(i,el); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntOffsetOffsetFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntOffsetOffsetFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,os) { return obj.java.call(i,os); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(PromiseVoidFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new PromiseVoidFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(promise) { obj.java.call(promise); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(PromiseBooleanVoidFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new PromiseBooleanVoidFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(promise,finished) { obj.java.call(promise,finished); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(PromiseIntIntVoidFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new PromiseIntIntVoidFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(promise,i,j) { obj.java.call(promise,i,j); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(IntStringIntFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new IntStringIntFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function(i,s) { return obj.java.call(i,s); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createFunction(VoidFunctionFunction function) {
		JSObject obj = createObject();
		obj.setMember("java", new VoidFunctionFunctionCaller(function));
		obj.eval("(function(obj) { obj.fn = function() { return obj.java.call(); } })(this)");
		return (JSObject)obj.getMember("fn");
	}

	static JSObject createArray(Object[] values) {
		JSObject array = createArray();
		for (int i=0 ; i<values.length ; i++) array.setSlot(i,values[i]);
		return array;
	}

	static JSObject createArray() {
		return (JSObject)window.eval("(function() { return []; })()");
	}

	static JSObject createObject() {
		return (JSObject)window.eval("(function() { return {}; })()");
	}

	///////////////////////////////////////////////

	// https://api.jquery.com/jQuery/#jQuery-selector-context
	// https://api.jquery.com/jQuery/#jQuery-html-ownerDocument
	public static JQuery $(String selector) {
		return new JQuery(window.call("jQuery", selector));
	}

	// https://api.jquery.com/jQuery/#jQuery-selector-context
	public static JQuery $(String selector, Element context) {
		return new JQuery(window.call("jQuery", selector, context));
	}

	// https://api.jquery.com/jQuery/#jQuery-selector-context
	public static JQuery $(String selector, JQuery context) {
		return new JQuery(window.call("jQuery", selector, context.js));
	}

	// https://api.jquery.com/jQuery/#jQuery-element
	public static JQuery $(Element element) {
		return new JQuery(window.call("jQuery", element));
	}
	
	// https://api.jquery.com/jQuery/#jQuery-elementArray
	public static JQuery $(Element ... elementArray) {
		return new JQuery(window.call("jQuery", createArray(elementArray)));
	}
	
	// https://api.jquery.com/jQuery/#jQuery-object
	public static JQuery $(PlainObject object) {
		return new JQuery(window.call("jQuery", object.toJSObject()));
	}
	
	// https://api.jquery.com/jQuery/#jQuery-selection
	public static JQuery $(JQuery selection) {
		return new JQuery(window.call("jQuery", selection.js));
	}
	
	// https://api.jquery.com/jQuery/#jQuery
	public static JQuery $() {
		return new JQuery(window.call("jQuery"));
	}

//	// https://api.jquery.com/jQuery/#jQuery-html-ownerDocument
//	public static JQuery $(String html) {
//		return new JQuery(null);
//	}

//  // https://api.jquery.com/jQuery/#jQuery-html-ownerDocument
//	public static JQuery $(String html, HTMLDocument ownerDocument) {
//		return new JQuery(null);
//	}
	
//	/* https://api.jquery.com/jQuery/#jQuery-html-attributes
//	 *
//	 * Javascript examples:
//	 * 
//	 * $( "<div></div>", {
//  	 *   "class": "my-div",
//  	 *   on: {
//   	 * 	   touchstart: function( event ) {
//     *       // Do something
//     *     }
//     *   }
//     * }).appendTo( "body" );
//	 * 
//	 * $( "<div/>", {
//  	 *   "class": "test",
//  	 *   text: "Click me!",
//     *   click: function() {
//     *     $( this ).toggleClass( "test" );
//	 *   }
//	 * })
//  	 * .appendTo( "body" );
//  	 * 
//	 */
	public static JQuery $(String html, PlainObject attributes) {
		return new JQuery(window.call("jQuery", html, attributes.toJSObject()));
	}
	
	// https://api.jquery.com/jQuery/#jQuery-callback
	public static JQuery $(VoidFunction callback) {
		readyFunctions.add(callback);
		return new JQuery(null);
	}
	
	/////////////////////////////////////////////////////////////////////
	
	public final int length;
	private final JSObject js;
	
	private JQuery(Object o) {
		if (o instanceof JSObject) {
			js = (JSObject)o;
			length = (int)js.getMember("length");
		} else {
			js = null;
			this.length = 0;
		}
	}
	
	// https://api.jquery.com/add/#add-selector
	// https://api.jquery.com/add/#add-html
	public JQuery add(String selector) {
		return new JQuery(js.call("add", selector));
	}
	
	// https://api.jquery.com/add/#add-elements
	public JQuery add(Element element) {
		return new JQuery(js.call("add", element));
	}
	
	// https://api.jquery.com/add/#add-selection
	public JQuery add(JQuery selection) {
		return new JQuery(js.call("add", selection.js));
	}
	
	// https://api.jquery.com/add/#add-selector-context
	public JQuery add(String selector, Element context) {
		return new JQuery(js.call("add", selector, context));
	}
	
	// https://api.jquery.com/addBack/#addBack-selector
	public JQuery addBack() {
		return new JQuery(js.call("addBack"));
	}
	
	// https://api.jquery.com/addBack/#addBack-selector
	public JQuery addBack(String selector) {
		return new JQuery(js.call("addBack", selector));
	}
	
	// https://api.jquery.com/addClass/#addClass-className
	public JQuery addClass(String classname) {
		js.call("addClass", classname);
		return this;
	}
	
	public JQuery addClass(IntStringStringFunction function) {
		js.call("addClass", createFunction(function));
		return this;
	}
	
	// https://api.jquery.com/after/#after-content-content
	public JQuery after(String content) {
		js.call("after", content);
		return this;
	}

	// https://api.jquery.com/after/#after-content-content
	public JQuery after(Element content) {
		js.call("after", content);
		return this;
	}

	// https://api.jquery.com/after/#after-content-content
	public JQuery after(Element ... content) {
		js.call("after", createArray(content));
		return this;
	}

	// https://api.jquery.com/after/#after-content-content
	public JQuery after(JQuery content) {
		js.call("after", content.js);
		return this;
	}
	
	// https://api.jquery.com/after/#after-function
	public JQuery after(IntStringFunction function) {
		return new JQuery(js.call("after", createFunction(function)));
	}
	
	// https://api.jquery.com/after/#after-function
	public JQuery after(IntElementFunction function) {
		return new JQuery(js.call("after", createFunction(function)));
	}
	
	// https://api.jquery.com/after/#after-function
	public JQuery after(IntJQueryFunction function) {
		return new JQuery(js.call("after", createFunction(function)));
	}
	
	// https://api.jquery.com/after/#after-function
	public JQuery after(IntStringStringFunction function) {
		return new JQuery(js.call("after", createFunction(function)));
	}
	
	// https://api.jquery.com/after/#after-function
	public JQuery after(IntStringElementFunction function) {
		return new JQuery(js.call("after", createFunction(function)));
	}
	
	// https://api.jquery.com/after/#after-function
	public JQuery after(IntStringJQueryFunction function) {
		return new JQuery(js.call("after", createFunction(function)));
	}
	
	// http://api.jquery.com/animate/
	public JQuery animate(PlainObject properties) {
		return new JQuery(js.call("animate", properties.toJSObject()));
	}

	// http://api.jquery.com/animate/
	public JQuery animate(PlainObject properties, VoidFunction complete) {
		return new JQuery(js.call("animate", properties.toJSObject(), createFunction(complete)));
	}

	// http://api.jquery.com/animate/
	public JQuery animate(PlainObject properties, int duration) {
		return new JQuery(js.call("animate", properties.toJSObject(), duration));
	}

	// http://api.jquery.com/animate/
	public JQuery animate(PlainObject properties, int duration, VoidFunction complete) {
		return new JQuery(js.call("animate", properties.toJSObject(),duration, createFunction(complete)));
	}

	// http://api.jquery.com/animate/
	public JQuery animate(PlainObject properties, int duration, String easing) {
		return new JQuery(js.call("animate", properties.toJSObject(), duration, easing));
	}

	// http://api.jquery.com/animate/
	public JQuery animate(PlainObject properties, int duration, String easing, VoidFunction complete) {
		return new JQuery(js.call("animate", properties.toJSObject(), duration, easing, createFunction(complete)));
	}

	// http://api.jquery.com/animate/
	public JQuery animate(PlainObject properties, AnimationOptions options) {
		return new JQuery(js.call("animate", properties.toJSObject(), options.toJSObject()));
	}

	// https://api.jquery.com/append/#append-content-content
	public JQuery append(String content) {
		js.call("append", content);
		return this;
	}

	// https://api.jquery.com/append/#append-content-content
	public JQuery append(Element content) {
		js.call("append", content);
		return this;
	}

	// https://api.jquery.com/append/#append-content-content
	public JQuery append(Element ... content) {
		js.call("append", createArray(content));
		return this;
	}

	// https://api.jquery.com/append/#append-content-content
	public JQuery append(JQuery content) {
		js.call("append", content.js);
		return this;
	}

	// https://api.jquery.com/append/#append-function
	public JQuery append(IntStringStringFunction function) {
		return new JQuery(js.call("append", createFunction(function)));
	}
	// https://api.jquery.com/append/#append-function
	public JQuery append(IntStringElementFunction function) {
		return new JQuery(js.call("append", createFunction(function)));
	}
	// https://api.jquery.com/append/#append-function
	public JQuery append(IntStringJQueryFunction function) {
		return new JQuery(js.call("append", createFunction(function)));
	}
	
	// https://api.jquery.com/appendTo/#appendTo-target
	public JQuery appendTo(String target) {
		js.call("appendTo", target);
		return this;
	}

	// https://api.jquery.com/appendTo/#appendTo-target
	public JQuery appendTo(Element target) {
		js.call("appendTo", target);
		return this;
	}

	// https://api.jquery.com/appendTo/#appendTo-target
	public JQuery appendTo(Element ... target) {
		js.call("appendTo", createArray(target));
		return this;
	}

	// https://api.jquery.com/appendTo/#appendTo-target
	public JQuery appendTo(JQuery target) {
		js.call("appendTo", target.js);
		return this;
	}
	
	// https://api.jquery.com/attr/#attr1
	public String attr(String attributeName) {
		return (String)js.call("attr", attributeName);
	}

	// https://api.jquery.com/attr/#attr1
	public JQuery attr(String attributeName, String value) {
		js.call("attr", attributeName, value);
		return this;
	}

	public JQuery attr(String attributeName, Number value) {
		js.call("attr", attributeName, value);
		return this;
	}

	// https://api.jquery.com/attr/#attr-attributes
	public JQuery attr(PlainObject attributes) {
		js.call("attr", attributes.toJSObject());
		return this;
	}
	
	// https://api.jquery.com/attr/#attr-attributeName-function
	public JQuery attr(String attributeName, IntStringStringFunction function) {
		js.call("attr", attributeName, createFunction(function));
		return this;
	}
	
	// https://api.jquery.com/attr/#attr-attributeName-function
	public JQuery attr(String attributeName, IntStringIntFunction function) {
		js.call("attr", attributeName, createFunction(function));
		return this;
	}

	// https://api.jquery.com/before/#before-content-content
	public JQuery before(String content) {
		js.call("before", content);
		return this;
	}

	// https://api.jquery.com/before/#before-content-content
	public JQuery before(Element content) {
		js.call("before", content);
		return this;
	}

	// https://api.jquery.com/before/#before-content-content
	public JQuery before(Element ... content) {
		js.call("before", createArray(content));
		return this;
	}

	// https://api.jquery.com/before/#before-content-content
	public JQuery before(JQuery content) {
		js.call("before", content.js);
		return this;
	}
	
	// https://api.jquery.com/before/#before-function
	public JQuery before(IntStringFunction function) {
		return new JQuery(js.call("before", createFunction(function)));
	}
	
	// https://api.jquery.com/before/#before-function
	public JQuery before(IntElementFunction function) {
		return new JQuery(js.call("before", createFunction(function)));
	}

	// https://api.jquery.com/before/#before-function
	public JQuery before(IntJQueryFunction function) {
		return new JQuery(js.call("before", createFunction(function)));
	}

	// https://api.jquery.com/before/#before-function
	public JQuery before(IntStringStringFunction function) {
		return new JQuery(js.call("before", createFunction(function)));
	}

	// https://api.jquery.com/before/#before-function
	public JQuery before(IntStringElementFunction function) {
		return new JQuery(js.call("before", createFunction(function)));
	}

	// https://api.jquery.com/before/#before-function
	public JQuery before(IntStringJQueryFunction function) {
		return new JQuery(js.call("before", createFunction(function)));
	}

	// https://api.jquery.com/bind/#bind-eventType-eventData-handler
	public JQuery bind(String eventType, EventHandler handler) {
		JSObject jsHandler = createFunction(handler);
		eventHandlerMap.put(handler.hashCode(), jsHandler);
		js.call("bind", eventType, jsHandler);
		return this;
	}

	// https://api.jquery.com/bind/#bind-eventType-eventData-handler
	public JQuery bind(String eventType, Object eventData, EventHandler handler) {
		JSObject jsHandler = createFunction(handler);
		eventHandlerMap.put(jsHandler.hashCode(), jsHandler);
		js.call("bind", eventType, eventData, jsHandler);
		return this;
	}
	
	// https://api.jquery.com/blur/
	public JQuery blur(EventHandler handler) {
		return bind("blur",handler);
	}
	
	// https://api.jquery.com/blur/
	public JQuery blur(Object data, EventHandler handler) {
		return bind("blur",data,handler);
	}
	
	// https://api.jquery.com/blur/
	public JQuery blur() {
		js.call("blur");
		return this;
	}

	// https://api.jquery.com/change/
	public JQuery change(EventHandler handler) {
		return bind("change",handler);
	}
	
	// https://api.jquery.com/change/
	public JQuery change(Object data, EventHandler handler) {
		return bind("change",data,handler);
	}
	
	// https://api.jquery.com/change/
	public JQuery change() {
		js.call("change");
		return this;
	}
	
	// https://api.jquery.com/children/
	public JQuery children() {
		return new JQuery(js.call("children"));
	}
	
	// https://api.jquery.com/children/
	public JQuery children(String selector) {
		return new JQuery(js.call("children",selector));
	}
	
	// https://api.jquery.com/clearQueue/
	public JQuery clearQueue() {
		return new JQuery(js.call("clearQueue"));
	}

	// https://api.jquery.com/clearQueue/
	public JQuery clearQueue(String queueName) {
		return new JQuery(js.call("clearQueue",queueName));
	}

	// https://api.jquery.com/click/
	public JQuery click(EventHandler handler) {
		return bind("click",handler);
	}
	
	// https://api.jquery.com/click/
	public JQuery click(Object data, EventHandler handler) {
		return bind("click",data,handler);
	}
	
	// https://api.jquery.com/click/
	public JQuery click() {
		js.call("click");
		return this;
	}
	
	// https://api.jquery.com/clone/
	public JQuery clone() {
		return clone(false,false);
	}
	
	// https://api.jquery.com/clone/
	public JQuery clone(boolean withDataAndEvents) {
		return clone(withDataAndEvents,withDataAndEvents);
	}
	
	// https://api.jquery.com/clone/
	public JQuery clone(boolean withDataAndEvents, boolean deepWithDataAndEvents) {
		return new JQuery(js.call("clone", withDataAndEvents,deepWithDataAndEvents));
	}
	
	// https://api.jquery.com/closest/
	public JQuery closest(String selector) {
		return new JQuery(js.call("closest", selector));
	}

	// https://api.jquery.com/closest/
	public JQuery closest(String selector, Element context) {
		return new JQuery(js.call("closest", selector, context));
	}

	// https://api.jquery.com/closest/
	public JQuery closest(JQuery selection) {
		return new JQuery(js.call("closest", selection));
	}

	// https://api.jquery.com/closest/
	public JQuery closest(Element element) {
		return new JQuery(js.call("closest", element));
	}

	// https://api.jquery.com/contents/
	public JQuery contents() {
		return new JQuery(js.call("contents"));
	}

	// https://api.jquery.com/contextmenu/
	public JQuery contextmenu(EventHandler handler) {
		bind("contextmenu",handler);
		return this;
	}
	
	// https://api.jquery.com/contextmenu/
	public JQuery contextmenu(Object eventData, EventHandler handler) {
		bind("contextmenu",eventData, handler);
		return this;
	}
	
	// https://api.jquery.com/contextmenu/
	public JQuery contextmenu() {
		js.call("contextmenu");
		return this;
	}
	
	// https://api.jquery.com/css/
	public String css(String propertyName) {
		return (String)js.call("css", propertyName);
	}

	// https://api.jquery.com/css/#css-propertyNames
	public PlainObject css(String ... propertyNames) {
		return new PlainObject((JSObject)js.call("css", createArray(propertyNames)));
	}

	// https://api.jquery.com/css/
	public JQuery css(String propertyName, String value) {
		js.call("css", propertyName, value);
		return this;
	}

	// https://api.jquery.com/css/
	public JQuery css(String propertyName, IntStringStringFunction function) {
		js.call("css", propertyName, createFunction(function));
		return this;
	}
	
	// https://api.jquery.com/css/
	public JQuery css(PlainObject properties) {
		js.call("css", properties.toJSObject());
		return this;		
	}
	
	// https://api.jquery.com/data/
	public JQuery data(String key, Object value) {
		js.call("data", key, value);
		return this;
	}
	
	// https://api.jquery.com/data/
	public JQuery data(PlainObject obj) {
		js.call("data", obj.toJSObject());
		return this;		
	}
	
	// https://api.jquery.com/data/
	public Object data(String key) {
		return js.call("data",key);
	}
	
	// https://api.jquery.com/data/
	public Object data() {
		return js.call("data");
	}
	
	// https://api.jquery.com/dblclick/
	public JQuery dblclick(EventHandler handler) {
		return bind("dblclick",handler);
	}
	
	// https://api.jquery.com/dblclick/
	public JQuery dblclick(Object data, EventHandler handler) {
		return bind("dblclick",data,handler);
	}
	
	// https://api.jquery.com/dblclick/
	public JQuery dblclick() {
		js.call("dblclick");
		return this;
	}

	// https://api.jquery.com/delay/
	public JQuery delay(int duration) {
		js.call("delay", duration);
		return this;
	}
	
	// https://api.jquery.com/delay/
	public JQuery delay(int duration, String queueName) {
		js.call("delay", duration, queueName);
		return this;
	}
	
	// https://api.jquery.com/delegate/
	public JQuery delegate(String selector, String eventType, EventHandler handler) {
		js.call("delegate", selector, eventType, createFunction(handler));
		return this;		
	
	}
	
	// https://api.jquery.com/delegate/
	public JQuery delegate(String selector, String eventType, Object eventData, EventHandler handler) {
		js.call("delegate", selector, eventType, eventData, createFunction(handler));
		return this;		

	}
	
	// https://api.jquery.com/delegate/
	public JQuery delegate(String selector, PlainObject events) {
		js.call("delegate", selector, events.toJSObject());
		return this;
	}
	
	// https://api.jquery.com/dequeue/
	public JQuery dequeue() {
		js.call("dequeue");
		return this;
	}

	// https://api.jquery.com/dequeue/
	public JQuery dequeue(String queueName) {
		js.call("dequeue",queueName);
		return this;
	}
	
	// https://api.jquery.com/detach/
	public JQuery detach() {
		js.call("detach");
		return this;
	}

	// https://api.jquery.com/detach/
	public JQuery detach(String selector) {
		js.call("detach",selector);
		return this;
	}
	
	// https://api.jquery.com/each/
	public JQuery each(IntElementVoidFunction function) {
		for (int i=0 ; i<this.length ; i++) {
			Element el = (Element)js.getSlot(i);
			function.apply(i, el);
		}
		return this;
	}
	
	// https://api.jquery.com/empty/
	public JQuery empty() {
		return new JQuery(js.call("empty"));
	}

	// https://api.jquery.com/end/
	public JQuery end() {
		return new JQuery(js.call("end"));
	}

	// https://api.jquery.com/eq/
	public JQuery eq(int index) {
		return new JQuery(js.call("eq", index));
	}

	// https://api.jquery.com/error/
	public JQuery error(EventHandler handler) {
		return bind("error", handler);
	}

	// https://api.jquery.com/error/
	public JQuery error(Object eventData, EventHandler handler) {
		return bind("error", eventData, handler);
	}
	
	// https://api.jquery.com/fadeIn/
	public JQuery fadeIn() {
		js.call("fadeIn");
		return this;
	}

	// https://api.jquery.com/fadeIn/
	public JQuery fadeIn(String duration) {
		js.call("fadeIn", duration);
		return this;
	}

	// https://api.jquery.com/fadeIn/
	public JQuery fadeIn(String duration, VoidFunction complete) {
		js.call("fadeIn", duration, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/fadeIn/
	public JQuery fadeIn(AnimationOptions options) {
		js.call("fadeIn", options.toJSObject());
		return this;
	}

	// https://api.jquery.com/fadeIn/
	public JQuery fadeIn(String duration, String easing) {
		js.call("fadeIn", duration, easing);
		return this;
	}

	// https://api.jquery.com/fadeIn/
	public JQuery fadeIn(String duration, String easing, VoidFunction complete) {
		js.call("fadeIn", duration, easing, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/fadeOut/
	public JQuery fadeOut() {
		js.call("fadeOut");
		return this;
	}

	// https://api.jquery.com/fadeOut/
	public JQuery fadeOut(String duration) {
		js.call("fadeOut", duration);
		return this;
	}

	// https://api.jquery.com/fadeOut/
	public JQuery fadeOut(String duration, VoidFunction complete) {
		js.call("fadeOut", duration, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/fadeOut/
	public JQuery fadeOut(AnimationOptions options) {
		js.call("fadeOut", options.toJSObject());
		return this;
	}

	// https://api.jquery.com/fadeOut/
	public JQuery fadeOut(String duration, String easing) {
		js.call("fadeOut", duration, easing);
		return this;
	}

	// https://api.jquery.com/fadeOut/
	public JQuery fadeOut(String duration, String easing, VoidFunction complete) {
		js.call("fadeOut", duration, easing, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/fadeTo/
	public JQuery fadeTo(String duration, double opacity) {
		js.call("fadeTo", duration, opacity);
		return this;
	}

	// https://api.jquery.com/fadeTo/
	public JQuery fadeTo(String duration, double opacity, VoidFunction complete) {
		js.call("fadeTo", duration, opacity, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/fadeTo/
	public JQuery fadeTo(String duration, double opacity, String easing) {
		js.call("fadeTo", duration, opacity, easing);
		return this;
	}

	// https://api.jquery.com/fadeTo/
	public JQuery fadeTo(String duration, double opacity, String easing, VoidFunction complete) {
		js.call("fadeTo", duration, opacity, easing, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/fadeToggle/
	public JQuery fadeToggle() {
		js.call("fadeToggle");
		return this;
	}

	// https://api.jquery.com/fadeToggle/
	public JQuery fadeToggle(String duration) {
		js.call("fadeToggle", duration);
		return this;
	}

	// https://api.jquery.com/fadeToggle/
	public JQuery fadeToggle(String duration, String easing) {
		js.call("fadeToggle",duration, easing);
		return this;
	}

	// https://api.jquery.com/fadeToggle/
	public JQuery fadeToggle(String duration, String easing, VoidFunction complete) {
		js.call("fadeToggle", duration, easing, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/fadeToggle/
	public JQuery fadeToggle(AnimationOptions options) {
		js.call("fadeToggle",options.toJSObject());
		return this;
	}

	// https://api.jquery.com/filter/
	public JQuery filter(String selector) {
		return new JQuery(js.call("filter", selector));
	}

	// https://api.jquery.com/filter/
	public JQuery filter(IntElementBooleanFunction selector) {
		return new JQuery(js.call("filter", createFunction(selector)));
	}

	// https://api.jquery.com/filter/
	public JQuery filter(Element elemements) {
		return new JQuery(js.call("filter", elemements));
	}

	// https://api.jquery.com/filter/
	public JQuery filter(Element ... elemements) {
		return new JQuery(js.call("filter", createArray(elemements)));
	}

	// https://api.jquery.com/filter/
	public JQuery filter(JQuery selector) {
		return new JQuery(js.call("filter", selector.js));
	}

	// https://api.jquery.com/find/
	public JQuery find(String selector) {
		return new JQuery(js.call("find", selector));
	}

	// https://api.jquery.com/find/
	public JQuery find(Element element) {
		return new JQuery(js.call("find", element));
	}

	// https://api.jquery.com/find/
	public JQuery find(JQuery element) {
		return new JQuery(js.call("find", element.js));
	}

	// https://api.jquery.com/finish/
	public JQuery finish() {
		js.call("finish");
		return this;
	}

	// https://api.jquery.com/finish/
	public JQuery finish(String queue) {
		js.call("finish", queue);
		return this;
	}

	// https://api.jquery.com/first/
	public JQuery first() {
		return new JQuery(js.call("first"));

	}
	// https://api.jquery.com/focus/
	public JQuery focus(EventHandler handler) {
		return bind("focus",handler);
	}
	
	// https://api.jquery.com/focus/
	public JQuery focus(Object data, EventHandler handler) {
		return bind("focus",data,handler);
	}
	
	// https://api.jquery.com/focus/
	public JQuery focus() {
		js.call("focus");
		return this;
	}
	
	// https://api.jquery.com/focusin/
	public JQuery focusin(EventHandler handler) {
		return bind("focusin",handler);
	}
	
	// https://api.jquery.com/focusin/
	public JQuery focusin(Object data, EventHandler handler) {
		return bind("focusin",data,handler);
	}
	
	// https://api.jquery.com/focusin/
	public JQuery focusin() {
		js.call("focusin");
		return this;
	}
	// https://api.jquery.com/focusout/
	public JQuery focusout(EventHandler handler) {
		return bind("focusout",handler);
	}
	
	// https://api.jquery.com/focusout/
	public JQuery focusout(Object data, EventHandler handler) {
		return bind("focusout",data,handler);
	}
	
	// https://api.jquery.com/focusout/
	public JQuery focusout() {
		js.call("focusout");
		return this;
	}
	
	// https://api.jquery.com/get/
	public Element[] get() {
		JSObject els = (JSObject)js.call("get");
		int count = (int)els.getMember("length");
		Element[] r = new Element[count];
		for (int i=0 ; i<count ; i++) {
			r[i] = (Element)els.getSlot(i);
		}
		return r;
	}

	// https://api.jquery.com/get/
	public Object get(int i) {
		return js.call("get", i);
	}
	
	// https://api.jquery.com/has/
	public JQuery has(String selector) {
		return new JQuery(js.call("has", selector));
	}

	// https://api.jquery.com/has/
	public JQuery has(Element selector) {
		return new JQuery(js.call("has", selector));
	}

	// https://api.jquery.com/hasClass/#hasClass-className
	public boolean hasClass(String className) {
		return (boolean)js.call("hasClass", className);
	}

	// https://api.jquery.com/height/
	public int height() {
		return (int)js.call("height");
	}

	// https://api.jquery.com/height/
	public JQuery height(String value) {
		js.call("height", value);
		return this;
	}

	// https://api.jquery.com/height/
	public JQuery height(IntIntStringFunction function) {
		js.call("height", createFunction(function));
		return this;
	}

	// https://api.jquery.com/hide/
	public JQuery hide() {
		js.call("hide");
		return this;
	}

	// https://api.jquery.com/hide/
	public JQuery hide(String duration) {
		js.call("hide", duration);
		return this;
	}

	// https://api.jquery.com/hide/
	public JQuery hide(String duration, VoidFunction complete) {
		js.call("hide", duration, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/hide/
	public JQuery hide(AnimationOptions options) {
		js.call("hide", options.toJSObject());
		return this;
	}

	// https://api.jquery.com/hide/
	public JQuery hide(String duration, String easing) {
		js.call("hide", duration, easing);
		return this;
	}

	// https://api.jquery.com/hide/
	public JQuery hide(String duration, String easing, VoidFunction complete) {
		js.call("hide", duration, easing,createFunction(complete));
		return this;
	}

	// https://api.jquery.com/hover/
	public JQuery hover(EventHandler handler) {
		return bind("hover",handler);
	}
	
	// https://api.jquery.com/hover/
	public JQuery hover(Object data, EventHandler handler) {
		return bind("hover",data,handler);
	}
	
	// https://api.jquery.com/hover/
	public JQuery hover() {
		js.call("hover");
		return this;
	}

	// https://api.jquery.com/html/
	public String html() {
		return (String)js.call("html");
	}

	// https://api.jquery.com/html/
	public JQuery html(String htmlString) {
		return new JQuery(js.call("html", htmlString));
	}

	// https://api.jquery.com/html/
	public JQuery html(IntStringStringFunction function) {
		return new JQuery(js.call("html",createFunction(function)));
	}

	// https://api.jquery.com/index/
	public int index() {
		return (int)js.call("index");
	}

	// https://api.jquery.com/index/
	public int index(String selector) {
		return (int)js.call("index", selector);
	}

	// https://api.jquery.com/index/
	public int index(Element element) {
		return (int)js.call("index", element);
	}
	
	// https://api.jquery.com/index/
	public int index(JQuery element) {
		return (int)js.call("index", element.js);
	}

	// https://api.jquery.com/innerHeight/
	public int innerHeight() {
		return (int)js.call("innerHeight");
	}

	// https://api.jquery.com/innerHeight/
	public JQuery innerHeight(String height) {
		js.call("innerHeight", height);
		return this;
	}

	// https://api.jquery.com/innerHeight/
	public JQuery innerHeight(int height) {
		js.call("innerHeight", height);
		return this;
	}

	// https://api.jquery.com/innerHeight/
	public JQuery innerHeight(IntIntStringFunction function) {
		js.call("innerHeight", createFunction(function));
		return this;
	}

	// https://api.jquery.com/innerWidth/
	public int innerWidth() {
		return (int)js.call("innerWidth");
	}

	// https://api.jquery.com/innerWidth/
	public JQuery innerWidth(String height) {
		js.call("innerWidth", height);
		return this;
	}

	// https://api.jquery.com/innerWidth/
	public JQuery innerWidth(int height) {
		js.call("innerWidth", height);
		return this;
	}

	// https://api.jquery.com/innerWidth/
	public JQuery innerWidth(IntIntStringFunction function) {
		js.call("innerWidth", createFunction(function));
		return this;
	}
	
	// https://api.jquery.com/insertAfter/
	public JQuery insertAfter(String target) {
		return new JQuery(js.call("insertAfter", target));
	}
	
	// https://api.jquery.com/insertAfter/
	public JQuery insertAfter(Element target) {
		return new JQuery(js.call("insertAfter", target));
	}
	// https://api.jquery.com/insertAfter/
	public JQuery insertAfter(Element ... target) {
		return new JQuery(js.call("insertAfter", createArray(target)));
	}
	// https://api.jquery.com/insertAfter/
	public JQuery insertAfter(JQuery target) {
		return new JQuery(js.call("insertAfter", target.js));
	}

	// https://api.jquery.com/insertBefore/
	public JQuery insertBefore(String target) {
		return new JQuery(js.call("insertBefore", target));
	}
	
	// https://api.jquery.com/insertBefore/
	public JQuery insertBefore(Element target) {
		return new JQuery(js.call("insertBefore", target));
	}
	// https://api.jquery.com/insertBefore/
	public JQuery insertBefore(Element ... target) {
		return new JQuery(js.call("insertBefore", createArray(target)));
	}
	// https://api.jquery.com/insertBefore/
	public JQuery insertBefore(JQuery target) {
		return new JQuery(js.call("insertBefore", target.js));
	}
	
	// https://api.jquery.com/is/
	public boolean is(String selector) {
		return (boolean)js.call("is", selector);
	}

	// https://api.jquery.com/is/
	public boolean is(IntElementBooleanFunction function) {
		return (boolean)js.call("is", createFunction(function));
	}

	// https://api.jquery.com/is/
	public boolean is(JQuery selection) {
		return (boolean)js.call("is", selection.js);
	}

	// https://api.jquery.com/is/
	public boolean is(Element ... elements) {
		return (boolean)js.call("is", createArray(elements));
	}

	// https://api.jquery.com/keydown/
	public JQuery keydown(EventHandler handler) {
		return bind("keydown",handler);
	}
	
	// https://api.jquery.com/keydown/
	public JQuery keydown(Object data, EventHandler handler) {
		return bind("keydown",data,handler);
	}
	
	// https://api.jquery.com/keydown/
	public JQuery keydown() {
		js.call("keydown");
		return this;
	}
	// https://api.jquery.com/keypress/
	public JQuery keypress(EventHandler handler) {
		return bind("keypress",handler);
	}
	
	// https://api.jquery.com/keypress/
	public JQuery keypress(Object data, EventHandler handler) {
		return bind("keypress",data,handler);
	}
	
	// https://api.jquery.com/keypress/
	public JQuery keypress() {
		js.call("keypress");
		return this;
	}
	// https://api.jquery.com/keyup/
	public JQuery keyup(EventHandler handler) {
		return bind("keyup",handler);
	}
	
	// https://api.jquery.com/keyup/
	public JQuery keyup(Object data, EventHandler handler) {
		return bind("keyup",data,handler);
	}
	
	// https://api.jquery.com/keyup/
	public JQuery keyup() {
		js.call("keyup");
		return this;
	}
	
	// https://api.jquery.com/last/
	public JQuery last() {
		return new JQuery(js.call("last"));
	}

	// https://api.jquery.com/map/
	public JQuery map(IntElementObjectFunction function) {
		return new JQuery(js.call("map", createFunction(function)));

	}
	// https://api.jquery.com/mousedown/
	public JQuery mousedown(EventHandler handler) {
		return bind("mousedown",handler);
	}
	
	// https://api.jquery.com/mousedown/
	public JQuery mousedown(Object data, EventHandler handler) {
		return bind("mousedown",data,handler);
	}
	
	// https://api.jquery.com/mousedown/
	public JQuery mousedown() {
		js.call("mousedown");
		return this;
	}
	// https://api.jquery.com/mouseenter/
	public JQuery mouseenter(EventHandler handler) {
		return bind("mouseenter",handler);
	}
	
	// https://api.jquery.com/mouseenter/
	public JQuery mouseenter(Object data, EventHandler handler) {
		return bind("mouseenter",data,handler);
	}
	
	// https://api.jquery.com/mouseenter/
	public JQuery mouseenter() {
		js.call("mouseenter");
		return this;
	}
	// https://api.jquery.com/mouseleave/
	public JQuery mouseleave(EventHandler handler) {
		return bind("mouseleave",handler);
	}
	
	// https://api.jquery.com/mouseleave/
	public JQuery mouseleave(Object data, EventHandler handler) {
		return bind("mouseleave",data,handler);
	}
	
	// https://api.jquery.com/mouseleave/
	public JQuery mouseleave() {
		js.call("mouseleave");
		return this;
	}
	// https://api.jquery.com/mousemove/
	public JQuery mousemove(EventHandler handler) {
		return bind("mousemove",handler);
	}
	
	// https://api.jquery.com/mousemove/
	public JQuery mousemove(Object data, EventHandler handler) {
		return bind("mousemove",data,handler);
	}
	
	// https://api.jquery.com/mousemove/
	public JQuery mousemove() {
		js.call("mousemove");
		return this;
	}
	// https://api.jquery.com/mouseout/
	public JQuery mouseout(EventHandler handler) {
		return bind("mouseout",handler);
	}
	
	// https://api.jquery.com/mouseout/
	public JQuery mouseout(Object data, EventHandler handler) {
		return bind("mouseout",data,handler);
	}
	
	// https://api.jquery.com/mouseout/
	public JQuery mouseout() {
		js.call("mouseout");
		return this;
	}
	// https://api.jquery.com/mouseover/
	public JQuery mouseover(EventHandler handler) {
		return bind("mouseover",handler);
	}
	
	// https://api.jquery.com/mouseover/
	public JQuery mouseover(Object data, EventHandler handler) {
		return bind("mouseover",data,handler);
	}
	
	// https://api.jquery.com/mouseover/
	public JQuery mouseover() {
		js.call("mouseover");
		return this;
	}
	// https://api.jquery.com/mouseup/
	public JQuery mouseup(EventHandler handler) {
		return bind("mouseup",handler);
	}
	
	// https://api.jquery.com/mouseup/
	public JQuery mouseup(Object data, EventHandler handler) {
		return bind("mouseup",data,handler);
	}
	
	// https://api.jquery.com/mouseup/
	public JQuery mouseup() {
		js.call("mouseup");
		return this;
	}
	
	// https://api.jquery.com/next/
	public JQuery next() {
		return new JQuery(js.call("next"));
	}

	// https://api.jquery.com/next/
	public JQuery next(String selector) {
		return new JQuery(js.call("next",selector));
	}

	// https://api.jquery.com/nextAll/
	public JQuery nextAll() {
		return new JQuery(js.call("nextAll"));
	}

	// https://api.jquery.com/nextAll/
	public JQuery nextAll(String selector) {
		return new JQuery(js.call("nextAll",selector));
	}

	// https://api.jquery.com/nextUntil/
	public JQuery nextUntil() {
		return new JQuery(js.call("nextUntil"));
	}

	// https://api.jquery.com/nextUntil/
	public JQuery nextUntil(String selector) {
		return new JQuery(js.call("nextUntil",selector));
	}

	// https://api.jquery.com/nextUntil/
	public JQuery nextUntil(String selector, String filter) {
		return new JQuery(js.call("nextUntil",selector,filter));
	}

	// https://api.jquery.com/nextUntil/
	public JQuery nextUntil(Element element) {
		return new JQuery(js.call("nextUntil", element));
	}

	// https://api.jquery.com/nextUntil/
	public JQuery nextUntil(Element element, String filter) {
		return new JQuery(js.call("nextUntil", element, filter));
	}

	// https://api.jquery.com/nextUntil/
	public JQuery nextUntil(JQuery element) {
		return new JQuery(js.call("nextUntil", element.js));
	}

	// https://api.jquery.com/nextUntil/
	public JQuery nextUntil(JQuery element, String filter) {
		return new JQuery(js.call("nextUntil", element, filter));
	}

	// https://api.jquery.com/not/
	public JQuery not(String selector) {
		return new JQuery(js.call("not", selector));
	}

	// https://api.jquery.com/not/
	public JQuery not(IntElementBooleanFunction function) {
		return new JQuery(js.call("not", createFunction(function)));
	}

	// https://api.jquery.com/not/
	public JQuery not(JQuery selection) {
		return new JQuery(js.call("not", selection.js));
	}

	// https://api.jquery.com/off/
	public JQuery off(String events) {
		js.call("off", events);
		return this;
	}
	
	// https://api.jquery.com/off/
	public JQuery off(String events, String selector) {
		js.call("off", events, selector);
		return this;
	}
	
	// https://api.jquery.com/off/
	public JQuery off(String events, EventHandler handler) {
		js.call("off", events, createFunction(handler));
		return this;
	}
	
	// https://api.jquery.com/off/
	public JQuery off(String events, String selector, EventHandler handler) {
		js.call("off", events, selector, createFunction(handler));
		return this;
	}
	
	// https://api.jquery.com/off/
	public JQuery off(PlainObject events) {
		js.call("off", events.toJSObject());
		return this;
	}
	
	// https://api.jquery.com/off/
	public JQuery off(PlainObject events, String selector) {
		js.call("off", events.toJSObject(), selector);
		return this;
	}
	
	// https://api.jquery.com/off/
	public JQuery off(Event event) {
		js.call("off", event);
		return this;
	}

	// https://api.jquery.com/off/
	public JQuery off() {
		js.call("off");
		return this;
	}
	
	// https://api.jquery.com/offset/
	public Offset offset() {
		return new Offset((JSObject)js.call("offset"));
	}
	
	// https://api.jquery.com/offset/
	public JQuery offset(Offset coordinates) {
		js.call("offset", coordinates);
		return this;
	}

	// https://api.jquery.com/offset/
	public JQuery offset(IntOffsetOffsetFunction function) {
		js.call("offset", createFunction(function));
		return this;
	}

	// https://api.jquery.com/offsetParent/
	public JQuery offsetParent() {
		return new JQuery(js.call("offsetParent"));
	}

	// https://api.jquery.com/on/
	public JQuery on(String events, EventHandler handler) {
		js.call("on", events, createFunction(handler));
		return this;		
	}

	// https://api.jquery.com/on/
	public JQuery on(String events, String selector, EventHandler handler) {
		js.call("on", events, selector, createFunction(handler));
		return this;		
	}

	// https://api.jquery.com/on/
	public JQuery on(String events, Object data, EventHandler handler) {
		js.call("on", events, data, createFunction(handler));
		return this;		
	}

	// https://api.jquery.com/on/
	public JQuery on(String events, String selector, Object data, EventHandler handler) {
		js.call("on", events, selector, data, createFunction(handler));
		return this;		
	}
	
	// https://api.jquery.com/on/
	public JQuery on(PlainObject events) {
		js.call("on", events.toJSObject());
		return this;		
	}

	// https://api.jquery.com/on/
	public JQuery on(PlainObject events, String selector) {
		js.call("on", events.toJSObject(), selector);
		return this;		
	}

	// https://api.jquery.com/on/
	public JQuery on(PlainObject events, Object data) {
		js.call("on", events.toJSObject(), data);
		return this;		
	}

	// https://api.jquery.com/on/
	public JQuery on(PlainObject events, String selector, Object data) {
		js.call("on", events.toJSObject(), selector, data);
		return this;		
	}
	
	// https://api.jquery.com/one/
	public JQuery one(String events, EventHandler handler) {
		js.call("one", events, createFunction(handler));
		return this;
	}
	
	// https://api.jquery.com/one/
	public JQuery one(String events, PlainObject data, EventHandler handler) {
		js.call("one", events, data.toJSObject(), createFunction(handler));
		return this;
	}
	
	// https://api.jquery.com/one/
	public JQuery one(String events, String selector, EventHandler handler) {
		js.call("one", events, selector, createFunction(handler));
		return this;
	}

	// https://api.jquery.com/one/
	public JQuery one(String events, Object data, EventHandler handler) {
		js.call("one", events, data, createFunction(handler));
		return this;
	}
	
	// https://api.jquery.com/one/
	public JQuery one(String events, String selector, Object data, EventHandler handler) {
		js.call("one", events, selector, data, createFunction(handler));
		return this;
	}

	// https://api.jquery.com/one/
	public JQuery one(PlainObject events) {
		js.call("one", events.toJSObject());
		return this;
	}

	// https://api.jquery.com/one/
	public JQuery one(PlainObject events, String selector) {
		js.call("one", events.toJSObject(), selector);
		return this;
	}

	// https://api.jquery.com/one/
	public JQuery one(PlainObject events, Object data) {
		js.call("one", events.toJSObject(), data);
		return this;
	}

	// https://api.jquery.com/one/
	public JQuery one(PlainObject events, String selector, Object data) {
		js.call("one", events.toJSObject(), selector, data);
		return this;
	}

	// https://api.jquery.com/outerHeight/
	public int outerHeight() {
		return (int)js.call("outerHeight");
	}

	// https://api.jquery.com/outerHeight/
	public JQuery outerHeight(String height) {
		js.call("outerHeight", height);
		return this;
	}

	// https://api.jquery.com/outerHeight/
	public JQuery outerHeight(int height) {
		js.call("outerHeight", height);
		return this;
	}

	// https://api.jquery.com/outerHeight/
	public JQuery outerHeight(IntIntStringFunction function) {
		js.call("outerHeight", createFunction(function));
		return this;
	}

	// https://api.jquery.com/outerWidth/
	public int outerWidth() {
		return (int)js.call("outerWidth");
	}

	// https://api.jquery.com/outerWidth/
	public JQuery outerWidth(String width) {
		js.call("outerWidth", width);
		return this;
	}

	// https://api.jquery.com/outerWidth/
	public JQuery outerWidth(int width) {
		js.call("outerWidth", width);
		return this;
	}

	// https://api.jquery.com/outerWidth/
	public JQuery outerWidth(IntIntStringFunction function) {
		js.call("outerWidth", createFunction(function));
		return this;
	}

	// https://api.jquery.com/parent/
	public JQuery parent() {
		return new JQuery(js.call("parent"));
	}

	// https://api.jquery.com/parent/
	public JQuery parent(String selector) {
		return new JQuery(js.call("parent",selector));
	}

	// https://api.jquery.com/parents/
	public JQuery parents() {
		return new JQuery(js.call("parents"));
	}

	// https://api.jquery.com/parents/
	public JQuery parents(String selector) {
		return new JQuery(js.call("parents",selector));
	}

	// https://api.jquery.com/parentsUntil/
	public JQuery parentsUntil() {
		return new JQuery(js.call("parentsUntil"));
	}
	// https://api.jquery.com/parentsUntil/
	public JQuery parentsUntil(String selector) {
		return new JQuery(js.call("parentsUntil",selector));
	}
	// https://api.jquery.com/parentsUntil/
	public JQuery parentsUntil(String selector, String filter) {
		return new JQuery(js.call("parentsUntil", selector, filter));
	}
	// https://api.jquery.com/parentsUntil/
	public JQuery parentsUntil(Element element) {
		return new JQuery(js.call("parentsUntil", element));
	}
	// https://api.jquery.com/parentsUntil/
	public JQuery parentsUntil(Element element, String filter) {
		return new JQuery(js.call("parentsUntil", element, filter));
	}
	// https://api.jquery.com/parentsUntil/
	public JQuery parentsUntil(JQuery element) {
		return new JQuery(js.call("parentsUntil", element.js));
	}
	// https://api.jquery.com/parentsUntil/
	public JQuery parentsUntil(JQuery element, String filter) {
		return new JQuery(js.call("parentsUntil", element.js, filter));
	}
	
	// https://api.jquery.com/position/
	public Offset position() {
		return new Offset((JSObject)js.call("position"));
	}
	
	// https://api.jquery.com/prepend/
	public JQuery prepend(String content) {
		return new JQuery(js.call("prepend", content));
	}

	// https://api.jquery.com/prepend/
	public JQuery prepend(Element content) {
		return new JQuery(js.call("prepend", content));
	}
	// https://api.jquery.com/prepend/
	public JQuery prepend(Element ... content) {
		return new JQuery(js.call("prepend", createArray(content)));
	}
	// https://api.jquery.com/prepend/
	public JQuery prepend(JQuery content) {
		return new JQuery(js.call("prepend", content.js));
	}
	
	// https://api.jquery.com/prepend/
	public JQuery prepend(IntStringStringFunction function) {
		return new JQuery(js.call("prepend", function));
	}

	// https://api.jquery.com/prependTo/
	public JQuery prependTo(String target) {
		return new JQuery(js.call("prependTo", target));
	}

	// https://api.jquery.com/prependTo/
	public JQuery prependTo(Element target) {
		return new JQuery(js.call("prependTo", target));
	}

	// https://api.jquery.com/prependTo/
	public JQuery prependTo(Element ... target) {
		return new JQuery(js.call("prependTo", createArray(target)));
	}

	// https://api.jquery.com/prependTo/
	public JQuery prependTo(JQuery target) {
		return new JQuery(js.call("prependTo", target.js));
	}

	// https://api.jquery.com/prev/
	public JQuery prev() {
		return new JQuery(js.call("prev"));
	}

	// https://api.jquery.com/prev/
	public JQuery prev(String selector) {
		return new JQuery(js.call("prev",selector));
	}

	// https://api.jquery.com/prevAll/
	public JQuery prevAll() {
		return new JQuery(js.call("prevAll"));
	}

	// https://api.jquery.com/prevAll/
	public JQuery prevAll(String selector) {
		return new JQuery(js.call("prevAll",selector));
	}

	// https://api.jquery.com/prevUntil/
	public JQuery prevUntil() {
		return new JQuery(js.call("prevUntil"));
	}

	// https://api.jquery.com/prevUntil/
	public JQuery prevUntil(String selector) {
		return new JQuery(js.call("prevUntil",selector));
	}

	// https://api.jquery.com/prevUntil/
	public JQuery prevUntil(String selector, String filter) {
		return new JQuery(js.call("prevUntil", selector, filter));
	}

	// https://api.jquery.com/prevUntil/
	public JQuery prevUntil(Element element) {
		return new JQuery(js.call("prevUntil", element));
	}

	// https://api.jquery.com/prevUntil/
	public JQuery prevUntil(Element element, String filter) {
		return new JQuery(js.call("prevUntil", element, filter));
	}

	// https://api.jquery.com/prevUntil/
	public JQuery prevUntil(JQuery element) {
		return new JQuery(js.call("prevUntil", element.js));
	}

	// https://api.jquery.com/prevUntil/
	public JQuery prevUntil(JQuery element, String filter) {
		return new JQuery(js.call("prevUntil", element.js, filter));
	}

	// https://api.jquery.com/promise/
	public Promise promise() {
		return new Promise((JSObject)js.call("promise"));
	}

	// https://api.jquery.com/promise/
	public Promise promise(String type) {
		return new Promise((JSObject)js.call("promise", type));
	}

	// https://api.jquery.com/promise/
	public Promise promise(String type, Object target) {
		return new Promise((JSObject)js.call("promise", type, target));
	}

	// https://api.jquery.com/prop/
	public Object prop(String propertyName) {
		return js.call("prop", propertyName);
	}

	// https://api.jquery.com/prop/
	public JQuery prop(String propertyName, Object value) {
		return new JQuery(js.call("prop", propertyName, value));
	}

	// https://api.jquery.com/prop/
	public Object prop(Object properties) {
		return js.call("prop", properties);
	}

	// https://api.jquery.com/prop/
	public Object prop(String propertyName, IntObjectObjectFunction function) {
		return js.call("prop", propertyName, createFunction(function));
	}

	// https://api.jquery.com/queue/
	public Object[] queue() {
		return (Object[])js.call("queue");
	}

	// https://api.jquery.com/queue/
	public Object[] queue(String queueName) {
		return (Object[])js.call("queue", queueName);
	}

	// https://api.jquery.com/queue/
	public JQuery queue(Object[] newQueue) {
		js.call("queue", createArray(newQueue));
		return this;
	}

	// https://api.jquery.com/queue/
	public JQuery queue(String queueName, Object ... newQueue) {
		js.call("queue", queueName, createArray(newQueue));
		return this;
	}

	// https://api.jquery.com/queue/
	public JQuery queue(VoidFunctionFunction callback) {
		js.call("queue", createFunction(callback));
		return this;
	}

	// https://api.jquery.com/queue/
	public JQuery queue(String queueName, VoidFunctionFunction callback) {
		js.call("queue", queueName, createFunction(callback));
		return this;
	}

	// https://api.jquery.com/ready/
	public JQuery ready(VoidFunction function) {
		readyFunctions.add(function);
		return this;
	}

	// https://api.jquery.com/remove/
	public JQuery remove() {
		return new JQuery(js.call("remove"));
	}

	// https://api.jquery.com/remove/
	public JQuery remove(String selector) {
		return new JQuery(js.call("remove",selector));
	}

	// https://api.jquery.com/removeAttr/
	public JQuery removeAttr(String attributeName) {
		js.call("removeAttr", attributeName);
		return this;
	}

	// https://api.jquery.com/removeClass/#removeClass-className
	public JQuery removeClass() {
		js.call("removeClass");
		return this;
	}

	// https://api.jquery.com/removeClass/#removeClass-className
	public JQuery removeClass(String classname) {
		js.call("removeClass", classname);
		return this;
	}
	
	// https://api.jquery.com/removeClass/#removeClass-function
	public JQuery removeClass(IntStringStringFunction function) {
		js.call("removeClass", createFunction(function));
		return this;
	}
	
	// https://api.jquery.com/removeData/
	public JQuery removeData() {
		js.call("removeData");
		return this;
	}

	// https://api.jquery.com/removeData/
	public JQuery removeData(String name) {
		js.call("removeData", name);
		return this;
	}

	// https://api.jquery.com/removeData/
	public JQuery removeData(String ... names) {
		js.call("removeData", createArray(names));
		return this;
	}

	// https://api.jquery.com/removeProp/
	public JQuery removeProp(String propertyName) {
		js.call("removeProp", propertyName);
		return this;
	}
	
	// https://api.jquery.com/replaceAll/
	public JQuery replaceAll(String target) {
		return new JQuery(js.call("replaceAll", target));
	}

	// https://api.jquery.com/replaceAll/
	public JQuery replaceAll(JQuery target) {
		return new JQuery(js.call("replaceAll", target.js));
	}

	// https://api.jquery.com/replaceAll/
	public JQuery replaceAll(Element target) {
		return new JQuery(js.call("replaceAll", target));
	}

	// https://api.jquery.com/replaceAll/
	public JQuery replaceAll(Element ... target) {
		return new JQuery(js.call("replaceAll", createArray(target)));
	}

	// https://api.jquery.com/replaceWith/
	public JQuery replaceWith(String newContent) {
		return new JQuery(js.call("replaceWith",newContent));
	}

	// https://api.jquery.com/replaceWith/
	public JQuery replaceWith(Element newContent) {
		return new JQuery(js.call("replaceWith",newContent));
	}

	// https://api.jquery.com/replaceWith/
	public JQuery replaceWith(Element ... newContent) {
		return new JQuery(js.call("replaceWith",createArray(newContent)));
	}

	// https://api.jquery.com/replaceWith/
	public JQuery replaceWith(JQuery newContent) {
		return new JQuery(js.call("replaceWith",newContent.js));
	}

	// https://api.jquery.com/replaceWith/
	public JQuery replaceWith(JQueryFunction newContent) {
		return new JQuery(js.call("replaceWith"));
	}

	// https://api.jquery.com/resize/
	public JQuery resize() {
		js.call("resize");
		return this;
	}

	// https://api.jquery.com/resize/
	public JQuery resize(EventHandler handler) {
		return bind("resize",handler);
	}

	// https://api.jquery.com/resize/
	public JQuery resize(Object eventData, EventHandler handler) {
		return bind("resize",eventData,handler);
	}

	// https://api.jquery.com/scroll/
	public JQuery scroll() {
		js.call("scroll");
		return this;
	}

	// https://api.jquery.com/scroll/
	public JQuery scroll(EventHandler handler) {
		return bind("scroll",handler);
	}

	// https://api.jquery.com/scroll/
	public JQuery scroll(Object eventData, EventHandler handler) {
		return bind("scroll",eventData,handler);
	}

	// https://api.jquery.com/scrollLeft/
	public int scrollLeft() {
		return (int)js.call("scrollLeft");
	}

	// https://api.jquery.com/scrollLeft/
	public int scrollLeft(int value) {
		return (int)js.call("scrollLeft", value);
	}
	
	// https://api.jquery.com/scrollTop/
	public int scrollTop() {
		return (int)js.call("scrollTop");
	}

	// https://api.jquery.com/scrollTop/
	public int scrollTop(int value) {
		return (int)js.call("scrollTop", value);
	}

	// https://api.jquery.com/select/
	public JQuery select() {
		js.call("select");
		return this;
	}

	// https://api.jquery.com/select/
	public JQuery select(EventHandler handler) {
		return bind("select",handler);
	}

	// https://api.jquery.com/select/
	public JQuery select(Object eventData, EventHandler handler) {
		return bind("select",eventData,handler);
	}

	// https://api.jquery.com/show/
	public JQuery show() {
		js.call("show");
		return this;
	}

	// https://api.jquery.com/show/
	public JQuery show(String duration) {
		js.call("show", duration);
		return this;
	}

	// https://api.jquery.com/show/
	public JQuery show(String duration, VoidFunction complete) {
		js.call("show", duration, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/show/
	public JQuery show(AnimationOptions options) {
		js.call("show",options.toJSObject());
		return this;
	}

	// https://api.jquery.com/show/
	public JQuery show(String duration, String easing) {
		js.call("show", duration, easing);
		return this;
	}

	// https://api.jquery.com/show/
	public JQuery show(String duration, String easing, VoidFunction complete) {
		js.call("show", duration, easing, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/siblings/
	public JQuery siblings() {
		return new JQuery(js.call("siblings"));
	}
	
	// https://api.jquery.com/siblings/
	public JQuery siblings(String selector) {
		return new JQuery(js.call("siblings", selector));
	}
	
	// https://api.jquery.com/size/
	public int size() {
		return (int)js.call("size");

	}
	
	// https://api.jquery.com/slice/
	public JQuery slice(int start) {
		return new JQuery(js.call("slice", start));
	}

	// https://api.jquery.com/slice/
	public JQuery slice(int start, int end) {
		return new JQuery(js.call("slice", start, end));
	}

	// https://api.jquery.com/slideToggle/
	public JQuery slideToggle() {
		js.call("slideToggle");
		return this;
	}

	// https://api.jquery.com/slideToggle/
	public JQuery slideToggle(String duration) {
		js.call("slideToggle", duration);
		return this;
	}

	// https://api.jquery.com/slideToggle/
	public JQuery slideToggle(String duration, VoidFunction complete) {
		js.call("slideToggle", duration, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/slideToggle/
	public JQuery slideToggle(AnimationOptions options) {
		js.call("slideToggle", options.toJSObject());
		return this;
	}

	// https://api.jquery.com/slideToggle/
	public JQuery slideToggle(String duration, String easing) {
		js.call("slideToggle", duration, easing);
		return this;
	}

	// https://api.jquery.com/slideToggle/
	public JQuery slideToggle(String duration, String easing, VoidFunction complete) {
		js.call("slideToggle", duration, easing, createFunction(complete));
		return this;
	}

	// https://api.jquery.com/slideDown/
	public JQuery slideDown() {
		js.call("slideDown");
		return this;
	}

	// https://api.jquery.com/slideDown/
	public JQuery slideDown(String duration) {
		js.call("slideDown", duration);
		return this;
	}

	// https://api.jquery.com/slideDown/
	public JQuery slideDown(String duration, VoidFunction complete) {
		js.call("slideDown", duration, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/slideDown/
	public JQuery slideDown(AnimationOptions options) {
		js.call("slideDown", options.toJSObject());
		return this;
	}

	// https://api.jquery.com/slideDown/
	public JQuery slideDown(String duration, String easing) {
		js.call("slideDown", duration, easing);
		return this;
	}

	// https://api.jquery.com/slideDown/
	public JQuery slideDown(String duration, String easing, VoidFunction complete) {
		js.call("slideDown", duration, easing, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/slideUp/
	public JQuery slideUp() {
		js.call("slideUp");
		return this;
	}

	// https://api.jquery.com/slideUp/
	public JQuery slideUp(String duration) {
		js.call("slideUp", duration);
		return this;
	}

	// https://api.jquery.com/slideUp/
	public JQuery slideUp(String duration, VoidFunction complete) {
		js.call("slideUp", duration, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/slideUp/
	public JQuery slideUp(AnimationOptions options) {
		js.call("slideUp", options.toJSObject());
		return this;
	}

	// https://api.jquery.com/slideUp/
	public JQuery slideUp(String duration, String easing) {
		js.call("slideUp", duration, easing);
		return this;
	}

	// https://api.jquery.com/slideUp/
	public JQuery slideUp(String duration, String easing, VoidFunction complete) {
		js.call("slideUp", duration, easing);
		return this;
	}

	// https://api.jquery.com/stop/
	public JQuery stop() {
		js.call("stop");
		return this;
	}

	// https://api.jquery.com/stop/
	public JQuery stop(boolean clearQueue) {
		js.call("stop", clearQueue);
		return this;
	}

	// https://api.jquery.com/stop/
	public JQuery stop(boolean clearQueue, boolean jumpToEnd) {
		js.call("stop", clearQueue, jumpToEnd);
		return this;
	}

	// https://api.jquery.com/stop/
	public JQuery stop(String queue) {
		js.call("stop", queue);
		return this;
	}

	// https://api.jquery.com/stop/
	public JQuery stop(String queue, boolean clearQueue) {
		js.call("stop", queue, clearQueue);
		return this;
	}

	// https://api.jquery.com/stop/
	public JQuery stop(String queue, boolean clearQueue, boolean jumpToEnd) {
		js.call("stop", queue, clearQueue, jumpToEnd);
		return this;
	}

	// https://api.jquery.com/submit/
	public JQuery submit(EventHandler handler) {
		return bind("submit",handler);
	}

	// https://api.jquery.com/submit/
	public JQuery submit(Object eventData, EventHandler handler) {
		return bind("submit",eventData,handler);
	}

	// https://api.jquery.com/submit/
	public JQuery submit() {
		js.call("submit");
		return this;
	}

	// https://api.jquery.com/text/
	public String text() {
		return (String)js.call("text");
	}

	// https://api.jquery.com/text/
	public JQuery text(Object str) {
		return new JQuery(js.call("text",str.toString()));
	}

	// https://api.jquery.com/text/
	public JQuery text(IntStringStringFunction function) {
		return new JQuery(js.call("text",createFunction(function)));
	}

	// https://api.jquery.com/toArray/
	public Element[] toArray() {
		JSObject result = (JSObject)js.call("toArray");
		Element[] els = new Element[(int)result.getMember("length")];
		for (int i=0 ; i<els.length ; i++) {
			els[i] = (Element)result.getSlot(i);
		}
		return els;
	}
	
	// https://api.jquery.com/toggle/
	public JQuery toggle() {
		js.call("toggle");
		return this;
	}

	// https://api.jquery.com/toggle/
	public JQuery toggle(int duration) {
		js.call("toggle", duration);
		return this;
	}

	// https://api.jquery.com/toggle/
	public JQuery toggle(int duration, VoidFunction complete) {
		js.call("toggle", duration, createFunction(complete));
		return this;
	}
	
	// https://api.jquery.com/toggle/
	public JQuery toggle(AnimationOptions options) {
		js.call("toggle", options.toJSObject());
		return this;
	}

	// https://api.jquery.com/toggle/
	public JQuery toggle(String duration, String easing) {
		js.call("toggle", duration, easing);
		return this;
	}
	// https://api.jquery.com/toggle/
	public JQuery toggle(String duration, String easing, VoidFunction complete) {
		js.call("toggle", duration, easing, createFunction(complete));
		return this;
	}

	// https://api.jquery.com/toggle/
	public JQuery toggle(boolean display) {
		js.call("toggle", display);
		return this;
	}

	// https://api.jquery.com/toggleClass/
	public JQuery toggleClass(String className) {
		js.call("toggle", className);
		return this;
	}

	// https://api.jquery.com/toggleClass/
	public JQuery toggleClass(String className, boolean state) {
		js.call("toggle", className, state);
		return this;
	}

	// https://api.jquery.com/toggleClass/
	public JQuery toggleClass() {
		js.call("toggle");
		return this;
	}

	// https://api.jquery.com/toggleClass/
	public JQuery toggleClass(boolean state) {
		js.call("toggle", state);
		return this;
	}

	// https://api.jquery.com/toggleClass/
	public JQuery toggleClass(IntStringBooleanStringFunction function) {
		js.call("toggleClass", createFunction(function));
		return this;
	}


	// https://api.jquery.com/toggleClass/
	public JQuery toggleClass(IntStringBooleanStringFunction function, boolean state) {
		js.call("toggleClass", createFunction(function), state);
		return this;
	}

	// https://api.jquery.com/trigger/
	public JQuery trigger(String eventType) {
		js.call("trigger", eventType);
		return this;
	}
	
	// https://api.jquery.com/trigger/
	public JQuery trigger(String eventType, PlainObject extraParameters) {
		js.call("trigger", eventType, extraParameters.toJSObject());
		return this;
	}

	// https://api.jquery.com/trigger/
	public JQuery trigger(String eventType, Object ... extraParameters) {
		js.call("trigger", eventType, createArray(extraParameters));
		return this;
	}

	// https://api.jquery.com/trigger/
	public JQuery trigger(Event event) {
		js.call("trigger", event.js);
		return this;
	}
	
	// https://api.jquery.com/trigger/
	public JQuery trigger(Event event, PlainObject extraParameters) {
		js.call("trigger", event.js,extraParameters.toJSObject());
		return this;
	}

	// https://api.jquery.com/trigger/
	public JQuery trigger(Event event, Object ... extraParameters) {
		js.call("trigger", event.js, createArray(extraParameters));
		return this;
	}

	// https://api.jquery.com/triggerHandler/
	public JQuery triggerHandler(String eventType) {
		js.call("triggerHandler", eventType);
		return this;
	}

	// https://api.jquery.com/triggerHandler/
	public JQuery triggerHandler(String eventType, PlainObject extraParameters) {
		js.call("triggerHandler", eventType, extraParameters.toJSObject());
		return this;
	}

	// https://api.jquery.com/triggerHandler/
	public JQuery triggerHandler(String eventType, Object ... extraParameters) {
		js.call("triggerHandler", eventType, createArray(extraParameters));
		return this;
	}

	// https://api.jquery.com/triggerHandler/
	public JQuery triggerHandler(Event event) {
		js.call("triggerHandler");
		return this;
	}

	// https://api.jquery.com/triggerHandler/
	public JQuery triggerHandler(Event event, PlainObject extraParameters) {
		js.call("triggerHandler", event.js, extraParameters.toJSObject());
		return this;
	}

	// https://api.jquery.com/triggerHandler/
	public JQuery triggerHandler(Event event, Object ... extraParameters) {
		js.call("triggerHandler", event.js, createArray(extraParameters));
		return this;
	}

	// https://api.jquery.com/unbind/
	public JQuery unbind() {
		js.call("unbind");
		return this;
	}
	
	// https://api.jquery.com/unbind/
	public JQuery unbind(String eventType) {
		js.call("unbind",eventType);
		return this;
	}
	
	// https://api.jquery.com/unbind/
	public JQuery unbind(String eventType, EventHandler handler) {
		js.call("unbind",eventType, eventHandlerMap.get(handler.hashCode()));
		return this;
	}
	
	// https://api.jquery.com/unbind/
	public JQuery unbind(String eventType, boolean b) {
		js.call("unbind",eventType,false);
		return this;
	}
	
	// https://api.jquery.com/unbind/
	public JQuery unbind(Event event) {
		js.call("unbind", event.js);
		return this;
	}
	
	// https://api.jquery.com/undelegate/
	public JQuery undelegate() {
		js.call("undelegate");
		return this;
	}

	// https://api.jquery.com/undelegate/
	public JQuery undelegate(String selector, String eventType) {
		js.call("undelegate",selector, eventType);
		return this;
	}

	// https://api.jquery.com/undelegate/
	public JQuery undelegate(String selector, String eventType, EventHandler handler) {
		return new JQuery(js.call("undelegate", selector, eventType, createFunction(handler)));
	}

	// https://api.jquery.com/undelegate/
	public JQuery undelegate(String selector, Object events) {
		return new JQuery(js.call("undelegate", selector, events));
	}

	// https://api.jquery.com/undelegate/
	public JQuery undelegate(String namespace) {
		js.call("undelegate", namespace);
		return this;
	}

	// https://api.jquery.com/unwrap/
	public JQuery unwrap() {
		return new JQuery(js.call("unwrap"));
	}
	
	// https://api.jquery.com/val/
	public Object val() {
		return js.call("val");
	}
	
	// https://api.jquery.com/val/
	public JQuery val(Object value) {
		js.call("val",value);
		return this;
	}

	// https://api.jquery.com/val/
	public JQuery val(IntStringStringFunction function) {
		js.call("val", createFunction(function));
		return this;
	}

	// https://api.jquery.com/width/
	public double width() {
		return (double)(int)js.call("width");
	}
	
	// https://api.jquery.com/width/
	public JQuery width(double value) {
		js.call("width",value);
		return this;
	}

	// https://api.jquery.com/width/
	public JQuery width(IntIntStringFunction function) {
		return new JQuery(js.call("width", createFunction(function)));
	}

	// https://api.jquery.com/width/
	public JQuery width(IntIntIntFunction function) {
		return new JQuery(js.call("width", createFunction(function)));
	}

	// https://api.jquery.com/wrap/
	public JQuery wrap(String selectorOrHTML) {
		return new JQuery(js.call("wrap", selectorOrHTML));
	}
	
	// https://api.jquery.com/wrap/
	public JQuery wrap(JQuery wrappingElement) {
		return new JQuery(js.call("wrap", wrappingElement.js));
	}
	
	// https://api.jquery.com/wrap/
	public JQuery wrap(Element wrappingElement) {
		return new JQuery(js.call("wrap", wrappingElement));
	}

	// https://api.jquery.com/wrap/
	public JQuery wrap(IntStringFunction function) {
		return new JQuery(js.call("wrap", createFunction(function)));
	}

	// https://api.jquery.com/wrapAll/
	public JQuery wrapAll(String selectorOrHTML) {
		return new JQuery(js.call("wrapAll", selectorOrHTML));
	}
	
	// https://api.jquery.com/wrapAll/
	public JQuery wrapAll(JQuery wrappingElement) {
		return new JQuery(js.call("wrapAll", wrappingElement.js));
	}
	
	// https://api.jquery.com/wrapAll/
	public JQuery wrapAll(Element wrappingElement) {
		return new JQuery(js.call("wrapAll", wrappingElement));
	}
	
	// https://api.jquery.com/wrapAll/
	public JQuery wrapAll(IntStringFunction function) {
		return new JQuery(js.call("wrapAll", createFunction(function)));
	}
	
	// https://api.jquery.com/wrapInner/
	public JQuery wrapInner(String selectorOrHTML) {
		return new JQuery(js.call("wrapInner", selectorOrHTML));
	}
	
	// https://api.jquery.com/wrapInner/
	public JQuery wrapInner(JQuery wrappingElement) {
		return new JQuery(js.call("wrapInner", wrappingElement.js));
	}
	
	// https://api.jquery.com/wrapInner/
	public JQuery wrapInner(Element wrappingElement) {
		return new JQuery(js.call("wrapInner", wrappingElement));
	}
	
	// https://api.jquery.com/wrapInner/
	public JQuery wrapInner(IntStringFunction function) {
		return new JQuery(js.call("wrapInner", createFunction(function)));
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("JQuery[");
		if (this.length>0) {
			sb.append(this.get(0));
			for (int i=1 ; i<this.length ; i++) {
				sb.append(",");
				sb.append(this.get(i).toString());
			}
		}
		sb.append("]");
		return sb.toString();
	}
}