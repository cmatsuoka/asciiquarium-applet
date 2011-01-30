function randFloat()
{
	return Math.random();
}

function randInt(max)
{
	return Math.floor(Math.random() * max);
}

function Asciiquarium(acolumns, arows) {
	// Under water
	var DEPTH_BUBBLE = 1;
	var DEPTH_SHARK = 2;
	var DEPTH_FISH_START = 3;
	var DEPTH_FISH_END = 20;
	var DEPTH_SEAWEED = 21;
	var DEPTH_CASTLE = 22;
	
	// Waterline
	var DEPTH_WATER_LINE3 = 2;
	var DEPTH_WATER_GAP3 = 3;
	var DEPTH_WATER_LINE2 = 4;
	var DEPTH_WATER_GAP2 = 5;
	var DEPTH_WATER_LINE1 = 6;
	var DEPTH_WATER_GAP1 = 7;
	var DEPTH_WATER_LINE0 = 8;
	var DEPTH_WATER_GAP0 = 9;
	
	var CELL_WIDTH = 8;
	var CELL_HEIGHT = 16;
	
	var ENTITY_TYPE_WATERLINE = 1;
	var ENTITY_TYPE_BUBBLE = 2;
	var ENTITY_TYPE_FISH = 3;
	var ENTITY_TYPE_TEETH = 4;
	
	var anim;
	var columns;
	var rows;
	
	var WATER_LEVEL = 5;
	
	var randomDeathCallback = {
		run: function(entity) {
			randomObject();
		}
	};
	
	/* 
	public abstract interface Renderer {
		void putChar(int row, int column, char c, char color);
	}
	*/
	
	var renderer;
	
	//Log.d("Asciiquarium", "Asciiquarium constructor");
	
	columns = acolumns;
	rows = arows;
	
	anim = new Animation(columns, rows);
	//anim.halfDelay(1);
	
	addEnvironment();
	addCastle();
	addAllSeaweed();
	addAllFish();
	randomObject();
	
	this.setRenderer = function(arenderer) {
		renderer = arenderer;
	}
	
	this.findEntityAt = function(x, y, type) {
		var fish = anim.getEntitiesOfType(ENTITY_TYPE_FISH);
		var target = null;
		var depth = 999;
		
		for (var ie = 0; ie < fish.length; ie++) {
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
	
	this.draw = function() {
		anim.animate();
		
		var buffer = anim.screen.text;
		var cbuffer = anim.screen.color;
		for (var row = 0; row < rows; row++) {
			var r = row * columns;
			for (var col = 0; col < columns; col++) {
				var c = buffer[r + col];
				var color = cbuffer[r+ col];
				//console.debug('will draw:');
				//console.debug('row: ', row, ' column: ', col);
				//console.debug('c: ', c, ' color: ', color);
				renderer.putChar(col, row, c, color);
			}
		}
		renderer.endFrame();
	}
	
	
	function addEnvironment() {
		var waterLineSegment = [
			"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
			"^^^^ ^^^  ^^^   ^^^    ^^^^      ",
			"^^^^      ^^^^     ^^^    ^^     ",
			"^^      ^^^^      ^^^    ^^^^^^  "
		];
		
		// tile the segments so they stretch across the screen
		var segmentSize = waterLineSegment[0].length;
		var segmentRepeat = columns / segmentSize + 1;
		
		for (var i = 0; i < waterLineSegment.length; i++) {
			var s = '';
			for (var j = 0; j < segmentRepeat; j++) {
				s += waterLineSegment[i];
			}
			waterLineSegment[i] = s;

			var entity = (new Entity()).init_s("water_seg_" + i,
					s.toString(), 0, WATER_LEVEL + i, 8 - i * 2);
			entity.type = ENTITY_TYPE_WATERLINE;
			entity.defaultColor = 'c';
			entity.physical = true;
			
			anim.addEntity(entity);
		}
	}
	
	function addCastle() {
		var castleImage = [
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
		];

		var castleMask = [
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
	     ];
	     
		anim.addEntity((new Entity()).init_a_a("castle", castleImage, castleMask,
				columns - 32, rows - 13, DEPTH_CASTLE));
	}
	
	function addAllSeaweed() {
		var seaweedCount = columns / 12;		// 15
		for (var i = 0; i < seaweedCount; i++)
			addSeaweed();
	}

	function addSeaweed() {
		var seaweedList = [];
		var height = randInt(4) + 3;
		
		var seaweedDeathCallback = {
			run: function(entity) {
				addSeaweed();
			}
		};
		
		seaweedList[0] = [];
		seaweedList[1] = [];
		
		for (var i = 0; i < height; i++) {
			var leftSide = i % 2;
			var rightSide = leftSide == 0 ? 1 : 0;
			seaweedList[leftSide].push("(");
			seaweedList[rightSide].push(" )");
		}
		
		var seaweedImage = [];
		seaweedImage[0] = seaweedList[0].slice(0);
		seaweedImage[1] = seaweedList[1].slice(0);
		
		var x = randInt(columns - 2) + 1;
		var y = rows - height;
		var animSpeed = randFloat() * 0.5 + 0.25;
		
		var entity = (new Entity()).init_aa("seaweed", seaweedImage, x, y, DEPTH_SEAWEED);
		
		// seaweed lives for 8 to 12 minutes
		entity.dieTime = Date.now() / 1000 + randInt(4 * 60) + 8 * 60;
		entity.deathCallback = seaweedDeathCallback;
		entity.callbackArguments[3] = animSpeed;
		entity.defaultColor = 'g';
		
		anim.addEntity(entity);
	}
	
	function addAllFish() {
		// figure out how many fish to add by the size of the screen
		// minus the stuff above the water
		var screenSize = (rows - (WATER_LEVEL + 4)) * columns;
		var fishCount = screenSize / 350;
		
		for (var i = 0; i < fishCount; i++) {
			addFish();
		}
	}
	
	function addBubble(fish) {
		var x = Math.floor(fish.fx);
		var y = Math.floor(fish.fy);
		var cbArgs = fish.callbackArguments;
		
		// moving right
		if (cbArgs[0] > 0) {
			x += fish.width;
		}
		y += fish.height / 2;
		
		// bubble always goes on top of the fish
		addBubble(x, y, fish.depth - 1);
	}
		
	function addBubble(x, y, depth) {
		var bubbleCollision = {
			run: function(bubble) {
				for (var ie = 0; ie < bubble.collisions.length; ie++) {
					var e = bubble.collisions[ie];
					if (e.type == ENTITY_TYPE_WATERLINE) {
						bubble.kill();
						break;
					}
				}
			}
		};
		
		var shape = [
			[ "." ], [ "o" ], [ "O" ], [ "O" ], [ "O" ]
		];

		var entity = (new Entity()).init_aa("bubble", shape, x, y, depth);
		entity.type = ENTITY_TYPE_BUBBLE;
		entity.callbackArguments[1] = -1.0;
		entity.callbackArguments[3] = 0.1;
		entity.dieOffscreen = true;
		entity.physical = true;
		entity.collHandler = bubbleCollision;
		entity.defaultColor = 'C';
		
		anim.addEntity(entity);
	}
	
	function addFish() {
		var fishCallback = {
			run: function(entity) {
				if (randInt(100) > 97)
					addBubble(entity);
			
				entity.move();
			}
		};
		
		var fishDeathCallback = {
			run: function(entity) {
				addFish();
			}
		};
		
		var fishCollision = {
			run: function(fish) {
				for (var ie = 0; ie < fish.collisions.length; ie++) {
					var e = fish.collisions[ie];
					if (e.type == ENTITY_TYPE_TEETH) {
						addSplat(e.fx, e.fy, e.depth);
						fish.kill();
						break;
					}
				}
			}
		};
	
		var fishImage = [
			[
				"       \\",
				"     ...\\..,",
				"\\  /'       \\",
				" >=     (  ' >",
				"/  \\      / /",
				"    `\"'\"'/''"
			],
			[
				"       2",
				"     1112111",
				"6  11       1",
				" 66     7  4 5",
				"6  1      3 1",
				"    11111311"
			],
			[
				"      /",
				"  ,../...",
				" /       '\\  /",
				"< '  )     =<",
				" \\ \\      /  \\",
				"  `'\\'\"'\"'"
			],
			[
				"      2",
				"  1112111",
				" 1       11  6",
				"5 4  7     66",
				" 1 3      1  6",
				"  11311111"
			],
			[
				"    \\",
				"\\ /--\\",
				">=  (o>",
				"/ \\__/",
				"    /"
			],
			[
				"    2",
				"6 1111",
				"66  745",
				"6 1111",
				"    3"
			],
			[
				"  /",
				" /--\\ /",
				"<o)  =<",
				" \\__/ \\",
				"  \\"
			],
			[
				"  2",
				" 1111 6",
				"547  66",
				" 1111 6",
				"  3"
			],
			[
				"       \\:.",
				"\\;,   ,;\\\\\\,,",
				"  \\\\\\;;:::::::o",
				"  ///;;::::::::<",
				" /;` ``/////``"
			],
			[
				"       222",
				"666   1122211",
				"  6661111111114",
				"  66611111111115",
				" 666 113333311"
			],
			[
				"      .:/",
				"   ,,///;,   ,;/",
				" o:::::::;;///",
				">::::::::;;\\\\\\",
				"  ''\\\\\\\\\\'' ';\\"
			],
			[
				"      222",
				"   1122211   666",
				" 4111111111666",
				"51111111111666",
				"  113333311 666"
			],
			[
				"  __",
				"><_'>",
				"   '"
			],
			[
				"  11",
				"61145",
				"   3"
			],
			[
				" __",
				"<'_><",
				" `"
			],
			[
				" 11",
				"54116",
				" 3"
			],
			[
				"   ..\\,",
				">='   ('>",
				"  '''/''"
			],
			[
				"   1121",
				"661   745",
				"  111311"
			],
			[
				"  ,/..",
				"<')   `=<",
				" ``\\```"
			],
			[
				"  1211",
				"547   166",
				" 113111"
			],
			[
				"   \\",
				"  / \\",
				">=_('>",
				"  \\_/",
				"   /"
			],
			[
				"   2",
				"  1 1",
				"661745",
				"  111",
				"   3"
			],
			[
				"  /",
				" / \\",
				"<')_=<",
				" \\_/",
				"  \\"
			],
			[
				"  2",
				" 1 1",
				"547166",
				" 111",
				"  3"
			],
			[
				"  ,\\",
				">=('>",
				"  '/"
			],
			[
				"  12",
				"66745",
				"  13"
			],
			[
				" /,",
				"<')=<",
				" \\`"
			],
			[
				" 21",
				"54766",
				" 31"
			],
			[
				"  __",
				"\\/ o\\",
				"/\\__/"
			],
			[
				"  11",
				"61 41",
				"61111"
			],
			[
				" __",
				"/o \\/",
				"\\__/\\"
			],
			[
				" 11",
				"14 16",
				"11116"
			]
		];
		
        // 1: body
        // 2: dorsal fin
        // 3: flippers
        // 4: eye
        // 5: mouth
        // 6: tailfin
        // 7: gills
		
		var fishNum = randInt(fishImage.length / 2);
		var fishIndex = fishNum * 2;
		var speed = randFloat() * 2 + 0.25;
		var depth = randInt(DEPTH_FISH_END - DEPTH_FISH_START) + DEPTH_FISH_START;
		var colorMask = fishImage[fishIndex + 1];
		
		randColor(colorMask);
		
		if (fishNum % 2 != 0)
			speed *= -1;
		
		var fishObject = (new Entity()).init_a_a("fish", fishImage[fishIndex], colorMask, 0, 0, depth);
		fishObject.type = ENTITY_TYPE_FISH;
		fishObject.callback = fishCallback;
		fishObject.autoTrans = true;
		fishObject.dieOffscreen = true;
		fishObject.deathCallback = fishDeathCallback;
		fishObject.callbackArguments[0] = speed;
		fishObject.physical = true;
		fishObject.collHandler = fishCollision;
		
		var maxHeight = WATER_LEVEL + 4;
		var minHeight = rows - fishObject.height;
		fishObject.fy = randInt(minHeight - maxHeight) + maxHeight;
		
		if (fishNum % 2 != 0) {
			fishObject.fx = columns - 2;
		} else {
			fishObject.fx = 1 - fishObject.width;
		}
		
		anim.addEntity(fishObject);
	}
	
	function addSplat(fx, fy, depth) {
		var splatImage = [
			[
				"",
				"   .",
				"  ***",
				"   '",
				""
			],
			[
				"",
				" \",*;`",
				" \"*,**",
				" *\"'~'",
				""
			],
			[
				"  , ,",
				" \" \",\"'",
				" *\" *'\"",
				"  \" ; .",
				""
			],
			[
				"* ' , ' `",
				"' ` * . '",
				" ' `' \",'",
				"* ' \" * .",
				"\" * ', '"
			]
		];

		var entity = (new Entity()).init_aa("splat", splatImage, Math.floor(fx) - 4, Math.floor(fy) - 2, depth - 2);
		entity.defaultColor = 'R';
		entity.callbackArguments[3] = 0.25;
		entity.transparent = ' ';
		entity.dieFrame = 15;
		anim.addEntity(entity);		
	}
	
	function addShark() {
		// when a shark dies, kill the "teeth" too, the associated
		// entity that does the actual collision
		var sharkDeathCallback = {
			run: function(entity) {
				var teeth = anim.getEntitiesOfType(ENTITY_TYPE_TEETH);
				for (var ie = 0; ie < teeth.length; ie++) {
					var e = teeth[ie];
					anim.delEntity(e);
				}
				randomObject();
			}
		};
	
		var sharkImage = [
			[
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
			],
			[
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
			]
		];
		
        var sharkMask = [
        	[
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
        	],
        	[
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
        	]
        ];

		var dir = randInt(2);
		var x = -53;
		var y = randInt(rows - (10 + (WATER_LEVEL + 4))) + (WATER_LEVEL + 4);
		var teethX = -9;
		var teethY = y + 7;
		var speed = 2.0;
		if (dir > 0) {
			speed *= -1;
			x = columns - 2;
			teethX = x + 9;
		}
		
		var teeth = (new Entity()).init_s("teeth", "*", teethX, teethY, DEPTH_SHARK + 1);
		teeth.type = ENTITY_TYPE_TEETH;
		teeth.callbackArguments[0] = speed;
		teeth.physical = true;
		anim.addEntity(teeth);
		
		var entity = (new Entity()).init_a_a("shark", sharkImage[dir], sharkMask[dir],
					x, y, DEPTH_SHARK);
		entity.callbackArguments[0] = speed;
		entity.dieOffscreen = true;
		entity.deathCallback = sharkDeathCallback;
		entity.defaultColor = 'C';
		anim.addEntity(entity);
	}
	
	function addShip() {
        var shipImage = [
        	[
        		"     |    |    |",
        		"    )_)  )_)  )_)",
        		"   )___))___))___)\\",
        		"  )____)____)_____)\\\\",
        		"_____|____|____|____\\\\\\__",
        		"\\                   /"
        	],
        	[
        		"         |    |    |",
        		"        (_(  (_(  (_(",
        		"      /(___((___((___(",
        		"    //(_____(____(____(",
        		"__///____|____|____|_____",
        		"    \\                   /"
        	]
        ];

        var shipMask = [
        	[
        		"     y    y    y",
        		"                 ",
        		"                  w",
        		"                   ww",
        		"yyyyyyyyyyyyyyyyyyyywwwyy",
        		"y                   y"
        	],
        	[
        		"         y    y    y",
        		"                     ",
        		"      w               ",
        		"    ww                 ",
        		"yywwwyyyyyyyyyyyyyyyyyyyy",
        		"    y                   y"
        	]
        ];

        var dir = randInt(2);
        var x = -24;
        var speed = 1.0;
        if (dir > 0) {
        	speed *= -1;
        	x = columns - 2;
        }
        
        var entity = (new Entity()).init_a_a("ship", shipImage[dir], shipMask[dir],
        			x, WATER_LEVEL - 5, DEPTH_WATER_GAP1);
        entity.callbackArguments[0] = speed;
        entity.dieOffscreen = true;
        entity.deathCallback = randomDeathCallback;
        entity.defaultColor = 'W';
        
        anim.addEntity(entity);
	}
	
	function addWhale() {
		var whaleImage = [
			[
				"        .-----:",
				"      .'       `.",
				",????/       (o) \\",
				"\\`._/          ,__)"
			],
			[
				"    :-----.",
				"  .'       `.",
				" / (o)       \\????,",
				"(__,          \\_.'/"
			]
		];
		
		var whaleMask = [
			[
				"             C C",
				"           CCCCCCC",
				"           C  C  C",
				"        BBBBBBB",
				"      BB       BB",
				"B    B       BWB B",
				"BBBBB          BBBB"
			],
			[
				"   C C",
				" CCCCCCC",
				" C  C  C",
				"    BBBBBBB",
				"  BB       BB",
				" B BWB       B    B",
				"BBBB          BBBBB"
			]
		];

		var waterSpout = [
			[
				"",
				"",
				"    :"
			],
			[
				"",
				"    :",
				"    :",
			],
			[
				"   . .",
				"   -:-",
				"    :",
			],
			[
				"   . .",
				"  .-:-.",
				"    :",
			],
			[
				"   . .",
				" '.-:-.`",
				" '  :  '"
			],
			[
				"",
				"  .- -.",
				" ;  :  ;"
			],
			[
				"",
				"",
				" ;     ;"
			]
		];
				
		var dir = randInt(2);
		var x;
		var speed = 1.0;
		var whaleAnim = [];
		
		if (dir > 0) {
			speed *= -1;
			x = columns - 2;
		} else {
			x = -18;
		}
		
		// no water spout
		for (var i = 0; i < 5; i++) {
			whaleAnim[i] = [];
			for (var j = 0; j < 3; j++)
				whaleAnim[i][j] = "";
			for (var j = 0; j < 4; j++)
				whaleAnim[i][3 + j] = whaleImage[dir][j];
		}
		
		// animate water spout
		for (var i = 0; i < 7; i++) {
			whaleAnim[5 + i] = [];
			for (var j = 0; j < 3; j++) {
				whaleAnim[5 + i][j] = dir > 0 ? "" : "          ";
				whaleAnim[5 + i][j] += waterSpout[i][j];
			}
			for (var j = 0; j < 4; j++)
				whaleAnim[5 + i][3 + j] = whaleImage[dir][j];
		}
		
		var entity = (new Entity()).init_aa_a("whale", whaleAnim, whaleMask[dir],
					x, WATER_LEVEL - 5, DEPTH_WATER_GAP2);
		entity.callbackArguments[0] = speed;
		entity.callbackArguments[3] = 1.0;
		entity.dieOffscreen = true;
		entity.deathCallback = randomDeathCallback;
		
		anim.addEntity(entity);
	}
	
	function addMonster() {
		var monsterImage = [
		[
			[
				"                                                          ____",
				"            __??????????????????????????????????????????/   o  \\",
				"          /    \\????????_?????????????????????_???????/     ____ >",
				"  _??????|  __  |?????/   \\????????_????????/   \\????|     |",
				" | \\?????|  ||  |????|     |?????/   \\?????|     |???|     |"
			],
			[
				"                                                          ____",
				"                                             __?????????/   o  \\",
				"             _?????????????????????_???????/    \\?????/     ____ >",
				"   _???????/   \\????????_????????/   \\????|  __  |???|     |",
				"  | \\?????|     |?????/   \\?????|     |???|  ||  |???|     |"
			],
			[
				"                                                          ____",
				"                                  __????????????????????/   o  \\",
				" _??????????????????????_???????/    \\????????_???????/     ____ >",
				"| \\??????????_????????/   \\????|  __  |?????/   \\????|     |",
				" \\ \\???????/   \\?????|     |???|  ||  |????|     |???|     |"
			],
			[
				"                                                          ____",
				"                       __???????????????????????????????/   o  \\",
				"  _??????????_???????/    \\????????_??????????????????/     ____ >",
				" | \\???????/   \\????|  __  |?????/   \\????????_??????|     |",
				"  \\ \\?????|     |???|  ||  |????|     |?????/   \\????|     |"
			]
		],
		[
			[
				"    ____",
				"  /  o   \\??????????????????????????????????????????__",
				"< ____     \\???????_?????????????????????_????????/    \\",
				"      |     |????/   \\????????_????????/   \\?????|  __  |??????_",
				"      |     |???|     |?????/   \\?????|     |????|  ||  |?????/ |"
			],
			[
				"    ____",
				"  /  o   \\?????????__",
				"< ____     \\?????/    \\???????_?????????????????????_",
				"      |     |???|  __  |????/   \\????????_????????/   \\???????_",
				"      |     |???|  ||  |???|     |?????/   \\?????|     |?????/ |"
			],
			[
				"    ____",
				"  /  o   \\????????????????????__",
				"< ____     \\???????_????????/    \\???????_??????????????????????_",
				"      |     |????/   \\?????|  __  |????/   \\????????_??????????/ |",
				"      |     |???|     |????|  ||  |???|     |?????/   \\???????/ /"
			],
			[
				"    ____",
				"  /  o   \\???????????????????????????????__",
				"< ____     \\??????????????????_????????/    \\???????_??????????_",
				"      |     |??????_????????/   \\?????|  __  |????/   \\???????/ |",
				"      |     |????/   \\?????|     |????|  ||  |???|     |?????/ /"
			]
		]];
		
		var monsterMask = [
			[
				"",
				"                                                           W",
				"",
				"",
				""
			],
			[
				"",
				"     W",
				"",
				"",
				""
			]
		];
		
		var dir = randInt(2);
		var x;
		var speed = 2.0;
		
		if (dir > 0) {
			speed *= -1;
			x = columns - 2;
		} else {
			x = -64;
		}
		
		var monsterAnimMask = [];
		for (var i = 0; i < 4; i++)
			monsterAnimMask[i] = monsterMask[dir];
		
		var entity = (new Entity()).init_aa_aa("monster", monsterImage[dir], monsterAnimMask,
							x, WATER_LEVEL - 3, DEPTH_WATER_GAP2);
		entity.callbackArguments[0] = speed;
		entity.callbackArguments[3] = 0.25;
		entity.dieOffscreen = true;
		entity.deathCallback = randomDeathCallback;
		entity.defaultColor = 'G';
		
		anim.addEntity(entity);
	}
	
	function addBigFish() {
        var bigFishImage = [
        	[
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
        		"'    .'  .    .     .   .-'",
        		"   .'____....----''.'=.'",
        		"   \"\"             .'.'",
        		"               ''\"'`"
        	],
        	[
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
        	]
        ];
        
        var bigFishMask = [
        	[
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
        	],
        	[
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
        	]
        ];

		var dir = randInt(2);
		var x;
		var speed = 3.0;
		if (dir > 0) {
			x = columns - 1;
			speed *= -1;
		} else {
			x = -34;
		}
		var maxHeight = WATER_LEVEL + 4;
		var minHeight = rows - 15;
		var y = randInt(minHeight - maxHeight) + maxHeight;
		randColor(bigFishMask[dir]);
		
		var entity = (new Entity()).init_a_a("big_fish", bigFishImage[dir],
					bigFishMask[dir], x, y, DEPTH_SHARK);
		entity.callbackArguments[0] = speed;
		entity.dieOffscreen = true;
		entity.deathCallback = randomDeathCallback;
		entity.defaultColor = 'Y';
		
		anim.addEntity(entity);
	}
	
	function randomObject() {
		switch (randInt(5)) {
			case 0: addShip(); break;
			case 1: addWhale(); break;
			case 2: addMonster(); break;
			case 3: addBigFish(); break;
			case 4: addShark(); break;
		}
	}
	
	function randColor(mask) {
		var colors = [ 'c','C','r','R','y','Y','b','B','g','G','m','M' ];
		var keys = [ '1', '2', '3', '5', '6', '7' ];
		
		var newColors = [];
		for (var i = 0; i < keys.length; i++)
			newColors[i] = colors[randInt(colors.length)];
		
		// Set eye white, rest as random color
		for (var i = 0; i < mask.length; i++) {
			mask[i] = mask[i].replace(/4/g, 'W');
			for (var j = 0; j < keys.length; j++) {
				var re = new RegExp(keys[j],"g");
				mask[i] = mask[i].replace(re, newColors[j]);
				delete re;
			}
		}
	}
}

/* vim: ts=4 sw=4
 */
