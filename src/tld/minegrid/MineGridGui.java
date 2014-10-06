/**
 * The parent GUI class that creates the main window and switches between screens.
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MineGridGui extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static final String TITLE = "MineGrid";

	private MineGrid game;
	private MineGridSettings settings = new MineGridSettings();
	private Scoreboard scoreboard = new Scoreboard();
	
	// The screen currently being displayed. 
	private MineGridScreen activeScreen;
	// The different screens for the program.
	private HomeScreen homeScreen = new HomeScreen(this);
	private GameScreen gameScreen = new GameScreen(this);
	private OptionsScreen optionsScreen = new OptionsScreen(this);
	
	/**
	 * Returns the MineGrid game instance.
	 */
	public MineGrid getGame() {
		return game;
	}
	
	/**
	 * Returns the Scoreboard instance.
	 */
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	/**
	 * Creates a new game and switches to it.
	 */
	public void newGame() {
		
		game = new MineGrid(settings);
		game.setScoreboard(scoreboard);
		
		gameScreen.newGame(game);
		switchScreen(gameScreen);
		// Enable the resume button now a game has been created.
		homeScreen.enableResume();
	}
	
	/**
	 * Switches from the current screen to the given screen.
	 * 
	 * @param	newScreen	the screen to display
	 */
	private void switchScreen(MineGridScreen newScreen) {
		if (activeScreen != null) {
			activeScreen.switchAway();
			this.remove(activeScreen);
		}
		this.add(newScreen);
		newScreen.switchTo();
		activeScreen = newScreen;
	}
	
	/**
	 * Sets up the different screens for the program.
	 */
	private void setupScreens() {
		
		homeScreen.addNewGameListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		
		homeScreen.addOptionsListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchScreen(optionsScreen);
				optionsScreen.renderSettings(settings);
			}
		});
		
		homeScreen.addResumeGameListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchScreen(gameScreen);
			}
		});
		
		gameScreen.addHomeListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchScreen(homeScreen);
			}
		});
		
		optionsScreen.addSaveListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				settings = optionsScreen.getSettings();
				switchScreen(homeScreen);
			}
		});
	}
	
	/**
	 * Sets the subtitle part of the window title.
	 */
	public void setSubTitle(String subTitle) {
		if (subTitle != null) {
			this.setTitle(TITLE + " - " + subTitle);
		}
		else {
			this.setTitle(TITLE);
		}
	}

	/**
	 * Constructs a MineGridGui, creating the main window.
	 */
	public MineGridGui() {
		super();

		this.setTitle(TITLE);
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Centre the window.
		this.setLocationRelativeTo(null);
		// Set the window icon.
		this.setIconImage(new ImageIcon("flag.png").getImage());
		
		setupScreens();
		switchScreen(homeScreen);
		
		this.setVisible(true);
	}
}
