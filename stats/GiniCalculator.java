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
        double giniIndex = 0;
        int numPeople = wealths.size();
        
        for (int i = 0; i < numPeople; i++) {
            wealthSumSoFar += wealths.get(i);
            // Calculate point on the Lorenz curve
            double lorenzPoint = (wealthSumSoFar / totalWealth) * 100;
            // Calculate Gini coefficient
            giniIndex += ((i + 1.0) / numPeople) - (wealthSumSoFar / totalWealth);
        }
        
        // Normalize Gini coefficient to range 0-1
        return giniIndex / numPeople;
    }
}
