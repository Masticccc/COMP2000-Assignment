import java.awt.Color;

public class VaccinatedState implements CellState {
    public static final VaccinatedState INSTANCE = new VaccinatedState();

    private VaccinatedState() {}

    @Override
    public String getType() {
        return "VACCINATED";
    }

    @Override
    public Color getCellColor() {
        return new Color(255, 165, 0);
    }

    @Override
    public CellState reactWith(CellState opponent) {
        if (opponent.getType().equals("INFECTED")) {
            return this;
        } else {
            return this;
        }
    }
}
