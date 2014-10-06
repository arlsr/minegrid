/**
 * The screen that enables the user to change game options.
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

public class OptionsScreen extends MineGridScreen {

	private static final long serialVersionUID = 1L;
	private static final String SCREEN_TITLE = "Options"; 
	
	private JSpinner spinWidth = new JSpinner(new SpinnerNumberModel(MineGrid.DEFAULT_WIDTH,
			MineGrid.MIN_WIDTH, MineGrid.MAX_WIDTH, 1));
	private JSpinner spinHeight = new JSpinner(new SpinnerNumberModel(MineGrid.DEFAULT_HEIGHT,
			MineGrid.MIN_HEIGHT, MineGrid.MAX_HEIGHT, 1));
	private JSpinner spinMines = new JSpinner(new SpinnerNumberModel(MineGrid.DEFAULT_MINES,
			MineGrid.MIN_MINES, MineGrid.MAX_MINES, 1));
	private JSpinner spinLives = new JSpinner(new SpinnerNumberModel(MineGrid.DEFAULT_LIVES,
			MineGrid.MIN_LIVES, MineGrid.MAX_LIVES, 1));
	private JSlider sliderMines = new JSlider(MineGrid.MIN_MINES, MineGrid.MAX_MINES, MineGrid.DEFAULT_MINES);
	
	private JButton btnSave = new JButton("save");
	
	/**
	 * Listens for changes to a slider and sets the value to the given spinner.
	 */
	private class SliderListener implements ChangeListener {
		
		private JSpinner spinner;
		
		public SliderListener(JSpinner spinner) {
			this.spinner = spinner;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			this.spinner.setValue(((JSlider)e.getSource()).getValue());
		}
	}

	/**
	 * Listens for changes to a slider and sets the value to the given slider.
	 */
	private class SpinnerListener implements ChangeListener {
		
		private JSlider slider;
		
		public SpinnerListener(JSlider slider) {
			this.slider = slider;
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			int value = (int)((JSpinner)e.getSource()).getValue();
			this.slider.setValue(value);
		}
	}
	
	/**
	 * Listens for changes to the grid dimension fields.
	 */
	private class DimensionChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			setLimits(getSettings());
		}
	}
	
	public OptionsScreen(MineGridGui gui) {
		super(gui);
		create();
	}
	
	@Override
	public String getTitle() {
		return SCREEN_TITLE;
	}
	
	/**
	 * Adds a listener for when the save button is clicked.
	 */
	public void addSaveListener(ActionListener al) {
		btnSave.addActionListener(al);
	}
	
	/**
	 * Fills in the options form with the given settings.
	 * 
	 * @param	settings	the settings values to apply to the options form
	 */
	public void renderSettings(MineGridSettings settings) {
		spinWidth.setValue(settings.getGridWidth());
		spinHeight.setValue(settings.getGridHeight());
		spinMines.setValue(settings.getMines());
		spinLives.setValue(settings.getLives());
		
		setLimits(settings);
	}
	
	/**
	 * Sets the value boundaries of the form components.
	 */
	private void setLimits(MineGridSettings settings) {
		int maxMines = settings.getMaxMines();
		((SpinnerNumberModel)spinMines.getModel()).setMaximum(settings.getMaxMines());
		sliderMines.setMaximum(maxMines);
	}
	
	/**
	 * Returns the current chosen settings from the form.
	 */
	public MineGridSettings getSettings() {
		MineGridSettings settings = new MineGridSettings();
		
		settings.setGridWidth((int)spinWidth.getValue());
		settings.setGridHeight((int)spinHeight.getValue());
		settings.setMines((int)spinMines.getValue());
		settings.setLives((int)spinLives.getValue());
		
		return settings;
	}
	
	/**
	 * Creates and adds all the necessary option components to the screen.
	 */
	private void create() {
		JPanel optionsHeader = new JPanel();
		JPanel optionsBody = new JPanel();
		JPanel panelDifficulty = new JPanel();
		JPanel panelGridSize = new JPanel();
		JPanel panelNumbers = new JPanel();
		
		Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
		Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 36);
		
		JLabel lblHeader = new JLabel(new ImageIcon("header.png"));
		JLabel lblHeading = new JLabel("Options");
		JLabel lblMines = new JLabel("mines");
		JLabel lblLives = new JLabel("lives");
		
		JRadioButton radioEasy = new JRadioButton("easy");
		JRadioButton radioMedium = new JRadioButton("medium");
		JRadioButton radioHard = new JRadioButton("hard");
		ButtonGroup grpDifficuly = new ButtonGroup();

		panelNumbers.setLayout(new BoxLayout(panelNumbers, BoxLayout.PAGE_AXIS));
		panelNumbers.setAlignmentX(CENTER_ALIGNMENT);
		lblMines.setAlignmentX(CENTER_ALIGNMENT);
		lblLives.setAlignmentX(CENTER_ALIGNMENT);
		spinMines.setMaximumSize(new Dimension(75, 100));
		sliderMines.setMaximumSize(new Dimension(300, 100));
		
		spinLives.setMaximumSize(new Dimension(75, 100));
		
		btnSave.setMnemonic(KeyEvent.VK_S);
		btnSave.setAlignmentX(CENTER_ALIGNMENT);
		btnSave.setFont(buttonFont);
		
		lblHeading.setFont(buttonFont);
		
		lblHeader.setAlignmentX(CENTER_ALIGNMENT);
		lblHeading.setAlignmentX(CENTER_ALIGNMENT);
		
		// Options header.
		optionsHeader.setLayout(new BoxLayout(optionsHeader, BoxLayout.PAGE_AXIS));
		optionsHeader.setAlignmentX(CENTER_ALIGNMENT);
		optionsHeader.setBackground(Color.WHITE);
		optionsHeader.add(lblHeader);
		optionsHeader.add(lblHeading);
		
		// Options body.
		optionsBody.setLayout(new BoxLayout(optionsBody, BoxLayout.PAGE_AXIS));
		optionsBody.setBackground(Color.WHITE);
		// Difficulty radio buttons.
		
		radioEasy.setMnemonic(KeyEvent.VK_E);
		radioMedium.setMnemonic(KeyEvent.VK_M);
		radioHard.setMnemonic(KeyEvent.VK_A);
		radioEasy.setFont(labelFont);
		radioMedium.setFont(labelFont);
		radioHard.setFont(labelFont);
		radioEasy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton)e.getSource()).isSelected()) {
					renderSettings(MineGridSettings.createEasySettings());
				}
			}
		});
		radioMedium.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton)e.getSource()).isSelected()) {
					renderSettings(MineGridSettings.createMediumSettings());
				}
			}
		});
		radioHard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton)e.getSource()).isSelected()) {
					renderSettings(MineGridSettings.createHardSettings());
				}
			}
		});
		
		grpDifficuly.add(radioEasy);
		grpDifficuly.add(radioMedium);
		grpDifficuly.add(radioHard);
		panelDifficulty.setBorder(BorderFactory.createTitledBorder("Difficulty Presets"));
		panelDifficulty.add(radioEasy);
		panelDifficulty.add(radioMedium);
		panelDifficulty.add(radioHard);
		// Grid size.
		
		panelGridSize.setBorder(BorderFactory.createTitledBorder("Grid Size"));
		spinWidth.setFont(labelFont);
		spinHeight.setFont(labelFont);
		panelGridSize.add(spinWidth);
		panelGridSize.add(new JLabel("x"));
		panelGridSize.add(spinHeight);
		
		spinWidth.addChangeListener(new DimensionChangeListener());
		spinHeight.addChangeListener(new DimensionChangeListener());
		
		
		panelNumbers.setBorder(BorderFactory.createTitledBorder("Quantities"));
		// Mines.
		
		lblMines.setFont(labelFont);
		lblMines.setLabelFor(spinMines);
		panelNumbers.add(lblMines);
		spinMines.setFont(labelFont);
		panelNumbers.add(sliderMines);
		panelNumbers.add(spinMines);
		panelNumbers.add(Box.createVerticalStrut(10));
		sliderMines.addChangeListener(new SliderListener(spinMines));
		spinMines.addChangeListener(new SpinnerListener(sliderMines));
		
		// Lives.
		lblLives.setFont(labelFont);
		lblLives.setLabelFor(spinLives);
		panelNumbers.add(lblLives);
		
		spinLives.setFont(labelFont);
		panelNumbers.add(spinLives);
		
		radioEasy.setBackground(Color.WHITE);
		radioMedium.setBackground(Color.WHITE);
		radioHard.setBackground(Color.WHITE);
		sliderMines.setBackground(Color.WHITE);
		panelDifficulty.setBackground(Color.WHITE);
		panelGridSize.setBackground(Color.WHITE);
		panelNumbers.setBackground(Color.WHITE);
		
		panelDifficulty.setMaximumSize(new Dimension(800, 500));
		panelGridSize.setMaximumSize(new Dimension(800, 500));
		panelNumbers.setMaximumSize(new Dimension(800, 500));
		
		((TitledBorder)panelDifficulty.getBorder()).setTitleFont(labelFont);
		((TitledBorder)panelGridSize.getBorder()).setTitleFont(labelFont);
		((TitledBorder)panelNumbers.getBorder()).setTitleFont(labelFont);
		
		optionsBody.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));
		
		optionsBody.add(Box.createVerticalGlue());
		optionsBody.add(panelDifficulty);
		optionsBody.add(panelGridSize);
		optionsBody.add(panelNumbers);
		optionsBody.add(Box.createVerticalGlue());
		optionsBody.add(btnSave);
		optionsBody.add(Box.createVerticalGlue());

		this.add(optionsHeader, BorderLayout.PAGE_START);
		this.add(optionsBody, BorderLayout.CENTER);
	}


}
