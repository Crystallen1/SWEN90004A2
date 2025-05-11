package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    int width, height;
    Patch[][] map;
    List<Turtle> turtles;
    Random random;

    public World(){}

    public World(int width, int height,int maxPeople,int maxVision,
                 int maxMetabolism,int minLifeExpectancy,int maxLifeExpectancy,
                 int percentBestLand,int grainGrowthInterval,int numGrainGrown) {
        this.width = width;
        this.height = height;
        this.map = new Patch[width][height];
        this.turtles = new ArrayList<>();
        this.random = new Random();
    }

    public void initialize(int seed) {

    }

    void initPatches()  //follow percent-best-land to initialize the map
    {

    }

    void initTurtle()   // initialize turtles
    {

    }

    public void step()            // each tick, turtles move and grow patches
    {

    }

    void growAllPatches() {

    }

    void harvestAndDistribute() {

    }

    void updateDeathsAndRebirths() {

    }

    public void printStats() {

    }
}