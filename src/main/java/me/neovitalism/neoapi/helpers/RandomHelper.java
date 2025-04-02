package me.neovitalism.neoapi.helpers;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RandomHelper {
    private static final Random RANDOM = new Random();

    public static boolean oneIn(int amount) {
        if (amount < 1) return false;
        return RandomHelper.RANDOM.nextInt(amount) == 0;
    }

    public static boolean oneIn(double amount) {
        return (amount >= 1) ? (RandomHelper.RANDOM.nextDouble() < 1 / amount) : (RandomHelper.RANDOM.nextDouble() < amount);
    }

    public static boolean chanceOutOf(int chances, int amount) {
        return RandomHelper.oneIn(amount / chances);
    }

    public static boolean percentChance(double percent) {
        return (RandomHelper.RANDOM.nextDouble() * 100) <= percent;
    }

    public static int numberBetween(int min, int max) {
        return RandomHelper.RANDOM.nextInt(max - min + 1) + min;
    }

    public static <T> T getRandomValue(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(RandomHelper.RANDOM.nextInt(list.size()));
    }

    public static <T> Set<T> getUniqueValues(Collection<T> originals, @Nullable Collection<T> alreadyUsed, int amount) {
        Set<T> toReturn = new HashSet<>();
        List<T> copy = new ArrayList<>(originals);
        if (alreadyUsed != null) copy.removeAll(alreadyUsed);
        if (copy.isEmpty()) return toReturn;
        if (copy.size() <= amount) {
            toReturn.addAll(copy);
            return toReturn;
        }
        for (int i = 0; i < amount; i++) {
            if (copy.isEmpty()) break;
            T value = RandomHelper.getRandomValue(copy);
            copy.remove(value);
            toReturn.add(value);
        }
        return toReturn;
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
