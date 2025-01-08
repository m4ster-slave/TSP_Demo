module com.tsp {
  requires javafx.controls;
  requires javafx.fxml;
  requires com.opencsv;
  requires me.xdrop.fuzzywuzzy;

  exports com.tsp;
  exports com.tsp.gui;
  exports com.tsp.util;
  exports com.tsp.algorithm;
}
