package me.neovitalism.neoapi.helpers;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomHelper {
    private static final Random RANDOM = new Random();

    public static boolean oneIn(int amount) {
        if (amount < 1) return false;
        return RandomHelper.RANDOM.nextInt(amount) == 0;
    }

    public static boolean chanceOutOf(int chances, int amount) {
        return RandomHelper.oneIn(amount / chances);
    }

    public static int numberBetween(int min, int max) {
        return RandomHelper.RANDOM.nextInt(max - min + 1) + min;
    }

    public static <T> T getRandomValue(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(RandomHelper.RANDOM.nextInt(list.size()));
    }

    public static <T> T getWeightedResult(Map<T, Double> weightedObjects) {
        double sum = 0;
        for (double weight : weightedObjects.values()) sum += weight;
        double random = (RandomHelper.RANDOM.nextDouble() * sum);
        for (Map.Entry<T, Double> entry : weightedObjects.entrySet()) {
            if (random < entry.getValue()) return entry.getKey();
            random -= entry.getValue();
        }
        return null;
    }

}
