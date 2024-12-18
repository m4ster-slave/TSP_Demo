package com.tsp.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.geometry.Point2D;

public class WorldMap extends Pane {
    private static final double MAP_ASPECT_RATIO = 2.0; // Standard world map ratio (width/height)
    private ImageView mapImageView;
    private double baseWidth = 800; // Initial width
    private double baseHeight = baseWidth / MAP_ASPECT_RATIO;

    public WorldMap() {
        // Load the world map image
        try {
            Image image = new Image(getClass().getResource("/images/world-map.png").toString());
            mapImageView = new ImageView(image);
        } catch (NullPointerException e) {

            // Fallback to blank background
            mapImageView = new ImageView();
            System.err.println("Warning: Could not load world map image");
        }

        mapImageView.setPreserveRatio(true);
        mapImageView.setSmooth(true);

        getChildren().add(mapImageView);

        layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double width = newValue.getWidth();
            double height = newValue.getHeight();
            
            // Calculate the scaling factor to fit the map while preserving aspect ratio
            double scale = Math.min(width / baseWidth, height / baseHeight);
            
            mapImageView.setFitWidth(baseWidth * scale);
            mapImageView.setFitHeight(baseHeight * scale);
            
            // Center the map in the pane
            mapImageView.setTranslateX((width - mapImageView.getFitWidth()) / 2);
            mapImageView.setTranslateY((height - mapImageView.getFitHeight()) / 2);
            
            // Reposition any existing points on the map
            repositionAllPoints();
        });
    }

    public Point2D geoToPixel(double lat, double lon) {
        // Convert geographic coordinates to normalized coordinates (0-1)
        double x = (lon + 180) / 360.0;
        double y = (90 - lat) / 180.0;
        
        // Convert to pixel coordinates
        double mapWidth = mapImageView.getFitWidth();
        double mapHeight = mapImageView.getFitHeight();
        
        return new Point2D(
            mapImageView.getTranslateX() + (x * mapWidth),
            mapImageView.getTranslateY() + (y * mapHeight)
        );
    }

    public Circle addCity(double lat, double lon, String name) {
        Point2D point = geoToPixel(lat, lon);
        
        Circle cityPoint = new Circle(5, Color.RED);
        cityPoint.setStroke(Color.WHITE);
        cityPoint.setStrokeWidth(2);
        
        cityPoint.getProperties().put("latitude", lat);
        cityPoint.getProperties().put("longitude", lon);
        cityPoint.getProperties().put("cityName", name);
        
        // Add tooltip with city name
        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(name);
        javafx.scene.control.Tooltip.install(cityPoint, tooltip);
        
        cityPoint.setLayoutX(point.getX());
        cityPoint.setLayoutY(point.getY());
        
        getChildren().add(cityPoint);
        return cityPoint;
    }

    private void repositionAllPoints() {
        getChildren().stream()
            .filter(node -> node instanceof Circle)
            .map(node -> (Circle) node)
            .forEach(circle -> {
                Double lat = (Double) circle.getProperties().get("latitude");
                Double lon = (Double) circle.getProperties().get("longitude");
                if (lat != null && lon != null) {
                    Point2D point = geoToPixel(lat, lon);
                    circle.setLayoutX(point.getX());
                    circle.setLayoutY(point.getY());
                }
            });
    }
}
