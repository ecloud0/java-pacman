import java.util.ArrayList;
import java.util.Collections;

// Calculates the shortest path using Breadth-First Search (BFS)
public class BFSPathFinder {

    // Internal class to track the path history
    private class Node {
        Position pos;
        Node parent;

        // Initializes the node with its position and where it came from
        Node(Position pos, Node parent) {
            this.pos = pos;
            this.parent = parent;
        }
    }

    // Finds the shortest path from start to goal strictly following UP, DOWN, LEFT, RIGHT order
    public ArrayList<Position> getFullShortestPath(Position start, Position goal, MapData mapData) {

        ArrayList<Position> path = new ArrayList<>();

        // If start and goal are the same, return a list with only the start position
        if (start.equals(goal)) {
            path.add(start);
            return path;
        }

        // Create our custom queue and the visited tracking matrix
        Queue<Node> queue = new Queue<>();
        int rows = mapData.getRows();
        int cols = mapData.getCols();
        boolean[][] visited = new boolean[rows][cols];

        // Add the starting point to the queue and mark it as visited
        queue.enqueue(new Node(start, null));
        visited[start.getRow()][start.getCol()] = true;

        // PRIORITY: UP, DOWN, LEFT, RIGHT
        // UP: row - 1, col 0
        // DOWN: row + 1, col 0
        // LEFT: row 0, col - 1
        // RIGHT: row 0, col + 1
        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        Node targetNode = null;

        // BFS Loop
        while (!queue.isEmpty()) {
            Node current = queue.dequeue();

            // Stop searching if we reached the goal
            if (current.pos.equals(goal)) {
                targetNode = current;
                break;
            }

            // Check all 4 neighbors in the exact required order
            for (int i = 0; i < 4; i++) {
                int nextRow = current.pos.getRow() + dRow[i];
                int nextCol = current.pos.getCol() + dCol[i];

                // Proceed only if the move is within bounds, not a wall, and not visited
                if (mapData.isValidMove(nextRow, nextCol) && !visited[nextRow][nextCol]) {

                    visited[nextRow][nextCol] = true; // Mark as visited

                    Position nextPos = new Position(nextRow, nextCol);
                    queue.enqueue(new Node(nextPos, current));
                }
            }
        }

        // If queue emptied and targetNode is still null, no path exists (blocked by walls)
        if (targetNode == null) {
            return null;
        }

        // Trace back from the goal to the start using parent references
        Node currentPathNode = targetNode;
        while (currentPathNode != null) {
            path.add(currentPathNode.pos);
            currentPathNode = currentPathNode.parent;
        }

        // Reverse the list so the order is from start to goal
        Collections.reverse(path);

        return path;
    }
}