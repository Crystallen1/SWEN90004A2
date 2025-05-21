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

    private static final Random random = new Random();

    public Turtle(int metabolism, int vision, int lifeExpectancy) {
        this.metabolism = metabolism;
        this.vision = vision;
        this.lifeExpectancy = lifeExpectancy;
        this.age = 0;
        this.wealth = 0;
    }

    public void decideDirection(World world) {
        Direction bestDir = null;
        int maxGrain = -1;

        // Find the best direction for the turtle to move to
        for (Direction dir : Direction.values()) {
            int totalGrain = 0;
            for (int dist = 1; dist <= vision; dist++) {
                int newX = (x + dir.getDx() * dist + world.width) % world.width;
                int newY = (y + dir.getDy() * dist + world.height) % world.height;
                totalGrain += world.map[newX][newY].getGrainHere();
            }
            if (totalGrain > maxGrain) {
                maxGrain = totalGrain;
                bestDir = dir;
            }
        }

        // Move the turtle
        if (bestDir != null) {
            move(bestDir, world);
        }
    }

    public void move(Direction dir, World world) {
        x = (x + dir.getDx() + world.width) % world.width;
        y = (y + dir.getDy() + world.height) % world.height;
    }

    public void harvest(World world) {
        Patch patch = world.map[x][y];
        List<Turtle> turtlesHere = new ArrayList<>();
        
        // Find all turtles on the same patch
        for (Turtle t : world.turtles) {
            if (t.x == x && t.y == y) {
                turtlesHere.add(t);
            }
        }
        
        // Distribute grain equally
        int grainPerTurtle = patch.getGrainHere() / turtlesHere.size();
        wealth += grainPerTurtle;
        patch.setGrainHere(0);
    }

    public void ageAndConsume() {
        age++;
        wealth -= metabolism;
    }

    public boolean isDead() {
        return age >= lifeExpectancy || wealth <= 0;
    }

    public void rebirth(World world) {
        this.age = 0;
        this.metabolism = 1 + random.nextInt(world.maxMetabolism);
        this.vision = 1 + random.nextInt(world.maxVision);
        this.lifeExpectancy = world.minLifeExpectancy + 
                             random.nextInt(world.maxLifeExpectancy - world.minLifeExpectancy + 1);
        this.wealth = this.metabolism + random.nextInt(50);
        this.x = random.nextInt(world.width);
        this.y = random.nextInt(world.height);
    }
}
