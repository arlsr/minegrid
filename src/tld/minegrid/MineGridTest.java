/**
 * Automated JUnit tests against the game.
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MineGridTest {
	
	// Fixed random seed to ensure test consistency.
	private static final Long RANDOM_SEED = 1L;
	private MineGrid mg;
	private MineGridSettings settings;
	
	@Before
	public void setUp() throws Exception {
		settings = new MineGridSettings();
		settings.setSeed(RANDOM_SEED);
		mg = new MineGrid(settings);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests creating a MineGrid instance succeeds with a range of values.
	 */
	@Test
	public void testConstruction() {

		new MineGrid(100, 100, 4, 1);
		new MineGrid(100, 100, 1000-5, 1);

		for (int y = MineGrid.MIN_HEIGHT; y <= MineGrid.MAX_HEIGHT; y++) {
			for (int x = MineGrid.MIN_WIDTH; x <= MineGrid.MAX_WIDTH; x++) {
				new MineGrid(x, y, x * y - 5, 1);
			}
		}
	}

	/**
	 * Tests the correct number of mines are planted.
	 */
	@Test
	public void testMinesPlanted() {
		final int mines = 27;
		MineGrid mg = new MineGrid(10, 10, mines, 1);
		int minesFound = 0;
		int[][] grid;
		
		// Reveal all the cells.
		for (int y = 0; y < mg.getHeight(); y++) {
			for (int x = 0; x < mg.getWidth(); x++) {
				mg.revealCell(x, y);
			}
		}
		
		// Count the mines.
		grid = mg.getGrid();
		for (int y = 0; y < mg.getHeight(); y++) {
			for (int x = 0; x < mg.getWidth(); x++) {
				if (grid[y][x] == MineGrid.MINE) {
					minesFound++;
				}
			}
		}
		assertEquals(mines, minesFound);
	}

	/**
	 * Tests planting a flag changes the grid state correctly.
	 */
	@Test
	public void testFlag() {
		final int x = 3;
		final int y = 4;

		assertEquals(MineGrid.UNKNOWN, mg.getGrid()[y][x]);
		// Toggle flag on.
		mg.flagCell(x, y);
		assertEquals(MineGrid.FLAG, mg.getGrid()[y][x]);
		// Toggle flag off.
		mg.flagCell(x, y);
		assertEquals(MineGrid.UNKNOWN, mg.getGrid()[y][x]);
	}
	
	/**
	 * Tests planting a flag changes the flag count correctly.
	 */
	@Test
	public void testFlagCount() {
		int flagCount = mg.getNumFlags();
		final int x = 3;
		final int y = 4;

		// Check there's the same number of flags available as mines.
		assertEquals(flagCount, mg.getTotalMines());
		assertEquals(MineGrid.UNKNOWN, mg.getGrid()[y][x]);
		// Toggle flag on.
		mg.flagCell(x, y);
		// Check flag count decreased.
		assertEquals(flagCount - 1, mg.getNumFlags());
		// Toggle flag off.
		mg.flagCell(x, y);
		// Check flag count restored.
		assertEquals(flagCount, mg.getNumFlags());
	}
	
	/**
	 * Tests planting multiple flags changes the flag count correctly.
	 */
	@Test
	public void testFlagCounts() {
		int flagCount = mg.getNumFlags();
		
		// Plant flags on all the cells.
		for (int y = 0; y < mg.getHeight(); y++) {
			for (int x = 0; x < mg.getWidth(); x++) {
				assertEquals(flagCount, mg.getNumFlags());
				assertTrue(mg.flagCell(x, y));
				flagCount--;
				assertEquals(flagCount, mg.getNumFlags());
			}
		}
		// Un-plant all the flags.
		for (int y = 0; y < mg.getHeight(); y++) {
			for (int x = 0; x < mg.getWidth(); x++) {
				assertEquals(flagCount, mg.getNumFlags());
				assertFalse(mg.flagCell(x, y));
				flagCount++;
				assertEquals(flagCount, mg.getNumFlags());
			}
		}
	}
	
	/**
	 * Tests flags can't be planted on revealed cells.
	 */
	@Test
	public void testFlagPlantRevealed() {
		
		// '1' cell.
		
		int flags = mg.getNumFlags();
		mg.revealCell(1, 0);
		assertFalse(mg.flagCell(1, 0));
		// Ensure the number of flags hasn't changed.
		assertEquals(flags, mg.getNumFlags());
		
		// Empty cell.
		
		flags = mg.getNumFlags();
		mg.revealCell(2, 0);
		assertFalse(mg.flagCell(2, 0));
		// Ensure the number of flags hasn't changed.
		assertEquals(flags, mg.getNumFlags());
		
		// Mine cell.
		
		flags = mg.getNumFlags();
		mg.revealCell(0, 0);
		assertFalse(mg.flagCell(0, 0));
		// Ensure the number of flags hasn't changed.
		assertEquals(flags, mg.getNumFlags());
	}

	/**
	 * Tests revealing a mine cell changes the grid state correctly.
	 */
	@Test
	public void testRevealMine() {
		final int x = 0;
		final int y = 0;
		assertEquals(MineGrid.UNKNOWN, mg.getGrid()[y][x]);
		mg.revealCell(x, y);
		assertEquals(MineGrid.MINE, mg.getGrid()[y][x]);
	}
	
	/**
	 * Tests revealing a safe cell changes the grid state correctly.
	 */
	@Test
	public void testRevealSafe() {
		final int x = 1;
		final int y = 0;
		assertEquals(MineGrid.UNKNOWN, mg.getGrid()[y][x]);
		mg.revealCell(x, y);
		assertEquals(1, mg.getGrid()[y][x]);
	}

	/**
	 * Tests getters respond with expected properties.
	 */
	@Test
	public void testProperties() {
		MineGrid mg = new MineGrid(20, 10, 50, 1);

		assertEquals(20, mg.getWidth());
		assertEquals(10, mg.getHeight());

		assertEquals(10, mg.getGrid().length);
		assertEquals(20, mg.getGrid()[0].length);

		assertEquals(50, mg.getTotalMines());
		assertEquals(0, new MineGrid(7, 9, 0, 1).getTotalMines());
		assertEquals(1, new MineGrid(9, 7, 1, 1).getTotalMines());
	}

	/**
	 * Tests multiple game instances can run without interference.
	 */
	@Test
	public void testInstances() {

		MineGrid mg1 = new MineGrid(3, 4, 5, 1);
		MineGrid mg2 = new MineGrid(5, 6, 20, 1);

		assertEquals(MineGrid.UNKNOWN, mg1.getGrid()[2][1]);
		assertEquals(MineGrid.UNKNOWN, mg2.getGrid()[2][1]);

		mg1.revealCell(1, 2);

		assertTrue(mg1.getGrid()[2][1] != MineGrid.UNKNOWN);
		assertEquals(MineGrid.UNKNOWN, mg2.getGrid()[2][1]);

		assertEquals(5, mg1.getTotalMines());
		assertEquals(20, mg2.getTotalMines());

		assertEquals(3, mg1.getWidth());
		assertEquals(5, mg2.getWidth());
	}

	/**
	 * Tests the game state has expected values from construction to after the
	 * first player action.
	 */
	@Test
	public void testGameState() {

		MineGrid mg = new MineGrid(10, 10, 20, 1);
		assertEquals(MineGrid.GameState.PRESTART, mg.getGameState());
		mg.flagCell(1, 1);
		assertEquals(MineGrid.GameState.STARTED, mg.getGameState());
	}

	/**
	 * Tests winning a game changes the state correctly.
	 */
	@Test
	public void testWinning() {
		assertEquals(MineGrid.GameState.PRESTART, mg.getGameState());
		mg.revealCell(2, 0);
		mg.revealCell(0, 1);
		mg.revealCell(0, 2);
		mg.revealCell(0, 4);
		mg.revealCell(1, 4);
		mg.revealCell(0, 5);
		mg.revealCell(5, 7);
		mg.revealCell(5, 9);
		mg.revealCell(9, 2);
		assertEquals(MineGrid.GameState.WON, mg.getGameState());
	}

	/**
	 * Tests losing a game changes the state correctly.
	 */
	@Test
	public void testLosing() {
		assertEquals(MineGrid.GameState.PRESTART, mg.getGameState());
		mg.revealCell(0, 0);
		assertEquals(MineGrid.GameState.LOST, mg.getGameState());
	}

	/**
	 * Tests play time is recorded correctly.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testPlayTime() throws InterruptedException {
		// Game hasn't started yet so time should be zero.
		assertEquals(0, mg.getTimeTaken());
		// Performing the first action should start the game timer.
		mg.flagCell(1, 1);
		assertEquals(MineGrid.GameState.STARTED, mg.getGameState());
		Thread.sleep(200);
		// The game time should have increased by 200ms.
		assertEquals(200, mg.getTimeTaken(), 50);
	}

	/**
	 * Tests losing lives.
	 */
	@Test
	public void testLives() {
		final int lives = 2;
		
		settings.setLives(lives);
		mg = new MineGrid(settings);
		
		assertEquals(lives, mg.getLives());
		assertEquals(MineGrid.MINE, mg.revealCell(0, 0));
		assertEquals(lives - 1, mg.getLives());
	}
	
	/**
	 * Tests score is 0 if game is lost.
	 */
	@Test
	public void testScoreAfterLost() {
		
		assertEquals(MineGrid.MINE, mg.revealCell(0, 0));
		assertEquals(MineGrid.GameState.LOST, mg.getGameState());
		assertEquals(0, mg.getScore());
	}


}
