import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        MapData mapData = null;

        // Load map data
        try {
            mapData = loadMapData("data/map.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Error: Map file could not be loaded. Exiting game.");
            return; // If the file does not exist, close the program safely
        }

        // safety check
        if (mapData == null) {
            return;
        }

        // create the player based on the starting position defined in the map data.
        Player player = new Player(mapData.getPlayerStart());

        // create enemies based on the starting positions defined in the map data
        Enemy[] enemies = new Enemy[]{new Pinky(mapData.getPinkyStart()), new Inky(mapData.getInkyStart()), new Blinky(mapData.getBlinkyStart())};

        // Initialize the game with the player, enemies, and map data.
        Game game = new Game(player, enemies, mapData);

        // Set up the renderer and draw the initial game state
        GameRenderer renderer = new GameRenderer(mapData, game);
        renderer.setupDraw();
        renderer.drawGame();

        // The main game loop
        while (true) {
            // Continuously take keyboard inputs
            game.processInput();
            // Update game rules, player/enemy movements and collisions
            game.update();

            //animation frames (mouth opening/closing)
            renderer.tickAnimation();

            //render everything to the screen
            renderer.drawGame();

            // pause for 30 milliseconds to maintain a stable frame rate
            StdDraw.pause(30);
        }


    }


    // Method for loading map data from a file. You should not need to modify this method.
    private static MapData loadMapData(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));

        int rows = scanner.nextInt();
        int cols = scanner.nextInt();
        scanner.nextLine();

        String[] playerLine = scanner.nextLine().split(" ");
        Position playerStart = new Position(
                Integer.parseInt(playerLine[1]),
                Integer.parseInt(playerLine[2])
        );

        String[] directEnemyLine = scanner.nextLine().split(" ");
        Position directEnemyStart = new Position(
                Integer.parseInt(directEnemyLine[1]),
                Integer.parseInt(directEnemyLine[2])
        );

        String[] randomChaseEnemyLine = scanner.nextLine().split(" ");
        Position randomChaseEnemyStart = new Position(
                Integer.parseInt(randomChaseEnemyLine[1]),
                Integer.parseInt(randomChaseEnemyLine[2])
        );

        String[] closestCornerEnemyLine = scanner.nextLine().split(" ");
        Position closestCornerEnemyStart = new Position(
                Integer.parseInt(closestCornerEnemyLine[1]),
                Integer.parseInt(closestCornerEnemyLine[2])
        );

        String[] cornerHeader = scanner.nextLine().split(" ");
        int cornerCount = Integer.parseInt(cornerHeader[1]);

        Position[] corners = new Position[cornerCount];
        for (int i = 0; i < cornerCount; i++) {
            int r = scanner.nextInt();
            int c = scanner.nextInt();
            scanner.nextLine();
            corners[i] = new Position(r, c);
        }

        char[][] map = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            String line = scanner.nextLine();
            for (int j = 0; j < cols; j++) {
                map[i][j] = line.charAt(j);
            }
        }

        scanner.close();

        return new MapData(
                map,
                playerStart,
                directEnemyStart,
                randomChaseEnemyStart,
                closestCornerEnemyStart,
                corners
        );
    }

}
