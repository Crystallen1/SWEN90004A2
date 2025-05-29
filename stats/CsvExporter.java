package stats;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {
    private PrintWriter csvWriter;
    private boolean isInitialized = false;
    private String filename;

    /**
     * Initialize CSV export file with headers
     * @param filename The name of the CSV file to create
     */
    public void initialize(String filename) {
        this.filename = filename;
        try {
            csvWriter = new PrintWriter(new FileWriter(filename));
            csvWriter.println("Round,Poor_Count,Middle_Count,Rich_Count,Gini_Coefficient");
            isInitialized = true;
            System.out.println("CSV export initialized: " + filename);
        } catch (IOException e) {
            System.err.println("Error initializing CSV file: " + e.getMessage());
            isInitialized = false;
        }
    }

    /**
     * Export simulation data to CSV
     * @param round Current simulation round/tick
     * @param poorCount Number of poor turtles
     * @param middleCount Number of middle class turtles
     * @param richCount Number of rich turtles
     * @param giniCoefficient Gini coefficient value
     */
    public void exportData(int round, int poorCount, int middleCount, int richCount, double giniCoefficient) {
        if (!isInitialized || csvWriter == null) {
            return;
        }

        csvWriter.printf("%d,%d,%d,%d,%.4f%n", 
            round, poorCount, middleCount, richCount, giniCoefficient);
        csvWriter.flush(); // Ensure data is written immediately
    }

    /**
     * Export wealth data by calculating class distribution and Gini coefficient
     * @param round Current simulation round/tick
     * @param wealths List of all turtle wealth values
     */
    public void exportWealthData(int round, List<Integer> wealths) {
        if (!isInitialized || csvWriter == null || wealths.isEmpty()) {
            return;
        }

        // Find max wealth for class distribution
        int maxWealth = wealths.stream().mapToInt(Integer::intValue).max().orElse(0);
        
        // Calculate wealth class distribution
        int poorCount = 0, middleCount = 0, richCount = 0;
        
        for (int wealth : wealths) {
            if (wealth <= maxWealth / 3) {
                poorCount++;
            } else if (wealth <= (maxWealth * 2 / 3)) {
                middleCount++;
            } else {
                richCount++;
            }
        }
        
        // Calculate Gini coefficient
        double giniCoefficient = GiniCalculator.compute(wealths);
        
        // Export the data
        exportData(round, poorCount, middleCount, richCount, giniCoefficient);
    }

    /**
     * Close the CSV file and clean up resources
     */
    public void close() {
        if (csvWriter != null) {
            csvWriter.close();
            isInitialized = false;
            System.out.println("CSV export completed: " + filename);
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