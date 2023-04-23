package me.neovitalism.neoapi.helpers;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomHelper {
    public static int randomIntBetween(int low, int high) {
        return new Random().nextInt((high - low) + 1) + low;
    }

    public static <T> T getWeightedResult(Map<T, Double> weightedObjects) {
        List<T> objects = weightedObjects.keySet().stream().toList();
        List<Double> weights = weightedObjects.values().stream().toList();
        double sum = 0;
        for (double weight : weights) {
            sum += weight;
        }
        double random = (Math.random() * sum);
        for(int i=0; i<weights.size(); i++) {
            if(random < weights.get(i)) {
                return objects.get(i);
            } else {
                random -= weights.get(i);
            }
        }
        return null;
    }
}
