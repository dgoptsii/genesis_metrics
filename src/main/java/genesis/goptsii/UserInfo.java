package genesis.goptsii;

import lombok.Data;

@Data

public class UserInfo {
    private boolean triedFreeTrial = false;
    private int freeTrialCount = 0;
    private int purchaseCount = 0;
    private String country;
    private String device;
    private String language;
    private String theme;
    private String refund;

    public void incrementPurchaseCount() {
        this.purchaseCount++;
    }

    public boolean hasTriedFreeTrial() {
        return triedFreeTrial;
    }

    public int getFreeTrialCount() {
        return freeTrialCount;
    }
    public void incrementFreeTrialCount() {
        this.freeTrialCount++;
    }
}
