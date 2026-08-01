package com.caravan.api;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class NominatimService {

    private static final String BASE_URL =
        "https://nominatim.openstreetmap.org/search?format=json&q=";

    public static double[] getCoordinates(String address) {
        try {
            String encoded = address.trim().replace(" ", "+");
            String url     = BASE_URL + encoded;

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);

                request.setHeader("User-Agent", "CaravanProject/1.0");

                String response = client.execute(request, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity()));

                JSONArray results = new JSONArray(response);

                if (results.isEmpty()) {
                    System.out.println("No location found for: " + address);
                    return null;
                }

                JSONObject location = results.getJSONObject(0);
                double lat = location.getDouble("lat");
                double lon = location.getDouble("lon");

                System.out.printf("%s → [%.6f, %.6f]%n", address, lat, lon);
                return new double[]{lat, lon};
            }

        } catch (Exception e) {
            System.out.println("Nominatim error: " + e.getMessage());
            return null;
        }
    }
}