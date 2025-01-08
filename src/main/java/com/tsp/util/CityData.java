package com.tsp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class CityData {
  public static class CityInfo {
    private final String name;
    private final long population;
    private final double latitude;
    private final double longitude;
    private final String iso2Code;

    public CityInfo(String name, long population, double longitude, double latitude, String iso2Code) {
      this.name = name;
      this.population = population;
      this.latitude = latitude;
      this.longitude = longitude;
      this.iso2Code = iso2Code;
    }

    // for manually added cities
    public CityInfo(String name, double longitude, double latitude) {
      this(name, 0, longitude, latitude, "XX");
    }

    public String getName() {
      return name;
    }

    public long getPopulation() {
      return population;
    }

    public double getLatitude() {
      return latitude;
    }

    public double getLongitude() {
      return longitude;
    }

    public String getIso2Code() {
      return iso2Code;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private final List<CityInfo> cities = new ArrayList<>();
  private static final int MAX_SUGGESTIONS = 10;

  public void loadFromCSV(String resourcePath) {
    /*
     * - getClass().getResourceAsStream(resourcePath) opens the CSV file as an input
     * stream from the application's classpath resources
     * - InputStreamReader wraps the stream to convert bytes to characters
     * - CSVReader provides CSV parsing functionality for the filter
     */
    try (InputStream is = getClass().getResourceAsStream(resourcePath);
        InputStreamReader isr = new InputStreamReader(is);
        CSVReader reader = new CSVReader(isr)) {

      // skip header
      reader.readNext();

      String[] line;
      while ((line = reader.readNext()) != null) {
        try {
          // header:
          // "city","city_ascii","lat","lng","country","iso2","iso3","admin_name","capital","population","id"
          String cityName = line[0];
          long population = Long.parseLong(line[9].replace("\"", ""));
          float lon = Float.parseFloat(line[3]);
          float lat = Float.parseFloat(line[2]);
          String iso2 = line[5].trim();

          cities.add(new CityInfo(cityName, population, lon, lat, iso2));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
          System.err.println("Error parsing line: " + Arrays.toString(line));
        }
      }

      System.out.println("Read " + cities.size() + " lines from resource: " + resourcePath);

      // sort cities by population in descending order for fuzzy search
      cities.sort((c1, c2) -> Long.compare(c2.population, c1.population));

    } catch (IOException | CsvValidationException e) {
      e.printStackTrace();
    }
  }

  public List<CityInfo> searchCities(String query) {
    if (query == null || query.trim().isEmpty()) {
      // return top cities by population if no query
      return cities.stream()
          .limit(MAX_SUGGESTIONS)
          .collect(Collectors.toList());
    }

    String normalizedQuery = query.toLowerCase().trim();

    // stream the cities list
    return cities.stream()
        // create pairs of (city, similarity score) using fuzzy matching
        .map(city -> new AbstractMap.SimpleEntry<>(
            city,
            FuzzySearch.ratio(normalizedQuery, city.getName().toLowerCase())))
        // keep only matches with >70% similarity
        .filter(entry -> entry.getValue() > 70)
        // sort by score then by population if scores equal
        .sorted((e1, e2) -> {
          int scoreDiff = e2.getValue().compareTo(e1.getValue());
          if (scoreDiff != 0)
            return scoreDiff;
          return Long.compare(e2.getKey().getPopulation(), e1.getKey().getPopulation());
        })
        // take only top N suggestions
        .limit(MAX_SUGGESTIONS)
        // extract just the city objects
        .map(AbstractMap.SimpleEntry::getKey)
        // collect to list
        .collect(Collectors.toList());
  }
}
