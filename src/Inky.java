import java.util.ArrayList;

public class Inky extends Enemy {

    public Inky(Position pos) {
        super(pos);
    }

    // Determines the target based on a 60/40 probability split
    @Override
    public Position selectTarget(Player player, MapData mapData) {

        // generate a random number between 0.0 and 1.0
        double chance = Math.random();

        // 60% chance: target pacman directly
        if (chance < 0.60) {
            return player.getPos();
        }
        // 40% chance: target a random valid neighboring tile
        else {
            ArrayList<Position> validNeighbors = new ArrayList<>();

            int[] dRow = {-1, 1, 0, 0};
            int[] dCol = {0, 0, -1, 1};

            // check all 4 adjacent tiles (Up, Down, Left, Right)
            for (int i = 0; i < 4; i++) {
                int nextRow = this.pos.getRow() + dRow[i];
                int nextCol = this.pos.getCol() + dCol[i];

                // if the neighbor is within bounds and not a wall, it is a valid target
                if (mapData.isValidMove(nextRow, nextCol)) {
                    validNeighbors.add(new Position(nextRow, nextCol));
                }
            }

            // if there are valid neighbors, pick one randomly
            if (!validNeighbors.isEmpty()) {
                int randomIndex = (int) (Math.random() * validNeighbors.size());
                return validNeighbors.get(randomIndex);
            }

            // if no valid neighbors (should not happen normally), target the player
            return player.getPos();
        }
    }
}
