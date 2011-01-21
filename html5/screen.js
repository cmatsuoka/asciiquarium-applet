function Screen(columns, rows) {

	//Log.d("Asciiquarium", "Screen constructor, size " + columns + "x" + rows);
	
	this.columns = columns;
	this.rows = rows;
	
	this.text = [];
	this.color = [];
	
	this.clear = function() {
		var size = this.columns * this.rows;
		for (var i = 0; i < size; i++) {
			this.text[i] = ' ';
			this.color[i] = ' ';
		}
	}
}
