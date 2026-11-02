public class Player {
    private Position pos;
    private int score;

    private Game.Direction currentDirection;
    private Game.Direction requestedDirection;

    private double visualRow;
    private double visualCol;

    private boolean moving;

    public Player(Position pos) {
        this.currentDirection = Game.Direction.NONE;
        this.requestedDirection = Game.Direction.NONE;
        this.pos = pos;
        this.score = 0;
        this.visualRow = pos.getRow();
        this.visualCol = pos.getCol();
        this.moving = false;

    }

    public void setRequestedDirection(Game.Direction direction) {
        this.requestedDirection = direction;
    }

    // Checks if the intended direction has a wall
    private boolean canMove(MapData map, Game.Direction dir) {
        if (dir == Game.Direction.NONE) return false;
        return map.isValidMove(pos.getRow() + dir.getDRow(), pos.getCol() + dir.getDCol());
    }

    // Main movement engine
    public void updateMovement(MapData map) {
        double speed = 0.15;

        boolean isAligned = Math.abs(visualRow - pos.getRow()) < speed / 2 &&
                Math.abs(visualCol - pos.getCol()) < speed / 2;

        if (isAligned) {
            // Snap to center
            visualRow = pos.getRow();
            visualCol = pos.getCol();

            // Try to turn to requested direction
            if (canMove(map, requestedDirection)) currentDirection = requestedDirection;

            // Check front and update logical position if clear
            moving = canMove(map, currentDirection);
            if (moving) {
                pos = new Position(pos.getRow() + currentDirection.getDRow(),
                        pos.getCol() + currentDirection.getDCol());
            }
        }

        // slide visually using Enum vector math
        if (moving) {
            visualRow += currentDirection.getDRow() * speed;
            visualCol += currentDirection.getDCol() * speed;
        }
    }

    public void addScore(int points) {
        this.score += points;
    }

    // Getters and Setters
    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Position getPos() {
        return pos;
    }

    public Game.Direction getCurrentDirection() {
        return currentDirection;
    }

    public double getVisualRow() {
        return visualRow;
    }

    public double getVisualCol() {
        return visualCol;
    }

    public int getScore() {
        return score;
    }

    // Setters
    public void setVisualRow(double visualRow) {
        this.visualRow = visualRow;
    }

    public void setVisualCol(double visualCol) {
        this.visualCol = visualCol;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public void setCurrentDirection(Game.Direction direction) {
        this.currentDirection = direction;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
