package com.caravan.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

public class RouteService {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("CARAVAN_ORS_API_KEY");
    private static final String BASE_URL =
        "https://api.openrouteservice.org/v2/directions/driving-car";

    public static double getDistanceKm(double[] origin, double[] destination) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpPost request = new HttpPost(BASE_URL);
            request.setHeader("Authorization", API_KEY);
            request.setHeader("Content-Type", "application/json");

            // Build request body
            JSONObject body = new JSONObject();
            JSONArray coordinates = new JSONArray();
            coordinates.put(new JSONArray()
                .put(origin[1])      
                .put(origin[0]));    
            coordinates.put(new JSONArray()
                .put(destination[1])
                .put(destination[0]));
            body.put("coordinates", coordinates);

            request.setEntity(new StringEntity(body.toString()));

            String response = client.execute(request, httpResponse ->
                EntityUtils.toString(httpResponse.getEntity()));

            JSONObject json     = new JSONObject(response);
            JSONArray  routes   = json.getJSONArray("routes");
            JSONObject summary  = routes.getJSONObject(0)
                                        .getJSONObject("summary");

            double distanceM  = summary.getDouble("distance");
            double distanceKm = distanceM / 1000.0;

            System.out.printf("Distance: %.2f km%n", distanceKm);
            return Math.round(distanceKm * 100.0) / 100.0;

        } catch (Exception e) {
            System.out.println("RouteService error: " + e.getMessage());
            return -1;
        }
    }

    public static double getDistanceFromAddresses(String pickup, String drop) {
        System.out.println("Fetching coordinates...");

        double[] origin      = NominatimService.getCoordinates(pickup);
        double[] destination = NominatimService.getCoordinates(drop);

        if (origin == null || destination == null) {
            System.out.println("Could not resolve coordinates.");
            return -1;
        }

        return getDistanceKm(origin, destination);
    }
}