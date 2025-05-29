package models;

public class ComparisonMain {
    public static void main(String[] args) {
        // Model parameter settings (same as original model)
        int width = 50;
        int height = 50;
        int maxPeople = 250;
        int maxVision = 5;
        int maxMetabolism = 15;
        int minLifeExpectancy = 1;
        int maxLifeExpectancy = 83;
        int percentBestLand = 10;
        int grainGrowthInterval = 1;
        int numGrainGrown = 4;
        
        // Create model comparator
        ModelComparator comparator = new ModelComparator(
            width, height, maxPeople, maxVision, maxMetabolism,
            minLifeExpectancy, maxLifeExpectancy, percentBestLand,
            grainGrowthInterval, numGrainGrown
        );
        
        // Initialize (use same random seed for fair comparison)
        int seed = 42;
        comparator.initialize(seed);
        
        // Run comparison (300 steps, report every 50 steps)
        int steps = 300;
        int reportInterval = 50;
        
        System.out.println("Model Parameters:");
        System.out.println("World Size: " + width + "x" + height);
        System.out.println("Population: " + maxPeople);
        System.out.println("Max Vision: " + maxVision);
        System.out.println("Max Metabolism: " + maxMetabolism);
        System.out.println("Life Expectancy Range: " + minLifeExpectancy + "-" + maxLifeExpectancy);
        System.out.println("Best Land Percentage: " + percentBestLand + "%");
        System.out.println("Grain Growth Interval: " + grainGrowthInterval + " rounds");
        System.out.println("Grain Growth Amount: " + numGrainGrown);
        System.out.println("Random Seed: " + seed);
        System.out.println();
        
        comparator.runComparison(steps, reportInterval);
    }
} 