package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ComparisonCsvExporter {
    private PrintWriter csvWriter;
    private boolean isInitialized = false;
    private String filename;

    /**
     * Initialize CSV export file with headers for comparison data
     * @param filename The name of the CSV file to create
     */
    public void initialize(String filename) {
        this.filename = filename;
        try {
            csvWriter = new PrintWriter(new FileWriter(filename));
            csvWriter.println("Round,Model_Type,Population,Poor_Count,Middle_Count,Rich_Count,Avg_Wealth,Gini_Coefficient");
            isInitialized = true;
            System.out.println("Comparison CSV export initialized: " + filename);
        } catch (IOException e) {
            System.err.println("Error initializing comparison CSV file: " + e.getMessage());
            isInitialized = false;
        }
    }

    /**
     * Export data for one model in comparison
     * @param round Current simulation round/tick
     * @param modelType Type of model (e.g., "Baseline", "Tax", "Spreading", "Inheritance")
     * @param population Current population size
     * @param wealths List of all turtle wealth values
     */
    public void exportModelData(int round, String modelType, int population, List<Integer> wealths) {
        if (!isInitialized || csvWriter == null || wealths.isEmpty()) {
            return;
        }

        // Find max wealth for class distribution
        int maxWealth = wealths.stream().mapToInt(Integer::intValue).max().orElse(0);
        
        // Calculate wealth class distribution
        int poorCount = 0, middleCount = 0, richCount = 0;
        int totalWealth = 0;
        
        for (int wealth : wealths) {
            totalWealth += wealth;
            if (wealth <= maxWealth / 3) {
                poorCount++;
            } else if (wealth <= (maxWealth * 2 / 3)) {
                middleCount++;
            } else {
                richCount++;
            }
        }
        
        // Calculate average wealth and Gini coefficient
        double avgWealth = (double) totalWealth / wealths.size();
        double giniCoefficient = GiniCalculator.compute(wealths);
        
        // Export the data
        csvWriter.printf("%d,%s,%d,%d,%d,%d,%.2f,%.4f%n", 
            round, modelType, population, poorCount, middleCount, richCount, avgWealth, giniCoefficient);
        csvWriter.flush(); // Ensure data is written immediately
    }

    /**
     * Close the CSV file and clean up resources
     */
    public void close() {
        if (csvWriter != null) {
            csvWriter.close();
            isInitialized = false;
            System.out.println("Comparison CSV export completed: " + filename);
        }
    }

    /**
     * Check if the exporter is properly initialized
     * @return true if initialized and ready to export
     */
    public boolean isInitialized() {
        return isInitialized;
    }
} 