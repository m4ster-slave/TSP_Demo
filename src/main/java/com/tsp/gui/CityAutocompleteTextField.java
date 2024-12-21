package com.tsp.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class CityAutocompleteTextField extends VBox {
    private final TextField textField = new TextField();
    private final ListView<CityData.CityInfo> suggestionList = new ListView<>();
    private final Popup popup = new Popup();
    private final CityData cityData = new CityData();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<javafx.concurrent.Task<List<CityData.CityInfo>>> currentTask = new AtomicReference<>();
    private CitySelectedCallback citySelectedCallback;

    public interface CitySelectedCallback {
        void onCitySelected(String name, double lat, double lon);
    }

    public void setOnCitySelected(CitySelectedCallback callback) {
        this.citySelectedCallback = callback;
    }

    public CityAutocompleteTextField() {
        cityData.loadFromCSV("/data/worldcities.csv");

        getChildren().add(textField);
        textField.setPromptText("Search for cities...");
        textField.setPadding(new Insets(10));
        textField.getStyleClass().add("search-bar");

        suggestionList.setMaxHeight(300);
        suggestionList.setPrefWidth(300);
        popup.getContent().add(suggestionList);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            javafx.concurrent.Task<List<CityData.CityInfo>> previousTask = currentTask.get();
            if (previousTask != null) {
                previousTask.cancel();
            }

            if (newValue.length() == 0) {
                Platform.runLater(popup::hide);
                return;
            }

            scheduledExecutor.schedule(() -> {
                if (!textField.getText().equals(newValue)) {
                    return;
                }

                javafx.concurrent.Task<List<CityData.CityInfo>> searchTask = new javafx.concurrent.Task<>() {
                    @Override
                    protected List<CityData.CityInfo> call() {
                        return cityData.searchCities(newValue);
                    }
                };

                searchTask.setOnSucceeded(event -> {
                    if (!searchTask.isCancelled()) {
                        List<CityData.CityInfo> suggestions = searchTask.getValue();
                        if (!suggestions.isEmpty()) {
                            Platform.runLater(() -> {
                                suggestionList.setItems(FXCollections.observableArrayList(suggestions));
                                if (!popup.isShowing()) {
                                    Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                                    popup.show(textField, bounds.getMinX(), bounds.getMaxY());
                                }
                            });
                        } else {
                            Platform.runLater(popup::hide);
                        }
                    }
                });

                currentTask.set(searchTask);
                executorService.submit(searchTask);
            }, 150, TimeUnit.MILLISECONDS);
        });

        suggestionList.setOnMouseClicked(event -> {
            CityData.CityInfo selectedItem = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                handleCitySelection(selectedItem);
            }
        });

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                suggestionList.getSelectionModel().selectFirst();
                suggestionList.requestFocus();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                popup.hide();
                event.consume();
            }
        });

        suggestionList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                CityData.CityInfo selectedItem = suggestionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    handleCitySelection(selectedItem);
                }
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                popup.hide();
                textField.requestFocus();
                event.consume();
            }
        });
    }

    private void handleCitySelection(CityData.CityInfo cityInfo) {
        textField.setText("");
        popup.hide();
        
        if (citySelectedCallback != null) {
            citySelectedCallback.onCitySelected(
                cityInfo.getName(),
                cityInfo.getLatitude(),
                cityInfo.getLongitude()
            );
        }
    }

    public void cleanup() {
        executorService.shutdownNow();
        scheduledExecutor.shutdownNow();
    }
}
