package model;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Turtle {
    int x, y;
    int age;
    int wealth;
    int metabolism;
    int vision;
    int lifeExpectancy;
    private Direction currentDirection;

    private static final Random random = new Random();

    public Turtle(int metabolism, int vision, int lifeExpectancy) {
        this.metabolism = metabolism;
        this.vision = vision;
        this.lifeExpectancy = lifeExpectancy;
        this.age = 0;
        this.wealth = 0; // Initial wealth is set in World
    }

    /**
     * Decide movement direction following NetLogo's turn-towards-grain logic
     */
    public void decideDirection(World world) {
        Direction bestDirection = Direction.NORTH; // Default direction
        int bestAmount = getGrainAhead(world, Direction.NORTH);
        
        // Check all 4 directions
        for (Direction dir : Direction.values()) {
            int grainInDirection = getGrainAhead(world, dir);
            if (grainInDirection > bestAmount) {
                bestAmount = grainInDirection;
                bestDirection = dir;
            }
        }
        
        currentDirection = bestDirection;
    }

    /**
     * Calculate total grain ahead in specified direction following NetLogo's grain-ahead logic
     */
    private int getGrainAhead(World world, Direction direction) {
        int total = 0;
        for (int distance = 1; distance <= vision; distance++) {
            Patch patch = world.getPatchAhead(x, y, direction, distance);
            total += patch.getGrainHere();
        }
        return total;
    }

    /**
     * Move, eat grain, age, and possibly die following NetLogo's move-eat-age-die logic
     */
    public void moveEatAgeDie(World world) {
        // Move forward 1 step
        if (currentDirection != null) {
            x = (x + currentDirection.getDx() + world.width) % world.width;
            y = (y + currentDirection.getDy() + world.height) % world.height;
        }
        
        // Consume grain according to metabolism
        wealth -= metabolism;
        
        // Age
        age++;
        
        // Check death conditions: no grain or exceeded life expectancy
        if (wealth < 0 || age >= lifeExpectancy) {
            setInitialTurtleVars(world);
        }
    }

    /**
     * Reset turtle variables following NetLogo's set-initial-turtle-vars logic
     */
    private void setInitialTurtleVars(World world) {
        // Randomly set new attributes
        this.lifeExpectancy = world.minLifeExpectancy + 
                             random.nextInt(world.maxLifeExpectancy - world.minLifeExpectancy + 1);
        this.metabolism = 1 + random.nextInt(world.maxMetabolism);
        this.vision = 1 + random.nextInt(world.maxVision);
        
        // Set wealth: metabolism + random 0-49
        this.wealth = this.metabolism + random.nextInt(50);
        
        // Set random age
        this.age = random.nextInt(this.lifeExpectancy);
        
        // Move to random location
        int[] location = world.getRandomPatchLocation();
        this.x = location[0];
        this.y = location[1];
    }

    // Keep original methods for compatibility
    public void move(World world) {
        if (currentDirection != null) {
            x = (x + currentDirection.getDx() + world.width) % world.width;
            y = (y + currentDirection.getDy() + world.height) % world.height;
        }
    }

    public void harvest(World world) {
        // This method is now handled in World class, keeping empty implementation for compatibility
    }

    public void ageAndConsume() {
        age++;
        wealth -= metabolism;
    }

    public boolean isDead() {
        return age >= lifeExpectancy || wealth <= 0;
    }

    public void rebirth(World world) {
        setInitialTurtleVars(world);
    }
}
