package com.belteshazzar.jquery;

import static com.belteshazzar.jquery.JQuery.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.html.HTMLDivElement;
import org.w3c.dom.html.HTMLElement;

import com.belteshazzar.jquery.AnimationOptions;
import com.belteshazzar.jquery.EventHandler;
import com.belteshazzar.jquery.JQuery;
import com.belteshazzar.jquery.PlainObject;

import javafx.application.Application;
import javafx.application.Platform;
import netscape.javascript.JSObject;

public class JQueryTest {

	private static CountDownLatch started;
	private static JQueryTestApplication app;
	
	public static void setApp(JQueryTestApplication app) {
		JQueryTest.app = app;
		started.countDown();
	}
	
    @BeforeClass
    public static void setup() throws InterruptedException {
    	Thread fxThread = new Thread("JavaFX Launch Thread") {
    		@Override
    		public void run() {
    			Application.launch(JQueryTestApplication.class, new String[] { "--junit=true" });
    		}
    	};
    	started = new CountDownLatch(1);
    	fxThread.setDaemon(true);
    	fxThread.start();
    	started.await();
    }
    
    @AfterClass
    public static void tearDown() {
    	Platform.exit();
    }
    
    @FunctionalInterface
    interface JQueryFunction {
    	JQuery apply(Document doc);
    }

    @FunctionalInterface
    interface DocumentFunction {
    	void apply(Document doc);
    }

    public void runInFxThread(final DocumentFunction fn) throws InterruptedException {
    	AssertionError[] errors = new AssertionError[1];
    	CountDownLatch latch = new CountDownLatch(1);
       	Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					fn.apply(app.webEngine.getDocument());
				} catch (AssertionError t) {
					errors[0] = t;
				}
				latch.countDown();
			}
		});
    	latch.await();
    	if (errors[0]!=null) throw errors[0];
    }
    
    public void compare(final JQueryFunction fn, String str) throws InterruptedException {
    	final Object[] os = new Object[1];
    	final JQuery[] jqs = new JQuery[1];
    	CountDownLatch latch = new CountDownLatch(2);
    	Exception ex = new Exception();
    	ex.fillInStackTrace();
    	Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					os[0] = app.webEngine.executeScript(str);
				} catch (Throwable t) {
					t.printStackTrace();
					System.err.println("Called by:");
					ex.printStackTrace();
				}
		    	latch.countDown();
			}
    		
    	});
    	Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					jqs[0] = fn.apply(app.webEngine.getDocument());
				} catch (Throwable t) {
					t.printStackTrace();
				}
				latch.countDown();
			}
		});
    	latch.await();
    	
    	JQuery java = jqs[0];
    	Object o = os[0];
    	
    	if (java==null) {
    		if (o!=null) fail("Java == null, expected js == null, found: " + o);
    	} else {
    		if (o==null) fail("Java != null, expected js != null, found js == null");
    		if (!(o instanceof JSObject)) fail("js isn't instance of JSObject, js = " + o);
    		JSObject js = (JSObject)o;
    		int length = (int)js.getMember("length");
    		if (java.length != length) fail("expected length=" + length + ", found length="+ java.length);
    		
    		for (int i=0 ; i<length ; i++) {
    			Object ji = (Object)js.getSlot(i);
    			Object javai = java.get(i);
    			if (ji instanceof Element && javai instanceof Element) {
    				assertEquals("elements [" + i + "] differ", ji,javai); 			
    			} else if (ji instanceof JSObject && javai instanceof JSObject) {
    				assertEquals("js objects [" + i + "] differ", new PlainObject((JSObject)ji), new PlainObject((JSObject)javai));
    			} else {
    				assertEquals("objects [" + i + "] differ", ji,javai);
    			}
    		}
    	}
    }
    
	@Test
	public void test$Selector() throws InterruptedException {
		compare((doc) ->  {
			return $( "div#content a" );
		}, "$('div#content a')");
	}
	
	@Test
	public void test$StringHTMLElement() throws InterruptedException {
		compare((doc) -> {
    		HTMLElement content = (HTMLElement)doc.getElementById("content");
    		return $("a", content);
		},"$('a', document.getElementById('content'))");
	}

	@Test
	public void test$StringJQuery() throws InterruptedException {
		compare((doc) -> {
			return $("a", $("#content"));
		}, "$('a', $('#content'))");
	}

	@Test
	public void test$HTMLElement() throws InterruptedException {
		compare((doc) -> {
    		HTMLElement content = (HTMLElement)doc.getElementById("content");
			return $(content);
		},"$(document.getElementById('content'))");
	}

	@Test
	public void test$HTMLElementArray() throws InterruptedException {
   		compare((doc) -> {
    		HTMLElement content = (HTMLElement)doc.getElementById("content");
    		HTMLElement footer = (HTMLElement)doc.getElementById("footer");
   			return $(new HTMLElement[] { content, footer });
   		},"$([ document.getElementById('content'), document.getElementById('footer') ])");
   	 }

	@Test
	public void test$PlainObject() throws InterruptedException {
		compare((doc) -> {
			return $(new PlainObject().set("fred", 3));
		},"$({ fred: 3 })");
	}

	@Test
	public void test$JQuery() throws InterruptedException {
		compare((doc) -> {
			return $($("div#content a"));
		}, "$($('div#content a'))");
	}

	@Test
	public void test$() throws InterruptedException {
		compare((doc) -> {
			return $();
		}, "$()");
	}
	
	@Test
	public void test$HTML() throws InterruptedException {
		runInFxThread((doc) ->  {
			assertTrue($( "<div/>" ).get(0) instanceof HTMLDivElement);
		});
	}

	@Test
	public void test$HTMLObject() throws InterruptedException {
		runInFxThread((doc) ->  {
			HTMLDivElement el = (HTMLDivElement)$( "<div/>" , new PlainObject().set("id", "fred")).get(0);
			assertEquals("fred",el.getAttribute("id"));
		});
	}

	@Test
	public void test$VoidFunction() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch callbacks = new CountDownLatch(1);
			$(() -> {
				callbacks.countDown();
			});
			try {
				callbacks.await(100, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("call back wasn't called");
			}
		});
	}

	@Test
	public void testAddSelectorString() throws InterruptedException {
		runInFxThread((doc) -> {
			JQuery jq = $("#content").add("#footer");
			assertNotNull(jq);
		});
	}

	@Test
	public void testAddHTMLString() throws InterruptedException {
		runInFxThread((doc) -> {
			JQuery jq = $("#content").add("<div/>");
			assertNotNull(jq);
		});
	}

	@Test
	public void testAddHTMLElement() throws DOMException, InterruptedException {
		runInFxThread((doc) -> {
			JQuery jq = $("#content").add(doc.createElement("div"));
			assertNotNull(jq);
		});
	}

	@Test
	public void testAddJQuery() throws InterruptedException {
		runInFxThread((doc) -> {
			JQuery jq = $("#content").add($("<div/>"));
			assertNotNull(jq);
		});
	}

	@Test
	public void testAddStringHTMLElement() throws InterruptedException {
		runInFxThread((doc) -> {
			JQuery jq = $("#content").add($("<div/>",doc.getElementById("#footer")));
			assertNotNull(jq);
		});
	}

	@Test
	public void testAddBack() throws InterruptedException {
		runInFxThread((doc) -> {
			JQuery jq = $("#content").addBack();
			assertNotNull(jq);
		});
	}

	@Test
	public void testAddBackString() throws InterruptedException {
		runInFxThread((doc) -> {
			JQuery jq = $("#content").children().addBack("#footer");
			assertNotNull(jq);
		});
	}

	@Test
	public void testAddClassString() throws InterruptedException {
		runInFxThread((doc) -> {
			$("#content").addClass("outlined");
		});
	}

	@Test
	public void testAddClassIntStringStringFunction() throws InterruptedException {
		runInFxThread((doc) -> {
			$("#content").addClass((index,current) -> { return "greyed"; });
		});
	}

	@Test
	public void testAfterString() throws InterruptedException {
		runInFxThread((doc) -> {
			$("#content p").after("<p>Test</p>");
		});
	}

	@Test
	public void testAfterElement() throws DOMException, InterruptedException {
		runInFxThread((doc) -> {
			int ps = $("#content p").length;
			int h1 = $("#content h1").length;
			assertEquals("should be no h1's",0,h1);
			$("#content p").after( doc.createElement("h1"));
			assertEquals("after should have added h1 elements",ps,$("#content h1").length);
		});
	}

	@Test
	public void testAfterJQuery() throws DOMException, InterruptedException {
		runInFxThread((doc) -> {
			int ps = $("#content p").length;
			int h1 = $("#content h2").length;
			assertEquals("should be no h2's",0,h1);
			$("#content p").after( $("<h2>"));
			assertEquals("after should have added h2 elements",ps,$("#content h2").length);
		});
	}
	
	@Test
	public void testAnimatePlainObjectAnimationOptions() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch done = new CountDownLatch(1);
			$( "#content" ).animate(
					new PlainObject()
						.set("opacity",".5")
						.set("height", "50%"),
					new AnimationOptions()
				  		.step((now,tween) -> {
				  			$( "body" ).append( "<div>" + tween + "</div>" );
				  		})
				  		.always((p,b) -> {
				  			done.countDown();
				  		})
				);
			try {
				done.await(500, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("animation didn't complete");
			}
		});
	}
	@Test
	public void testAnimatePlainObjectIntStringVoidFunction() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch done = new CountDownLatch(1);
			$( "#content" ).animate(
					new PlainObject()
						.set("opacity",".5")
						.set("height", "50%"),
					300,
					"swing",
					() -> {
						done.countDown();
					}
				);
			try {
				done.await(500, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("animation didn't complete");
			}
		});
	}

	@Test
	public void testBlur() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").blur((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").blur(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").blur();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of blur events");
			}
		});
	}

	@Test
	public void testChange() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").change((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").change(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").change();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of change events");
			}
		});
	}

	@Test
	public void testClick() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").click((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").click(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").click();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of click events");
			}
		});
	}

	@Test
	public void testContextmenu() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").contextmenu((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").contextmenu(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").contextmenu();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of contextmenu events");
			}
		});
	}

	@Test
	public void testCssString() throws InterruptedException {
		runInFxThread((doc) -> {
			assertEquals("400",$("#content").css("font-weight"));
		});
	}

	@Test
	public void testCssStringArray() throws InterruptedException {
		runInFxThread((doc) -> {
			$("#content").css("font-weight","font-family");			
		});
	}

	@Test
	public void testCssStringString() throws InterruptedException {
		runInFxThread((doc) -> {
			$("#content").css("font-weight","bold");
		});
	}

	@Test
	public void testCssStringIntStringStringFunction() throws InterruptedException {
		runInFxThread((doc) -> {
			$("#content").css("font-weight",(indx,value) -> {
				return "bold";
			});
		});
	}

	@Test
	public void testCssPlainObject() throws InterruptedException {
		runInFxThread((doc) -> {
			assertEquals("400", $("#content").css("font-weight"));
			assertNotNull($("#content").css(new PlainObject().set("font-weight","bold")));
			assertEquals("bold",$("#content").css("font-weight"));
		});
	}

	@Test
	public void testDblclick() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").dblclick((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").dblclick(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").dblclick();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of dblclick events");
			}
		});
	}

	@Test
	public void testEach() throws InterruptedException {
		runInFxThread((doc) -> {
			int[] count = { 0 };
			$("a").each((i,el) -> {
				count[0]++;
			});
			assertEquals("increment for each a",8,count[0]);
		});
	}

	@Test
	public void testFocus() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").focus((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").focus(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").focus();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of focus events");
			}
		});
	}

	@Test
	public void testFocusin() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").focusin((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").focusin(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").focusin();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of focusin events");
			}
		});
	}

	@Test
	public void testFocusout() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").focusout((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").focusout(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").focusout();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of focusout events");
			}
		});
	}

	@Test
	public void testGet() throws InterruptedException {
		runInFxThread((doc) -> {
			Element[] els =  $("p").get();
			assertEquals("should be 6 p elements",6,els.length);
		});
	}
	
	@Test
	public void testGetI() throws InterruptedException {
		runInFxThread((doc) -> {
			Object el =  $("p").get(3);
			assertNotNull("should be 6 p elements",el);
			assertTrue("el should be an Element",el instanceof Element);
		});
	}

	@Test
	public void testHover() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").hover((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").hover(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").hover();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of hover events");
			}
		});
	}

	@Test
	public void testKeydown() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").keydown((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").keydown(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").keydown();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of keydown events");
			}
		});
	}

	@Test
	public void testKeypress() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").keypress((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").keypress(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").keypress();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of keypress events");
			}
		});
	}

	@Test
	public void testKeyup() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").keyup((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").keyup(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").keyup();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of keyup events");
			}
		});
	}

	@Test
	public void testMap() throws InterruptedException {
		runInFxThread((doc) -> {
			JQuery ps = $("p");
			System.err.println("testMap() ::: ps = " + ps);
			JQuery mapped = ps.map((indx,el) -> {
				return indx + " : " + el.getTextContent();
			});
			System.err.println("testMap() ::: mapped = " + mapped);
			assertEquals("mapped array should be same size as ps",ps.length,mapped.length);
		});
	}

	@Test
	public void testMousedown() throws DOMException, InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").mousedown((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").mousedown(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").mousedown();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of mousedown events");
			}
		});
	}

	@Test
	public void testMouseenter() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").mouseenter((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").mouseenter(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").mouseenter();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of mouseenter events");
			}
		});
	}

	@Test
	public void testMouseleave() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").mouseleave((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").mouseleave(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").mouseleave();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of mouseleave events");
			}
		});
	}

	@Test
	public void testMousemove() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").mousemove((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").mousemove(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").mousemove();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of mousemove events");
			}
		});
	}

	@Test
	public void testMouseout() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").mouseout((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").mouseout(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").mouseout();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of mouseout events");
			}
		});
	}

	@Test
	public void testMouseover() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").mouseover((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").mouseover(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").mouseover();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of mouseover events");
			}
		});
	}

	@Test
	public void testMouseup() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch events = new CountDownLatch(3);
			$("#content").mouseup((ev) -> {
				events.countDown();
			});
			String data = "my event data";
			$("#content").mouseup(data, (ev) -> {
				events.countDown();
				if (ev.data()==data) events.countDown();
			});
			$("#content").mouseup();
			try {
				events.await(300, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("didn't get correct number of mouseup events");
			}
		});
	}

	@Test
	public void testRemoveClass() throws InterruptedException {
		compare((doc) -> {
			return $("#content").removeClass();
		}, "$(\"#content\").removeClass()");
	}

	@Test
	public void testRemoveClassString() throws InterruptedException {
		compare((doc) -> {
			return $("#content").removeClass("outlined");
		}, "$(\"#content\").removeClass(\"outlined\")");
	}

	@Test
	public void testRemoveClassIntStringStringFunction() throws InterruptedException {
		compare((doc) -> {
			return $("#content").removeClass((i,cls) -> { return "greyed"; });
		}, "$(\"#content\").removeClass(function (i,cls) { return \"greyed\"; })");
	}

	@Test
	public void testToArray() throws InterruptedException {
		runInFxThread((doc) -> {
    		Element[] els = $("p").toArray();
    		assertEquals("# of paragraphs",6,els.length);
		});
	}

	@Test
	public void testUnbindStringEventHandler() throws InterruptedException {
		runInFxThread((doc) -> {
			int[] clicks = { 0 };
			EventHandler handler = (ev) -> {
				clicks[0]++;
			};
			$("#link2").bind("click", handler);
			$("#link2").click();
			assertEquals("should be one click event",1,clicks[0]);
			$("#link2").unbind("click",handler);
			$("#link2").click();
			assertEquals("should be one click event after unbind",1,clicks[0]);
		});
	}

	@Test
	public void testWidth() throws InterruptedException {
		runInFxThread((doc) -> {
			assertEquals(782,$("#content").width(),0.0);
		});
	}

	@Test
	public void testWidthDouble() throws InterruptedException {
		runInFxThread((doc) -> {
			double width = $("#content").width();
			$("#content").width(width*1.2);
			assertEquals(Math.round(width*1.2),$("#content").width(),0.0);
		});
	}
	
	@Test
	public void testFadeToStringDoubleVoidFunction() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch done = new CountDownLatch(1);
			$("#content").fadeTo("100", 1.0, () -> done.countDown());
			try {
				done.await(200, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("fadeTo done callback not called in time");
			}
		});
	}
	@Test
	public void testFadeToStringDoubleStringVoidFunction() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch done = new CountDownLatch(1);
			$("#content").fadeTo("100", 1.0, "swing", () -> done.countDown());
			try {
				done.await(200, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("fadeTo done callback not called in time");
			}
		});
	}

	@Test
	public void testAnimatePlainObjectIntVoidFunction() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch done = new CountDownLatch(1);
			$( "#content" ).animate(
					new PlainObject()
						.set("opacity",".5")
						.set("height", "50%"),
					300,
					() -> {
						done.countDown();
					}
				);
			try {
				done.await(500, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("animation didn't complete");
			}
		});
	}
	@Test
	public void testAnimatePlainObjectIntString() throws InterruptedException {
		runInFxThread((doc) -> {
			$( "#content" ).animate(
					new PlainObject()
						.set("opacity",".5")
						.set("height", "50%"),
					300,
					"swing"
				);
		});
	}

	@Test
	public void testDelegateStringStringObjEventHandler() throws InterruptedException {
		runInFxThread((doc) -> {
			CountDownLatch done = new CountDownLatch(1);
			$("#content").delegate("a", "click", "my data", (ev) -> {
				done.countDown();
			});
			Element el = doc.createElement("a");
			$("#content").add(el);
			$(el).click();
			try {
				done.await(500, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				fail("delegate didn't get event");
			}
		});
	}

}

