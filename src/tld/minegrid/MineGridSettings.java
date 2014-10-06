/**
 * A class to represent game settings for ease of passing between contexts.
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

public class MineGridSettings {
	private int gridWidth = MineGrid.DEFAULT_WIDTH;
	private int gridHeight = MineGrid.DEFAULT_HEIGHT;
	private int mines = MineGrid.DEFAULT_MINES;
	private int lives = MineGrid.DEFAULT_LIVES;
	private Long seed;

	public void setGridSize(int gridWidth, int gridHeight) {
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
	}

	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
	}

	public void setGridHeight(int gridHeight) {
		this.gridHeight = gridHeight;
	}

	public void setMines(int mines) {
		this.mines = mines;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}
	
	public void setSeed(Long seed) {
		this.seed = seed;
	}

	public int getGridWidth() {
		return gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public int getMines() {
		return mines;
	}

	public int getLives() {
		return lives;
	}
	
	public Long getSeed() {
		return seed;
	}
	
	/**
	 * Returns the maximum number of mines allowed for the current settings.
	 */
	public int getMaxMines() {
		return (gridWidth * gridHeight) - 5;
	}

	public static MineGridSettings createHardSettings() {
		MineGridSettings settings = new MineGridSettings();
		settings.setGridWidth(25);
		settings.setGridHeight(25);
		settings.setMines(25 * 25 / 7);
		settings.setLives(1);
		return settings;
	}
	
	public static MineGridSettings createMediumSettings() {
		MineGridSettings settings = new MineGridSettings();
		settings.setGridWidth(15);
		settings.setGridHeight(15);
		settings.setMines(15 * 15 / 8);
		settings.setLives(1);
		return settings;
	}
	
	public static MineGridSettings createEasySettings() {
		MineGridSettings settings = new MineGridSettings();
		settings.setGridWidth(8);
		settings.setGridHeight(8);
		settings.setMines(8 * 8 / 9);
		settings.setLives(1);
		return settings;
	}
}
