package model;

import java.util.Random;

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

        //find the best direction for the turtle to move to
        for (Direction dir : Direction.values()) {
            for (int dist = 1; dist <= vision; dist++) {
                int newX = (x + dir.getDx() * dist + world.width) % world.width;
                int newY = (y + dir.getDy() * dist + world.height) % world.height;

                int grain = world.map[newX][newY].getGrainHere();
                if (grain > maxGrain) {
                    maxGrain = grain;
                    bestDir = dir;
                }
            }
        }

        //move the turtle
        if (bestDir != null) {
            move(bestDir, world);
        }
    }

    public void move(Direction dir, World world) {
        int bestX = x;
        int bestY = y;
        int maxGrain = -1;

        for (int dist = 1; dist <= vision; dist++) {
            int newX = (x + dir.getDx() * dist + world.width) % world.width;
            int newY = (y + dir.getDy() * dist + world.height) % world.height;

            int grain = world.map[newX][newY].getGrainHere();
            if (grain > maxGrain) {
                maxGrain = grain;
                bestX = newX;
                bestY = newY;
            }
        }

        // Move and harvest grain
        x = bestX;
        y = bestY;
        Patch patch = world.map[x][y];
        wealth += patch.getGrainHere();
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
        // Find min and max wealth among current turtles
        int minWealth = Integer.MAX_VALUE;
        int maxWealth = Integer.MIN_VALUE;

        for (Turtle t : world.turtles) {
            minWealth = Math.min(minWealth, t.wealth);
            maxWealth = Math.max(maxWealth, t.wealth);
        }

        this.age = 0;
        this.metabolism = 1 + random.nextInt(4);       // Example: [1, 4]
        this.vision = 1 + random.nextInt(5);           // Example: [1, 5]
        this.lifeExpectancy = 60 + random.nextInt(40); // Example: [60, 99]
        this.wealth = minWealth + random.nextInt(maxWealth - minWealth + 1);
        this.x = random.nextInt(world.width);
        this.y = random.nextInt(world.height);
    }
}
