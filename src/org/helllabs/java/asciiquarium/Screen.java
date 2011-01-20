package org.helllabs.java.asciiquarium;


public class Screen {
	int columns;
	int rows;
	char[] text;
	char[] color;
	
	public Screen(int columns, int rows) {
		//Log.d("Asciiquarium", "Screen constructor, size " + columns + "x" + rows);
		
		this.columns = columns;
		this.rows = rows;
		
		text = new char[columns * rows];
		color = new char[columns * rows];
	}
	
	public void clear() {
		int size = columns * rows;
		for (int i = 0; i < size; i++) {
			text[i] = ' ';
			color[i] = ' ';
		}
	}
}
