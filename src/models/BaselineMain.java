package models;

import core.World;

public class BaselineMain {
    public static void main(String[] args) {

        World world = new World(50, 50, 250, 5, 15, 
        1, 83, 10, 
        1, 4);
        
        // Initialize CSV export
        world.initializeCsvExport("data/output/wealth_simulation_results.csv");
        
        world.initialize(42);

        for (int i = 1; i <= 300; i++) {
            world.step();
            if (i % 10 == 0) {
                world.printStats();
            }
        }
        
        // Close CSV export
        world.closeCsvExport();
        
        System.out.println("Simulation completed. Results exported to data/output/wealth_simulation_results.csv");
    }
}
