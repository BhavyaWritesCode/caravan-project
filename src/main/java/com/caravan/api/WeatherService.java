package com.caravan.api;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;

public class WeatherService {

    private static final String BASE_URL =
        "https://api.open-meteo.com/v1/forecast?" +
        "hourly=weathercode&forecast_days=1&";

    // ─── WEATHER CODES ───────────────────────────────────────────────
    // Open-Meteo WMO codes:
    // 0        = Clear sky
    // 1,2,3    = Partly cloudy
    // 45,48    = Fog
    // 51-67    = Drizzle / Rain
    // 71-77    = Snow
    // 80-82    = Rain showers
    // 95-99    = Thunderstorm

    public static boolean isSafeToDispatch(double[] coordinates) {
        try {
            String url = BASE_URL +
                "latitude="  + coordinates[0] +
                "&longitude=" + coordinates[1];

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                request.setHeader("User-Agent", "CaravanProject/1.0");

                String response = client.execute(request, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity()));

                JSONObject json    = new JSONObject(response);
                JSONObject hourly  = json.getJSONObject("hourly");
                int weatherCode    = hourly.getJSONArray("weathercode").getInt(0); 

                String condition   = getWeatherDescription(weatherCode);
                boolean safe       = weatherCode < 80; 

                System.out.println("🌤️  Weather: " + condition + " (code=" + weatherCode + ")");
                System.out.println(safe ? "Safe to dispatch." : "Unsafe weather — dispatch with caution!");
                return safe;
            }

        } catch (Exception e) {
            System.out.println("Weather check failed: " + e.getMessage() +
                               " — proceeding with dispatch.");
            return true; 
        }
    }

    public static boolean isSafeToDispatchFromAddress(String address) {
        double[] coords = NominatimService.getCoordinates(address);
        if (coords == null) {
            System.out.println("Could not get coordinates — skipping weather check.");
            return true;
        }
        return isSafeToDispatch(coords);
    }

    private static String getWeatherDescription(int code) {
        if (code == 0) return "Clear sky";
        if (code <= 3) return "Partly cloudy";
        if (code <= 48) return "Foggy";
        if (code <= 67) return "Rainy";
        if (code <= 77) return "Snowy";
        if (code <= 82) return "Rain showers";
        if (code <= 99) return "Thunderstorm";
        return "Unknown";
    }

    public static void printWeatherReport(String address) {
        System.out.println("\n========== WEATHER CHECK ==========");
        System.out.println("Location : " + address);
        double[] coords = NominatimService.getCoordinates(address);
        if (coords == null) {
            System.out.println("Could not fetch weather.");
            return;
        }
        isSafeToDispatch(coords);
        System.out.println("===================================\n");
    }
}