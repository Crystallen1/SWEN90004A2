package models;

import java.util.ArrayList;
import java.util.List;
import core.World;
import core.Turtle;

public class TaxRedistributionWorld extends World {
    private int taxInterval; // Tax interval (max_age/2 rounds)
    private static final double TAX_RATE = 0.2; // 20% tax rate
    
    public TaxRedistributionWorld(int width, int height, int maxPeople, int maxVision,
                                 int maxMetabolism, int minLifeExpectancy, int maxLifeExpectancy,
                                 int percentBestLand, int grainGrowthInterval, int numGrainGrown) {
        super(width, height, maxPeople, maxVision, maxMetabolism, minLifeExpectancy, 
              maxLifeExpectancy, percentBestLand, grainGrowthInterval, numGrainGrown);
        this.taxInterval = maxLifeExpectancy / 2; // Tax every max_age/2 rounds
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
    
    @Override
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
        
        // 5. Tax redistribution (every taxInterval rounds)
        if (ticks % taxInterval == 0 && ticks > 0) {
            redistributeWealth();
        }
        
        ticks++;
        
        // Export to CSV
        if (csvExporter.isInitialized()) {
            List<Integer> wealths = new ArrayList<>();
            for (Turtle turtle : turtles) {
                wealths.add(turtle.wealth);
            }
            csvExporter.exportWealthData(ticks, wealths);
        }
    }
    
    /**
     * Tax redistribution: collect tax from rich and distribute to poor
     */
    private void redistributeWealth() {
        if (turtles.isEmpty()) return;
        
        // Calculate wealth class boundaries
        int maxWealth = turtles.stream().mapToInt(t -> t.wealth).max().orElse(0);
        int poorThreshold = maxWealth / 3;
        int richThreshold = (maxWealth * 2) / 3;
        
        // Classify turtles
        List<Turtle> richTurtles = new ArrayList<>();
        List<Turtle> poorTurtles = new ArrayList<>();
        
        for (Turtle turtle : turtles) {
            if (turtle.wealth > richThreshold) {
                richTurtles.add(turtle);
            } else if (turtle.wealth <= poorThreshold) {
                poorTurtles.add(turtle);
            }
        }
        
        // If no rich or poor turtles, no redistribution
        if (richTurtles.isEmpty() || poorTurtles.isEmpty()) {
            return;
        }
        
        // Collect tax from rich turtles
        int totalTax = 0;
        for (Turtle richTurtle : richTurtles) {
            int tax = (int) (richTurtle.wealth * TAX_RATE);
            richTurtle.wealth -= tax;
            totalTax += tax;
        }
        
        // Distribute equally to poor turtles
        if (totalTax > 0) {
            int redistributionPerPoor = totalTax / poorTurtles.size();
            for (Turtle poorTurtle : poorTurtles) {
                poorTurtle.wealth += redistributionPerPoor;
            }
        }
        
        System.out.println("Tax Redistribution - Tick " + ticks + ": Collected " + totalTax + 
                          " wealth from " + richTurtles.size() + " rich turtles, distributed to " + 
                          poorTurtles.size() + " poor turtles");
    }
    
    @Override
    public void printStats() {
        System.out.println("=== Tax Redistribution Model Statistics ===");
        super.printStats();
        System.out.println("Tax Interval: " + taxInterval + " rounds");
        System.out.println("Tax Rate: " + (TAX_RATE * 100) + "%");
    }
} 