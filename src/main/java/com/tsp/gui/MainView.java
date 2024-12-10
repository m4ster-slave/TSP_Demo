package com.tsp.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainView extends Application {
    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane(new Label("TSP Solver"));
        Scene scene = new Scene(root, 800, 600);
        
        // Load CSS
        scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        
        primaryStage.setTitle("TSP Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
