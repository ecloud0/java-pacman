public class Blinky extends Enemy {

    public Blinky(Position pos) {
        super(pos);
    }

    // determines the target cell by finding the closest corner to pacman
    @Override
    public Position selectTarget(Player player, MapData mapData) {
        Position[] corners = mapData.getCorners();

        // if map has no corners for some reason, target the player directly
        if (corners == null || corners.length == 0) {
            return player.getPos();
        }

        Position pacmanPos = player.getPos();
        Position closestCorner = corners[0];

        // initialize with maximum possible integer value
        int minDistance = Integer.MAX_VALUE;

        // iterate through all available corners to find the nearest one
        for (int i = 0; i < corners.length; i++) {
            Position corner = corners[i];

            int dRow = pacmanPos.getRow() - corner.getRow();
            int dCol = pacmanPos.getCol() - corner.getCol();

            // Calculate squared Euclidean distance to avoid floating-point math overhead
            int distance = (dRow * dRow) + (dCol * dCol);

            // CRITICAL: strict less than (<) ensures the tie-breaking rule.
            // If distances are equal, it wont update, keeping the corner that appeared first.
            if (distance < minDistance) {
                minDistance = distance;
                closestCorner = corner;
            }
        }

        return closestCorner;
    }

}
