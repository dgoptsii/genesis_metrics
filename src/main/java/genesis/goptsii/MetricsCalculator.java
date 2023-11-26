package genesis.goptsii;

import java.util.Map;

class MetricsCalculator {

        public static MetricsSummary calculateMetrics(Map<String, UserInfo> userInfoMap) {
            MetricsSummary metricsSummary = new MetricsSummary();

            for (UserInfo userInfo : userInfoMap.values()) {
                // Count free trials
                if (userInfo.hasTriedFreeTrial()) {
                    metricsSummary.incrementFreeTrialCount();
                }

                // Count purchases
                int purchaseCount = userInfo.getPurchaseCount();
                metricsSummary.incrementPurchaseCount(purchaseCount);

                // Count conversion from trial to purchases
                if (userInfo.hasTriedFreeTrial() && purchaseCount > 0) {
                    metricsSummary.incrementConversionFromTrial();
                }

                // Count conversion between purchases
                for (int i = 1; i < purchaseCount; i++) {
                    metricsSummary.incrementConversionCount(i);
                }
            }

            return metricsSummary;
        }

        // MetricsSummary class to encapsulate the summary of metrics
         static class MetricsSummary {
            private int freeTrialCount = 0;
            private int[] purchaseCounts = new int[5];
            private int[] conversionCounts = new int[4]; // Conversion from trial to 1st purchase, 1st to 2nd, 2nd to 3rd, 3rd to 4th

            public void incrementFreeTrialCount() {
                freeTrialCount++;
            }

            public void incrementPurchaseCount(int purchaseCount) {
                if (purchaseCount > 0 && purchaseCount <= 5) {
                    purchaseCounts[purchaseCount - 1]++;
                }
            }

            public void incrementConversionFromTrial() {
                conversionCounts[0]++;
            }

            public void incrementConversionCount(int index) {
                if (index > 0 && index < 4) {
                    conversionCounts[index]++;
                }
            }

            // Provide getters for the metrics
            public int getFreeTrialCount() {
                return freeTrialCount;
            }

            public int getPurchaseCount(int index) {
                return (index > 0 && index <= 5) ? purchaseCounts[index - 1] : 0;
            }

            public int getConversionCount(int index) {
                return (index >= 0 && index < 4) ? conversionCounts[index] : 0;
            }
        }
    }