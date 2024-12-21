package com.tsp.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView extends Application {
    private WorldMap worldMap;
    private VBox algorithmSelector;
    private CityAutocompleteTextField searchBar;
    private ListView<String> selectedCitiesList;
    private ObservableList<String> selectedCities;
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        setupWorldMap();
        setupAlgorithmSelector();
        setupSelectedCitiesList();  // This now adds to algorithmSelector
        setupSearchBar();
        
        // Create a VBox for the right side containing the algorithm selector
        // (which now includes the cities list)
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.getChildren().add(algorithmSelector);
        VBox.setVgrow(algorithmSelector, Priority.ALWAYS);
        
        root.setCenter(worldMap);
        root.setRight(rightPanel);
        root.setBottom(searchBar);
        
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        
        primaryStage.setTitle("TSP Solver");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            searchBar.cleanup();
        });
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
    
    private void setupSelectedCitiesList() {
        VBox citiesContainer = new VBox(5);
        Label title = new Label("Selected Cities");
        title.getStyleClass().add("section-title");
        
        selectedCities = FXCollections.observableArrayList();
        selectedCitiesList = new ListView<>(selectedCities);
        selectedCitiesList.setPrefHeight(300);
        
        Button clearButton = new Button("Clear All");
        clearButton.setOnAction(e -> {
            selectedCities.clear();
            worldMap.clearCities();
        });
        
        citiesContainer.getChildren().addAll(title, selectedCitiesList, clearButton);
        VBox.setVgrow(selectedCitiesList, Priority.ALWAYS);
        
        // Replace the algorithmSelector reference with the combined container
        algorithmSelector.getChildren().add(citiesContainer);
    }
    
    private void setupSearchBar() {
        searchBar = new CityAutocompleteTextField();
        searchBar.setOnCitySelected((name, lat, lon) -> {
            if (!selectedCities.contains(name)) {
                selectedCities.add(name);
                worldMap.addCity(lon, lat, name);
            }
        });
    }
}
