package models;

import core.World;
import java.util.ArrayList;
import java.util.List;
import utils.GiniCalculator;
import core.Turtle;

public class InheritanceMain {
    private static List<Double> baselineGini = new ArrayList<>();
    private static List<Double> inheritanceGini = new ArrayList<>();
    private static List<Double> baselineAvgWealth = new ArrayList<>();
    private static List<Double> inheritanceAvgWealth = new ArrayList<>();
    
    public static void main(String[] args) {
        // Create inheritance model
        InheritanceWorld inheritanceWorld = new InheritanceWorld(50, 50, 250, 5, 15, 
        1, 83, 10, 
        1, 4);
        
        // Create baseline model for comparison
        World baselineWorld = new World(50, 50, 250, 5, 15, 
        1, 83, 10, 
        1, 4);
        
        // Use same random seed to ensure fair comparison
        inheritanceWorld.initialize(42);
        baselineWorld.initialize(42);
        
        for (int i = 1; i <= 300; i++) {
            // Run one step for both models
            inheritanceWorld.step();
            baselineWorld.step();
            
            // Collect statistics
            collectStatistics(i, inheritanceWorld, baselineWorld);
            
            // Print comparison results every 10 steps
            if (i % 10 == 0) {
                printComparisonStats(i, inheritanceWorld, baselineWorld);
            }
        }
        
        // Print final summary report
        printFinalSummary();
        
        System.out.println("\nSimulation completed!");
    }
    
    /**
     * Collect statistics from both models
     */
    private static void collectStatistics(int step, InheritanceWorld inheritanceWorld, World baselineWorld) {
        // Inheritance model statistics
        if (!inheritanceWorld.getTurtles().isEmpty()) {
            List<Integer> inheritanceWealths = new ArrayList<>();
            int inheritanceTotal = 0;
            for (Turtle turtle : inheritanceWorld.getTurtles()) {
                inheritanceWealths.add(turtle.wealth);
                inheritanceTotal += turtle.wealth;
            }
            inheritanceGini.add(GiniCalculator.compute(inheritanceWealths));
            inheritanceAvgWealth.add((double) inheritanceTotal / inheritanceWealths.size());
        }
        
        // Baseline model statistics
        if (!baselineWorld.getTurtles().isEmpty()) {
            List<Integer> baselineWealths = new ArrayList<>();
            int baselineTotal = 0;
            for (Turtle turtle : baselineWorld.getTurtles()) {
                baselineWealths.add(turtle.wealth);
                baselineTotal += turtle.wealth;
            }
            baselineGini.add(GiniCalculator.compute(baselineWealths));
            baselineAvgWealth.add((double) baselineTotal / baselineWealths.size());
        }
    }
    
    /**
     * Print comparison statistics
     */
    private static void printComparisonStats(int step, InheritanceWorld inheritanceWorld, World baselineWorld) {
        System.out.println("=== Step " + step + " Comparison Report ===");
        
        if (!inheritanceGini.isEmpty() && !baselineGini.isEmpty()) {
            double currentInheritanceGini = inheritanceGini.get(inheritanceGini.size() - 1);
            double currentBaselineGini = baselineGini.get(baselineGini.size() - 1);
            double currentInheritanceAvgWealth = inheritanceAvgWealth.get(inheritanceAvgWealth.size() - 1);
            double currentBaselineAvgWealth = baselineAvgWealth.get(baselineAvgWealth.size() - 1);
            
            System.out.printf("Inheritance Model - Population: %d, Avg Wealth: %.2f, Gini: %.4f%n",
                             inheritanceWorld.getTurtles().size(),
                             currentInheritanceAvgWealth,
                             currentInheritanceGini);
            
            System.out.printf("Baseline Model    - Population: %d, Avg Wealth: %.2f, Gini: %.4f%n",
                             baselineWorld.getTurtles().size(),
                             currentBaselineAvgWealth,
                             currentBaselineGini);
        }
        
        System.out.println("=====================================\n");
    }
    
    /**
     * Print final summary report
     */
    private static void printFinalSummary() {
        System.out.println("\n=== Final Comparison Summary (300 steps) ===");
        
        if (!inheritanceGini.isEmpty() && !baselineGini.isEmpty()) {
            // Calculate averages
            double avgInheritanceGini = inheritanceGini.stream()
                                       .mapToDouble(Double::doubleValue)
                                       .average().orElse(0.0);
            double avgBaselineGini = baselineGini.stream()
                                   .mapToDouble(Double::doubleValue)
                                   .average().orElse(0.0);
            double avgInheritanceWealth = inheritanceAvgWealth.stream()
                                         .mapToDouble(Double::doubleValue)
                                         .average().orElse(0.0);
            double avgBaselineWealth = baselineAvgWealth.stream()
                                     .mapToDouble(Double::doubleValue)
                                     .average().orElse(0.0);
            
            System.out.println("Average Gini Coefficient:");
            System.out.printf("  Inheritance Model: %.4f%n", avgInheritanceGini);
            System.out.printf("  Baseline Model:    %.4f%n", avgBaselineGini);
            double giniChangePercent = ((avgInheritanceGini - avgBaselineGini) / avgBaselineGini) * 100;
            System.out.printf("  Relative Change:   %.2f%% (%s)%n", 
                             Math.abs(giniChangePercent), 
                             giniChangePercent > 0 ? "inheritance increases inequality" : "inheritance reduces inequality");
            
            System.out.println("\nAverage Wealth:");
            System.out.printf("  Inheritance Model: %.2f%n", avgInheritanceWealth);
            System.out.printf("  Baseline Model:    %.2f%n", avgBaselineWealth);
            double wealthChangePercent = ((avgInheritanceWealth - avgBaselineWealth) / avgBaselineWealth) * 100;
            System.out.printf("  Relative Change:   %.2f%% (%s)%n", 
                             Math.abs(wealthChangePercent), 
                             wealthChangePercent > 0 ? "inheritance increases total wealth" : "inheritance decreases total wealth");
            
            // Final Gini coefficients
            System.out.println("\nFinal Gini Coefficient (Step 300):");
            double finalInheritanceGini = inheritanceGini.get(inheritanceGini.size() - 1);
            double finalBaselineGini = baselineGini.get(baselineGini.size() - 1);
            System.out.printf("  Inheritance Model: %.4f%n", finalInheritanceGini);
            System.out.printf("  Baseline Model:    %.4f%n", finalBaselineGini);
            double finalGiniChange = ((finalInheritanceGini - finalBaselineGini) / finalBaselineGini) * 100;
            System.out.printf("  Final Difference:  %.2f%%\n", finalGiniChange);
            
        }
        
        System.out.println("===========================");
    }
}
