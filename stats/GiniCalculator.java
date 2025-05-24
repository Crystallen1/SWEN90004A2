package stats;

import java.util.List;
import java.util.Collections;

public class GiniCalculator {
    public static double compute(List<Integer> wealths) {
        if (wealths.isEmpty()) {
            return 0.0;
        }
        
        Collections.sort(wealths);
        double totalWealth = wealths.stream().mapToInt(Integer::intValue).sum();
        double wealthSumSoFar = 0;
        double areaBetweenCurves = 0;
        int numPeople = wealths.size();
        
        // Calculate area between Lorenz curve and 45-degree line
        for (int i = 0; i < numPeople; i++) {
            double x1 = (double) i / numPeople;
            double x2 = (double) (i + 1) / numPeople;
            double y1 = wealthSumSoFar / totalWealth;
            wealthSumSoFar += wealths.get(i);
            double y2 = wealthSumSoFar / totalWealth;
            
            // Area of trapezoid between points
            double trapezoidArea = (x2 - x1) * (y1 + y2) / 2;
            // Area of rectangle under 45-degree line
            double rectangleArea = (x2 - x1) * (x1 + x2) / 2;
            // Add difference to total area
            areaBetweenCurves += rectangleArea - trapezoidArea;
        }
        
        // Gini coefficient is the area between curves divided by 0.5
        return areaBetweenCurves * 2;
    }
}
