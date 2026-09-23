

public class SimulationStats {
    private final int neutralCount;
    private final int infectedCount;
    private final int antivirusCount;
    private final int vaccinatedCount;
    private final int deadCellCount;
    private final int mutatedCellCount;
    private final float r0Value;
    private final int tickSnapshot;

    public SimulationStats(int neutralCount, int infectedCount, int antivirusCount, int vaccinatedCount, int deadCellCount, int mutatedCellCount,
        float r0Value, int tickSnapshot){
        this.neutralCount = neutralCount;
        this.infectedCount = infectedCount;
        this.antivirusCount = antivirusCount;
        this.vaccinatedCount = vaccinatedCount;
        this.deadCellCount = deadCellCount;
        this.mutatedCellCount = mutatedCellCount;
        this.r0Value = r0Value;
        this.tickSnapshot = tickSnapshot;
    }

    public int getNeutralCount(){
        return neutralCount;
    }
    public int getInfectedCount(){
        return infectedCount;
    }
    public int getAntivirusCount(){
        return antivirusCount;
    }
    public int getVaccinatedCount(){
    return vaccinatedCount;
    }
    public int getDeadCellCount(){
        return deadCellCount;
    }
    public int getMutatedCellCount(){
        return mutatedCellCount;
    }
    public float getR0Value(){
        return r0Value;
    }
    public int getTickSnapshot(){
        return tickSnapshot;
    }
}
