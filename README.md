# jQuery4JavaFX

jQuery4JavaFX is an implementation of the jQuery API in Java for use with the JavaFX WebView component (javafx.scene.web.WebView). It allows you to interact with the HTML contents of the WebView using the standard jQuery API in your Java code.

# Example

The following example application is included in the test source folder. It loads http://en.wikipedia.org and runs a jQuery snippet when the page is loaded.

```java

import static com.belteshazzar.jquery.JQuery.$;
import com.belteshazzar.jquery.JQuery;

public class ExampleApplication extends Application {

    @Override
    public void start(Stage stage) {
       // config options
    	JQuery.config.src = JQuery.DEFAULT_JQUERY_REMOTE;
    	JQuery.config.clearReadyFunctions = false;
    	
    	// jQuery ready function
    	$(() -> {
    		System.err.println("page loaded");
    		System.err.println("<a> count = " + $("a").length);
    		$("a").mouseover((ev) -> {
    			$(ev.currentTarget()).css("border", "1px solid red");
    		});
    		$("a").mouseout((ev) -> {
    			$(ev.currentTarget()).css("border", "none");
    		});
    	});

        // create the scene
        stage.setTitle("Web View");
        Browser browser = new Browser();
        Scene scene = new Scene(browser,750,500, Color.web("#666970"));
        stage.setScene(scene);
        stage.show();
        browser.load("http://en.wikipedia.org");
    }      
}

class Browser extends Region {
	 
    private WebView browser;
    private WebEngine webEngine;
     
    public Browser() {
        browser = new WebView();
        webEngine = browser.getEngine();
        JQuery.setEngine(webEngine); // JQuery needs a reference to the WebEngine
        getChildren().add(browser);
    }
    
    public void load(String url) {
    	webEngine.load(url);
    }
}

```

# jQuery Script Location

By default jQuery4JavaFX will load jQuery from the JQuery.DEFAULT_JQUERY_LOCAL which is included in the jar.

When loading a remote web page WebView doesn't show any errors but fails to load this script so you need to provide a remove script location. JQuery.DEFAULT_JQUERY_REMOTE is set to "https://code.jquery.com/jquery-2.2.2.min.js" which seems to work find in most cases.

# Page Load (ready) Functions

Hopefully the only real place where this library differs from what you would normally expect when using jQuery is on page load. Because the jQuery code is defined in Java it can exist across multiple pages. You can define functions to call when a page loads using the normal $(function) format - which in Java is written using a lamba expression $(() -> {}). The configuration option JQuery.config.clearReadyFunctions controls if ready functions are removed once executed on page load. This example sets this to false (by default it is true) so that the ready script is executed after any page is loaded, such as when you visit a link. This example is pretty rudimentary - there's no 'loading' indicator or address bar - but link navigation will work.

# Use with Maven

Functionality of this package is contained in 
Java package `com.belteshazzar.jquery`.

To use the package, you need to use following Maven dependency:

```xml
<dependency>
  <groupId>com.belteshazzar</groupId>
  <artifactId>jQuery4JavaFX</artifactId>
  <version>0.0.3</version>
</dependency>
```

## Non-Maven Download

For non-Maven use cases, you download jars from [Central Maven repository](http://repo1.maven.org/maven2/com/belteshazzar/jQuery4JavaFX/0.0.3/jQuery4JavaFX-0.0.3.jar).
