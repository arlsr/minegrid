/**
 * The screen where the game is played.
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class GameScreen extends MineGridScreen {

	private static final long serialVersionUID = 1L;
	private static final ImageIcon HEADER_LOGO = new ImageIcon(
			new ImageIcon("header.png").getImage().getScaledInstance(159, 26, Image.SCALE_SMOOTH));
	private static final String SCREEN_TITLE = "Game";

	private MineGrid game;
	// Mine grid buttons panel.
	private JPanel gridPanel;
	private MineButton[][] cellButtons;
	private JLabel lblFlags = new JLabel();
	private JLabel lblTimer = new JLabel();
	private JLabel lblScore = new JLabel();
	private JButton btnHome = new JButton("home");

	private String playerName = System.getProperty("user.name");

	public GameScreen(MineGridGui gui) {
		super(gui);
	}

	@Override
	public String getTitle() {
		return SCREEN_TITLE;
	}

	/**
	 * Listens for mouse events on grid buttons.
	 */
	private class ButtonListener implements MouseListener {

		private int x;
		private int y;

		public ButtonListener(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		/**
		 * Triggers game events when mouse is released on a grid button.
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			int mouseButton = e.getButton();

			// Left click.
			if (mouseButton == MouseEvent.BUTTON1) {
				game.revealCell(x, y);
			}
			// Right click or any other kind of click.
			else {
				game.flagCell(x, y);
			}
			update();
		}

	}

	/**
	 * Adds a listener for when the home button is clicked.
	 */
	public void addHomeListener(ActionListener al) {
		btnHome.addActionListener(al);
	}

	/**
	 * Creates the screen when the game has been created.
	 */
	public void newGame(MineGrid game) {
		this.game = game;
		this.removeAll();
		create();
	}

	/**
	 * Creates the grid buttons and adds them to the panel.
	 */
	private void createButtons() {
		cellButtons = new MineButton[game.getHeight()][game.getWidth()];

		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				cellButtons[y][x] = new MineButton();
				cellButtons[y][x].addMouseListener(new ButtonListener(x, y));
				gridPanel.add(cellButtons[y][x]);
			}
		}
	}

	/**
	 * Updates the grid buttons to reflect the state of the grid.
	 */
	private void updateButtons() {
		int grid[][] = game.getGrid();
		int state;

		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				state = grid[y][x];
				cellButtons[y][x].setState(state);
			}
		}
	}
	
	/**
	 * Starts a timer to update the stats display.
	 */
	private void startTimer() {
		ActionListener taskPerformer = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				long timeTaken = game.getTimeTaken();
				long minutes = timeTaken / 1000 / 60;
				long seconds = (timeTaken / 1000) - minutes * 60;
				long milliseconds = timeTaken % 1000;
				lblTimer.setText(String.format("Time: %02d:%02d:%02d", minutes, seconds, milliseconds / 10));
				lblFlags.setText(String.format("Mines Flagged: %d/%d",
						game.getTotalMines() - game.getNumFlags(), game.getTotalMines()));
				lblScore.setText(String.format("Score: %d", game.getScore()));
				if (game.getNumFlags() < 0) {
					lblFlags.setForeground(Color.RED);
				}
				else {
					lblFlags.setForeground(Color.BLACK);
				}
			}
		};
		new Timer(100, taskPerformer).start();
		// Perform the timer action once without waiting.
		taskPerformer.actionPerformed(null);
	}

	/**
	 * Creates and adds all the necessary option components to the screen.
	 */
	private void create() {
		JPanel gameDisplayPanel = new JPanel(new BorderLayout());
		JLabel lblHeader = new JLabel(HEADER_LOGO);
		Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
		JPanel centre = new JPanel();

		gridPanel = new JPanel(new GridLayout(game.getHeight(), game.getWidth()));

		gridPanel.setBackground(Color.WHITE);
		centre.setBackground(Color.WHITE);
		gameDisplayPanel.setBackground(Color.WHITE);

		lblFlags.setFont(labelFont);
		lblTimer.setFont(labelFont);
		lblScore.setFont(labelFont);

		createButtons();
		updateButtons();

		btnHome.setMnemonic(KeyEvent.VK_H);

		centre.add(lblFlags);
		centre.add(Box.createHorizontalStrut(20));
		centre.add(lblTimer);
		centre.add(Box.createHorizontalStrut(20));
		centre.add(lblScore);

		gameDisplayPanel.add(lblHeader, BorderLayout.LINE_START);
		gameDisplayPanel.add(btnHome, BorderLayout.LINE_END);
		gameDisplayPanel.add(centre, BorderLayout.CENTER);

		this.add(gameDisplayPanel, BorderLayout.PAGE_START);
		this.add(gridPanel, BorderLayout.CENTER);

		startTimer();
	}

	/**
	 * Updates the GUI to reflect the current state of the game.
	 */
	private void update() {

		updateButtons();
		checkGameState();
	}

	/**
	 * Requests the necessary information and submits the score.
	 */
	private void submitScore() {

		Object response;
		boolean success = false;
	
		response = JOptionPane.showInputDialog(this, "Name:", "Score Submission",
				JOptionPane.QUESTION_MESSAGE, null, null, playerName);
		
		if (response != null && response.toString().length() > 0) {
			playerName = response.toString();
			success = game.submitScore(playerName);
			if (success) {
				// TODO: Don't allow resubmitting the score this game.
				JOptionPane.showMessageDialog(this, "Score submitted!", "Score Submission",
					JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(this, "Score submission failed, sorry.\nTry again later.",
						"Score Submission",	JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Notifies the user of winning and presents options of actions to take.
	 */
	private void showWonMessage() {
		String winMessage = String.format("You won!\nTime taken: %s seconds\nScore: %d",
				game.getTimeTaken() / 1000, game.getScore());
		boolean canSubmitScore = gui.getScoreboard().enabled();
		Object[] options;
		int choice;

		if (canSubmitScore) {
			options = new Object[] { "submit score", "new game", "cancel" };
		}
		else {
			options = new Object[] { "new game", "cancel" };
		}

		choice = JOptionPane.showOptionDialog(null, winMessage, "Game Won",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if (choice == 0 && canSubmitScore) {
			submitScore();
		}
		else if ((choice == 1 && canSubmitScore) || choice == 0) {
			this.gui.newGame();
		}

	}

	/**
	 * Checks if the game is over or in another state.
	 */
	private void checkGameState() {
		if (game.getGameState() == MineGrid.GameState.WON) {
			showWonMessage();
		}
		else if (game.getGameState() == MineGrid.GameState.LOST) {
			Object[] options = { "new game", "cancel" };
			int choice = JOptionPane.showOptionDialog(null, "You lost by detonating a mine!", "Game Over",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (choice == 0) {
				this.gui.newGame();
			}
		}
	}

}
