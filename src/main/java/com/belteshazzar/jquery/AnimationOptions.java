package com.belteshazzar.jquery;

import com.belteshazzar.jquery.functions.IntTweenVoidFunction;
import com.belteshazzar.jquery.functions.PromiseBooleanVoidFunction;
import com.belteshazzar.jquery.functions.PromiseIntIntVoidFunction;
import com.belteshazzar.jquery.functions.PromiseVoidFunction;
import com.belteshazzar.jquery.functions.VoidFunction;

import netscape.javascript.JSObject;

// https://api.jquery.com/slideDown/
// https://api.jquery.com/slideUp/
public class AnimationOptions {
	public Integer duration;
	public String easing;
	public String queue;
	public PlainObject specialEasing;
	public IntTweenVoidFunction step;
	public PromiseIntIntVoidFunction progress;
	public VoidFunction complete;
	public PromiseVoidFunction start;
	public PromiseBooleanVoidFunction done;
	public PromiseBooleanVoidFunction fail;
	public PromiseBooleanVoidFunction always;
	
	public JSObject toJSObject() {
		JSObject js = JQuery.createObject();
		if (duration!=null) js.setMember("duration", duration);
		if (easing!=null) js.setMember("easing", easing);
		if (queue!=null) {
			if (queue.equalsIgnoreCase("true")) js.setMember("queue", true);
			else js.setMember("queue", queue);
		}
		if (specialEasing!=null) js.setMember("specialEasing", specialEasing.toJSObject());
		if (step!=null) js.setMember("step", JQuery.createFunction(step));
		if (progress!=null) js.setMember("progress", JQuery.createFunction(progress));
		if (complete!=null) js.setMember("complete", JQuery.createFunction(complete));
		if (start!=null) js.setMember("start", JQuery.createFunction(start));
		if (done!=null) js.setMember("done", JQuery.createFunction(done));
		if (fail!=null) js.setMember("fail", JQuery.createFunction(fail));
		if (always!=null) js.setMember("duration", JQuery.createFunction(always));
		return js;
	}

	public AnimationOptions duration(Integer duration) {
		this.duration = duration;
		return this;
	}
	public AnimationOptions easing(String easing) {
		this.easing = easing;
		return this;
	}
	public AnimationOptions queue(String queue) {
		this.queue = queue;
		return this;
	}
	public AnimationOptions step(IntTweenVoidFunction step) {
		this.step = step;
		return this;
	}
	public AnimationOptions done(PromiseBooleanVoidFunction done) {
		this.done = done;
		return this;
	}
	public AnimationOptions fail(PromiseBooleanVoidFunction fail) {
		this.fail = fail;
		return this;
	}
	public AnimationOptions always(PromiseBooleanVoidFunction always) {
		this.always = always;
		return this;
	}

}