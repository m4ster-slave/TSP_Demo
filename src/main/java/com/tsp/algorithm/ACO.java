package com.tsp.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tsp.gui.CityData;

public class ACO implements TSPAlgorithm {
    private double pathLength;
    private long executionTime;
    
    private static final int NUM_ANTS = 30;
    private static final int MAX_ITERATIONS = 100;
    private static final double EVAPORATION_RATE = 0.15;
    private static final double ALPHA = 2.0;  // pheromone wichtigkeit
    private static final double BETA = 2.0;   // distanz wichtigkeit
    private static final double Q = 100.0;   // pheromone platzierfaktor

    @Override
    public List<CityData.CityInfo> findPath(List<CityData.CityInfo> cities) {
        long startTime = System.nanoTime();
        
        int numCities = cities.size();
        double[][] distances = calculateDistanceMatrix(cities);
        double[][] pheromones = initializePheromones(numCities);
        List<CityData.CityInfo> bestPath = null;
        double bestLength = Double.MAX_VALUE;
        
        Random random = new Random();
        
        // Main ACO loop
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            // Generiert Lösungen für alle Ameisen
            for (int ant = 0; ant < NUM_ANTS; ant++) {
                List<CityData.CityInfo> currentPath = constructAntPath(cities, distances, pheromones, random);
                double currentLength = calculatePathLength(currentPath);
                
                if (currentLength < bestLength) {
                    bestLength = currentLength;
                    bestPath = new ArrayList<>(currentPath);
                }
            }
            
            // Update pheromones
            evaporatePheromones(pheromones);
            updatePheromones(pheromones, cities, bestPath, bestLength);
        }
        
        this.pathLength = bestLength;
        this.executionTime = (System.nanoTime() - startTime) / 1_000_000;
        
        return bestPath;
    }

    private List<CityData.CityInfo> constructAntPath(List<CityData.CityInfo> cities, 
                                                    double[][] distances, 
                                                    double[][] pheromones,
                                                    Random random) {
        int numCities = cities.size();
        List<CityData.CityInfo> path = new ArrayList<>();
        boolean[] visited = new boolean[numCities];
        
        // Startpunkt -> Erste Stadt
        int currentCity = 0;
        path.add(cities.get(currentCity));
        visited[currentCity] = true;
        
        // Weg zu den verbleibenden Städten
        for (int i = 1; i < numCities; i++) {
            int nextCity = selectNextCity(currentCity, visited, pheromones, distances, random);
            path.add(cities.get(nextCity));
            visited[nextCity] = true;
            currentCity = nextCity;
        }
        
        // Ziel: -> Erste Stadt
        path.add(cities.get(0));
        
        return path;
    }

    private int selectNextCity(int currentCity, 
                             boolean[] visited, 
                             double[][] pheromones, 
                             double[][] distances,
                             Random random) {
        List<Integer> unvisitedCities = new ArrayList<>();
        List<Double> probabilities = new ArrayList<>();
        double total = 0.0;
        
        // Berechnen Sie die Wahrscheinlichkeiten für jede nicht besuchte Stadt
        int cityIndex = 0;
        for (boolean isVisited : visited) {
            if (!isVisited) {
                unvisitedCities.add(cityIndex);
                double probability = Math.pow(pheromones[currentCity][cityIndex], ALPHA) * 
                                   Math.pow(1.0 / distances[currentCity][cityIndex], BETA);
                probabilities.add(probability);
                total += probability;
            }
            cityIndex++;
        }
        
        // Wählt nächste Stadt zufällig
        double r = random.nextDouble() * total;
        double sum = 0.0;
        
        for (int i = 0; i < unvisitedCities.size(); i++) {
            sum += probabilities.get(i);
            if (sum >= r) {
                return unvisitedCities.get(i);
            }
        }
        
        // Fallback: Rückkehr zur ersten nicht besuchten Stadt
        if (!unvisitedCities.isEmpty()) {
            return unvisitedCities.get(0);
        }
        return -1;
    }

    private double[][] calculateDistanceMatrix(List<CityData.CityInfo> cities) {
        int numCities = cities.size();
        double[][] distances = new double[numCities][numCities];
        
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                distances[i][j] = calculateDistance(cities.get(i), cities.get(j));
            }
        }
        
        return distances;
    }

    private double[][] initializePheromones(int numCities) {
        double[][] pheromones = new double[numCities][numCities];
        double initialPheromone = 1.0;
        
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromones[i][j] = initialPheromone;
            }
        }
        
        return pheromones;
    }

    private void evaporatePheromones(double[][] pheromones) {
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones.length; j++) {
                pheromones[i][j] *= (1.0 - EVAPORATION_RATE);
            }
        }
    }

    private void updatePheromones(double[][] pheromones, 
                                List<CityData.CityInfo> cities,
                                List<CityData.CityInfo> bestPath,
                                double bestLength) {
        double deposit = Q / bestLength;
        
        for (int i = 0; i < bestPath.size() - 1; i++) {
            int cityIndex1 = cities.indexOf(bestPath.get(i));
            int cityIndex2 = cities.indexOf(bestPath.get(i + 1));
            pheromones[cityIndex1][cityIndex2] += deposit;
            pheromones[cityIndex2][cityIndex1] += deposit;
        }
    }

    @Override
    public String getName() {
        return "Ant Colony Optimization";
    }

    @Override
    public double getPathLength() {
        return this.pathLength;
    }

    @Override
    public long getExecutionTime() {
        return this.executionTime;
    }

    private double calculatePathLength(List<CityData.CityInfo> path) {
        double length = 0.0;
        

        for (int i = 0; i < path.size() - 1; i++) {
            length += calculateDistance(path.get(i), path.get(i + 1));
        }
        
        return length;
    }

    private double calculateDistance(CityData.CityInfo city1, CityData.CityInfo city2) {
        double lat1 = Math.toRadians(city1.getLatitude());
        double lon1 = Math.toRadians(city1.getLongitude());
        double lat2 = Math.toRadians(city2.getLatitude());
        double lon2 = Math.toRadians(city2.getLongitude());

        // WGS-84 Erdparameter
        double a = 6378137.0; // Äquatorradius in Metern
        double b = 6356752.314245; // Polarradius in Metern
        double f = 1 / 298.257223563; // Abplattung

        double L = lon2 - lon1; // Differenz der Längengrade
        double U1 = Math.atan((1 - f) * Math.tan(lat1));
        double U2 = Math.atan((1 - f) * Math.tan(lat2));
        double sinU1 = Math.sin(U1);
        double cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2);
        double cosU2 = Math.cos(U2);

        double lambda = L;
        double lambdaP;
        double iterLimit = 100;
        double cosSqAlpha;
        double sinSigma;
        double cos2SigmaM;
        double cosSigma;
        double sigma;
        double sinLambda;
        double cosLambda;

        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt(
                    (cosU2 * sinLambda) * (cosU2 * sinLambda) +
                    (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) *
                    (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            );

            if (sinSigma == 0) return 0;

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

            if (Double.isNaN(cos2SigmaM)) cos2SigmaM = 0;

            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha *
                    (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));

        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0) return 0;

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) -
                B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

        return (b * A * (sigma - deltaSigma)) / 1000.0; // Umwandlung in Kilometer
    }
}

