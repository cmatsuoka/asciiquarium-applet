package org.helllabs.java.asciiquarium;

import java.util.List;

public class Entity implements Comparable<Entity> {
	String name;
	int type = -1;
	String[][] shape;
	String[][] mask;
	float fx, fy;
	int height;
	int width;
	int depth;
	float[] callbackArguments = { 0.0F, 0.0F, 0.0F, 0.0F };	
	EntityCallback callback = null;
	EntityCallback deathCallback = null;
	EntityCallback collHandler = null;
	List<Entity> collisions = null;
	boolean autoTrans = false;
	boolean dieOffscreen = false;
	boolean physical = false;
	long dieTime = -1;
	int dieFrame = -1;
	float fframe = 0.0F;
	int frame = 0;
	int frameCount = 0;
	Animation animation;
	char defaultColor = 'w';
	char transparent = '?';
	
	abstract interface EntityCallback {
		public void run(Entity entity);
	}
	
	public Entity(String name, String[] shape, String[] mask, int x, int y, int depth) {
		this.shape = new String[1][];
		this.shape[0] = shape;
		this.mask = new String[1][];
		this.mask[0] = mask;		
		setVars(name, x, y, depth);
	}
	
	public Entity(String name, String shape, int x, int y, int depth) {
		this.shape = new String[1][0];
		this.shape[0] = new String[] { shape };
		this.mask = null;
		setVars(name, x, y, depth);		
	}
	
	public Entity(String name, String[][] shape, String[][] mask, int x, int y, int depth) {
		this.shape = shape;
		this.mask = mask;		
		setVars(name, x, y, depth);
	}
	
	public Entity(String name, String[][] shape, String[] mask, int x, int y, int depth) {
		this.shape = shape;
		this.mask = new String[shape.length][];
		for (int i = 0; i < shape.length; i++)
			this.mask[i] = mask;		
		setVars(name, x, y, depth);
	}
	
	public Entity(String name, String[][] shape, int x, int y, int depth) {
		this.shape = shape;
		this.mask = null;
		setVars(name, x, y, depth);	
	}
	
	private void setVars(String name, int x, int y, int depth) {
		this.name = name;
		this.fx = x;
		this.fy = y;
		this.depth = depth;
		
		int width = 0;
		for (String s : shape[0]) {		// we assume all frames have the same size
			if (s.length() > width)
				width = s.length();
		}
		this.height = shape[0].length;
		this.width = width;
		this.callback = moveCallback;
		
		//Log.i("Asciiquarium", "Entity constructor: " + name + " (" + this.width + "x" + this.height + ")");
	}

	public void draw(Screen screen) {
		draw(screen, 0);
	}
	
	public void draw(Screen screen, int frame) {
		//Log.d("Asciiquarium", "Entity: draw " + name + " " + frame + "/" + (shape.length - 1));
		int x = (int)fx;
		int y = (int)fy;
		
		if (frame >= shape.length || x >= screen.columns || y >= screen.rows)
			return;
			
		for (int lineNum = 0; lineNum < height; lineNum++) {
			int yy = y + lineNum;
			
			if (yy < 0)
				continue;
			
			if (yy >= screen.rows)
				break;
			
			final int length = shape[frame][lineNum].length();

			/* Log.d("Asciiquarium", "Entity: draw start=" + start + " end=" + end +
					" line=" + yy + " col=" + x); */

			final String line = shape[frame][lineNum];
			String attr = "";
			int aLength = 0;
			
			if (mask != null) {
				attr = mask[frame][lineNum];
				aLength = attr.length();
			}
			
			int offset = yy * screen.columns + x;
			boolean flag = false;
			char c, a;				// character and attribute
			for (int srcPos = 0; srcPos < length; srcPos++, offset++) {
				c = line.charAt(srcPos); 
					
				if (c != ' ')
					flag = true;
				
				if (x + srcPos < 0 || x + srcPos >= screen.columns)
					continue;
				
				if (flag && c != transparent) {
					screen.text[offset] = c;
					
					if (srcPos < aLength) {
						a = attr.charAt(srcPos);
						if (a == ' ')
							a = defaultColor;				
						screen.color[offset] = a;
					} else {
						screen.color[offset] = defaultColor;
					}
				}
			}
		}
		
		//Log.d("Asciiquarium", "Entity: draw complete");
	}

	// The default callback. You can also call this from your own
	// callback to do the work of moving and animating the entity
	// after you have done whatever other processing you want to do.

	EntityCallback moveCallback = new EntityCallback() {
		public void run(Entity entity) {
			entity.move();
		}
	};
	
	public void move() {		
        if (callbackArguments[3] > 0) {            
            fframe += callbackArguments[3];
            if (fframe >= shape.length)
            	fframe -= shape.length;
            frame = (int)fframe;
        }
        frameCount++;
        
		fx += callbackArguments[0];
		fy += callbackArguments[1];
        
        //Log.d("Asciiquarium", name + " frame = " + frame);
	}

	// Remove this entity from the animation. This is equivalent to:
	//
	//	  animation.delEntity(entity);
	//
	// This does not destroy the object, so you can still
	// read it later (or put it in a different animation) as long
	// as you have a reference to it.
	
	public void kill() {
		animation.delEntity(this);
	}
	
	// Comparable
	
	@Override
	public int compareTo(Entity e) {
		return this.depth < e.depth ? 1 : this.depth > e.depth ? -1 : 0;
	}
}
