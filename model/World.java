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
    
    // Global constant, corresponding to NetLogo's max-grain
    private static final int MAX_GRAIN = 50;

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
        setupPatches();
        setupTurtles();
    }

    /**
     * Setup patches following NetLogo's setup-patches logic exactly
     */
    private void setupPatches() {
        // First initialize all patches with max-grain-here = 0
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y] = new Patch(x, y, 0);
            }
        }

        // Give some patches the highest grain amount - these are the "best land"
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (random.nextDouble() * 100.0 <= percentBestLand) {
                    map[x][y].setMaxGrain(MAX_GRAIN);
                    map[x][y].setGrainHere(MAX_GRAIN);
                }
            }
        }

        // First phase: repeat 5 times, reset best land grain then diffuse
        for (int i = 0; i < 5; i++) {
            // Reset best land grain to maximum value
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (map[x][y].getMaxGrain() != 0) {
                        map[x][y].setGrainHere(map[x][y].getMaxGrain());
                    }
                }
            }
            diffuseGrain(0.25);
        }

        // Second phase: diffuse 10 more times
        for (int i = 0; i < 10; i++) {
            diffuseGrain(0.25);
        }

        // Finalize patches: round grain amounts and set max-grain-here to current grain amount
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Patch patch = map[x][y];
                int grainAmount = (int) Math.floor(patch.getGrainHere());
                patch.setGrainHere(grainAmount);
                patch.setMaxGrain(grainAmount);
            }
        }
    }

    /**
     * Diffuse grain following NetLogo's diffuse logic
     */
    private void diffuseGrain(double rate) {
        double[][] newGrain = new double[width][height];
        
        // Calculate new grain distribution
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double currentGrain = map[x][y].getGrainHere();
                double keepAmount = currentGrain * (1 - rate);
                double shareAmount = currentGrain * rate / 4; // Share with 4 neighbors
                
                newGrain[x][y] += keepAmount;
                
                // Share with 4 neighbors (up, down, left, right)
                int[] dx = {0, 0, 1, -1};
                int[] dy = {1, -1, 0, 0};
                
                for (int i = 0; i < 4; i++) {
                    int nx = (x + dx[i] + width) % width;
                    int ny = (y + dy[i] + height) % height;
                    newGrain[nx][ny] += shareAmount;
                }
            }
        }
        
        // Update grain amounts
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y].setGrainHere((int) newGrain[x][y]);
            }
        }
    }

    /**
     * Setup turtles following NetLogo's setup-turtles logic
     */
    private void setupTurtles() {
        for (int i = 0; i < maxPeople; i++) {
            // Set initial turtle variables
            int metabolism = 1 + random.nextInt(maxMetabolism);
            int vision = 1 + random.nextInt(maxVision);
            int lifeExpectancy = minLifeExpectancy + 
                               random.nextInt(maxLifeExpectancy - minLifeExpectancy + 1);
            
            Turtle turtle = new Turtle(metabolism, vision, lifeExpectancy);
            
            // Randomly place on a patch
            turtle.x = random.nextInt(width);
            turtle.y = random.nextInt(height);
            
            // Set initial wealth: metabolism + random 0-49
            turtle.wealth = metabolism + random.nextInt(50);
            
            // Set random age
            turtle.age = random.nextInt(lifeExpectancy);
            
            turtles.add(turtle);
        }
    }

    /**
     * Main step function following NetLogo's go logic
     */
    public void step() {
        // 1. All turtles decide their direction
        for (Turtle turtle : turtles) {
            turtle.decideDirection(this);
        }
        
        // 2. Harvest grain (before moving)
        harvest();
        
        // 3. All turtles move, eat grain, age, and possibly die
        for (Turtle turtle : turtles) {
            turtle.moveEatAgeDie(this);
        }
        
        // 4. Grow grain at specified intervals
        if (ticks % grainGrowthInterval == 0) {
            growGrain();
        }
        
        ticks++;
    }

    /**
     * Harvest grain following NetLogo's harvest logic
     */
    private void harvest() {
        // First let all turtles harvest grain
        for (Turtle turtle : turtles) {
            Patch patch = map[turtle.x][turtle.y];
            
            // Count how many turtles are on this patch
            int turtlesOnPatch = 0;
            for (Turtle t : turtles) {
                if (t.x == turtle.x && t.y == turtle.y) {
                    turtlesOnPatch++;
                }
            }
            
            // Distribute grain equally
            if (turtlesOnPatch > 0) {
                int grainPerTurtle = patch.getGrainHere() / turtlesOnPatch;
                turtle.wealth += grainPerTurtle;
            }
        }
        
        // Then set grain to 0 on all patches with turtles
        for (Turtle turtle : turtles) {
            map[turtle.x][turtle.y].setGrainHere(0);
        }
    }

    /**
     * Grow grain following NetLogo's grow-grain logic
     */
    private void growGrain() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x][y].growGrain(numGrainGrown);
            }
        }
    }

    /**
     * Print statistics including wealth distribution and Gini coefficient
     */
    public void printStats() {
        if (turtles.isEmpty()) {
            System.out.println("=== World Status ===");
            System.out.println("Number of Turtles: 0");
            System.out.println("==================");
            return;
        }

        // Calculate wealth statistics
        List<Integer> wealths = new ArrayList<>();
        int totalWealth = 0;
        int minWealth = Integer.MAX_VALUE;
        int maxWealth = Integer.MIN_VALUE;
        
        for (Turtle turtle : turtles) {
            wealths.add(turtle.wealth);
            totalWealth += turtle.wealth;
            minWealth = Math.min(minWealth, turtle.wealth);
            maxWealth = Math.max(maxWealth, turtle.wealth);
        }
        
        double avgWealth = (double) totalWealth / turtles.size();
        double gini = stats.GiniCalculator.compute(wealths);
        
        // Calculate wealth class distribution (following NetLogo's recolor-turtles logic)
        int[] wealthClasses = new int[3]; // 0: poor (red), 1: middle (green), 2: rich (blue)
        
        for (Turtle turtle : turtles) {
            if (turtle.wealth <= maxWealth / 3) {
                wealthClasses[0]++; // poor
            } else if (turtle.wealth <= (maxWealth * 2 / 3)) {
                wealthClasses[1]++; // middle
            } else {
                wealthClasses[2]++; // rich
            }
        }
        
        System.out.println("=== World Status (Tick: " + ticks + ") ===");
        System.out.println("Number of Turtles: " + turtles.size());
        System.out.println("Average Wealth: " + String.format("%.2f", avgWealth));
        System.out.println("Minimum Wealth: " + minWealth);
        System.out.println("Maximum Wealth: " + maxWealth);
        System.out.println("Gini Coefficient: " + String.format("%.4f", gini));
        System.out.println("Wealth Distribution:");
        System.out.println("  Poor (≤" + (maxWealth/3) + "): " + wealthClasses[0] + 
                          " (" + String.format("%.1f", 100.0 * wealthClasses[0] / turtles.size()) + "%)");
        System.out.println("  Middle (" + (maxWealth/3 + 1) + "-" + (maxWealth*2/3) + "): " + wealthClasses[1] + 
                          " (" + String.format("%.1f", 100.0 * wealthClasses[1] / turtles.size()) + "%)");
        System.out.println("  Rich (≥" + (maxWealth*2/3 + 1) + "): " + wealthClasses[2] + 
                          " (" + String.format("%.1f", 100.0 * wealthClasses[2] / turtles.size()) + "%)");
        System.out.println("==================");
    }

    /**
     * Get patch ahead at specified distance in given direction
     */
    public Patch getPatchAhead(int x, int y, Direction direction, int distance) {
        int newX = (x + direction.getDx() * distance + width) % width;
        int newY = (y + direction.getDy() * distance + height) % height;
        return map[newX][newY];
    }

    /**
     * Get random patch location
     */
    public int[] getRandomPatchLocation() {
        return new int[]{random.nextInt(width), random.nextInt(height)};
    }
}