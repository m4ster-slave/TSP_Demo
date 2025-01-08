package com.tsp.gui;

import com.tsp.util.CityData;
import com.tsp.util.CityData.CityInfo;

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
  private final TextField textField = new TextField(); // user input
  private final ListView<CityInfo> suggestionList = new ListView<>(); // list for suggestions
  private final Popup popup = new Popup();
  private final CityData cityData = new CityData(); // reads data from CSV file

  // executes search tasks as a new thread
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  // schedules delayed searches
  private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

  // tracks if a current search is ongoing
  private final AtomicReference<javafx.concurrent.Task<List<CityData.CityInfo>>> currentTask = new AtomicReference<>();

  // callback for when a city is selected
  private CitySelectedCallback citySelectedCallback;

  public interface CitySelectedCallback {
    void onCitySelected(String name, double lat, double lon); // interface for callback
  }

  public void setOnCitySelected(CitySelectedCallback callback) {
    this.citySelectedCallback = callback; // sets the callback for city selection
  }

  public CityAutocompleteTextField() {
    cityData.loadFromCSV("/data/worldcities.csv");

    getChildren().add(textField);
    textField.setPromptText("Search for cities...");
    textField.setPadding(new Insets(10));
    textField.getStyleClass().add("search-bar");

    suggestionList.setMaxHeight(300);
    suggestionList.setMaxWidth(300);
    popup.getContent().add(suggestionList);

    textField.textProperty().addListener((observable, oldValue, newValue) -> {

      // if current task hasnt finished cancel it
      javafx.concurrent.Task<List<CityInfo>> previousTask = currentTask.get();
      if (previousTask != null) {
        previousTask.cancel();
      }

      // hide the popup if the input is empty
      if (newValue.length() == 0) {
        Platform.runLater(popup::hide);
        return;
      }

      scheduledExecutor.schedule(() -> {
        // ensure the input hasn’t changed
        if (!textField.getText().equals(newValue)) {
          return;
        }

        // perform the search operation
        javafx.concurrent.Task<List<CityInfo>> searchTask = new javafx.concurrent.Task<>() {
          @Override
          protected List<CityInfo> call() {
            return cityData.searchCities(newValue);
          }
        };

        searchTask.setOnSucceeded(event -> {
          if (!searchTask.isCancelled()) { // ensure task wasnt cancelled
            // get the cityData list returned from the fuzzy search
            List<CityInfo> suggestions = searchTask.getValue();
            if (!suggestions.isEmpty()) {
              /*
               * Run the specified Runnable on the JavaFX Application Thread at
               * some unspecified time in the future. This method, which may be
               * called from any thread, will post the Runnable to an event queue
               * and then return immediately to the caller. The Runnables are
               * executed in the order they are posted. A runnable passed into
               * the runLater method will be executed before any Runnable passed
               * into a subsequent call to runLater. If this method is called
               * after the JavaFX runtime has been shutdown, the call will be
               * ignored: the Runnable will not be executed and no exception
               * will be thrown.
               */

              Platform.runLater(() -> {
                suggestionList.setItems(FXCollections.observableArrayList(suggestions));
                if (!popup.isShowing()) {
                  // calculate position with bounds to fit popup to textbox
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
      }, 100, TimeUnit.MILLISECONDS); // delay execution
    });

    // handle clicking on a city
    suggestionList.setOnMouseClicked(event -> {
      CityInfo selectedItem = suggestionList.getSelectionModel().getSelectedItem();
      if (selectedItem != null) {
        handleCitySelection(selectedItem);
      }
    });

    textField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.DOWN) { // navigate to the suggestion list
        suggestionList.getSelectionModel().selectFirst();
        suggestionList.requestFocus();
        event.consume(); // indicate event as fully handled
      } else if (event.getCode() == KeyCode.ESCAPE) { // hide popup
        popup.hide();
        event.consume();
      }
    });

    suggestionList.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        CityInfo selectedItem = suggestionList.getSelectionModel().getSelectedItem();
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

  // return a city from the search
  private void handleCitySelection(CityInfo cityInfo) {
    textField.setText("");
    popup.hide();

    if (citySelectedCallback != null) {
      citySelectedCallback.onCitySelected(
          cityInfo.getName(),
          cityInfo.getLatitude(),
          cityInfo.getLongitude());
    }
  }

  // shut down the executor service, and scheduled executer
  public void cleanup() {
    executorService.shutdownNow();
    scheduledExecutor.shutdownNow();
  }
}
