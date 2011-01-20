package org.helllabs.java.asciiquarium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Animation {
	private List<Entity> list;
	private List<Entity> addQueue;
	private List<Entity> deleteQueue;
	Screen screen;
	int physicalCount;
	
	public Animation(int columns, int rows) {
		
		//Log.d("Asciiquarium", "Animation constructor");
		
		screen = new Screen(columns, rows);		
		list = new ArrayList<Entity>();
		addQueue = new ArrayList<Entity>();
		deleteQueue = new ArrayList<Entity>();
		physicalCount = 0;
	}
	
	// ########################## PHYSICS UTILITIES ##########################
	
	private void findCollisions() {
		//Log.i("Asciiquarium", "Animation: find collisions");
		
		for (Entity e : list) {
			if (!e.physical)
				continue;
			
			for (Entity f : list) {
				if (!f.physical)
					continue;
				if (e.equals(f))
					continue;
				if ((e.fx < f.fx && e.fx + e.width <= f.fx) || e.fx >= f.fx + f.width)
					continue;
				if ((e.fy < f.fy && e.fy + e.height <= f.fy) || e.fy >= f.fy + f.height)
					continue;
		
				//Log.i("Asciiquarium", "Animation: " + e.name + " collided with " + f.name);
				e.collisions.add(f);
			}
		}
	}
	
	// ########## END PHYSICS UTILITIES ###########
	
	// Perform a single animation cycle. Runs all of the callbacks,
	// does collision detection, and updates the display.
	
	void animate() {		
		// To prevent crashes with concurrent access
		for (Entity e : addQueue)
			list.add(e);
		addQueue.clear();

		doCallbacks();
		if (physicalCount > 0) {
			findCollisions();
			collisionHandlers();
		}
		removeDeletedEntities();
		buildScreen();
		//displayScreen();		
	}
		
	private void buildScreen() {
		screen.clear();
		
		Collections.sort(list);		
		for (Entity e : list) {
			e.draw(screen, e.frame);
		}
	}
	
	// Add one or more animation entities to the animation.
	
	void addEntity(Entity entity) {
		entity.animation = this;
		if (entity.physical) {
			entity.collisions = new ArrayList<Entity>();
			physicalCount++;
		}
		addQueue.add(entity);
	}
	
	// Removes an entity from the animation.
	
	void delEntity(Entity entity) {
		deleteQueue.add(entity);
	}
	
	// Go through the list of entities that have been queued for
	// deletion using delEntity and remove them
	
	private void removeDeletedEntities() {
		// copy list so we can modify it
		Entity[] delList = deleteQueue.toArray(new Entity[0]);
		
		for (Entity e : delList) {
			if (e.deathCallback != null) {
				//Log.i("Asciiquarium", "Animation: invoke death callback for " + e.name);
				e.deathCallback.run(e);
			}
			
			if (e.physical)
				physicalCount--;

			list.remove(e);
		}
		
		deleteQueue.clear();
	}
	
	// Returns a reference to a list of all entities in the animation
	// that have the given type.
	
	Entity[] getEntitiesOfType(int type) {
		List<Entity> l = new ArrayList<Entity>();
		for (Entity e : list) {
			if (e.type == type)
				l.add(e);
		}
		return l.toArray(new Entity[0]);
	}
	
	// Run the callback routines for all entities that have them, and update
	// the entity accordingly. Also checks for auto death status

	private void doCallbacks() {
		//Log.i("Asciiquarium", "Animation: do callbacks");
		for (Entity e : list) {
			
			//Log.i("Asciiquarium", "Animation: do " + e.name + " callbacks");
			
			// check for methods to automatically die
			// dieTime
			
			if (e.dieFrame > 0) {
				if (e.frameCount >= e.dieFrame) {
					//Log.i("Asciiquarium", "Animation: " + e.name + " dies at frame " + e.frame);
					delEntity(e);
				}
			}
			
			// dieFrame
			// dieEntity
			
			if (e.dieOffscreen) {
				if (e.fx >= screen.columns || e.fy >= screen.rows ||
						e.fx < -e.width || e.fy < -e.height) {
					//Log.i("Asciiquarium", "Animation: " + e.name + " dies offscreen");
					delEntity(e);
					continue;
				}
			}
						
			// callback
			if (e.callback != null) {
				e.callback.run(e);
			}
		}
	}

	private void collisionHandlers() {
		for (Entity e : list) {
			if (e.collisions != null) {
				if (e.collHandler != null)
					e.collHandler.run(e);
				e.collisions.clear();
			}
		}
	}
}
