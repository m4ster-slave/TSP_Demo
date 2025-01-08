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

    // Konstruktor für CSV-geladene Städte mit ISO2-Code
    public CityInfo(String name, long population, double longitude, double latitude, String iso2Code) {
      this.name = name;
      this.population = population;
      this.latitude = latitude;
      this.longitude = longitude;
      this.iso2Code = iso2Code;
    }

    // Konstruktor für manuell hinzugefügte Städte
    public CityInfo(String name, long population, double longitude, double latitude) {
      this(name, population, longitude, latitude, "XX"); // Default ISO2 Code
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
    try (InputStream is = getClass().getResourceAsStream(resourcePath);
        InputStreamReader isr = new InputStreamReader(is);
        CSVReader reader = new CSVReader(isr)) {

      // Skip header
      reader.readNext();

      String[] line;
      while ((line = reader.readNext()) != null) {
        try {
          // Header:
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

      // Sort cities by population in descending order for fuzzy search
      cities.sort((c1, c2) -> Long.compare(c2.population, c1.population));

    } catch (IOException | CsvValidationException e) {
      e.printStackTrace();
    }
  }

  public List<CityInfo> searchCities(String query) {
    if (query == null || query.trim().isEmpty()) {
      // Return top cities by population if no query
      return cities.stream()
          .limit(MAX_SUGGESTIONS)
          .collect(Collectors.toList());
    }

    String normalizedQuery = query.toLowerCase().trim();

    // Fuzzy search on city names
    return cities.stream()
        .map(city -> new AbstractMap.SimpleEntry<>(
            city,
            FuzzySearch.ratio(normalizedQuery, city.getName().toLowerCase())))
        .filter(entry -> entry.getValue() > 50) // Minimum similarity threshold
        .sorted((e1, e2) -> {
          // Primary sort by fuzzy match score
          int scoreDiff = e2.getValue().compareTo(e1.getValue());
          if (scoreDiff != 0)
            return scoreDiff;

          // Secondary sort by population
          return Long.compare(e2.getKey().getPopulation(), e1.getKey().getPopulation());
        })
        .limit(MAX_SUGGESTIONS)
        .map(AbstractMap.SimpleEntry::getKey)
        .collect(Collectors.toList());
  }
}
