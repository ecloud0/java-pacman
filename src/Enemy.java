import java.util.ArrayList;

public abstract class Enemy {
    protected Position pos;
    protected Game.Direction direction;
    protected BFSPathFinder finder;

    protected double visualRow;
    protected double visualCol;

    public Enemy(Position pos){
        this.pos = pos;
        this.direction = Game.Direction.NONE;
        this.visualRow = pos.getRow();
        this.visualCol = pos.getCol();
        // to not get a NullPointerException
        this.finder = new BFSPathFinder();
    }

    public Enemy(Position pos, BFSPathFinder finder) {
        this.pos = pos;
        this.finder = finder;
        this.direction = Game.Direction.NONE;
        this.visualRow = pos.getRow();
        this.visualCol = pos.getCol();
    }

    // Moves the enemy towards a target position using BFS and handles visual sliding
    public void move(Player player, MapData mapData) {

        // slightly slower than Pac-Man
        double speed = 0.12;

        // Check if the ghost's visual body has arrived at its current logical grid cell
        boolean isAligned = Math.abs(visualRow - pos.getRow()) < speed / 2 &&
                Math.abs(visualCol - pos.getCol()) < speed / 2;

        if (isAligned) {
            // Snap to the exact grid center to prevent drifting
            visualRow = pos.getRow();
            visualCol = pos.getCol();

            //Ask the specific ghost for its target
            Position target = selectTarget(player, mapData);

            if (target != null) {
                // Find the shortest path using BFS
                ArrayList<Position> path = finder.getFullShortestPath(this.pos, target, mapData);

                // Update logical position to the very next step in the path
                if (path != null && path.size() > 1) {
                    Position nextStep = path.get(1);
                    this.direction = getDirectionFromPositions(this.pos, nextStep);
                    this.pos = nextStep; // The mind moves instantly...
                } else {
                    this.direction = Game.Direction.NONE; // Stop if no path exists
                }
            }
        }

        // ...but the body slides visually over time
        if (this.direction != Game.Direction.NONE) {
            this.visualRow += this.direction.getDRow() * speed;
            this.visualCol += this.direction.getDCol() * speed;
        }
    }

    // Abstract method; subclasses must implement their own targeting strategy
    public abstract Position selectTarget(Player player, MapData mapData);

    // Calculates the movement direction from one position to another
    protected Game.Direction getDirectionFromPositions(Position from, Position to) {
        int dRow = to.getRow() - from.getRow();
        int dCol = to.getCol() - from.getCol();

        if (dRow == -1) return Game.Direction.UP;
        if (dRow == 1) return Game.Direction.DOWN;
        if (dCol == -1) return Game.Direction.LEFT;
        if (dCol == 1) return Game.Direction.RIGHT;

        return Game.Direction.NONE;
    }

    public double getVisualRow() {
        return visualRow;
    }

    public double getVisualCol() {
        return visualCol;
    }

    public Game.Direction getDirection() {
        return direction;
    }

    public Position getPos() {
        return pos;
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

    public void setDirection(Game.Direction direction) {
        this.direction = direction;
    }


    }


