package com.tsp.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class ContinentsData {
    public static class ContinentInfo {
        private final String country;
        private final String continent;
        private final String subRegion;
        private final String iso2;
        
        public ContinentInfo(String country, String continent, String subRegion, String iso2) {
            this.country = country;
            this.continent = continent;
            this.subRegion = subRegion;
            this.iso2 = iso2;
        }
        
        public String getCountry() { return country; }
        public String getContinent() { return continent; }
        public String getSubRegion() { return subRegion; }
        public String getIso2() { return iso2; }
        
        @Override
        public String toString() {
            return String.format("%s (%s, %s)", country, continent, subRegion);
        }
    }

    private final Map<String, ContinentInfo> countryToContinentMap = new HashMap<>();

    public void loadFromCSV(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath);
             InputStreamReader isr = new InputStreamReader(is);
             CSVReader reader = new CSVReader(isr)) {
            
            // Skip header
            reader.readNext();

            String[] line;
            while ((line = reader.readNext()) != null) {
                try {
                    // Header: name,alpha-2,alpha-3,country-code,iso_3166-2,region,sub-region,intermediate-region,region-code,sub-region-code,intermediate-region-code
                    String country = line[0].trim();
                    String iso2 = line[1].trim();
                    String continent = line[5].trim(); // region in CSV
                    String subRegion = line[6].trim();

                    countryToContinentMap.put(iso2, new ContinentInfo(country, continent, subRegion, iso2));
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing line: " + String.join(",", line));
                }
            }

            System.out.println("Loaded " + countryToContinentMap.size() + " countries from " + resourcePath);

        } catch (IOException | CsvValidationException e) {
            System.err.println("Error loading continents data from " + resourcePath);
            e.printStackTrace();
        }
    }

    public ContinentInfo getContinentInfo(String iso2Code) {
        if (iso2Code == null) {
            return null;
        }
        return countryToContinentMap.get(iso2Code.trim().toUpperCase());
    }
}
