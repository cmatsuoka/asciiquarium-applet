package org.helllabs.java.asciiquarium;

import org.helllabs.java.asciiquarium.Asciiquarium.Renderer;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;


public class MyApplet extends Applet implements Runnable {
	private static final long serialVersionUID = 1L;
	private int width, height;
	private int columns, rows;
	private int cellWidth, cellHeight;
	private long previousTime;
	private Asciiquarium asciiquarium;
	private TerminalBlitter blitter;
	private Thread animThread;
	private boolean running;
	private Graphics graphics;
	private Image image;

	public void init() {		
		cellWidth = Asciiquarium.CELL_WIDTH;
		cellHeight = Asciiquarium.CELL_HEIGHT;	
		columns = 100;
		rows = 40;		
		width = columns * cellWidth;
		height = rows * cellHeight;
		
		image = createImage(width, height);
		graphics = image.getGraphics();
		
		asciiquarium = new Asciiquarium(columns, rows);
		asciiquarium.setRenderer(renderer);
		blitter = new TerminalBlitter(columns, rows, cellWidth, cellHeight);
		
		running = true;
		animThread = new Thread(this);
		animThread.start();
	}
	
	public void destroy() {
		running = false;
		if (animThread.isAlive()) {
			try {
				animThread.join();
			} catch (InterruptedException e) { }
		}
	}

	public void paint(Graphics g) {
		update(g);
	}
	
	public void update(Graphics g) {
		doDraw();
		g.drawImage(image, 0, 0, this);
	}
	
	void doDraw() {	
		long currentTime = System.currentTimeMillis();
		long elapsed = currentTime - previousTime;
		if (elapsed > 150) {
			// clear background
			graphics.setColor(Color.black);
	        graphics.fillRect(0, 0, width, height);
			
			if (blitter != null && asciiquarium != null) {
				asciiquarium.draw();
			}
			previousTime = currentTime;
		}
	}
	
	Renderer renderer = new Renderer() {
		public void putChar(int column, int row, char c, char color) {
			blitter.setChar(graphics, column, row, c, color);
		}
	};

	@Override
	public void run() {
		this.running = true;
		
		while (running) {
			repaint();
			
			try {
				Thread.sleep(160);
			} catch (InterruptedException e) { }
		}		
	}
}

