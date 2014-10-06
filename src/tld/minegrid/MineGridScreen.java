/**
 * Represents a screen in the program, such as the home, options or game screens.
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

import java.awt.BorderLayout;

import javax.swing.*;

public abstract class MineGridScreen extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String SCREEN_TITLE = null;

	protected MineGridGui gui;
	
	/**
	 * Constructs a MineGridScreen.
	 * 
	 * @param	gui	the parent MineGridGui instance
	 */
	public MineGridScreen(MineGridGui gui) {
		super(new BorderLayout());
		this.gui = gui;
	}
	
	/**
	 * Switches to this screen.
	 */
	public void switchTo() {
		this.setVisible(true);
		gui.setSubTitle(this.getTitle());
	}
	
	/**
	 * Lets this screen know to switch away.
	 */
	public void switchAway() {
		this.setVisible(false);
	}
	
	/**
	 * Returns the title of the screen.
	 */
	public String getTitle() {
		return SCREEN_TITLE;
	}
	
}
