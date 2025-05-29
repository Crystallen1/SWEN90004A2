package models;

import core.World;

public class InheritanceWorld extends World{
    public InheritanceWorld(int width, int height, int maxPeople, int maxVision,
                               int maxMetabolism, int minLifeExpectancy, int maxLifeExpectancy,
                               int percentBestLand, int grainGrowthInterval, int numGrainGrown) {
        super(width, height, maxPeople, maxVision, maxMetabolism, minLifeExpectancy, maxLifeExpectancy, percentBestLand, grainGrowthInterval, numGrainGrown);
    }

/**
     * Setup turtles using InheritanceTurtle instead of regular Turtle
     */
    @Override
    protected void setupTurtles() {
        for (int i = 0; i < maxPeople; i++) {
            // Set initial turtle variables
            int metabolism = 1 + random.nextInt(maxMetabolism);
            int vision = 1 + random.nextInt(maxVision);
            int lifeExpectancy = minLifeExpectancy + 
                               random.nextInt(maxLifeExpectancy - minLifeExpectancy + 1);
            
            InheritanceTurtle turtle = new InheritanceTurtle(metabolism, vision, lifeExpectancy);
            
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
}
