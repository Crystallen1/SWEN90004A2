package models;

import core.Turtle;
import core.World;
import core.Patch;
import java.util.Random;
public class WealthSpreadingTurtle extends Turtle {
    private static final double WEALTH_SPREADING_RATE = 0.2; // 20% wealth spreading rate
    protected static final Random random = new Random();
    private int wealthSpreadingCounter = 0; // Counter for wealth spreading cycles

    public WealthSpreadingTurtle(int metabolism, int vision, int lifeExpectancy) {
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
        
        // Initialize wealth spreading counter to random value to avoid synchronization
        this.wealthSpreadingCounter = world.getMaxLifeExpectancy() / 8;
        
        // Place turtle at a random location in the world
        int[] location = world.getRandomPatchLocation();
        this.x = location[0];
        this.y = location[1];
    }
    
    /**
     * Move, eat grain, age and possibly die, with wealth spreading for rich turtles
     */
    @Override
    public void moveEatAgeDie(World world) {
        // Store previous position before moving
        int previousX = this.x;
        int previousY = this.y;
        
        // Increment wealth spreading counter
        wealthSpreadingCounter++;
        
        // Calculate wealth spreading interval (maxLifeExpectancy / 2)
        int spreadingInterval = Math.max(1, this.lifeExpectancy / 2);
        
        // Check if it's time to spread wealth and if turtle is rich
        int wealthToSpread = 0;
        if (wealthSpreadingCounter >= spreadingInterval && isRich(world)) {
            wealthToSpread = (int) (this.wealth * WEALTH_SPREADING_RATE);
            if (wealthToSpread > 0) {
                this.wealth -= wealthToSpread;
                wealthSpreadingCounter = 0; // Reset counter after spreading
            }
        }
        
        // Call parent class movement logic
        super.moveEatAgeDie(world);
        
        // After moving, leave wealth at previous position if there was wealth to spread
        if (wealthToSpread > 0) {
            Patch previousPatch = world.getPatch(previousX, previousY);
            previousPatch.addSpreadWealth(wealthToSpread);
        }
    }
    
    /**
     * Determine if this turtle is rich (wealth > 2/3 of max wealth)
     */
    private boolean isRich(World world) {
        if (world.getTurtles().isEmpty()) return false;
        
        int maxWealth = world.getTurtles().stream().mapToInt(t -> t.wealth).max().orElse(0);
        int richThreshold = (maxWealth * 2) / 3;
        return this.wealth > richThreshold;
    }
} 