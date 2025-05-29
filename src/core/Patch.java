package core;

public class Patch {

    private int grainHere;
    private int maxGrain;
    private int spreadWealth; // Wealth spread to this patch

    public Patch(int maxGrain) {
        this.maxGrain = maxGrain;
        grainHere = 0;
        spreadWealth = 0;
    }

    /**
     * This method grows the grain on the patch by the growth rate.
     * If the grain on the patch is more than the max grain, it is set to the max 
     * grain.
     * @param growthRate
     */
    public void growGrain(int growthRate) {
        if (maxGrain == 0) {
            return;
        }
        if (grainHere < maxGrain) {
            grainHere = Math.min(grainHere + growthRate, maxGrain);
        }
    }

    public int getGrainHere() {
        return grainHere;
    }

    /**
     * This method set the grain on the patch to the given value. (Used for after 
     * harvesting)
     * @param grainHere
     */
    public void setGrainHere(int grainHere) {
        this.grainHere = grainHere;
    }

    public int getMaxGrain() {
        return maxGrain;
    }

    /**
     * This method set the max grain on the patch to the given value after diffusion.
     * @param maxGrain
     */
    public void setMaxGrain(int maxGrain) {
        this.maxGrain = maxGrain;
    }

    /**
     * Add spread wealth to this patch
     * @param wealth Amount of wealth to add
     */
    public void addSpreadWealth(int wealth) {
        this.spreadWealth += wealth;
    }

    /**
     * Harvest and clear spread wealth on this patch
     * @return Accumulated spread wealth
     */
    public int harvestSpreadWealth() {
        int wealth = this.spreadWealth;
        this.spreadWealth = 0;
        return wealth;
    }

    /**
     * Get current spread wealth (without clearing)
     * @return Current amount of spread wealth
     */
    public int getSpreadWealth() {
        return spreadWealth;
    }

}