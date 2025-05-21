package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    int width, height;
    Patch[][] map;
    List<Turtle> turtles;
    Random random;
    int maxPeople;
    int maxVision;
    int maxMetabolism;
    int minLifeExpectancy;
    int maxLifeExpectancy;
    int percentBestLand;
    int grainGrowthInterval;
    int numGrainGrown;
    int ticks;

    public World(){}

    public World(int width, int height, int maxPeople, int maxVision,
                 int maxMetabolism, int minLifeExpectancy, int maxLifeExpectancy,
                 int percentBestLand, int grainGrowthInterval, int numGrainGrown) {
        this.width = width;
        this.height = height;
        this.maxPeople = maxPeople;
        this.maxVision = maxVision;
        this.maxMetabolism = maxMetabolism;
        this.minLifeExpectancy = minLifeExpectancy;
        this.maxLifeExpectancy = maxLifeExpectancy;
        this.percentBestLand = percentBestLand;
        this.grainGrowthInterval = grainGrowthInterval;
        this.numGrainGrown = numGrainGrown;
        this.map = new Patch[width][height];
        this.turtles = new ArrayList<>();
        this.random = new Random();
        this.ticks = 0;
    }

    public void initialize(int seed) {
        random.setSeed(seed);
        initPatches();
        initTurtle();
    }

    void initPatches() {
        // Calculate the number of best land patches
        int bestLandCount = (width * height * percentBestLand) / 100;
        int[] bestLandIndices = new int[bestLandCount];
        
        // Randomly select positions for best land
        for (int i = 0; i < bestLandCount; i++) {
            bestLandIndices[i] = random.nextInt(width * height);
        }

        // Initialize all patches
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = x + y * width;
                boolean isBestLand = false;
                for (int bestIndex : bestLandIndices) {
                    if (index == bestIndex) {
                        isBestLand = true;
                        break;
                    }
                }
                // Best land has twice the maximum grain of regular land
                int maxGrain = isBestLand ? 100 : 50;
                map[x][y] = new Patch(x, y, maxGrain);
                if (isBestLand) {
                    map[x][y].setGrainHere(maxGrain);
                }
            }
        }
    }

    void initTurtle() {
        for (int i = 0; i < maxPeople; i++) {
            int metabolism = 1 + random.nextInt(maxMetabolism);
            int vision = 1 + random.nextInt(maxVision);
            int lifeExpectancy = minLifeExpectancy + random.nextInt(maxLifeExpectancy - minLifeExpectancy + 1);
            Turtle turtle = new Turtle(metabolism, vision, lifeExpectancy);
            turtle.x = random.nextInt(width);
            turtle.y = random.nextInt(height);
            turtles.add(turtle);
        }
    }

    public void step() {
        // First let all turtles decide their direction
        for (Turtle turtle : turtles) {
            turtle.decideDirection(this);
        }
        
        // Then let all turtles harvest
        for (Turtle turtle : turtles) {
            turtle.harvest(this);
        }
        
        // Update turtle states
        for (Turtle turtle : turtles) {
            turtle.ageAndConsume();
        }
        
        // Handle deaths and rebirths
        updateDeathsAndRebirths();
        
        // Grow grain at specified intervals
        if (ticks % grainGrowthInterval == 0) {
            growAllPatches();
        }
        
        ticks++;
    }

    void growAllPatches() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y].growGrain(numGrainGrown);
            }
        }
    }

    void updateDeathsAndRebirths() {
        List<Turtle> deadTurtles = new ArrayList<>();
        for (Turtle turtle : turtles) {
            if (turtle.isDead()) {
                deadTurtles.add(turtle);
            }
        }
        
        for (Turtle deadTurtle : deadTurtles) {
            turtles.remove(deadTurtle);
            deadTurtle.rebirth(this);
            turtles.add(deadTurtle);
        }
    }

    public void printStats() {
        int totalWealth = 0;
        int minWealth = Integer.MAX_VALUE;
        int maxWealth = Integer.MIN_VALUE;
        List<Integer> wealths = new ArrayList<>();
        
        for (Turtle turtle : turtles) {
            totalWealth += turtle.wealth;
            minWealth = Math.min(minWealth, turtle.wealth);
            maxWealth = Math.max(maxWealth, turtle.wealth);
            wealths.add(turtle.wealth);
        }
        
        double avgWealth = turtles.isEmpty() ? 0 : (double) totalWealth / turtles.size();
        double gini = stats.GiniCalculator.compute(wealths);
        
        // Calculate wealth class distribution
        int[] wealthClasses = new int[3]; // 0: poor, 1: middle, 2: rich
        for (Turtle turtle : turtles) {
            if (turtle.wealth <= maxWealth / 3) {
                wealthClasses[0]++;
            } else if (turtle.wealth <= (maxWealth * 2 / 3)) {
                wealthClasses[1]++;
            } else {
                wealthClasses[2]++;
            }
        }
        
        System.out.println("=== World Status ===");
        System.out.println("Number of Turtles: " + turtles.size());
        System.out.println("Average Wealth: " + String.format("%.2f", avgWealth));
        System.out.println("Minimum Wealth: " + minWealth);
        System.out.println("Maximum Wealth: " + maxWealth);
        System.out.println("Gini Coefficient: " + String.format("%.4f", gini));
        System.out.println("Wealth Distribution:");
        System.out.println("  Poor (≤" + String.format("%.0f", maxWealth/3.0) + "): " + wealthClasses[0]);
        System.out.println("  Middle (" + String.format("%.0f", maxWealth/3.0) + "-" + String.format("%.0f", maxWealth*2/3.0) + "): " + wealthClasses[1]);
        System.out.println("  Rich (≥" + String.format("%.0f", maxWealth*2/3.0) + "): " + wealthClasses[2]);
        System.out.println("==================");
    }
}