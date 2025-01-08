package com.tsp.algorithm;

import com.tsp.util.CityData.CityInfo;
import java.util.List;

public interface TSPAlgorithm {
  List<CityInfo> findPath(List<CityInfo> cities);

  String getName();

  double getPathLength();

  long getExecutionTime();
}
