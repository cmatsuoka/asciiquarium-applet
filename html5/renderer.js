function Renderer(container, rows, columns)
{
	var elements;

	this.putChar = function(column, row, c, color) {
		var colors = [
			"#9f0000",	// r
			"#009f00",	// g
			"#9f9f00",	// y
			"#00009f",	// b
			"#9f009f",	// m
			"#009f9f",	// c
			"#9f9f9f",	// w
			"#ff0000",	// R
			"#00ff00",	// G
			"#ffff00",	// Y
			"#0000ff",	// B
			"#ff00ff",	// M
			"#00ffff",	// C
			"#ffffff",	// W
		];
		
		
		var n;
		
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

		var e = elements[row][column];
		e.css('color', colors[n]);
		if (c == ' ')
			c = '&nbsp;'

		e.html(c);
	}

	this.endFrame = function () {
	}

	var refchar = $('<char>&nbsp;</char>');
	var refrow = $('<row></row>');
	var newline = $('<newline></newline>');

	elements = [];
	for (var r = 0; r < rows; r++) {
		var row = refrow.clone();
		container.append(row);

		elements[r] = [];
		for (var c = 0; c < columns; c++) {
			var n = refchar.clone();
			elements[r][c] = n;
			row.append(n);
		}

		row.append(newline.clone());
	}
}
