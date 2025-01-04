package com.tsp.gui;

import java.util.List;

import com.tsp.gui.CityData.CityInfo;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class PathRenderer extends Group {
    private final WorldMap worldMap;
    private Color pathColor = Color.GREEN;
    
    public PathRenderer(WorldMap worldMap) {
        this.worldMap = worldMap;
    }
    
    public void setPathColor(Color color) {
        this.pathColor = color;
    }
    
    public void renderPath(List<CityInfo> path) {
        getChildren().clear();
        
        if (path == null || path.size() < 2) return;
        
        for (int i = 0; i < path.size(); i++) {
            CityInfo city1 = path.get(i);
            CityInfo city2 = path.get((i + 1) % path.size());
            
            Point2D point1 = worldMap.geoToPixel(city1.getLatitude(), city1.getLongitude());
            Point2D point2 = worldMap.geoToPixel(city2.getLatitude(), city2.getLongitude());
            
            Line line = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
            line.setStroke(pathColor);
            line.setStrokeWidth(2);
            line.setOpacity(0.6);
            
            getChildren().add(line);
        }
    }
    
    public void clear() {
        getChildren().clear();
    }
}
