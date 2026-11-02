import java.awt.event.KeyEvent;

public class Game {

    public enum GameState {
        START_SCREEN,
        READY,
        PLAYING,
        PAUSED,
        WON,
        LOST,
        BACKTRACKING
    }

    public enum Direction {
        UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1), NONE(0, 0);

        private final int dRow;
        private final int dCol;

        Direction(int dRow, int dCol) {
            this.dRow = dRow;
            this.dCol = dCol;
        }

        public int getDRow() { return dRow; }
        public int getDCol() { return dCol; }
    }

    private final Player player;
    private final Enemy[] enemies;
    private GameState gameState;
    private final MapData mapData;
    private boolean pFlag = false;
    private boolean spaceFlag = false;
    // Stores the history of game states for the rewind feature
    private final Stack<GameSnapshot> historyStack = new Stack<>();


    public Game(Player player, Enemy[] enemies, MapData mapData) {
        this.player = player;
        this.enemies = enemies;
        this.mapData = mapData;
        this.gameState = GameState.START_SCREEN;
    }

    // handles keyboard inputs
    public void processInput() {
        if (StdDraw.isKeyPressed(KeyEvent.VK_Q)) System.exit(0);

        if (!StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) spaceFlag = false;

        if (gameState == GameState.START_SCREEN) {
            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) && !spaceFlag) {
                spaceFlag = true;
                gameState = GameState.READY;
            }
        }
        else if (gameState == GameState.READY) {
            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) && !spaceFlag) {
                spaceFlag = true;
                gameState = GameState.PLAYING;
                historyStack.push(new GameSnapshot(player, enemies, player.getScore(), false, -1, -1));
            }
        }
        else if (gameState == GameState.PLAYING) {
            if (!StdDraw.isKeyPressed(KeyEvent.VK_P)) pFlag = false;
            // pause the game
            if (StdDraw.isKeyPressed(KeyEvent.VK_P) && !pFlag) {
                pFlag = true;
                gameState = GameState.PAUSED;}
            // player movements
            else if (StdDraw.isKeyPressed(KeyEvent.VK_UP) || StdDraw.isKeyPressed(KeyEvent.VK_W)) player.setRequestedDirection(Direction.UP);
            else if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN) || StdDraw.isKeyPressed(KeyEvent.VK_S)) player.setRequestedDirection(Direction.DOWN);
            else if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT) || StdDraw.isKeyPressed(KeyEvent.VK_A)) player.setRequestedDirection(Direction.LEFT);
            else if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT) || StdDraw.isKeyPressed(KeyEvent.VK_D)) player.setRequestedDirection(Direction.RIGHT);
        }
        else if (gameState == GameState.PAUSED) {
            if (!StdDraw.isKeyPressed(KeyEvent.VK_P)) pFlag = false;
            if (StdDraw.isKeyPressed(KeyEvent.VK_P) && !pFlag) {
                pFlag = true;
                gameState = GameState.PLAYING;
            }
        }

        // R key triggers BACKTRACKING only in WON or LOST states
        else if (gameState == GameState.WON || gameState == GameState.LOST) {
            if (StdDraw.isKeyPressed(KeyEvent.VK_R)) {
                gameState = GameState.BACKTRACKING;
            }
        }
    }

    public void update() {
        if (gameState == GameState.PLAYING) {

            // Store logical positions before movement to detect changes
            Position oldPlayerPos = player.getPos();
            Position[] oldEnemyPos = new Position[enemies.length];
            for (int i = 0; i < enemies.length; i++) oldEnemyPos[i] = enemies[i].getPos();

            // 1. Move the Player (Pac-Man)
            player.updateMovement(mapData);

            // 2. Check Pellet Consumption
            boolean pelletEaten = false;
            int eatenRow = -1, eatenCol = -1;

            if (mapData.hasPellet(player.getPos().getRow(), player.getPos().getCol())) {
                pelletEaten = true;
                eatenRow = player.getPos().getRow();
                eatenCol = player.getPos().getCol();
                mapData.removePellet(eatenRow, eatenCol);
                player.addScore(10);
            }

            // 3. Move all Enemies (Ghosts)
            for (Enemy enemy : enemies) {
                enemy.move(player, mapData);
            }

            //Did a logical move or pellet consumption occur?
            boolean logicalMove = pelletEaten || !player.getPos().equals(oldPlayerPos);
            for (int i = 0; i < enemies.length; i++) {
                if (!enemies[i].getPos().equals(oldEnemyPos[i])) logicalMove = true;
            }

            // If there was a logical move, save the state to the stack
            if (logicalMove) {
                historyStack.push(new GameSnapshot(player, enemies, player.getScore(), pelletEaten, eatenRow, eatenCol));
            }

            checkEnemyCollision();
            checkWinCondition();
        }
        // BACKTRACKING state
        else if (gameState == GameState.BACKTRACKING) {
            if (!historyStack.isEmpty()) {
                // Pop a snapshot every tick for a fast rewind effect
                GameSnapshot past = historyStack.pop();

                // Restore player and teleport to grid position (disable sliding)
                player.setPos(past.playerPos);
                player.setCurrentDirection(past.playerDir);
                player.setVisualRow(past.playerPos.getRow());
                player.setVisualCol(past.playerPos.getCol());
                player.setScore(past.score);

                // Restore enemies and teleport to grid positions
                for (int i = 0; i < enemies.length; i++) {
                    enemies[i].setPos(past.enemyPos[i]);
                    enemies[i].setDirection(past.enemyDir[i]);
                    enemies[i].setVisualRow(past.enemyPos[i].getRow());
                    enemies[i].setVisualCol(past.enemyPos[i].getCol());
                }

                // If a pellet was eaten in this frame, restore it
                if (past.pelletEaten) {
                    mapData.restorePellet(past.eatenRow, past.eatenCol);
                }
            } else {
                // If stack is empty (reached the beginning), set game to START_SCREEN
                gameState = GameState.START_SCREEN;
            }
        }
    }

    // check collisions between Pac-Man and any Ghost
    private void checkEnemyCollision() {
        double hitRadius = 0.5; // Threshold for collision detection

        for (Enemy enemy : enemies) {
            // Calculate absolute distance between Visual Coordinates for smooth collision
            double dRow = Math.abs(player.getVisualRow() - enemy.getVisualRow());
            double dCol = Math.abs(player.getVisualCol() - enemy.getVisualCol());

            // If the visual centers of the entities overlap within the radius, the player dies
            if (dRow < hitRadius && dCol < hitRadius) {
                gameState = GameState.LOST;
                break;
            }
        }
    }

    // if the player has won the game
    private void checkWinCondition() {
        boolean noPelletsLeft = true;

        // Scan the entire map
        for (int r = 0; r < mapData.getRows(); r++) {
            for (int c = 0; c < mapData.getCols(); c++) {
                // If even one pellet is found, the game is not won yet
                if (mapData.hasPellet(r, c)) {
                    noPelletsLeft = false;
                    break;
                }
            }
            if (!noPelletsLeft) break; // Optimization: Stop checking if we found a pellet
        }

        // If the entire map scan found zero pellets, the player wins
        if (noPelletsLeft) {
            gameState = GameState.WON;
        }
    }

    // A helper class to store the exact positions of all entities at a given frame
    private static class GameSnapshot {
        public Position playerPos;
        public Direction playerDir;

        public Position[] enemyPos;
        public Direction[] enemyDir;

        public int score;
        public boolean pelletEaten;
        public int eatenRow, eatenCol;

        public GameSnapshot(Player p, Enemy[] enemies, int score, boolean pelletEaten, int r, int c) {
            this.playerPos = p.getPos();
            this.playerDir = p.getCurrentDirection();

            this.enemyPos = new Position[enemies.length];
            this.enemyDir = new Direction[enemies.length];
            for (int i = 0; i < enemies.length; i++) {
                this.enemyPos[i] = enemies[i].getPos();
                this.enemyDir[i] = enemies[i].getDirection();
            }

            this.score = score;
            this.pelletEaten = pelletEaten;
            this.eatenRow = r;
            this.eatenCol = c;
        }
    }

    public Player getPlayer() {
        return player;
    }
    public Enemy[] getEnemies() {
        return enemies;
    }
    public GameState getGameState() {
        return gameState;
    }

    // Allows internal methods to change the game state (to WON or LOST)
    public void setGameState(GameState state) { this.gameState = state; }
    public MapData getMapData() { return mapData; }


}