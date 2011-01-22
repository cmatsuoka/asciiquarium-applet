package org.helllabs.java.asciiquarium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.helllabs.java.asciiquarium.Entity.EntityCallback;


public class Asciiquarium {
	// Under water
	final static int DEPTH_BUBBLE = 1;
	final static int DEPTH_SHARK = 2;
	final static int DEPTH_FISH_START = 3;
	final static int DEPTH_FISH_END = 20;
	final static int DEPTH_SEAWEED = 21;
	final static int DEPTH_CASTLE = 22;
	
	// Waterline
	final static int DEPTH_WATER_LINE3 = 2;
	final static int DEPTH_WATER_GAP3 = 3;
	final static int DEPTH_WATER_LINE2 = 4;
	final static int DEPTH_WATER_GAP2 = 5;
	final static int DEPTH_WATER_LINE1 = 6;
	final static int DEPTH_WATER_GAP1 = 7;
	final static int DEPTH_WATER_LINE0 = 8;
	final static int DEPTH_WATER_GAP0 = 9;
	
	final static int CELL_WIDTH = 8;
	final static int CELL_HEIGHT = 16;
	
	final static int ENTITY_TYPE_WATERLINE = 1;
	final static int ENTITY_TYPE_BUBBLE = 2;
	final static int ENTITY_TYPE_FISH = 3;
	final static int ENTITY_TYPE_TEETH = 4;
	
	Animation anim;
	int columns;
	int rows;
	Random random;
	
	final static int WATER_LEVEL = 6;
	
	public abstract interface Renderer {
		void putChar(int row, int column, char c, char color);
	}
	
	Renderer renderer;
	
	public Asciiquarium(int columns, int rows) {
		
		//Log.d("Asciiquarium", "Asciiquarium constructor");
		
		this.columns = columns;
		this.rows = rows;
		
		random = new Random();
		
		anim = new Animation(columns, rows);
		//anim.halfDelay(1);
		
		addEnvironment();
		addCastle();
		addAllSeaweed();
		addAllFish();
		randomObject();
	}
	
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}
	
	public Entity findEntityAt(int x, int y, int type) {
		final Entity[] fish = anim.getEntitiesOfType(ENTITY_TYPE_FISH);
		Entity target = null;
		int depth = 999;
		
		for (Entity e : fish) {
			if ((e.fx < x && e.fx + e.width <= x) || e.fx >= x)
				continue;
			if ((e.fy < y && e.fy + e.height <= y) || e.fy >= y)
				continue;
			
			if (e.depth < depth) {
				depth = e.depth;
				target = e;
			}
		}
		
		return target;
	}
	
	public void draw() {
		anim.animate();
		
		char[] buffer = anim.screen.text;
		char[] cbuffer = anim.screen.color;
		char c;
		for (int row = 0; row < rows; row++) {
			int r = row * columns;
			for (int col = 0; col < columns; col++) {
				c = buffer[r + col];
				if (c != ' ')
					renderer.putChar(col, row, c, cbuffer[r + col]);
			}
		}		
	}
	
	
	private void addEnvironment() {
		final String[] waterLineSegment = new String[] {
			"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
			"^^^^ ^^^  ^^^   ^^^    ^^^^      ",
			"^^^^      ^^^^     ^^^    ^^     ",
			"^^      ^^^^      ^^^    ^^^^^^  "
		};
		
		// tile the segments so they stretch across the screen
		final int segmentSize = waterLineSegment[0].length();
		final int segmentRepeat = columns / segmentSize + 1;
		
		for (int i = 0; i < waterLineSegment.length; i++) {
			StringBuffer s = new StringBuffer();
			for (int j = 0; j < segmentRepeat; j++) {
				s.append(waterLineSegment[i]); 
			}
			waterLineSegment[i] = s.toString();

			Entity entity = new Entity("water_seg_" + i,
					s.toString(), 0, WATER_LEVEL + i, 8 - i * 2);
			entity.type = ENTITY_TYPE_WATERLINE;
			entity.defaultColor = 'c';
			entity.physical = true;
			
			anim.addEntity(entity);
		}
	}
	
	private void addCastle() {
		final String[] castleImage = new String[] {
			"               T~~",
			"               |",
			"              /^\\",
			"             /   \\",
			" _   _   _  /     \\  _   _   _",
			"[ ]_[ ]_[ ]/ _   _ \\[ ]_[ ]_[ ]",
			"|_=__-_ =_|_[ ]_[ ]_|_=-___-__|",
			" | _- =  | =_ = _    |= _=   |",
			" |= -[]  |- = _ =    |_-=_[] |",
			" | =_    |= - ___    | =_ =  |",
			" |=  []- |-  /| |\\   |=_ =[] |",
			" |- =_   | =| | | |  |- = -  |",
			" |_______|__|_|_|_|__|_______|"
		};

		final String[] castleMask = new String[] {
			"                RR",
			"",
			"              yyy",
			"             y   y",
			"            y     y",
			"           y       y",
			"",
			"",
			"",
			"              yyy",
			"             yy yy",
			"            y y y y",
			"            yyyyyyy"
	     };
	     
		anim.addEntity(new Entity("castle", castleImage, castleMask,
				columns - 32, rows - 13, DEPTH_CASTLE));
	}
	
	private void addAllSeaweed() {
		final int seaweedCount = columns / 12;		// 15
		for (int i = 0; i < seaweedCount; i++)
			addSeaweed();		
	}
	
	private void addSeaweed() {
		List<String>[] seaweedList = new ArrayList[2];
		//int height = random.nextInt(4) + 3;
		int height = random.nextInt(8) + 3;
		
		seaweedList[0] = new ArrayList<String>();
		seaweedList[1] = new ArrayList<String>();
		
		for (int i = 0; i < height; i++) {
			int leftSide = i % 2;
			int rightSide = leftSide == 0 ? 1 : 0;
			seaweedList[leftSide].add("(");
			seaweedList[rightSide].add(" )");
		}
		
		String[][] seaweedImage = new String[2][];
		seaweedImage[0] = seaweedList[0].toArray(new String[0]);
		seaweedImage[1] = seaweedList[1].toArray(new String[0]);
		
		final int x = random.nextInt(columns - 2) + 1;
		final int y = rows - height;
		final float animSpeed = random.nextFloat() * 0.5F + 0.25F;
		
		Entity entity = new Entity("seaweed", seaweedImage, x, y, DEPTH_SEAWEED);
		
		// seaweed lives for 8 to 12 minutes
		entity.dieTime = System.currentTimeMillis() / 1000L + random.nextInt(4 * 60) + 8 * 60;
		entity.deathCallback = seaweedDeathCallback;
		entity.callbackArguments[3] = animSpeed;
		entity.defaultColor = 'g';
		
		anim.addEntity(entity);
	}
	
	EntityCallback seaweedDeathCallback = new EntityCallback() {
		public void run(Entity entity) {
			addSeaweed();
		}
	};
	
	private void addAllFish() {
		// figure out how many fish to add by the size of the screen
		// minus the stuff above the water
		final int screenSize = (rows - (WATER_LEVEL + 4)) * columns;
		final int fishCount = screenSize / 350;
		
		for (int i = 0; i < fishCount; i++) {
			addFish();
		}
	}
	
	private void addBubble(Entity fish) {
		int x = (int)fish.fx;
		int y = (int)fish.fy;
		float[] cbArgs = fish.callbackArguments;
		
		// moving right
		if (cbArgs[0] > 0) {
			x += fish.width;
		}
		y += fish.height / 2;		
		
		// bubble always goes on top of the fish
		addBubble(x, y, fish.depth - 1);
	}
		
	private void addBubble(int x, int y, int depth) {
		final String[][] shape = {
			{ "." }, { "o" }, { "O" }, { "O" }, { "O" }
		};

		Entity entity = new Entity("bubble", shape,	x, y, depth);
		entity.type = ENTITY_TYPE_BUBBLE;
		entity.callbackArguments[1] = -1.0F;
		entity.callbackArguments[3] = 0.1F;
		entity.dieOffscreen = true;
		entity.physical = true;
		entity.collHandler = bubbleCollision;
		entity.defaultColor = 'C';
		
		anim.addEntity(entity);		
	}
	
	EntityCallback bubbleCollision = new EntityCallback() {
		public void run(Entity bubble) {
			for (Entity e : bubble.collisions) {
				if (e.type == ENTITY_TYPE_WATERLINE) {
					bubble.kill();
					break;
				}
			}
		}
	};
	
	private void addFish() {
		final String fishImage[][] = {
			{
				"       \\",
				"     ...\\..,",
				"\\  /'       \\",
				" >=     (  ' >",
				"/  \\      / /",
				"    `\"'\"'/''"
			},
			{
				"       2",
				"     1112111",
				"6  11       1",
				" 66     7  4 5",
				"6  1      3 1",
				"    11111311"
			},
			{
				"      /",
				"  ,../...",
				" /       '\\  /",
				"< '  )     =<",
				" \\ \\      /  \\",
				"  `'\\'\"'\"'"
			},
			{
				"      2",
				"  1112111",
				" 1       11  6",
				"5 4  7     66",
				" 1 3      1  6",
				"  11311111"
			},
			{
				"    \\",
				"\\ /--\\",
				">=  (o>",
				"/ \\__/",
				"    /"
			},
			{
				"    2",
				"6 1111",
				"66  745",
				"6 1111",
				"    3"
			},
			{
				"  /",
				" /--\\ /",
				"<o)  =<",
				" \\__/ \\",
				"  \\"
			},
			{
				"  2",
				" 1111 6",
				"547  66",
				" 1111 6",
				"  3"
			},
			{
				"       \\:.",
				"\\;,   ,;\\\\\\,,",
				"  \\\\\\;;:::::::o",
				"  ///;;::::::::<",
				" /;` ``/////``"
			},
			{
				"       222",
				"666   1122211",
				"  6661111111114",
				"  66611111111115",
				" 666 113333311"
			},
			{
				"      .:/",
				"   ,,///;,   ,;/",
				" o:::::::;;///",
				">::::::::;;\\\\",
				"  ''\\\\\\\\\'' ';\\"
			},
			{
				"      222",
				"   1122211   666",
				" 4111111111666",
				"51111111111666",
				"  113333311 666"
			},
			{
				"  __",
				"><_'>",
				"   '"
			},
			{
				"  11",
				"61145",
				"   3"
			},
			{
				" __",
				"<'_><",
				" `"
			},
			{
				" 11",
				"54116",
				" 3"
			},
			{
				"   ..\\,",
				">='   ('>",
				"  '''/''"
			},
			{
				"   1121",
				"661   745",
				"  111311"
			},
			{
				"  ,/..",
				"<')   `=<",
				" ``\\```"
			},
			{
				"  1211",
				"547   166",
				" 113111"
			},
			{
				"   \\",
				"  / \\",
				">=_('>",
				"  \\_/",
				"   /"
			},
			{
				"   2",
				"  1 1",
				"661745",
				"  111",
				"   3"
			},
			{
				"  /",
				" / \\",
				"<')_=<",
				" \\_/",
				"  \\"
			},
			{
				"  2",
				" 1 1",
				"547166",
				" 111",
				"  3"
			},
			{
				"  ,\\",
				">=('>",
				"  '/"
			},
			{
				"  12",
				"66745",
				"  13"
			},
			{
				" /,",
				"<')=<",
				" \\`"
			},
			{
				" 21",
				"54766",
				" 31"
			},
			{
				"  __",
				"\\/ o\\",
				"/\\__/"
			},
			{
				"  11",
				"61 41",
				"61111"
			},
			{
				" __",
				"/o \\/",
				"\\__/\\"
			},
			{
				" 11",
				"14 16",
				"11116"
			}
		};
		
        // 1: body
        // 2: dorsal fin
        // 3: flippers
        // 4: eye
        // 5: mouth
        // 6: tailfin
        // 7: gills
		
		int fishNum = random.nextInt(fishImage.length / 2);
		int fishIndex = fishNum * 2;
		float speed = random.nextFloat() * 2 + 0.25F;
		int depth = random.nextInt(DEPTH_FISH_END - DEPTH_FISH_START) + DEPTH_FISH_START;
		String[] colorMask = fishImage[fishIndex + 1];
		
		randColor(colorMask);
		
		if (fishNum % 2 != 0)
			speed *= -1;
		
		Entity fishObject = new Entity("fish", fishImage[fishIndex], colorMask, 0, 0, depth);
		fishObject.type = ENTITY_TYPE_FISH;
		fishObject.callback = fishCallback;
		fishObject.autoTrans = true;
		fishObject.dieOffscreen = true;
		fishObject.deathCallback = fishDeathCallback;
		fishObject.callbackArguments[0] = speed;
		fishObject.physical = true;
		fishObject.collHandler = fishCollision;
		
		int maxHeight = WATER_LEVEL + 4;
		int minHeight = rows - fishObject.height;
		fishObject.fy = random.nextInt(minHeight - maxHeight) + maxHeight;
		
		if (fishNum % 2 != 0) {
			fishObject.fx = columns - 2;
		} else {
			fishObject.fx = 1 - fishObject.width;
		}
		
		anim.addEntity(fishObject);
	}
	
	EntityCallback fishCallback = new EntityCallback() {
		public void run(Entity entity) {
			if (random.nextInt(100) > 97)
				addBubble(entity);
		
			entity.move();
		}
	};
	
	EntityCallback fishDeathCallback = new EntityCallback() {
		public void run(Entity entity) {
			addFish();
		}
	};
	
	EntityCallback fishCollision = new EntityCallback() {
		public void run(Entity fish) {
			for (Entity e : fish.collisions) {
				if (e.type == ENTITY_TYPE_TEETH) {
					addSplat(e.fx, e.fy, e.depth);
					fish.kill();
					break;
				}
			}
		}
	};
	
	private void addSplat(float fx, float fy, int depth) {
		final String[][] splatImage = {
			{
				"",
				"   .",
				"  ***",
				"   '",
				""
			},
			{
				"",
				" \",*;`",
				" \"*,**",
				" *\"'~'",
				""
			},
			{
				"  , ,",
				" \" \",\"'",
				" *\" *'\"",
				"  \" ; .",
				""
			},
			{
				"* ' , ' `",
				"' ` * . '",
				" ' `' \",'",
				"* ' \" * .",
				"\" * ', '"
			}
		};

		Entity entity = new Entity("splat", splatImage, (int)fx - 4, (int)fy - 2, depth - 2);
		entity.defaultColor = 'R';
		entity.callbackArguments[3] = 0.25F;
		entity.transparent = ' ';
		entity.dieFrame = 15;
		anim.addEntity(entity);		
	}
	
	private void addShark() {
		final String[][] sharkImage = {
			{
				"                              __",
				"                             ( `\\",
				"  ,??????????????????????????)   `\\",
				";' `.????????????????????????(     `\\__",
				" ;   `.?????????????__..---''          `~~~~-._",
				"  `.   `.____...--''                       (b  `--._",
				"    >                     _.-'      .((      ._     )",
				"  .`.-`--...__         .-'     -.___.....-(|/|/|/|/'",
				" ;.'?????????`. ...----`.___.',,,_______......---'",
				" '???????????'-'"
			},
			{
				"                     __",
				"                    /' )",
				"                  /'   (??????????????????????????,",
				"              __/'     )????????????????????????.' `;",
				"      _.-~~~~'          ``---..__?????????????.'   ;",
				" _.--'  b)                       ``--...____.'   .'",
				"(     _.      )).      `-._                     <",
				" `\\|\\|\\|\\|)-.....___.-     `-.         __...--'-.'.",
				"  `---......_______,,,`.___.'----... .'?????????`.;",
				"                                    `-`???????????`"
			}
		};
		
        final String[][] sharkMask = {
        	{
        		"",
        		"",
        		"",
        		"",
        		"",
        		"                                           cR",
        		"", 
        		"                                          cWWWWWWWW",
        		"",
        		""
        	},
        	{
        		"",
        		"",
        		"",
        		"",
        		"",
        		"        Rc",
        		"",
        		"  WWWWWWWWc",
        		"",
        		""
        	}
        };

		int dir = random.nextInt(2);
		int x = -53;
		int y = random.nextInt(rows - (10 + (WATER_LEVEL + 4))) + (WATER_LEVEL + 4);
		int teethX = -9;
		int teethY = y + 7;
		float speed = 2.0F;
		if (dir > 0) {
			speed *= -1;
			x = columns - 2;
			teethX = x + 9;
		}
		
		Entity teeth = new Entity("teeth", "*", teethX, teethY, DEPTH_SHARK + 1);
		teeth.type = ENTITY_TYPE_TEETH;
		teeth.callbackArguments[0] = speed;
		teeth.physical = true;
		anim.addEntity(teeth);
		
		Entity entity = new Entity("shark", sharkImage[dir], sharkMask[dir],
					x, y, DEPTH_SHARK);
		entity.callbackArguments[0] = speed;
		entity.dieOffscreen = true;
		entity.deathCallback = sharkDeathCallback;
		entity.defaultColor = 'C';
		anim.addEntity(entity);
	}
	
	// when a shark dies, kill the "teeth" too, the associated
	// entity that does the actual collision
	EntityCallback sharkDeathCallback = new EntityCallback() {
		public void run(Entity entity) {
			Entity[] teeth = anim.getEntitiesOfType(ENTITY_TYPE_TEETH);
			for (Entity e : teeth)
				anim.delEntity(e);
			randomObject();
		}
	};	
	
	private void addShip() {
        final String[][] shipImage = {
        	{
        		"     |    |    |",
        		"    )_)  )_)  )_)",
        		"   )___))___))___)\\",
        		"  )____)____)_____)\\\\",
        		"_____|____|____|____\\\\\\__",
        		"\\                   /"
        	},
        	{
        		"         |    |    |",
        		"        (_(  (_(  (_(",
        		"      /(___((___((___(",
        		"    //(_____(____(____(",
        		"__///____|____|____|_____",
        		"    \\                   /"
        	}
        };

        final String[][] shipMask = {
        	{
        		"     y    y    y",
        		"                 ",
        		"                  w",
        		"                   ww",
        		"yyyyyyyyyyyyyyyyyyyywwwyy",
        		"y                   y"
        	},
        	{
        		"         y    y    y",
        		"                     ",
        		"      w               ",
        		"    ww                 ",
        		"yywwwyyyyyyyyyyyyyyyyyyyy",
        		"    y                   y"
        	}
        };

        int dir = random.nextInt(2);
        int x = -24;
        float speed = 1.0F;
        if (dir > 0) {
        	speed *= -1;
        	x = columns - 2;
        }
        
        Entity entity = new Entity("ship", shipImage[dir], shipMask[dir],
        			x, WATER_LEVEL - 5, DEPTH_WATER_GAP1);
        entity.callbackArguments[0] = speed;
        entity.dieOffscreen = true;
        entity.deathCallback = randomDeathCallback;
        entity.defaultColor = 'W';
        
        anim.addEntity(entity);
	}
	
	final private void addWhale() {
		final String[][] whaleImage = {
			{
				"        .-----:",
				"      .'       `.",
				",????/       (o) \\",
				"\\`._/          ,__)"
			},
			{
				"    :-----.",
				"  .'       `.",
				" / (o)       \\????,",
				"(__,          \\_.'/"
			}
		};
		
		final String[][] whaleMask = {
			{
				"             C C",
				"           CCCCCCC",
				"           C  C  C",
				"        BBBBBBB",
				"      BB       BB",
				"B    B       BWB B",
				"BBBBB          BBBB"
			},
			{
				"   C C",
				" CCCCCCC",
				" C  C  C",
				"    BBBBBBB",
				"  BB       BB",
				" B BWB       B    B",
				"BBBB          BBBBB"
			}
		};

		final String[][] waterSpout = {
			{
				"",
				"",
				"    :"
			},
			{
				"",
				"    :",
				"    :",
			},
			{
				"   . .",
				"   -:-",
				"    :",
			},
			{
				"   . .",
				"  .-:-.",
				"    :",
			},
			{
				"   . .",
				" '.-:-.`",
				" '  :  '"
			},
			{
				"",
				"  .- -.",
				" ;  :  ;"
			},
			{
				"",
				"",
				" ;     ;"
			}
		};
				
		int dir = random.nextInt(2);
		int x;
		float speed = 1.0F;
		String[][] whaleAnim = new String[12][];
		
		if (dir > 0) {
			speed *= -1;
			x = columns - 2;
		} else {
			x = -18;
		}
		
		// no water spout
		for (int i = 0; i < 5; i++) {
			whaleAnim[i] = new String[7];
			for (int j = 0; j < 3; j++)
				whaleAnim[i][j] = "";
			for (int j = 0; j < 4; j++)
				whaleAnim[i][3 + j] = whaleImage[dir][j];
		}
		
		// animate water spout
		for (int i = 0; i < 7; i++) {
			whaleAnim[5 + i] = new String[7];
			for (int j = 0; j < 3; j++) {
				whaleAnim[5 + i][j] = dir > 0 ? "" : "          "; 						
				whaleAnim[5 + i][j] += waterSpout[i][j];
			}
			for (int j = 0; j < 4; j++)
				whaleAnim[5 + i][3 + j] = whaleImage[dir][j];
		}
		
		Entity entity = new Entity("whale", whaleAnim, whaleMask[dir],
					x, WATER_LEVEL - 5, DEPTH_WATER_GAP2);
		entity.callbackArguments[0] = speed;
		entity.callbackArguments[3] = 1.0F;
		entity.dieOffscreen = true;
		entity.deathCallback = randomDeathCallback;
		
		anim.addEntity(entity);
	}
	
	private void addMonster() {
		final String[][][] monsterImage = {
		{
			{
				"                                                          ____",
				"            __??????????????????????????????????????????/   o  \\",
				"          /    \\????????_?????????????????????_???????/     ____ >",
				"  _??????|  __  |?????/   \\????????_????????/   \\????|     |",
				" | \\?????|  ||  |????|     |?????/   \\?????|     |???|     |"
			},
			{
				"                                                          ____",
				"                                             __?????????/   o  \\",
				"             _?????????????????????_???????/    \\?????/     ____ >",
				"   _???????/   \\????????_????????/   \\????|  __  |???|     |",
				"  | \\?????|     |?????/   \\?????|     |???|  ||  |???|     |"
			},
			{
				"                                                          ____",
				"                                  __????????????????????/   o  \\",
				" _??????????????????????_???????/    \\????????_???????/     ____ >",
				"| \\??????????_????????/   \\????|  __  |?????/   \\????|     |",
				" \\ \\???????/   \\?????|     |???|  ||  |????|     |???|     |"
			},
			{
				"                                                          ____",
				"                       __???????????????????????????????/   o  \\",
				"  _??????????_???????/    \\????????_??????????????????/     ____ >",
				" | \\???????/   \\????|  __  |?????/   \\????????_??????|     |",
				"  \\ \\?????|     |???|  ||  |????|     |?????/   \\????|     |"
			}
		},
		{
			{
				"    ____",
				"  /  o   \\??????????????????????????????????????????__",
				"< ____     \\???????_?????????????????????_????????/    \\",
				"      |     |????/   \\????????_????????/   \\?????|  __  |??????_",
				"      |     |???|     |?????/   \\?????|     |????|  ||  |?????/ |"
			},
			{
				"    ____",
				"  /  o   \\?????????__",
				"< ____     \\?????/    \\???????_?????????????????????_",
				"      |     |???|  __  |????/   \\????????_????????/   \\???????_",
				"      |     |???|  ||  |???|     |?????/   \\?????|     |?????/ |"
			},
			{
				"    ____",
				"  /  o   \\????????????????????__",
				"< ____     \\???????_????????/    \\???????_??????????????????????_",
				"      |     |????/   \\?????|  __  |????/   \\????????_??????????/ |",
				"      |     |???|     |????|  ||  |???|     |?????/   \\???????/ /"
			},
			{
				"    ____",
				"  /  o   \\???????????????????????????????__",
				"< ____     \\??????????????????_????????/    \\???????_??????????_",
				"      |     |??????_????????/   \\?????|  __  |????/   \\???????/ |",
				"      |     |????/   \\?????|     |????|  ||  |???|     |?????/ /"
			}
		}};
		
		final String[][] monsterMask = {
			{
				"",
				"                                                           W",
				"",
				"",
				""
			},
			{
				"",
				"     W",
				"",
				"",
				""
			}
		};
		
		int dir = random.nextInt(2);
		int x;
		float speed = 2.0F;
		
		if (dir > 0) {
			speed *= -1;
			x = columns - 2;
		} else {
			x = -64;
		}
		
		String[][] monsterAnimMask = new String[4][];
		for (int i = 0; i < 4; i++)
			monsterAnimMask[i] = monsterMask[dir];
		
		Entity entity = new Entity("monster", monsterImage[dir], monsterAnimMask,
							x, WATER_LEVEL - 3, DEPTH_WATER_GAP2);
		entity.callbackArguments[0] = speed;
		entity.callbackArguments[3] = 0.25F;
		entity.dieOffscreen = true;
		entity.deathCallback = randomDeathCallback;
		entity.defaultColor = 'G';
		
		anim.addEntity(entity);
	}
	
	private void addBigFish() {
        final String[][] bigFishImage = {
        	{
        		" ______",
        		"`\"\"-.  `````-----.....__",
        		"     `.  .      .       `-.",
        		"       :     .     .       `.",
        		" ,     :   .    .          _ :",
        		": `.   :                  (@) `._",
        		" `. `..'     .     =`-.       .__)",
        		"   ;     .        =  ~  :     .-",
        		" .' .'`.   .    .  =.-'  `._ .'",
        		": .'   :               .   .'",
        		" '   .'  .    .     .   .-'",
        		"   .'____....----''.'=.'",
        		"   \"\"             .'.'",
        		"               ''\"'`"
        	},
        	{
        		"                           ______",
        		"          __.....-----'''''  .-\"\"'",
        		"       .-'       .      .  .'",
        		"     .'       .     .     :",
        		"    : _          .    .   :     ,",
        		" _.' (@)                  :   .' :",
        		"(__.       .-'=     .     `..' .'",
        		" \"-.     :  ~  =        .     ;",
        		"   `. _.'  `-.=  .    .   .'`. `.",
        		"     `.   .               :   `. :",
        		"       `-.   .     .    .  `.   `",
        		"          `.=`.``----....____`.",
        		"            `.`.             \"\"",
        		"              '`\"``"
        	}
        };
        
        final String[][] bigFishMask = {
        	{
        		" 111111",
        		"11111  11111111111111111",
        		"     11  2      2       111",
        		"       1     2     2       11",
        		" 1     1   2    2          1 1",
        		"1 11   1                  1W1 111",
        		" 11 1111     2     1111       1111",
        		"   1     2        1  1  1     111",
        		" 11 1111   2    2  1111  111 11",
        		"1 11   1               2   11",
        		" 1   11  2    2     2   111",
        		"   111111111111111111111",
        		"   11             1111",
        		"               11111"
        	},
        	{
        		"                           111111",
        		"          11111111111111111  11111",
        		"       111       2      2  11",
        		"     11       2     2     1",
        		"    1 1          2    2   1     1",
        		" 111 1W1                  1   11 1",
        		"1111       1111     2     1111 11",
        		" 111     1  1  1        2     1",
        		"   11 111  1111  2    2   1111 11",
        		"     11   2               1   11 1",
        		"       111   2     2    2  11   1",
        		"          111111111111111111111",
        		"            1111             11",
        		"              11111"
        	}
        };

		int dir = random.nextInt(2);
		int x;
		float speed = 3.0F;
		if (dir > 0) {
			x = columns - 1;
			speed *= -1;
		} else {
			x = -34;
		}
		int maxHeight = WATER_LEVEL + 4;
		int minHeight = rows - 15;
		int y = random.nextInt(minHeight - maxHeight) + maxHeight;
		randColor(bigFishMask[dir]);
		
		Entity entity = new Entity("big_fish", bigFishImage[dir],
					bigFishMask[dir], x, y, DEPTH_SHARK);
		entity.callbackArguments[0] = speed;
		entity.dieOffscreen = true;
		entity.deathCallback = randomDeathCallback;
		entity.defaultColor = 'Y';
		
		anim.addEntity(entity);
	}
	
	EntityCallback randomDeathCallback = new EntityCallback() {
		public void run(Entity entity) {
			randomObject();
		}
	};
	
	private void randomObject() {
		switch (random.nextInt(5)) {
			case 0: addShip(); break;
			case 1: addWhale(); break;
			case 2: addMonster(); break;
			case 3: addBigFish(); break;
			case 4: addShark(); break;
		}
	}
	
	private void randColor(String[] mask) {
		final char[] colors = { 'c','C','r','R','y','Y','b','B','g','G','m','M' };
		final char[] keys = { '1', '2', '3', '5', '6', '7' };
		
		char[] newColors = new char[keys.length];
		for (int i = 0; i < newColors.length; i++)
			newColors[i] = colors[random.nextInt(colors.length)];
		
		// Set eye white, rest as random color
		for (int i = 0; i < mask.length; i++) {
			mask[i] = mask[i].replace('4', 'W');
			for (int j = 0; j < keys.length; j++) {
				mask[i] = mask[i].replace(keys[j], newColors[j]);
			}
		}
	}
}
