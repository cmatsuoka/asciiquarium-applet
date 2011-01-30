function Animation(columns, rows) {
	//Log.d("Asciiquarium", "Animation constructor");
	var that = this;

	that.screen = new Screen(columns, rows);
	that.list = [];
	that.addQueue = [];
	that.deleteQueue = [];
	that.physicalCount = 0;
	
	// ########################## PHYSICS UTILITIES ##########################
	
	function findCollisions() {
		//Log.i("Asciiquarium", "Animation: find collisions");
		
		for (var ie = 0; ie < that.list.length; ie++) {
			var e = that.list[ie];
			if (!e.physical)
				continue;
			
			for (var iif = 0; iif < that.list.length; iif++) {
				var f = that.list[iif];
				if (!f.physical)
					continue;
				if (e == f)
					continue;
				if ((e.fx < f.fx && e.fx + e.width <= f.fx) || e.fx >= f.fx + f.width)
					continue;
				if ((e.fy < f.fy && e.fy + e.height <= f.fy) || e.fy >= f.fy + f.height)
					continue;
		
				//Log.i("Asciiquarium", "Animation: " + e.name + " collided with " + f.name);
				e.collisions.push(f);
			}
		}
	}
	
	// ########## END PHYSICS UTILITIES ###########
	
	// Perform a single animation cycle. Runs all of the callbacks,
	// does collision detection, and updates the display.
	
	that.animate = function() {		
		// To prevent crashes with concurrent access
		for (var ie = 0; ie < that.addQueue.length; ie++) {
			var e = that.addQueue[ie];
			that.list.push(e);
		}
		that.addQueue = [];

		doCallbacks();
		if (that.physicalCount > 0) {
			findCollisions();
			collisionHandlers();
		}
		removeDeletedEntities();
		buildScreen();
		//displayScreen();		
	}
		
	function buildScreen() {
		that.screen.clear();
		
		that.list.sort(compareEntities);
		for (var ie = 0; ie < that.list.length; ie++) {
			var e = that.list[ie];
			e.draw2(that.screen, e.frame);
		}
	}
	
	// Add one or more animation entities to the animation.
	
	that.addEntity = function(entity) {
		entity.animation = that;
		if (entity.physical) {
			entity.collisions = [];
			that.physicalCount++;
		}
		that.addQueue.push(entity);
	}
	
	// Removes an entity from the animation.
	
	this.delEntity = function(entity) {
		that.deleteQueue.push(entity);
	}
	
	// Go through the list of entities that have been queued for
	// deletion using delEntity and remove them
	
	function removeDeletedEntities() {
		// copy list so we can modify it
		var delList = that.deleteQueue.slice(0);
		
		for (var ie = 0; ie < delList.length; ie++) {
			var e = delList[ie];
			if (e.deathCallback != null) {
				//Log.i("Asciiquarium", "Animation: invoke death callback for " + e.name);
				e.deathCallback.run(e);
			}
			
			if (e.physical)
				that.physicalCount--;

			// that.list.remove(e)
			for (var id = 0; id < that.list.length; id++) {
				if (that.list[id] == e) {
					that.list.splice(id, 1);
					delete e;
					break;
				}
			}
		}
		
		that.deleteQueue = [];
	}
	
	// Returns a reference to a list of all entities in the animation
	// that have the given type.
	
	this.getEntitiesOfType = function(type) {
		var l = [];
		for (var ie = 0; ie < that.list.length; ie++) {
			var e = that.list[ie];
			if (e.type == type)
				l.push(e);
		}
		return l.slice(0);
	}
	
	// Run the callback routines for all entities that have them, and update
	// the entity accordingly. Also checks for auto death status

	function doCallbacks() {
		//Log.i("Asciiquarium", "Animation: do callbacks");
		for (var ie = 0; ie < that.list.length; ie++) {
			var e = that.list[ie];
			
			//Log.i("Asciiquarium", "Animation: do " + e.name + " callbacks");
			
			// check for methods to automatically die
			// dieTime
			
			if (e.dieFrame > 0) {
				if (e.frameCount >= e.dieFrame) {
					//Log.i("Asciiquarium", "Animation: " + e.name + " dies at frame " + e.frame);
					that.delEntity(e);
				}
			}
			
			// dieFrame
			// dieEntity
			
			if (e.dieOffscreen) {
				if (e.fx >= that.screen.columns || e.fy >= that.screen.rows ||
						e.fx < -e.width || e.fy < -e.height) {
					//Log.i("Asciiquarium", "Animation: " + e.name + " dies offscreen");
					that.delEntity(e);
					continue;
				}
			}
						
			// callback
			if (e.callback != null) {
				e.callback.run(e);
			}
		}
	}

	function collisionHandlers() {
		for (var ie = 0; ie < that.list.length; ie++) {
			var e = that.list[ie];
			if (e.collisions != null) {
				if (e.collHandler != null)
					e.collHandler.run(e);
				e.collisions.splice(0);
			}
		}
	}
}

/* vim: ts=4 sw=4
 */
