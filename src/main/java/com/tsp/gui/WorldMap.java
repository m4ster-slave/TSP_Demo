package com.tsp.gui;

import com.tsp.util.CityData.CityInfo;

import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class WorldMap extends Pane {
  private static final double MAP_ASPECT_RATIO = 2.0;
  private ImageView mapImageView;
  private double baseWidth = 800;
  private double baseHeight = baseWidth / MAP_ASPECT_RATIO;

  public WorldMap() {
    /*
     * add image and check if image has been found bc there is a problem with the
     * resource folder sometimes
     */
    try {
      Image image = new Image(getClass().getResource("/images/world-map.png").toString());
      mapImageView = new ImageView(image);
    } catch (NullPointerException e) {
      mapImageView = new ImageView();
      System.err.println("Warning: Could not load world map image");
    }

    mapImageView.setPreserveRatio(true);

    getChildren().add(mapImageView);

    // reposition all points if image is resized + on app startup
    layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
      double width = newValue.getWidth();
      double height = newValue.getHeight();

      double scale = Math.min(width / baseWidth, height / baseHeight);

      mapImageView.setFitWidth(baseWidth * scale);
      mapImageView.setFitHeight(baseHeight * scale);

      mapImageView.setTranslateX((width - mapImageView.getFitWidth()) / 2);
      mapImageView.setTranslateY((height - mapImageView.getFitHeight()) / 2);

      repositionAllPoints();
    });
  }

  // translate coordinates to pixels on the Pane
  public Point2D geoToPixel(double lat, double lon) {
    double x = (lon + 180) / 360.0;
    double y = (90 - lat) / 180.0;

    double mapWidth = mapImageView.getFitWidth();
    double mapHeight = mapImageView.getFitHeight();

    return new Point2D(
        mapImageView.getTranslateX() + (x * mapWidth),
        mapImageView.getTranslateY() + (y * mapHeight));
  }

  public void addCity(CityInfo city) {
    Point2D point = geoToPixel(city.getLatitude(), city.getLongitude());

    Circle cityPoint = new Circle(5, Color.web("#3498db"));
    cityPoint.setStroke(Color.WHITE);
    cityPoint.setStrokeWidth(1);

    cityPoint.setUserData(city);

    // install a tootip so it displays the city name when hovering over it
    Tooltip tooltip = new Tooltip(city.getName());
    Tooltip.install(cityPoint, tooltip);

    cityPoint.setLayoutX(point.getX());
    cityPoint.setLayoutY(point.getY());

    getChildren().add(cityPoint);
  }

  public void clearCities() {
    getChildren().removeIf(node -> node instanceof Circle);
  }

  /*
   * - filter out each Circle object
   * - cast them to a Circle
   * - iterate over them and reposition the point
   */
  private void repositionAllPoints() {
    getChildren().stream()
        .filter(node -> node instanceof Circle)
        .map(node -> (Circle) node)
        .forEach(circle -> {
          CityInfo city = (CityInfo) circle.getUserData();
          if (city != null) {
            Point2D point = geoToPixel(city.getLatitude(), city.getLongitude());
            circle.setLayoutX(point.getX());
            circle.setLayoutY(point.getY());
          }
        });
  }
}
