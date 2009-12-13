// setup the coreref namespace
if (coreref == null || typeOf(coreref) != 'object') { var coreref = new Object(); }

// create our CoreViewer object
coreref.CoreViewer = function(selector, config, start) {
	var $$ = this; // save for easy reference
	var depth = 0;
	var offset = 0;
	var drag;
	
	// fields
	$$.tracks = [];
	
	// methods
	$$.lookAt = function(depth) {
		this.depth = depth;
		this.offset = 0;
		$$.center();
	}
	
	$$.center = function() {
		for (var i = 0; i < $$.tracks.length; i++) {
			var track = $$.tracks[i];
			for (var j = 0; j < track.tiles.length; j++) {
				var tile = track.tiles[j];
				var url = track.path({ top: $$.depth + tile.c, base: $$.depth + tile.c + 1, scale: config.scale });
				$(tile.image).css({
					left: ((tile.c * config.scale) + offset) + "px"
				}).attr({
					src: url
				});
			}
		}
	}
	
	$$.update = function() {
		for (var i = 0; i < $$.tracks.length; i++) {
			var track = $$.tracks[i];
			for (var j = 0; j < track.tiles.length; j++) {
				var tile = track.tiles[j];
				$(tile.image).css({
					left: ((tile.c * config.scale) + offset) + "px"
				});
			}
		}
	}
	
	// initialization
	$(selector).each(function(i) {
		// get the track configuration
		var tc = config.tracks[this.id];		
		if (tc != null) {
			// create our track object
		 	var track = { 
				id: this.id, 
				tiles: [],
				height: 0,
				path: tc.path
			 };
	
			// setup the track and tiles
			$(this).children(".tiles").each(function() {
				var count = 8 * Math.ceil($(this).width() / config.scale);
				var c = Math.floor(count / 2);
				for (var j = 0; j < count; j++) {
					var tile = { c: j - c, x: 0, y: 0, image: new Image() };
					track.tiles[j] = tile;
					$(this).append(tile.image);
					$(tile.image).load(function() {
						track.height = Math.max($(this).height(), track.height)
						$('#' + track.id).css({ height: track.height + "px" });
					});
				}
			}).bind('dragstart', function(e) {
				drag = e;
			}).bind('drag', function(e) {
				offset = offset + (e.offsetX - drag.offsetX);
				if ($$.depth - offset / config.scale <= config.min) {
					offset = config.scale;
				}
				if ($$.depth - offset / config.scale >= config.max) {
					offset = -config.scale;
				}
				drag = e;
				$$.update();
			}).bind('dragend', function(e) {
				if (offset > config.scale) {
					offset -= config.scale;
					$$.depth--;
					$$.center();
				} else if (offset < -config.scale) {
					offset += config.scale;
					$$.depth++;
					$$.center();
				}
				$('#status').text(offset + "px, " + $$.depth + "m");
			});
		
			// add our track
			$$.tracks[i] = track;
		}
	});
	
	// look at our starting depth
	$$.lookAt(start);
}