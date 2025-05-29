package models;

import java.util.ArrayList;
import java.util.List;
import core.World;
import core.Turtle;
import core.Patch;

public class WealthSpreadingWorld extends World {
    
    public WealthSpreadingWorld(int width, int height, int maxPeople, int maxVision,
                               int maxMetabolism, int minLifeExpectancy, int maxLifeExpectancy,
                               int percentBestLand, int grainGrowthInterval, int numGrainGrown) {
        super(width, height, maxPeople, maxVision, maxMetabolism, minLifeExpectancy, 
              maxLifeExpectancy, percentBestLand, grainGrowthInterval, numGrainGrown);
    }
    
    /**
     * Setup turtles using WealthSpreadingTurtle instead of regular Turtle
     */
    @Override
    protected void setupTurtles() {
        for (int i = 0; i < maxPeople; i++) {
            // Set initial turtle variables
            int metabolism = 1 + random.nextInt(maxMetabolism);
            int vision = 1 + random.nextInt(maxVision);
            int lifeExpectancy = minLifeExpectancy + 
                               random.nextInt(maxLifeExpectancy - minLifeExpectancy + 1);
            
            WealthSpreadingTurtle turtle = new WealthSpreadingTurtle(metabolism, vision, lifeExpectancy);
            
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
     * Harvest grain, including regular grain and spread wealth
     */
    @Override
    protected void harvest() {
        // First let all turtles harvest regular grain
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
            
            // Harvest spread wealth (each turtle can get some)
            int spreadWealth = patch.getSpreadWealth();
            if (spreadWealth > 0 && turtlesOnPatch > 0) {
                int wealthPerTurtle = spreadWealth / turtlesOnPatch;
                turtle.wealth += wealthPerTurtle;
            }
        }
        
        // Then set grain and spread wealth to 0 on patches with turtles
        for (Turtle turtle : turtles) {
            Patch patch = map[turtle.x][turtle.y];
            patch.setGrainHere(0);
            patch.harvestSpreadWealth(); // Clear spread wealth
        }
    }
    
    @Override
    public void printStats() {
        System.out.println("=== Wealth Spreading Model Statistics ===");
        super.printStats();
        
        // Calculate total spread wealth
        int totalSpreadWealth = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                totalSpreadWealth += map[x][y].getSpreadWealth();
            }
        }
        System.out.println("Total spread wealth on patches: " + totalSpreadWealth);
    }
} 