/**
 * The main game class that handles game logic. 
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

import java.util.ArrayList;
import java.util.Random;

public class MineGrid {
	
	public static enum GameState {
		PRESTART,
		STARTED,
		WON,
		LOST
	}
	private class Cell {
		private int x;
		private int y;
		private boolean mine = false;
		private boolean flagged = false;
		private boolean revealed = false;
		private int score;

		public Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public ArrayList<Cell> getNeighbours() {
			ArrayList<Cell> neighbours = new ArrayList<Cell>();

			// For every possible surrounding coordinate.
			for (int y = this.y - 1; y < this.y + 2; y++) {
				for (int x = this.x - 1; x < this.x + 2; x++) {
					// If the coordinate is valid, add the cell as a neighbour.
					if (y >= 0 && y < gridHeight && x >= 0 && x < gridWidth && (x != this.x || y != this.y)) {
						neighbours.add(grid[y][x]);
					}
				}
			}
			return neighbours;
		}

		public int getState() {
			int state = UNKNOWN;
			if (revealed) {
				if (mine) {
					state = MINE;
				}
				else {
					state = score;
				}
			}
			else if (flagged) {
				return FLAG;
			}
			return state;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void incScore() {
			score++;
		}

		public void plantMine() {
			mine = true;
		}

		public int reveal() {
			revealed = true;
			return getState();
		}

		/**
		 * Toggles whether the cell has a flag or not.
		 * 
		 * @return	whether cell has a flag
		 */
		public boolean toggleFlag() {
			// Only allow flag operations on unknown cells.
			if (!revealed) {
				flagged = !flagged;
			}
			return flagged;
		}
	}
	public static final int MIN_WIDTH = 3;
	public static final int MIN_HEIGHT = 3;
	public static final int MIN_MINES = MIN_WIDTH * MIN_HEIGHT - 5;
	public static final int MIN_LIVES = 1;
	public static final int MAX_WIDTH = 50;
	public static final int MAX_HEIGHT = 50;
	public static final int MAX_MINES = MAX_WIDTH * MAX_HEIGHT - 5;
	public static final int MAX_LIVES = 10;
	public static final int DEFAULT_WIDTH = 10;
	public static final int DEFAULT_HEIGHT = 10;
	
	public static final int DEFAULT_MINES = 7;
	public static final int DEFAULT_LIVES = 1;
	public static final int UNKNOWN = -1;
	
	public static final int MINE = -2;;

	public static final int FLAG = -3;
	private GameState gameState;
	private long startTime;
	private long endTime;
	private int gridWidth;
	private int gridHeight;
	private int numMines;
	private int numFlags;
	private int lives;
	private int totalLives;
	// The number of possible mine cells remaining.
	private int possibles;
	// The random seed.
	private Long seed;
	private Cell[][] grid;
	private MineGridSettings settings = new MineGridSettings();

	private Scoreboard scoreboard;;

	public MineGrid(int gridWidth, int gridHeight, int numMines, int numLives) {
		setup(gridWidth, gridHeight, numMines, numLives, null);
	}

	/**
	 * Constructs an instance using the given game settings.
	 * 
	 * @param	settings	game settings to use for this game
	 */
	public MineGrid(MineGridSettings settings) {
		setup(settings.getGridWidth(), settings.getGridHeight(), settings.getMines(), settings.getLives(),
				settings.getSeed());
	}

	/**
	 * Reacts to a request to toggle a flag at the target cell coordinates.
	 * 
	 * @param x
	 *            the x coordinate of the cell
	 * @param y
	 *            the y coordinate of the cell
	 * @return whether flag is now present
	 */
	public boolean flagCell(int x, int y) {
		boolean flagPlanted;
		int previousCellState = grid[y][x].getState();
		
		playerActed();
		
		flagPlanted = grid[y][x].toggleFlag();
		if (flagPlanted) {
			numFlags--;
		}
		// A flag was uprooted so return it to the inventory.
		else if (previousCellState == FLAG) {
			numFlags++;
		}
		
		updateGameState();
		
		return flagPlanted;
	}
	
	/**
	 * Returns the current game state for determining whether it has begun or finished.
	 */
	public GameState getGameState() {
		return gameState;
	}
	
	/**
	 * Returns the state of each cell in the grid. 
	 */
	public int[][] getGrid() {

		int[][] stateGrid = new int[gridHeight][gridWidth];

		for (int y = 0; y < gridHeight; y++) {
			for (int x = 0; x < gridWidth; x++) {
				stateGrid[y][x] = grid[y][x].getState();
			}
		}

		return stateGrid;
	}
	
	/**
	 * Returns the number of cells that make up the height of the grid.
	 */
	public int getHeight() {
		return gridHeight;
	}
	
	/**
	 * Returns the number of lives remaining.
	 */
	public int getLives() {
		return lives;
	}

	/**
	 * Returns the number of flags remaining.
	 */
	public int getNumFlags() {
		return numFlags;
	}

	/**
	 * Calculates and returns the player's current score.
	 */
	public int getScore() {
		double mineRatio = (double)numMines / (gridWidth * gridHeight);
		double baseScore = (numMines * Math.min(gridWidth, gridHeight) * mineRatio) / totalLives;
		double timeSec = getTimeTaken()/1000.0;
		double timeBonus = Math.max(0, (baseScore * (4.0 - (timeSec / baseScore))));
		
		int score = Math.max(0, (int)((baseScore + timeBonus) * 10));
		if (gameState == GameState.LOST) {
			score = 0;
		}
		return score;
	}

	/**
	 * Returns the time taken playing the current game.
	 */
	public long getTimeTaken() {
		long timeTaken;
		if (gameState == GameState.STARTED) {
			timeTaken = System.currentTimeMillis() - startTime;
		}
		else if (gameState == GameState.WON || gameState == GameState.LOST) {
			timeTaken = endTime - startTime;
		}
		else {
			timeTaken = 0;
		}
		return timeTaken;
	}

	/**
	 * Returns the number of mines in total.
	 */
	public int getTotalMines() {
		return numMines;
	}
	
	/**
	 * Returns the number of cells that make up the width of the grid.
	 */
	public int getWidth() {
		return gridWidth;
	}
	
	/**
	 * Reacts to a request to reveal a cell at the given coordinates.
	 * 
	 * @param x
	 *            the x coordinate of the cell
	 * @param y
	 *            the y coordinate of the cell
	 * @return	the cell state of the revealed cell
	 */
	public int revealCell(int x, int y) {
		Cell targetCell = grid[y][x];
		int cellState = targetCell.getState();
		
		playerActed();
		
		if (cellState == UNKNOWN) {
			cellState = targetCell.reveal();
			if (cellState != MINE) {
				possibles--;
			}
		}

		// Reveal surrounding cells when zero cell is found.
		if (cellState == 0) {
			for (Cell neighbour : targetCell.getNeighbours()) {
				if (neighbour.getState() == UNKNOWN) {
					revealCell(neighbour.getX(), neighbour.getY());
				}
			}
		}
		else if (cellState == MINE) {
			lives--;
		}
		
		updateGameState();
		return cellState;
	}
	
	/**
	 * Sets the scoreboard system to use.
	 * 
	 * @param	scoreboard	Scoreboard instance to use
	 */
	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}
	
	/**
	 * Submits the game score to the high-score database.
	 */
	public boolean submitScore(String name) {
		
		boolean submitted = false;
		
		if (scoreboard.enabled() && gameState == GameState.WON) { 
		
			try {
				submitted = scoreboard.insertScore(name, this.getScore(), this.getTimeTaken(),
					gridWidth, gridHeight, numMines, totalLives);
			}
			catch (java.sql.SQLException e) {
				submitted = false;
			}
				
		}
		return submitted;
	}
	
	private void createGrid() {

		grid = new Cell[gridHeight][gridWidth];

		for (int y = 0; y < gridHeight; y++) {

			for (int x = 0; x < gridWidth; x++) {
				grid[y][x] = new Cell(x, y);
			}
		}
	}
	
	/**
	 * Randomly distributes the set number of mines over the grid.
	 */
	private void plantMines() {

		ArrayList<Cell> cells = new ArrayList<Cell>();
		Random rand = new Random();
		int randIndex;
		Cell mineCell;
		
		// Force random to use the given seed if set.
		if (seed != null) {
			rand.setSeed(seed.longValue());
		}

		// Add all the cells to an ArrayList so mine locations be chosen
		// randomly without duplicates.
		for (int y = 0; y < gridHeight; y++) {
			for (int x = 0; x < gridWidth; x++) {
				cells.add(grid[y][x]);
			}
		}

		// Plant the mines in random cells.
		for (int i = 0; i < numMines; i++) {
			randIndex = rand.nextInt(cells.size());
			mineCell = cells.remove(randIndex);
			mineCell.plantMine();
			for (Cell neighbour : mineCell.getNeighbours()) {
				neighbour.incScore();
			}
		}

	}
	
	/**
	 * Called when the player performs a game action.
	 */
	private void playerActed() {
		
		// Start the timer on the player's first action.
		if (gameState == GameState.PRESTART) {
			gameState = GameState.STARTED;
			startTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * Reveal every cell in the grid.
	 */
	private void revealAll() {
		for (int y = 0; y < gridHeight; y++) {
			for (int x = 0; x < gridWidth; x++) {
				grid[y][x].reveal();
			}
		}
	}
	
	private void setup(int gridWidth, int gridHeight, int numMines, int numLives, Long seed) {
		
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.numMines = numMines;
		this.totalLives = this.lives = numLives;
		// Random seed.
		this.seed = seed;
		numFlags = numMines;
		gameState = GameState.PRESTART;
		possibles = gridWidth * gridHeight;
		// Update the settings object to the current settings.
		settings.setGridWidth(gridWidth);
		settings.setGridHeight(gridHeight);
		settings.setMines(numMines);
		settings.setLives(numLives);

		createGrid();
		plantMines();
	}
	
	/**
	 * Determines if the game has been won or lost.
	 */
	private void updateGameState() {
		
		if (gameState == GameState.STARTED) {
			// Game is lost if all lives have been used.
			if (lives <= 0) {
				gameState = GameState.LOST;
				endTime = System.currentTimeMillis();
				revealAll();
			}
			// Game is won if all non-mine cells are revealed without losing.
			else if (possibles == numMines) {
				gameState = GameState.WON;
				endTime = System.currentTimeMillis();
				revealAll();
			}
		}
	}

}
