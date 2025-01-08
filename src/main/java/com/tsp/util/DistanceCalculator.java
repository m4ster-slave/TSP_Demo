package com.tsp.util;

import com.tsp.util.CityData.CityInfo;
import java.util.List;

// Quelle: https://www.baeldung.com/java-find-distance-between-points
public class DistanceCalculator {
  // WGS-84 Earth parameters
  private static final double EARTH_RADIUS_A = 6378137.0; // Equatorial radius in meters
  private static final double EARTH_RADIUS_B = 6356752.314245; // Polar radius in meters
  private static final double FLATTENING = 1 / 298.257223563; // Earth's flattening

  public static double calculateDistance(CityInfo city1, CityInfo city2) {
    double lat1 = Math.toRadians(city1.getLatitude());
    double lon1 = Math.toRadians(city1.getLongitude());
    double lat2 = Math.toRadians(city2.getLatitude());
    double lon2 = Math.toRadians(city2.getLongitude());

    // Vincenty formula parameters
    double L = lon2 - lon1;
    double U1 = Math.atan((1 - FLATTENING) * Math.tan(lat1));
    double U2 = Math.atan((1 - FLATTENING) * Math.tan(lat2));
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
                  (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));

      if (sinSigma == 0)
        return 0;

      cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
      sigma = Math.atan2(sinSigma, cosSigma);
      double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
      cosSqAlpha = 1 - sinAlpha * sinAlpha;
      cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

      if (Double.isNaN(cos2SigmaM))
        cos2SigmaM = 0;

      double C = FLATTENING / 16 * cosSqAlpha * (4 + FLATTENING * (4 - 3 * cosSqAlpha));
      lambdaP = lambda;
      lambda = L + (1 - C) * FLATTENING * sinAlpha *
          (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));

    } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

    if (iterLimit == 0)
      return 0;

    double uSq = cosSqAlpha * (EARTH_RADIUS_A * EARTH_RADIUS_A - EARTH_RADIUS_B * EARTH_RADIUS_B)
        / (EARTH_RADIUS_B * EARTH_RADIUS_B);
    double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
    double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
    double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) -
        B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

    return (EARTH_RADIUS_B * A * (sigma - deltaSigma)) / 1000.0; // Convert to kilometers
  }

  public static double calculatePathLength(List<CityInfo> path) {
    if (path == null || path.size() < 2)
      return 0;

    double length = 0;
    for (int i = 0; i < path.size() - 1; i++) {
      length += calculateDistance(path.get(i), path.get(i + 1));
    }
    return length;
  }
}
