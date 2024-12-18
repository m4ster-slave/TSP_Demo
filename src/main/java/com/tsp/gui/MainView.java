package com.tsp.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Random;

public class MainView extends Application {
    private WorldMap worldMap;
    private VBox algorithmSelector;
    private TextField searchBar;
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        setupWorldMap();
        setupAlgorithmSelector();
        setupSearchBar();
        
        root.setCenter(worldMap);
        root.setRight(algorithmSelector);
        root.setBottom(searchBar);
        
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        
        primaryStage.setTitle("TSP Solver");
        primaryStage.setScene(scene);
        primaryStage.show();


        addSampleCities();
    }
    
    private void setupWorldMap() {
        worldMap = new WorldMap();
        worldMap.getStyleClass().add("map-view");
    }
    
    private void setupAlgorithmSelector() {
        algorithmSelector = new VBox(10);
        algorithmSelector.setPadding(new Insets(10));
        algorithmSelector.setAlignment(Pos.TOP_LEFT);
        
        Label title = new Label("Algorithm Selection");
        title.getStyleClass().add("section-title");
        
        ToggleGroup algorithmGroup = new ToggleGroup();
        
        RadioButton bruteForceBtn = new RadioButton("Nearest Neighbor");
        RadioButton nearestNeighborBtn = new RadioButton("Branch and Bound");
        RadioButton antColonyBtn = new RadioButton("Ant Colony Optimization");
        
        bruteForceBtn.setToggleGroup(algorithmGroup);
        nearestNeighborBtn.setToggleGroup(algorithmGroup);
        antColonyBtn.setToggleGroup(algorithmGroup);
        
        bruteForceBtn.getStyleClass().add("algorithm-radio");
        nearestNeighborBtn.getStyleClass().add("algorithm-radio");
        antColonyBtn.getStyleClass().add("algorithm-radio");
        
        algorithmSelector.getChildren().addAll(
            title,
            bruteForceBtn,
            nearestNeighborBtn,
            antColonyBtn
        );
    }
    
    private void setupSearchBar() {
        searchBar = new TextField();
        searchBar.setPromptText("Search for cities...");
        searchBar.setPadding(new Insets(10));
        searchBar.getStyleClass().add("search-bar");
        
        searchBar.setOnAction(e -> searchCities(searchBar.getText()));
    }
    
    private void searchCities(String query) {
        // TODO: city search functionality
        System.out.println("Searching for: " + query);
    }

    private void addSampleCities() {
        // Add some sample cities to test the map
        worldMap.addCity(48.2082, 16.3719, "Vienna");      // Vienna, Austria
        worldMap.addCity(35.6762, 139.6503, "Tokyo");      // Tokyo, Japan
        worldMap.addCity(40.7128, -74.0060, "New York");   // New York, USA
        worldMap.addCity(-33.8688, 151.2093, "Sydney");    // Sydney, Australia
    }
}
