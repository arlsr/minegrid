/**
 * A custom button to represent each square on the grid.
 * 
 * @author arlsr
 * @date 2014
 */

package tld.minegrid;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

public class MineButton extends JButton {
	private static final long serialVersionUID = 1L;
	private static final Color[] SCORE_COLOURS = {
		Color.BLUE,
		new Color(0, 128, 0),
		Color.RED,
		new Color(0, 0, 128),
		new Color(0, 64, 0),
		new Color(128, 0, 0),
		Color.BLACK,
		Color.MAGENTA
	};
	private static final ImageIcon ICON_EXPLODED = new ImageIcon("explosion.png");
	private static final ImageIcon ICON_FLAG = new ImageIcon("flag.png");
	private static final Font FONT = new Font(Font.SANS_SERIF, Font.BOLD, 12);
	private boolean lockIconRatio = false;
	private boolean scaleDown = false;
	private int lastState = MineGrid.UNKNOWN;
	
	private class MineButtonListener implements ComponentListener {

		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		/**
		 * Responds to the button being resized by adjusting its properties.
		 */
		@Override
		public void componentResized(ComponentEvent e) {
			adjustFont();
			adjustIcon();
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}
		
	}

	/**
	 * Constructs a MineButton.
	 */
	public MineButton() {

		// Add a component listener to monitor for changes in the button's size.
		this.addComponentListener(new MineButtonListener());
		
		// Remove the inside margin to stop the label being truncated on small
		// buttons.
		this.setMargin(new Insets(0, 0, 0, 0));
		
		adjustFont();
	}

	/**
	 * Scales the label font size to the button size.
	 */
	private void adjustFont() {
		int height = this.getHeight();
		// Scale the font size to 63% of the button height.
		// Minimum size is 12, maximum size is the button width.
		int size = (int)Math.min(this.getWidth(), Math.max(12, height * 0.63));

		// Only change the font size if it's different to the current size.
		if (size != this.getFont().getSize()) {
			this.setFont(FONT.deriveFont(Font.BOLD, size));
		}
	}
	
	/**
	 * Scales the button image to the button size.
	 */
	private void adjustIcon() {
		ImageIcon icon = (ImageIcon)this.getIcon();
		Image image;
		
		if (icon != null) {
			image = icon.getImage();
			
			// If the button is bigger than the icon size then scale the icon to fit the button.
			//	or if the button is smaller than the icon.
			if (this.getWidth() > icon.getIconWidth() || this.getHeight() > icon.getIconHeight() ||
					(scaleDown && 
					(icon.getIconWidth() > this.getWidth() || icon.getIconHeight() > this.getHeight()))) {
				
				int newWidth = this.getWidth();
				int newHeight = this.getHeight();
				
				if (lockIconRatio) {
					double ratio = icon.getIconHeight() / icon.getIconWidth();
					newWidth *= 0.5;
					newHeight = (int)(newWidth * ratio); 
				}
				
				icon = new ImageIcon(image.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH));
				this.setIcon(icon);
			}
		}
	}

	/**
	 * Sets the properties of the button to reflect the grid cell state.
	 * 
	 * @param	state	the MineGrid cell state
	 */
	public void setState(int state) {

		if (state != lastState) {
			if (state == MineGrid.UNKNOWN) {
				this.setIcon(null);
			}
			else if (state == MineGrid.FLAG) {
				this.setIcon(ICON_FLAG);
				lockIconRatio = true;
				scaleDown = true;
				adjustIcon();
			}
			else if (state == MineGrid.MINE) {
				this.setIcon(ICON_EXPLODED);
				lockIconRatio = false;
				scaleDown = false;
				adjustIcon();
			}
			else {
				this.setIcon(null);
				if (state > 0) {
					this.setText(Integer.toString(state));
					this.setForeground(SCORE_COLOURS[state - 1]);
					this.setBackground(new Color(238, 238, 238));
				}
				else {
					this.setEnabled(false);
				}
			}
			lastState = state;
		}
	}

}
