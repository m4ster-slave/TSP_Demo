package com.tsp.algorithm;

import com.tsp.gui.CityData.CityInfo;
import com.tsp.util.DistanceCalculator;
import com.tsp.util.TSPUtils;
import java.util.*;

public class BranchAndBound implements TSPAlgorithm {
    private double pathLength;
    private long executionTime;
    private static final double INFINITY = Double.MAX_VALUE;
    private static final long TIME_LIMIT_MS = 20000; // second timeout
    private long startTime;

    private static class Node implements Comparable<Node> {
        List<Integer> path;
        boolean[] visited;
        double cost;
        double bound;
        int level;

        Node(int numCities) {
            path = new ArrayList<>();
            visited = new boolean[numCities];
            cost = 0;
            bound = 0;
            level = 0;
        }

        Node(Node parent) {
            path = new ArrayList<>(parent.path);
            visited = Arrays.copyOf(parent.visited, parent.visited.length);
            cost = parent.cost;
            bound = parent.bound;
            level = parent.level;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.bound, other.bound);
        }
    }

    @Override
    public List<CityInfo> findPath(List<CityInfo> cities) {
        startTime = System.nanoTime();
        
        int n = cities.size();
        if (n < 2) {
            return new ArrayList<>(cities);
        }

        // Calculate distance matrix
        double[][] distances = TSPUtils.calculateDistanceMatrix(cities);

        // Initialize priority queue with root node
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Node root = new Node(n);
        root.path.add(0); // Start from first city
        root.visited[0] = true;
        root.bound = calculateBound(root, distances);

        pq.offer(root);
        Node bestNode = null;
        double bestCost = INFINITY;

        // Branch and bound main loop
        while (!pq.isEmpty() && (System.nanoTime() - startTime) / 1_000_000 < TIME_LIMIT_MS) {
            Node current = pq.poll();

            // Skip if bound is worse than best solution
            if (current.bound >= bestCost) {
                continue;
            }

            // If all cities are visited
            if (current.level == n - 1) {
                // Add cost to return to starting city
                double finalCost = current.cost + distances[current.path.get(n-1)][0];
                if (finalCost < bestCost) {
                    current.path.add(0); // Complete the cycle
                    bestCost = finalCost;
                    bestNode = current;
                }
                continue;
            }

            // Try all possible next cities
            for (int i = 0; i < n; i++) {
                if (!current.visited[i]) {
                    Node newNode = new Node(current);
                    newNode.path.add(i);
                    newNode.visited[i] = true;
                    newNode.level = current.level + 1;
                    newNode.cost = current.cost + distances[current.path.get(current.level)][i];
                    newNode.bound = calculateBound(newNode, distances);

                    if (newNode.bound < bestCost) {
                        pq.offer(newNode);
                    }
                }
            }
        }

        // Convert path indices back to cities
        List<CityInfo> finalPath = new ArrayList<>();
        if (bestNode != null) {
            for (int index : bestNode.path) {
                finalPath.add(cities.get(index));
            }
            this.pathLength = bestCost;
        } else {
            // Fallback to simple path if no solution found
            finalPath = new ArrayList<>(cities);
            finalPath.add(cities.get(0));
            this.pathLength = DistanceCalculator.calculatePathLength(finalPath);
        }

        this.executionTime = System.nanoTime() - startTime; 
        return finalPath;
    }

    private double calculateBound(Node node, double[][] distances) {
        int n = distances.length;
        double bound = node.cost;

        // For each unvisited city, add minimum cost edge
        for (int i = 0; i < n; i++) {
            if (!node.visited[i]) {
                double minEdge = INFINITY;
                for (int j = 0; j < n; j++) {
                    if (i != j && (!node.visited[j] || j == 0)) {
                        minEdge = Math.min(minEdge, distances[i][j]);
                    }
                }
                bound += minEdge;
            }
        }

        // Add minimum edge back to start city if not all cities are visited
        if (node.level < n - 1) {
            double minReturn = INFINITY;
            int lastCity = node.path.get(node.path.size() - 1);
            for (int i = 0; i < n; i++) {
                if (!node.visited[i]) {
                    minReturn = Math.min(minReturn, distances[i][0]);
                }
            }
            bound += minReturn;
        }

        return bound;
    }

    @Override
    public String getName() {
        return "Branch And Bound";
    }

    @Override
    public double getPathLength() {
        return pathLength;
    }

    @Override
    public long getExecutionTime() {
        return executionTime;
    }
}
