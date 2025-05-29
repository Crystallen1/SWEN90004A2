package models;

import core.Turtle;
import core.World;
import java.util.Random;

/**
 * InheritanceTurtle class extends the base Turtle class
 * This turtle implementation includes inheritance mechanics for the wealth simulation
 */
public class InheritanceTurtle extends Turtle {
    
    // Static random number generator shared across all instances
    protected static final Random random = new Random();
    
    /**
     * Constructor for InheritanceTurtle
     * @param metabolism - the turtle's metabolism rate
     * @param vision - the turtle's vision range
     * @param lifeExpectancy - the turtle's expected lifespan
     */
    InheritanceTurtle(int metabolism, int vision, int lifeExpectancy) {
        super(metabolism, vision, lifeExpectancy);
    }

    /**
     * Initialize the turtle's attributes when it's created or spawned
     * This method sets up random initial values for the turtle's characteristics
     * @param world - the world environment containing simulation parameters
     */
    @Override
    public void setInitialTurtleVars(World world) {
        // Randomly set life expectancy within the world's defined range
        this.lifeExpectancy = world.getMinLifeExpectancy() + 
                             random.nextInt(world.getMaxLifeExpectancy() 
                             - world.getMinLifeExpectancy() + 1);
        
        // Set random metabolism value (1 to max metabolism)
        this.metabolism = 1 + random.nextInt(world.getMaxMetabolism());
        
        // Set random vision range (1 to max vision)
        this.vision = 1 + random.nextInt(world.getMaxVision());
        
        // Set initial wealth: base metabolism + small random bonus (0-2)
        // If turtle has no wealth, give it initial wealth
        if (this.wealth <= 0) {
            this.wealth = this.metabolism + random.nextInt(3);
        } else {
            // If turtle already has wealth (inheritance), ensure minimum based on metabolism
            this.wealth = Math.max(this.metabolism + random.nextInt(3), this.wealth);
        }
        
        // Set random starting age (0 to life expectancy)
        this.age = random.nextInt(this.lifeExpectancy);
        
        // Place turtle at a random location in the world
        int[] location = world.getRandomPatchLocation();
        this.x = location[0];
        this.y = location[1];
    }
}
