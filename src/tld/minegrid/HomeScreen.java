/**
 * The screen that displays first when the program starts and links to other screens.
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class HomeScreen extends MineGridScreen {

	private static final long serialVersionUID = 1L;
	
	private JButton btnNewGame = new JButton("new game");
	private JButton btnResumeGame = new JButton("resume game");
	private JButton btnOptions = new JButton("options");
	private Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 36);
	private JPanel panelScoreboard = new JPanel();
	
	public HomeScreen(MineGridGui gui) {
		super(gui);
		create();
	}
	
	/**
	 * Adds a listener for when the new game button is clicked.
	 */
	public void addNewGameListener(ActionListener al) {
		btnNewGame.addActionListener(al);
	}
	
	/**
	 * Adds a listener for when the resume game button is clicked.
	 */
	public void addResumeGameListener(ActionListener al) {
		btnResumeGame.addActionListener(al);
	}
	
	/**
	 * Adds a listener for when the options button is clicked.
	 */
	public void addOptionsListener(ActionListener al) {
		btnOptions.addActionListener(al);
	}
	
	/**
	 * Enables the resume game feature.
	 */
	public void enableResume() {
		btnResumeGame.setVisible(true);
	}
	
	private void createScoreboard() {
		JTable tblHighScores;
		JScrollPane scrollPane;
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		
		panelScoreboard.setVisible(false);
		panelScoreboard.removeAll();
		panelScoreboard.setBackground(Color.WHITE);
		
		// Convert to the Vector type required by the table. 
		for (Object[] row : gui.getScoreboard().getScores()) {
			rows.add(new Vector<Object>(Arrays.asList(row)));
		}
		
		tblHighScores = new JTable(rows,
				new Vector<String>(Arrays.asList("#", "Name", "Grid", "Mines", "Lives", "Time", "Score")));

		tblHighScores.setAlignmentX(CENTER_ALIGNMENT);
		tblHighScores.setPreferredScrollableViewportSize(new Dimension(400, 80));
		
		// #
		tblHighScores.getColumnModel().getColumn(0).setPreferredWidth(20);
		tblHighScores.getColumnModel().getColumn(0).setMaxWidth(20);
		// Grid
		tblHighScores.getColumnModel().getColumn(2).setPreferredWidth(50);
		tblHighScores.getColumnModel().getColumn(2).setMaxWidth(50);
		// Mines
		tblHighScores.getColumnModel().getColumn(3).setPreferredWidth(35);
		tblHighScores.getColumnModel().getColumn(3).setMaxWidth(35);
		// Lives
		tblHighScores.getColumnModel().getColumn(4).setPreferredWidth(35);
		tblHighScores.getColumnModel().getColumn(4).setMaxWidth(35);
		// Time
		tblHighScores.getColumnModel().getColumn(5).setPreferredWidth(70);
		tblHighScores.getColumnModel().getColumn(5).setMaxWidth(70);
		// Score
		tblHighScores.getColumnModel().getColumn(6).setPreferredWidth(60);
		tblHighScores.getColumnModel().getColumn(6).setMaxWidth(60);
		
		scrollPane = new JScrollPane(tblHighScores);
		scrollPane.setMaximumSize(new Dimension(400, 200));
		
		panelScoreboard.add(scrollPane);
		panelScoreboard.setVisible(true);
	}
	
	private void addScoreboard(JPanel panel) {
		JPanel panelHeader = new JPanel();
		JLabel lblScoreboard = new JLabel("scoreboard");
		JButton btnRefresh = new JButton(new ImageIcon("refresh.png"));
		
		createScoreboard();
		
		lblScoreboard.setAlignmentX(CENTER_ALIGNMENT);
		lblScoreboard.setFont(buttonFont.deriveFont(Font.PLAIN));
		
		btnRefresh.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createScoreboard();
			}
		});
		
		panelHeader.add(lblScoreboard);
		panelHeader.add(btnRefresh);
		panelHeader.setBackground(Color.WHITE);
		panelHeader.setMaximumSize(new Dimension(1000, 100));
		
		panel.add(Box.createVerticalStrut(20));
		panel.add(panelHeader);
		panel.add(panelScoreboard);
		panel.add(Box.createGlue());
	}
	
	/**
	 * Creates and adds all the necessary option components to the screen.
	 */
	private void create() {
		JLabel lblHeader = new JLabel(new ImageIcon("header.png"));
		JPanel centre = new JPanel();
		
		btnNewGame.setMnemonic(KeyEvent.VK_N);
		btnResumeGame.setMnemonic(KeyEvent.VK_R);
		btnOptions.setMnemonic(KeyEvent.VK_O);
		
		this.setBackground(Color.WHITE);
		this.add(lblHeader, BorderLayout.PAGE_START);
		
		btnNewGame.setAlignmentX(CENTER_ALIGNMENT);	
		btnOptions.setAlignmentX(CENTER_ALIGNMENT);
		btnResumeGame.setAlignmentX(CENTER_ALIGNMENT);
		
		btnNewGame.setFont(buttonFont);
		btnOptions.setFont(buttonFont);
		btnResumeGame.setFont(buttonFont);
		
		btnOptions.setIcon(new ImageIcon("cogs.png"));
		
		// Hide the resume game button until a game has been created.
		btnResumeGame.setVisible(false);
		
		centre.setLayout(new BoxLayout(centre, BoxLayout.PAGE_AXIS));
		
		centre.setBackground(Color.WHITE);
		
		centre.add(Box.createGlue());
		centre.add(Box.createVerticalStrut(20));
		centre.add(btnNewGame);
		centre.add(Box.createVerticalStrut(20));
		centre.add(btnResumeGame);
		centre.add(Box.createVerticalStrut(40));
		centre.add(btnOptions);
		centre.add(Box.createGlue());
		if (gui.getScoreboard().enabled()) {
			addScoreboard(centre);
		}
		this.add(centre, BorderLayout.CENTER);		
	}


}
