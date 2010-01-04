// setup the coreref namespace
if (coreref == null || typeOf(coreref) != 'object') { var coreref = new Object(); }

// create our CoreViewer object
coreref.CoreViewer = function(selector, config) {
	var $$ = this; // save for easy reference
	var drag = 0;
	var offset = 0;
	var rotation = 0;
	var max;
	var heights = new Object();
	var tooltips;

	// convenience methods
	function round(val) { return Math.round(100 * val) / 100 }
	function hash()		{ return round(phys(-offset)) }
	function scale(val) { return config.scale * val }
	function phys(val)	{ return val / config.scale }
	function top()		{ return config.top }
	function base()		{ return config.base }
	function width()	{ return config.base - config.top }

	// initialize our tracks
	$(selector).each(function(i) {
		var track = $(this);
		var tc = config.tracks[this.id];
		if (tc != null) {
			// save our max offset
			max = -scale(width()) + track.width();

			// add our spinner
			var spinner = $('<img src="' + config.root + 'images/spinner.gif"></img>').css({
				position: 'absolute'
			}).appendTo(track);

			// figure out our url
			var url = tc.path({ top: config.top, base: config.base, scale: config.scale });
			var img = new Image();
			$(img).load(function() {
				// hide our spinner
				spinner.hide();

				// calculate our heights
				var height = $(img).height();
				if (track.hasClass('animated')) {
					height = height / Math.PI;
				}
				heights[track.attr('id')] = height;

				// setup our track
				track.css({
					height: height + "px",
					background: "url('" + url + "') no-repeat top left"
				}).bind('dragstart', function(event) {
					drag = event;
				}).bind('drag', function(event) {
					offset = Math.min(0, Math.max(max, offset + (event.offsetX - drag.offsetX)));
					if (track.hasClass('animated') && track.hasClass('paused')) {
						rotation = (rotation + ((event.offsetY - drag.offsetY) / 100)) % 1;
					}
					drag = event;
					$$.update();
				}).addClass('ready');

				if (track.hasClass('animated')) {
					track.css({
						backgroundRepeat: "repeat-y"
					}).dblclick(function() {
						if (track.hasClass('paused')) {
							track.removeClass('paused');
						} else {
							track.addClass('paused');
						}
					}).append('<div style="position: relative; height: ' + height + 'px; background: url(\'' + config.root + 'services/resources/overlay/' + height + '\') repeat-x top left; z-index: 10"></div>');
				}
				$$.update();
			}).attr('src', url).appendTo($('body')).css({display: 'none'});
		}
	}).qtip({
		position: { target: 'mouse' },
		style: 'light',
		api: {
			onPositionUpdate: function() {
				var depth = round(top() - phys(offset) + phys(this.getPosition().left - this.elements.target.position().left));
				this.updateContent(depth + 'm', false);
				if (tooltips != null) {
					for (var i in tooltips) {
						var t = tooltips[i];
						if (t.top <= depth && t.base >= depth) {
							this.updateContent("<b>" + t.top + " - " + t.base + "m</b><br/>" + t.text, false);
						}
					}
				}
			}
		}
	});

	$$.update = function() {
		// update our track offsets
		$('.track').each(function(j) {
			if ($(this).hasClass('animated')) {
				$(this).css({ backgroundPosition: offset + 'px ' + Math.round(heights[this.id] * rotation * Math.PI) + 'px' });
			} else {
				$(this).css({ backgroundPosition: offset + 'px 0px' });
			}
		});

		// update the location hash to add the offset
		window.location.hash = hash();
	}

	// load our tooltips
	if (config.descriptions != null) {
		$.ajax({
			dataType: 'json',
			url: config.descriptions({ top: config.top, base: config.base, scale: config.scale }),
			success: function(data, status) {
				tooltips = data;
			}
		});
	}

	// links to move left and right
	$('#moveRight').click(function() {
		if (offset == max) {
			window.location = config.path({
				top: config.base, base: config.base + (config.base - config.top), scale: config.scale
			});
		}
		offset = Math.max(max, offset - scale(0.25));
		$$.update();
		return false;
	});
	$('#moveLeft').click(function() {
		if (offset == 0 && top() > 0) {
			window.location = config.path({
				top: Math.max(0, top() - width()), base: top(), scale: config.scale
			}) + '#' + (-phys(max));
		}
		offset = Math.min(0, offset + scale(0.25));
		$$.update();
		return false;
	});
	$('#jumpButton').click(function() {
		var depth = parseFloat($('#jumpField').val());
		if (depth != NaN && depth != null) {
			window.location = config.path({
				top: depth, base: depth + (width()), scale: config.scale
			});
		}
	});

	// start the animation
	setInterval(function() {
		rotation = (rotation + 0.005) % 1;
		$('.animated').each(function(i) {
			if ($(this).hasClass('ready') && !$(this).hasClass('paused')) {
				$(this).css({ backgroundPosition: offset + 'px ' + Math.round(heights[this.id] * rotation * Math.PI) + 'px' });
			}
		});
	}, 50);

	// handle hash on load
	if (window.location.hash != null && window.location.hash != '') {
		var val = -scale(parseFloat(window.location.hash.substring(1)));
		if (val != NaN) {
			offset = val;
			$$.update();
		}
	}
}