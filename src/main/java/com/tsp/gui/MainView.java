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
    private AlgorithmPanel algorithmPanel;
    private CityAutocompleteTextField searchBar;
    private ListView<CityData.CityInfo> selectedCitiesList;
    private ObservableList<CityData.CityInfo> selectedCities;
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        setupWorldMap();
        setupAlgorithmSelector();
        setupSelectedCitiesList();
        setupSearchBar();
        setupAlgorithmPanel();
        
        
        // Create a VBox for the right side for the algorithm selector and cities list
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.getChildren().addAll(algorithmSelector, algorithmPanel);
        VBox.setVgrow(algorithmSelector, Priority.ALWAYS);
        
        //position elements
        root.setCenter(worldMap);
        root.setRight(rightPanel);
        root.setBottom(searchBar);
        
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
        
        primaryStage.setTitle("TSP Solver");
        primaryStage.setScene(scene);
        primaryStage.show();


        // cleanup csv data when application ends
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
        
        RadioButton nearestNeighborBtn = new RadioButton("Nearest Neighbor");
        RadioButton branchAndBoundBtn = new RadioButton("Branch and Bound");
        RadioButton antColonyBtn = new RadioButton("Ant Colony Optimization");
        
        nearestNeighborBtn.setToggleGroup(algorithmGroup);
        branchAndBoundBtn.setToggleGroup(algorithmGroup);
        antColonyBtn.setToggleGroup(algorithmGroup);
        
        nearestNeighborBtn.getStyleClass().add("algorithm-radio");
        branchAndBoundBtn.getStyleClass().add("algorithm-radio");
        antColonyBtn.getStyleClass().add("algorithm-radio");
        
        algorithmSelector.getChildren().addAll(
            title,
            nearestNeighborBtn,
            branchAndBoundBtn,
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
        
        algorithmSelector.getChildren().add(citiesContainer);
    }
    
    private void setupSearchBar() {
        searchBar = new CityAutocompleteTextField();
        searchBar.setOnCitySelected((name, lat, lon) -> {
            CityData.CityInfo newCity = new CityData.CityInfo(name, 0, lon, lat);
            if (!selectedCities.contains(newCity)) {
                selectedCities.add(newCity);
                worldMap.addCity(newCity);
                algorithmPanel.runAlgorithms(selectedCities);
            }
        });
    }

  private void setupAlgorithmPanel() {
      algorithmPanel = new AlgorithmPanel(worldMap);
  }
}
