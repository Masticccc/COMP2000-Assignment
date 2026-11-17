import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;


// Cell Class
public class Cell {
    private double x;
    private double y;
    private double velX;
    private double velY;
    public double speed = Settings.CELL_SPEED;
    private int size = Settings.CELL_SIZE;

    private double speedMultiplier = 1.0;
    private double sizeMultiplier = 1.0;
    private MovementStrategy movementStrategy = new NormalMovement();
    private int id;
    private CellState state;
    private Color color; // Default color for the cell
    public boolean isMutated = false; // Flag to indicate if the cell is mutated
    public Resistance resistance = new Resistance(); // Resistance level for the cell
    public int infectionsCaused = 0;
    public boolean hasBeenInfected = false;
    public boolean infectedThisWindow = false;

    private boolean collisionEnabled = Settings.hasCollision; // Collision enabled or not

    private int tick = 0;

    public Cell(double startX, double startY, int id, CellState initialState) {
        this.x = startX;
        this.y = startY;
        this.id = id;
        this.state = initialState;
        this.color = initialState.getCellColor(); // Set color based on initial state
        this.velX = ((Math.random() * 3) - 1) * speed;
        this.velY = ((Math.random() * 3) - 1) * speed;
    }

    public Cell(double startX, double startY, int id, CellState initialState, Color color, boolean isMutated) {
        this.x = startX;
        this.y = startY;
        this.id = id;
        this.state = initialState;
        this.color = color;
        this.isMutated = isMutated;
        this.velX = ((Math.random() * 3) - 1) * speed;
        this.velY = ((Math.random() * 3) - 1) * speed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public CellState getState() {
        return state;
    }

    public int getSize() {
        return (int) Math.round(size * sizeMultiplier);
    }

    public void changeState(CellState newState) {
        this.state = newState;
    }

    public void draw(Graphics g) { // Drawing loop for the cell
    tick++;
    int currentSize = getSize(); // For size changes

    if(state.getType().equals("INFECTED")) { // "Pulsate" if the cell is infected
        currentSize = (int) (getSize() + Math.sin(tick * 0.1) * 5); // Sin wave for the pulsing
    }

    int offset = (currentSize - size) / 2;
    int drawX = (int) x - offset;
    int drawY = (int) y - offset;

    int centerX = drawX + (currentSize / 2);
    int centerY = drawY + (currentSize / 2);

    // get glow color to determine glow color based on the type
    Color glowColor = null;
    if (state.getType().equals("INFECTED")) {
        glowColor = Color.RED;
    } else if (state.getType().equals("ANTIVIRUS")) {
        glowColor = Color.BLUE;
    } else {
        glowColor = Color.GRAY;
    }

    // 2. Draw the glowing border underneath if a match was found
    
    Graphics2D g2d = (Graphics2D) g.create(); // Create a copy to protect graphics state
    
    // outer layer
    g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 40)); 
    g2d.fillOval(drawX - 8, drawY - 8, currentSize + 16, currentSize + 16);
    
    // middle layer
    g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 100)); 
    g2d.fillOval(drawX - 4, drawY - 4, currentSize + 8, currentSize + 8);
    
    // innner layer
    g2d.setColor(glowColor);
    g2d.drawOval(drawX, drawY, currentSize, currentSize);
    
    g2d.dispose(); // Clean up the copy


    // Add spikes to INFECTED cell
    if (state.getType().equals("INFECTED")) {
        Graphics2D g2dSpikes = (Graphics2D) g.create();


        g2dSpikes.setColor(state.getCellColor()); // Match the main cell color
        
        g2dSpikes.translate(centerX, centerY);
        
        int spikeCount = 10; // Total number of spikes around the cell
        int spikeLength = currentSize + 20; // How far the spikes stick out past the cell
        int spikeThickness = currentSize / 5; // Thickness of the spikes
        int cornerArc = 15; // How rounded the spike corners are

        for (int i = 0; i < spikeCount; i++) {
            // Draw a rounded rectangle in the center
            g2dSpikes.fillRoundRect(-spikeLength / 2, -spikeThickness / 2, spikeLength, spikeThickness, cornerArc, cornerArc);
            
            // Rotate the canvas for the next spike (360 degrees divided by spike count)
            g2dSpikes.rotate(Math.toRadians(360.0 / spikeCount));
        }
        
        g2dSpikes.dispose();
    }

    g.setColor(state.getCellColor());
    g.fillOval(drawX, drawY, currentSize, currentSize);
}


    public void onCollision(Cell opponent) { // change the cell states depending on the type of reaction
        CellState thisOriginState = this.getState();
        CellState oppOriginState = opponent.getState();
        CellState oppNewState = opponent.state.reactWith(thisOriginState);
        CellState thisNewState = this.state.reactWith(oppOriginState);
        // === Resistance check ===
        if (this.state == InfectedState.INSTANCE && thisNewState == NeutralState.INSTANCE) {
            if (this.resistance.canResistAntivirus()) {
                thisNewState = InfectedState.INSTANCE;
                this.resistance.increaseLevel();
            }
        }
        if (opponent.state == InfectedState.INSTANCE && oppNewState == NeutralState.INSTANCE) {
            if (opponent.resistance.canResistAntivirus()) {
                oppNewState = InfectedState.INSTANCE;
                opponent.resistance.increaseLevel();
            }
        }
        // === End resistance check ===
        if (oppOriginState == NeutralState.INSTANCE && oppNewState == InfectedState.INSTANCE) {
            infectionsCaused++;
            opponent.hasBeenInfected = true;
            opponent.infectedThisWindow = true;
        } else if (thisOriginState == NeutralState.INSTANCE && thisNewState == InfectedState.INSTANCE) {
            opponent.infectionsCaused++;
            hasBeenInfected = true;
            infectedThisWindow = true;
        }
        changeState(thisNewState);
        opponent.changeState(oppNewState);
        if (collisionEnabled) {
            bounceOff(opponent);
        }

    }

    public void bounceOff(Cell opponent) { // Bounce off logic
        double tempVelX = this.velX;
        double tempVelY = this.velY;

        this.velX = opponent.velX;
        this.velY = opponent.velY;

        opponent.velX = tempVelX;
        opponent.velY = tempVelY;

        // Separate cells to prevent sticking
        double dx = opponent.x - this.x;
        double dy = opponent.y - this.y;
        double distance = Math.hypot(dx, dy);

        if (distance < size) {
            double overlap = size - distance;
            double separationX = (dx / distance) * (overlap / 2 + 1);
            double separationY = (dy / distance) * (overlap / 2 + 1);

            this.x -= separationX;
            this.y -= separationY;
            opponent.x += separationX;
            opponent.y += separationY;
        }
    }

    public void move(int panelMaxWidth, int panelMaxHeight, int panelMinWidth, int panelMinHeight) { // Move logic
        if (panelMaxWidth <= 0 || panelMaxHeight <= 0) {
            return;
        }
        x += velX * movementStrategy.getSpeedMultiplier();
        y += velY * movementStrategy.getSpeedMultiplier();

        if (x <= panelMinWidth) {
            x = panelMinWidth;
            velX = -velX;
        } else if (x + getSize() >= panelMaxWidth) {
            x = panelMaxWidth - getSize();
            velX = -velX;
        }

        if (y <= panelMinHeight) {
            y = panelMinHeight;
            velY = -velY;
        } else if (y + getSize() >= panelMaxHeight) {
            y = panelMaxHeight - getSize();
            velY = -velY;
        }
    }

    public boolean collidesWith(Cell other) { // Collision logic

        int radiusA = getSize() / 2;
        int radiusB = other.getSize() / 2;

        double centXA = x + radiusA;
        double centYA = y + radiusA;

        double centXB = other.x + radiusB;
        double centYB = other.y + radiusB;

        double distance = Math.hypot(
                centXB - centXA,
                centYB - centYA);

        return distance <= radiusA + radiusB;
    }

    public void setMovementStrategy(MovementStrategy strategy) {
        if (strategy != null) {
            this.movementStrategy = strategy;
        }
    }

    public void setSpeedMultiplier(double multiplier) {

        double speedChange = multiplier / speedMultiplier;

        velX *= speedChange;
        velY *= speedChange;

        speedMultiplier = multiplier;
    }

    public void setSizeMultiplier(double multiplier) {

        sizeMultiplier = multiplier;
    }
}