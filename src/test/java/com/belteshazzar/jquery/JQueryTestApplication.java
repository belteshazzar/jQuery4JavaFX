package com.belteshazzar.jquery;

import static com.belteshazzar.jquery.JQuery.$;

import com.belteshazzar.jquery.AnimationOptions;
import com.belteshazzar.jquery.JQuery;
import com.belteshazzar.jquery.PlainObject;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class JQueryTestApplication extends Application {

	private Scene scene;
    WebEngine webEngine;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Web View");
        
        WebView webView = new WebView();
        this.webEngine = webView.getEngine();
    	JQuery.setEngine(webEngine);

        Pane root = new Pane();
        this.scene = new Scene(root,750,500, Color.web("#666970"));
        root.getChildren().add(webView);
        stage.setScene(scene);
        stage.show();
       	webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>() {
                @Override
                public void changed(ObservableValue<? extends State> ov,
                    State oldState, State newState) {
                        if (newState == State.SUCCEEDED) {
                        	if (getParameters().getNamed().containsKey("junit")) {
	                        	JQuery.$(() -> {
	                        		JQueryTest.setApp(JQueryTestApplication.this);
	                        	});
                        	} else {
	                        	JQuery.$(() -> {
	                        		// setup interactive tests
	                        		$("#content").mouseover((ev) -> {
	                        			$("#content").addClass("outlined");
	                        		});
	                        		$("#content").mouseout((ev) -> {
	                        			$(ev.currentTarget()).removeClass("outlined");
	                        			$("#msg").text("mouseout = " + ev);
	                        		});
	                        		AnimationOptions slideUpOptions = new AnimationOptions();
	                        		slideUpOptions.duration = 900;
	                        		slideUpOptions.easing = "swing";
	                        		slideUpOptions.queue = "true";
	                        		slideUpOptions.start = (p) -> {
	                        			$("#msg").text("animation started: " + p);
	                        		};
	                        		slideUpOptions.done = (p,j) -> {
	                        			$("#msg").text("animation done: " + p + ", jumpedToEnd=" + j);
	                        		};
	                        		$("#slideUp").click((ev) -> {
	                        			$("#content").slideUp(slideUpOptions).dequeue();
	                        		});
	                        		$("#slideDown").click((ev) -> $("#content").slideDown());
	                        		$("#toggle").click((ev) -> $("#content").toggle(400,() -> $("#msg").text("toggle complete")));
	                        		
	                        		$("#animate").click((ev) -> {
	                        			$("#content").animate(new PlainObject()
	                        				.set("opacity", 0.25)
	                        				.set("left", "+=50")
	                        				.set("height", "toggle"), 5000, () -> {
	                        					$("#msg").text("animation complete");
	                        				});
	                        		});
	                        		$("#step").click((ev) -> {
	                        			$( "#content" ).animate(
                        					new PlainObject()
                        						.set("opacity",".5")
                        						.set("height", "50%"),
                        					new AnimationOptions()
	                        			  		.step((now,tween) -> {
	                        			  			$( "body" ).append( "<div>" + tween + "</div>" );
	                        			  		}));
	                        			});
	                        		$("#css").click((ev) -> {
	                        			$("#content").css("font-weight", "bold");
	                        			$("#msg").text( $( "#content" ).css("borderTopWidth", "borderRightWidth", "borderBottomWidth", "borderLeftWidth" ));
	                        		});
	                        	});
                        		
                        	}
                        } else {
                        }
                    }
                }
        );
       	webEngine.load(this.getClass().getResource("test.html").toExternalForm());
    }
    
    public static void main(String[] args) {
    	launch(args);
    }
 
}

