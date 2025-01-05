package com.tsp.util;

import com.tsp.gui.CityData.CityInfo;
import java.util.List;

public class TSPUtils {
    public static double[][] calculateDistanceMatrix(List<CityInfo> cities) {
        int n = cities.size();
        double[][] distances = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distances[i][j] = DistanceCalculator.calculateDistance(cities.get(i), cities.get(j));
            }
        }
        
        return distances;
    }
    
    public static int getCityIndex(List<CityInfo> cities, CityInfo city) {
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).equals(city)) {
                return i;
            }
        }
        return -1;
    }
    
    public static CityInfo getCityByIndex(List<CityInfo> cities, int index) {
        if (index >= 0 && index < cities.size()) {
            return cities.get(index);
        }
        return null;
    }
}
