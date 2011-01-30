function compareEntities(a, b)
{
	return a.depth < b.depth ? 1 : a.depth > b.depth ? -1 : 0;
}

function Entity() {
	var that = this;

	that.type = -1;
	that.callbackArguments = [ 0.0, 0.0, 0.0, 0.0 ];
	that.callback = null;
	that.deathCallback = null;
	that.collHandler = null;
	that.collisions = null;
	that.autoTrans = false;
	that.dieOffscreen = false;
	that.physical = false;
	that.dieTime = -1;
	that.dieFrame = -1;
	that.fframe = 0.0;
	that.frame = 0;
	that.frameCount = 0;
	that.defaultColor = 'w';
	that.transparent = '?';
	

	//public Entity(String name, String[] shape, String[] mask, int x, int y, int depth) {
	that.init_a_a = function(aname, ashape, amask, ax, ay, adepth) {
		that.shape = [];
		that.shape[0] = ashape;
		that.mask = [];
		that.mask[0] = amask;
		setVars(aname, ax, ay, adepth);
		return that;
	}
	
	// public Entity(String name, String shape, int x, int y, int depth) {
	that.init_s = function(aname, ashape, ax, ay, adepth) {
		that.shape = []
		that.shape[0] = [ashape];
		that.mask = null;
		setVars(aname, ax, ay, adepth);
		return that;
	}
	
	// public Entity(String name, String[][] shape, String[][] mask, int x, int y, int depth) {
	that.init_aa_aa = function(aname, ashape, amask, ax, ay, adepth) {
		that.shape = ashape;
		that.mask = amask;
		setVars(aname, ax, ay, adepth);
		return that;
	}
	
	// public Entity(String name, String[][] shape, String[] mask, int x, int y, int depth) {
	that.init_aa_a = function(aname, ashape, amask, ax, ay, adepth) {
		that.shape = ashape;
		that.mask = [];
		for (var i = 0; i < ashape.length; i++) {
			that.mask[i] = amask;
		}
		setVars(aname, ax, ay, adepth);
		return that;
	}
	
	// public Entity(String name, String[][] shape, int x, int y, int depth) {
	that.init_aa = function(aname, ashape, ax, ay, adepth) {
		that.shape = ashape;
		that.mask = null;
		setVars(aname, ax, ay, adepth);
		return that;
	}
	
	function setVars(aname, ax, ay, adepth) {
		that.name = aname;
		that.fx = ax;
		that.fy = ay;
		that.depth = adepth;
		
		var vwidth = 0;
		/* STOPPED HERE */
		for (var is = 0; is < that.shape[0].length; is++) {		// we assume all frames have the same size
			var s = that.shape[0][is];
			if (s.length > vwidth)
				vwidth = s.length;
		}
		that.height = that.shape[0].length;
		that.width = vwidth;
		that.callback = that.moveCallback;
		
		//Log.i("Asciiquarium", "Entity constructor: " + that.name + " (" + that.width + "x" + that.height + ")");
	}

	that.draw = function(screen) {
		that.draw2(screen, 0);
	}
	
	that.draw2 = function(screen, frame) {
		//Log.d("Asciiquarium", "Entity: draw " + that.name + " " + frame + "/" + (that.shape.length - 1));
		var x = Math.floor(that.fx);
		var y = Math.floor(that.fy);
		
		if (frame >= that.shape.length || x >= screen.columns || y >= screen.rows)
			return;
		
		for (var lineNum = 0; lineNum < that.height; lineNum++) {
			var yy = y + lineNum;
			
			if (yy < 0)
				continue;
			
			if (yy >= screen.rows)
				break;
			
			var length = that.shape[frame][lineNum].length;

			/* Log.d("Asciiquarium", "Entity: draw start=" + start + " end=" + end +
					" line=" + yy + " col=" + x); */

			var line = that.shape[frame][lineNum];
			var attr = "";
			var aLength = 0;
			
			if (that.mask != null) {
				attr = that.mask[frame][lineNum];
				aLength = attr.length;
			}
			
			var offset = yy * screen.columns + x;
			var flag = false;
			var c, a;				// character and attribute
			for (var srcPos = 0; srcPos < length; srcPos++, offset++) {
				c = line.charAt(srcPos); 
					
				if (c != ' ')
					flag = true;
				
				if (x + srcPos < 0 || x + srcPos >= screen.columns)
					continue;
				
				if (flag && c != that.transparent) {
					screen.text[offset] = c;
					
					if (srcPos < aLength) {
						a = attr.charAt(srcPos);
						if (a == ' ')
							a = that.defaultColor;
						screen.color[offset] = a;
					} else {
						screen.color[offset] = that.defaultColor;
					}
				}
			}
		}
		
		//Log.d("Asciiquarium", "Entity: draw complete");
	}

	// The default callback. You can also call that from your own
	// callback to do the work of moving and animating the entity
	// after you have done whatever other processing you want to do.

	that.moveCallback = {
		run: function(entity) {
			entity.move();
		}
	}
	
	that.move = function () {
		if (that.callbackArguments[3] > 0) {
			that.fframe += that.callbackArguments[3];
			if (that.fframe >= that.shape.length)
				that.fframe -= that.shape.length;
			that.frame = Math.floor(that.fframe);
		}
        that.frameCount++;
        
		that.fx += that.callbackArguments[0];
		that.fy += that.callbackArguments[1];
        
        //Log.d("Asciiquarium", that.name + " frame = " + that.frame);
	}

	// Remove that entity from the animation. that is equivalent to:
	//
	//	  that.animation.delEntity(entity);
	//
	// that does not destroy the object, so you can still
	// read it later (or put it in a different animation) as long
	// as you have a reference to it.
	
	that.kill = function () {
		that.animation.delEntity(that);
	}
}

/* vim: ts=4 sw=4
 */
