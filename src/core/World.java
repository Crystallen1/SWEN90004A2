package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import utils.CsvExporter;
import utils.GiniCalculator;

public class World {
    protected int width, height;
    protected Patch[][] map;
    protected List<Turtle> turtles;
    protected Random random;
    protected int maxPeople;
    protected int maxVision;
    protected int maxMetabolism;
    protected int minLifeExpectancy;
    protected int maxLifeExpectancy;
    protected int percentBestLand;
    protected int grainGrowthInterval;
    protected int numGrainGrown;
    protected int ticks;
    
    // Global constant, corresponding to NetLogo's max-grain
    private static final int MAX_GRAIN = 50;
    
    // CSV export
    protected CsvExporter csvExporter;

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
        this.csvExporter = new CsvExporter();
    }

    /**
     * Initialize CSV export file
     */
    public void initializeCsvExport(String filename) {
        csvExporter.initialize(filename);
    }

    /**
     * Close CSV export file
     */
    public void closeCsvExport() {
        csvExporter.close();
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
                map[x][y] = new Patch(0);
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
    protected void setupTurtles() {
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
        
        // Export to CSV after each step
        if (csvExporter.isInitialized()) {
            List<Integer> wealths = new ArrayList<>();
            for (Turtle turtle : turtles) {
                wealths.add(turtle.wealth);
            }
            csvExporter.exportWealthData(ticks, wealths);
        }
    }

    /**
     * Harvest grain following NetLogo's harvest logic
     */
    protected void harvest() {
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
    protected void growGrain() {
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
        double gini = GiniCalculator.compute(wealths);
        
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
                          " (" + String.format("%.1f", 100.0 * wealthClasses[0] 
                          / turtles.size()) + "%)");
        System.out.println("  Middle (" + (maxWealth/3 + 1) + "-" + (maxWealth*2/3) + "): " 
                          + wealthClasses[1]  
                          + " (" + String.format("%.1f", 100.0 * wealthClasses[1] 
                          / turtles.size()) + "%)");
        System.out.println("  Rich (≥" + (maxWealth*2/3 + 1) + "): " + wealthClasses[2] + 
                          " (" + String.format("%.1f", 100.0 * wealthClasses[2] 
                          / turtles.size()) + "%)");
        
        // Analyze wealth by vision levels
        // analyzeWealthByVision();
        
        System.out.println("==================");
    }

    /**
     * Analyze wealth distribution by vision levels
     */
    private void analyzeWealthByVision() {
        // Group turtles by vision level
        int[] visionCounts = new int[maxVision + 1];
        double[] visionWealthSum = new double[maxVision + 1];
        int[] visionMinWealth = new int[maxVision + 1];
        int[] visionMaxWealth = new int[maxVision + 1];
        
        // Initialize arrays
        for (int i = 0; i <= maxVision; i++) {
            visionMinWealth[i] = Integer.MAX_VALUE;
            visionMaxWealth[i] = Integer.MIN_VALUE;
        }
        
        // Collect data for each vision level
        for (Turtle turtle : turtles) {
            int vision = turtle.vision;
            if (vision >= 1 && vision <= maxVision) {
                visionCounts[vision]++;
                visionWealthSum[vision] += turtle.wealth;
                visionMinWealth[vision] = Math.min(visionMinWealth[vision], turtle.wealth);
                visionMaxWealth[vision] = Math.max(visionMaxWealth[vision], turtle.wealth);
            }
        }
        
        System.out.println("\nWealth Analysis by Vision Level:");
        System.out.println("Vision | Count | Avg Wealth | Min | Max");
        System.out.println("-------|-------|------------|-----|----");
        
        for (int vision = 1; vision <= maxVision; vision++) {
            if (visionCounts[vision] > 0) {
                double avgWealth = visionWealthSum[vision] / visionCounts[vision];
                System.out.printf("  %2d   |  %3d  |   %7.2f  | %3d | %3d%n", 
                    vision, visionCounts[vision], avgWealth, 
                    visionMinWealth[vision], visionMaxWealth[vision]);
            }
        }
        
        // Calculate correlation between vision and average wealth
        double correlation = calculateVisionWealthCorrelation();
        System.out.println("\nVision-Wealth Correlation: " + String.format("%.4f", correlation));
        if (correlation > 0.3) {
            System.out.println("Strong positive correlation: Higher vision leads to higher wealth");
        } else if (correlation > 0.1) {
            System.out.println("Moderate positive correlation: Higher vision somewhat leads to higher wealth");
        } else if (correlation > -0.1) {
            System.out.println("Weak correlation: Vision has little impact on wealth");
        } else {
            System.out.println("Negative correlation: Higher vision leads to lower wealth");
        }
    }

    /**
     * Calculate correlation coefficient between vision and wealth
     */
    private double calculateVisionWealthCorrelation() {
        if (turtles.isEmpty()) return 0.0;
        
        // Calculate means
        double visionSum = 0, wealthSum = 0;
        for (Turtle turtle : turtles) {
            visionSum += turtle.vision;
            wealthSum += turtle.wealth;
        }
        double visionMean = visionSum / turtles.size();
        double wealthMean = wealthSum / turtles.size();
        
        // Calculate correlation coefficient
        double numerator = 0, visionSumSq = 0, wealthSumSq = 0;
        for (Turtle turtle : turtles) {
            double visionDiff = turtle.vision - visionMean;
            double wealthDiff = turtle.wealth - wealthMean;
            numerator += visionDiff * wealthDiff;
            visionSumSq += visionDiff * visionDiff;
            wealthSumSq += wealthDiff * wealthDiff;
        }
        
        double denominator = Math.sqrt(visionSumSq * wealthSumSq);
        return denominator == 0 ? 0 : numerator / denominator;
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

    /**
     * Get patch at specified coordinates
     */
    public Patch getPatch(int x, int y) {
        return map[x][y];
    }

    /**
     * Get list of all turtles
     */
    public List<Turtle> getTurtles() {
        return turtles;
    }

    /**
     * Get max life expectancy
     */
    public int getMaxLifeExpectancy() {
        return maxLifeExpectancy;
    }
    public int getMinLifeExpectancy() {
        return minLifeExpectancy;
    }
    public int getMaxMetabolism() {
        return maxMetabolism;
    }
    public int getMaxVision() {
        return maxVision;
    }
}