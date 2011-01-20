package org.helllabs.java.asciiquarium;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


public class TerminalBlitter {
	int columns;
	int rows;
	int cellWidth;
	int cellHeight;
	Font font;
	static final Color[]colors = new Color[] {
		new Color(0x7f0000),	// r
		new Color(0x007f00),	// g
		new Color(0x7f7f00),	// y
		new Color(0x00007f),	// b
		new Color(0x7f007f),	// m
		new Color(0x007f7f),	// c
		new Color(0x7f7f7f),	// w
		new Color(0xff0000),	// R
		new Color(0x00ff00),	// G
		new Color(0xffff00),	// Y
		new Color(0x0000ff),	// B
		new Color(0xff00ff),	// M
		new Color(0x00ffff),	// C
		new Color(0xffffff)		// W
	};
	
	public TerminalBlitter(int columns, int rows, int cellWidth, int cellHeight) {
		//Log.i("Asciiquarium", "TerminalBlitter constructor: " + columns + "x" + rows + " (" + cellWidth + "x" + cellHeight + ")");
		this.columns = columns;
		this.rows = rows;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		
		font = new Font("Monospaced", Font.PLAIN, cellHeight);
	}


	public void setChar(Graphics graphics, int column, int row, char c, char color) {
		int n;
		String ca = Character.toString(c);
		
		switch (color) {
			case 'r': n = 0; break; case 'R': n = 7; break;
			case 'g': n = 1; break; case 'G': n = 8; break;
			case 'y': n = 2; break; case 'Y': n = 9; break;
			case 'b': n = 3; break; case 'B': n = 10; break;
			case 'm': n = 4; break; case 'M': n = 11; break;
			case 'c': n = 5; break; case 'C': n = 12; break;
			case 'w': n = 6; break; case 'W': n = 13; break;
			default: n = 6;
		}
		 
		int x = column * cellWidth;
		int y = row * cellHeight;
		
		graphics.setColor(colors[n]);

		if (graphics != null) {
			graphics.drawString(ca, x, y);
		}
	}
}
