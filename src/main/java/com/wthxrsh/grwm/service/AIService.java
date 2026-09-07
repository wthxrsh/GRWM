package com.wthxrsh.grwm.service;

import com.wthxrsh.grwm.service.WeatherService.WeatherData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AIService {

    public record AdviceResult(String summary, List<String> recommendations) {
    }

    public AdviceResult recommend(WeatherData weather, String stylePreference) {
        Set<String> items = new LinkedHashSet<>();
        String condition = weather.condition() == null ? "" : weather.condition();
        double temp = weather.temperature();

        if (temp < 0) {
            items.add("Thermal base layer");
            items.add("Heavy insulated winter coat");
            items.add("Wool beanie and gloves");
            items.add("Insulated waterproof boots");
        } else if (temp < 8) {
            items.add("Long-sleeve knit top");
            items.add("Wool or puffer coat");
            items.add("Scarf");
            items.add("Warm trousers or jeans");
        } else if (temp < 15) {
            items.add("Light sweater or hoodie");
            items.add("Trench or denim jacket");
            items.add("Joggers or jeans");
            items.add("Closed-toe sneakers");
        } else if (temp < 22) {
            items.add("Breathable t-shirt or blouse");
            items.add("Light overshirt or cardigan");
            items.add("Chinos or jeans");
            items.add("Low-top sneakers or loafers");
        } else if (temp < 28) {
            items.add("Short-sleeve tee or linen shirt");
            items.add("Shorts or light trousers");
            items.add("Breathable sneakers or sandals");
            items.add("Sunglasses");
        } else {
            items.add("Linen or cotton short-sleeve top");
            items.add("Light shorts");
            items.add("Open-toe sandals");
            items.add("Sunglasses and a cap");
        }

        String lower = condition.toLowerCase(Locale.ROOT);
        if (lower.contains("rain") || lower.contains("drizzle") || lower.contains("snow")) {
            items.add("Waterproof rain jacket");
            items.add("Umbrella");
            items.add("Water-resistant shoes");
        }
        if (lower.contains("snow") || lower.contains("freezing")) {
            items.add("Thermal inner layers");
            items.add("Waterproof snow boots");
        }
        if (lower.contains("fog") || lower.contains("overcast") || lower.contains("cloudy")) {
            items.add("Versatile mid-layer");
        }

        if (weather.windSpeed() >= 25) {
            items.add("Windbreaker or layered outer shell");
        }
        if (weather.humidity() >= 75 && temp > 18) {
            items.add("Moisture-wicking quick-dry fabric");
        }

        if (stylePreference != null && !stylePreference.isBlank()) {
            String style = stylePreference.toLowerCase(Locale.ROOT);
            if (style.contains("formal")) {
                items.add("Blazer or structured jacket");
                items.add("Pressed shirt and tailored trousers");
                items.add("Leather dress shoes");
            } else if (style.contains("sporty") || style.contains("athleisure")) {
                items.add("Performance hoodie or track jacket");
                items.add("Tech-fabric joggers");
                items.add("Running sneakers");
            } else if (style.contains("boho") || style.contains("bohemian")) {
                items.add("Flowy blouse or tunic");
                items.add("Wide-leg or embroidered trousers");
                items.add("Ankle boots or flat sandals");
            } else if (style.contains("minimalist") || style.contains("minimal")) {
                items.add("Clean neutral-toned basics");
                items.add("Simple unadorned outer layer");
                items.add("Minimal white sneakers");
            }
        }

        List<String> recommendations = new ArrayList<>(items);
        if (recommendations.size() > 8) {
            recommendations = recommendations.subList(0, 8);
        }

        String summary = String.format(
                "It's about %.0f°C (feels like %.0f°C) with %s%s. %s",
                temp,
                weather.feelsLike(),
                condition.toLowerCase(Locale.ROOT),
                weather.windSpeed() >= 25 ? " and strong winds" : "",
                recommendations.isEmpty()
                        ? "Keep it simple and comfortable."
                        : "This outfit keeps you comfortable in the current conditions.");

        return new AdviceResult(summary, recommendations);
    }
}