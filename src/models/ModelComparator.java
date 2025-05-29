package models;

import java.util.ArrayList;
import java.util.List;
import core.Turtle;
import utils.GiniCalculator;
import core.World;
public class ModelComparator {
    private World baselineWorld;
    private TaxRedistributionWorld taxWorld;
    private WealthSpreadingWorld spreadingWorld;
    
    private List<Double> baselineGini;
    private List<Double> taxGini;
    private List<Double> spreadingGini;
    
    private List<Double> baselineAvgWealth;
    private List<Double> taxAvgWealth;
    private List<Double> spreadingAvgWealth;
    
    public ModelComparator(int width, int height, int maxPeople, int maxVision,
                          int maxMetabolism, int minLifeExpectancy, int maxLifeExpectancy,
                          int percentBestLand, int grainGrowthInterval, int numGrainGrown) {
        
        // Create three model instances
        baselineWorld = new InheritanceWorld(width, height, maxPeople, maxVision, maxMetabolism,
                                 minLifeExpectancy, maxLifeExpectancy, percentBestLand,
                                 grainGrowthInterval, numGrainGrown);
        
        taxWorld = new TaxRedistributionWorld(width, height, maxPeople, maxVision, maxMetabolism,
                                             minLifeExpectancy, maxLifeExpectancy, percentBestLand,
                                             grainGrowthInterval, numGrainGrown);
        
        spreadingWorld = new WealthSpreadingWorld(width, height, maxPeople, maxVision, maxMetabolism,
                                                 minLifeExpectancy, maxLifeExpectancy, percentBestLand,
                                                 grainGrowthInterval, numGrainGrown);
        
        // Initialize data collection lists
        baselineGini = new ArrayList<>();
        taxGini = new ArrayList<>();
        spreadingGini = new ArrayList<>();
        
        baselineAvgWealth = new ArrayList<>();
        taxAvgWealth = new ArrayList<>();
        spreadingAvgWealth = new ArrayList<>();
    }
    
    /**
     * Initialize all models using the same random seed for fair comparison
     */
    public void initialize(int seed) {
        baselineWorld.initialize(seed);
        taxWorld.initialize(seed);
        spreadingWorld.initialize(seed);
        
        System.out.println("=== Model Comparator Initialized ===");
        System.out.println("Baseline Model: Standard wealth world model");
        System.out.println("Tax Model: Every " + (taxWorld.getMaxLifeExpectancy()/2) + " rounds, collect 20% tax from rich and distribute to poor");
        System.out.println("Spreading Model: Rich turtles leave 20% wealth behind when moving");
        System.out.println("====================================");
    }
    
    /**
     * Run comparison analysis
     */
    public void runComparison(int steps, int reportInterval) {
        System.out.println("Starting " + steps + " step model comparison...\n");
        
        for (int step = 1; step <= steps; step++) {
            // Run one step for each model
            baselineWorld.step();
            taxWorld.step();
            spreadingWorld.step();
            
            // Collect statistics
            collectStatistics();
            
            // Report periodically
            if (step % reportInterval == 0) {
                reportComparison(step);
            }
        }
        
        // Final summary
        finalSummary(steps);
    }
    
    /**
     * Collect statistics for current step
     */
    private void collectStatistics() {
        // Baseline model statistics
        List<Integer> baselineWealths = new ArrayList<>();
        int baselineTotal = 0;
        for (Turtle turtle : baselineWorld.getTurtles()) {
            baselineWealths.add(turtle.wealth);
            baselineTotal += turtle.wealth;
        }
        if (!baselineWealths.isEmpty()) {
            baselineGini.add(GiniCalculator.compute(baselineWealths));
            baselineAvgWealth.add((double) baselineTotal / baselineWealths.size());
        }
        
        // Tax model statistics
        List<Integer> taxWealths = new ArrayList<>();
        int taxTotal = 0;
        for (Turtle turtle : taxWorld.getTurtles()) {
            taxWealths.add(turtle.wealth);
            taxTotal += turtle.wealth;
        }
        if (!taxWealths.isEmpty()) {
            taxGini.add(GiniCalculator.compute(taxWealths));
            taxAvgWealth.add((double) taxTotal / taxWealths.size());
        }
        
        // Spreading model statistics
        List<Integer> spreadingWealths = new ArrayList<>();
        int spreadingTotal = 0;
        for (Turtle turtle : spreadingWorld.getTurtles()) {
            spreadingWealths.add(turtle.wealth);
            spreadingTotal += turtle.wealth;
        }
        if (!spreadingWealths.isEmpty()) {
            spreadingGini.add(GiniCalculator.compute(spreadingWealths));
            spreadingAvgWealth.add((double) spreadingTotal / spreadingWealths.size());
        }
    }
    
    /**
     * Report comparison results for current step
     */
    private void reportComparison(int step) {
        System.out.println("=== Step " + step + " Comparison Report ===");
        
        // Baseline model
        System.out.printf("Baseline Model - Population: %d, Avg Wealth: %.2f, Gini: %.4f%n",
                         baselineWorld.getTurtles().size(),
                         baselineAvgWealth.get(baselineAvgWealth.size() - 1),
                         baselineGini.get(baselineGini.size() - 1));
        
        // Tax model
        System.out.printf("Tax Model - Population: %d, Avg Wealth: %.2f, Gini: %.4f%n",
                         taxWorld.getTurtles().size(),
                         taxAvgWealth.get(taxAvgWealth.size() - 1),
                         taxGini.get(taxGini.size() - 1));
        
        // Spreading model
        System.out.printf("Spreading Model - Population: %d, Avg Wealth: %.2f, Gini: %.4f%n",
                         spreadingWorld.getTurtles().size(),
                         spreadingAvgWealth.get(spreadingAvgWealth.size() - 1),
                         spreadingGini.get(spreadingGini.size() - 1));
        
        System.out.println("=======================================\n");
    }
    
    /**
     * Final summary report
     */
    private void finalSummary(int steps) {
        System.out.println("=== Final Comparison Summary (" + steps + " steps) ===");
        
        // Calculate averages
        double avgBaselineGini = baselineGini.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgTaxGini = taxGini.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgSpreadingGini = spreadingGini.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        double avgBaselineWealth = baselineAvgWealth.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgTaxWealth = taxAvgWealth.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgSpreadingWealth = spreadingAvgWealth.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        System.out.println("Average Gini Coefficient:");
        System.out.printf("  Baseline Model: %.4f%n", avgBaselineGini);
        System.out.printf("  Tax Model: %.4f (Relative Change: %.2f%%)%n", avgTaxGini, 
                         ((avgTaxGini - avgBaselineGini) / avgBaselineGini) * 100);
        System.out.printf("  Spreading Model: %.4f (Relative Change: %.2f%%)%n", avgSpreadingGini,
                         ((avgSpreadingGini - avgBaselineGini) / avgBaselineGini) * 100);
        
        System.out.println("\nAverage Wealth:");
        System.out.printf("  Baseline Model: %.2f%n", avgBaselineWealth);
        System.out.printf("  Tax Model: %.2f (Relative Change: %.2f%%)%n", avgTaxWealth,
                         ((avgTaxWealth - avgBaselineWealth) / avgBaselineWealth) * 100);
        System.out.printf("  Spreading Model: %.2f (Relative Change: %.2f%%)%n", avgSpreadingWealth,
                         ((avgSpreadingWealth - avgBaselineWealth) / avgBaselineWealth) * 100);
        
    }
} 