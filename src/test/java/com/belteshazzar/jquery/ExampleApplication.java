package com.belteshazzar.jquery;

import static com.belteshazzar.jquery.JQuery.$;

import com.belteshazzar.jquery.JQuery;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class ExampleApplication extends Application {

    @Override
    public void start(Stage stage) {
    	if (getParameters().getRaw().size()!=1) {
    		System.err.println("Please provide url to load as command line parameter");
    		Platform.exit();
    	}
    	JQuery.config.src = JQuery.DEFAULT_JQUERY_REMOTE;
    	JQuery.config.clearReadyFunctions = false;
    	JQuery.$(() -> {
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
        System.err.println("Loading: " + getParameters().getRaw().get(0));
       	browser.load(getParameters().getRaw().get(0));
    }
    
    public static void main(String[] args) {
     	launch(args);
    }
}

class Browser extends Region {
	 
    private WebView browser;
    private WebEngine webEngine;
     
    public Browser() {
        browser = new WebView();
        webEngine = browser.getEngine();
        JQuery.setEngine(webEngine);
        getChildren().add(browser);
    }
    
    public void load(String url) {
    	webEngine.load(url);
    }
 
    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override 
    protected double computePrefWidth(double height) {
        return 750;
    }
 
    @Override 
    protected double computePrefHeight(double width) {
        return 500;
    }
}

