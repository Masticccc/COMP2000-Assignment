import java.awt.Color;

public class NeutralState implements CellState {
    public static final NeutralState INSTANCE = new NeutralState(); // So we don't create a new state object on each reaction

    private NeutralState() {}

    @Override public String getType() { return "NEUTRAL"; }
    @Override public Color getCellColor() { return new Color(60, 123, 26); }

    @Override
    public CellState reactWith(CellState opponent) {
        if (opponent.getType().equals("INFECTED")) {
            return InfectedState.INSTANCE; // Neutral becomes Infected
        } else if (opponent.getType().equals("ANTIVIRUS")) {
            return VaccinatedState.INSTANCE;  // Neutral becomes vaccinated
        } else {
            return this;  // Neutral on Neutral. No change
        }
    }
}