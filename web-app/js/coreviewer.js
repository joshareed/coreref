// setup the coreref namespace
if (coreref == null || typeOf(coreref) != 'object') { var coreref = new Object(); }

// create our CoreViewer object
coreref.CoreViewer = function(selector) {
	// internal variables
	var $$ = this; // save for easy reference
	var config;
	var tooltips;

	// state
	var drag = 0;
	var offset = 0;
	var rotation = 0;
	var max;
	var heights = new Object();

	// internal convenience methods
	function round(val) { return Math.round(100 * val) / 100 }
	function hash()		{ return round(phys(-offset)) }
	function scale(val) { return $$.config.scale * val }
	function phys(val)	{ return val / $$.config.scale }
	function top()		{ return $$.config.top }
	function base()		{ return $$.config.base }
	function width()	{ return $$.config.base - $$.config.top }
	function bind(template, data) {
		var bound = template;
		for (p in data) { bound = bound.replace(new RegExp('{' + p + '}', 'g'), data[p]) }
		return bound;
	}

	// public methods
	$$.configure = function(config) {
		$(function() {
			// save our config for external use
			$$.config = config;

			// set reasonable defaults
			if (config.scale == null) { config.scale = 2000; }
			if (config.top == null) {
				config.top = parseFloat(location.href.substring(location.href.lastIndexOf('/') + 1).replace(location.hash, ''));
			}
			if (config.base == null) { config.base = config.top + 5; }

			$('.track').each(function(i) {
				var track = $(this);
				var tc = config.tracks[this.id];
				if (tc != null) {
					// save our max offset
					max = -scale(width()) + track.width();

					// add our spinner
					var spinner = $(bind('<img src="{root}/images/spinner.gif"></img>', { root: config.root })).css({
						position: 'absolute'
					}).appendTo(track);

					// figure out our url
					var url = bind(tc.url, config);
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
							$$.redraw();
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
							}).append(bind('<div style="position: relative; height: {height}px; background: url(\'{root}/services/resources/overlay/{height}\') repeat-x top left; z-index: 10"></div>',
								{ root: config.root, height: Math.round(height) }));
						}
						$$.redraw();
					}).attr('src', url).appendTo($('body')).css({display: 'none'});
				}
			}).qtip({
				position: { target: 'mouse' },
				style: 'light',
				api: {
					onPositionUpdate: function() {
						var depth = round(top() - phys(offset) + phys(this.getPosition().left - this.elements.target.position().left + 2) - 0.01);
						this.updateContent(depth + 'm', false);
						if (tooltips != null) {
							for (var i in tooltips) {
								var t = tooltips[i];
								if (t.top <= depth && t.base > depth) {
									this.updateContent("<b>" + t.top + " - " + t.base + "m</b><br/>" + t.text, false);
								}
							}
						}
					}
				}
			});

			// load our tooltips
			if (config.descriptions != null && config.descriptions.url != null) {
				$.ajax({
					dataType: 'json',
					url: bind(config.descriptions.url, config),
					success: function(data, status) {
						tooltips = data;
					}
				});
			}

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
				var val = parseFloat(window.location.hash.substring(1));
				if (val != NaN) {
					offset = -scale(Math.min(val, -phys(max)));
					$$.redraw();
				}
			}
		});
	}

	$$.pan = function(value) {
		if (offset == 0 && top() > 0 && value < 0) {
			window.location = bind($$.config.url, {
				top: Math.max(0, top() - width()), base: top(), scale: scale(1), root: $$.config.root, project: $$.config.project
			}) + '#' + (-phys(max));
		} else if (offset == max && value > 0) {
			window.location = bind($$.config.url, {
				top: base(), base: base() + width(), scale: scale(1), root: $$.config.root, project: $$.config.project
			});
		} else {
			offset = Math.max(max, Math.min(0, offset - scale(value)));
			$$.redraw();
		}
	}

	$$.lookAt = function(value) {
		/*
		window.location = config.path({
			top: depth, base: depth + (width()), scale: config.scale
		});
		*/
	}

	$$.redraw = function() {
		// update our track offsets
		$(selector).each(function(j) {
			if ($(this).hasClass('animated')) {
				$(this).css({ backgroundPosition: offset + 'px ' + Math.round(heights[this.id] * rotation * Math.PI) + 'px' });
			} else {
				$(this).css({ backgroundPosition: offset + 'px 0px' });
			}
		});

		// update the location hash to add the offset
		window.location.hash = hash();
	}
}