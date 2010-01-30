// setup the coreref namespace
if (coreref == null || typeOf(coreref) != 'object') { var coreref = {}; }

// create our CoreViewer object
coreref.CoreViewer = function(selector) {
	// internal variables
	var $$ = this; // save for easy reference
	var config;
	var tooltips;

	// state variables
	var drag = 0;
	var offset = 0;
	var rotation = 0;
	var maxOffset;
	var width;
	var heights = {};

	// internal convenience methods
	function round(val) { return Math.round(100 * val) / 100; }
	function hash()		{ return round(phys(-offset)); }
	function scale(val) { return $$.config.scale * val; }
	function phys(val)	{ return val / $$.config.scale; }
	function bind(template, data) {
		var bound = template;
		for (var p in data) {
			if (data.hasOwnProperty(p)) {
				bound = bound.replace(new RegExp('{' + p + '}', 'g'), data[p]);
			}
		}
		return bound;
	}

	$$.bounds = function() {
		return {
			top: $$.config.top,
			base: $$.config.base,
			visible: {
				top: $$.config.top + phys(-offset),
				base: $$.config.top + phys(-offset) + phys(width)
			}
		};
	};

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
			if (config.base == null) { config.base = config.top + 3; }

			$('.track').each(function(i) {
				var track = $(this);
				var tc = config.tracks[this.id];
				if (tc != null) {
					// save our width and max offset
					width = track.width();
					maxOffset = scale(config.top - config.base) + track.width();

					// build an image track
					if (tc.type == 'image') { // build an image track
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
								if (track.hasClass('animated') && track.hasClass('paused')) {
									rotation = (rotation + ((event.offsetY - drag.offsetY) / 100)) % 1;
								}
								$$.pan(phys(drag.offsetX - event.offsetX));
								drag = event;
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

						// add our tooltips
						track.qtip({
							position: { target: 'mouse' },
							style: 'light',
							api: {
								onPositionUpdate: function() {
									var depth = round($$.bounds().visible.top + phys(this.getPosition().left - this.elements.target.position().left + 2) - 0.01);
									this.updateContent(depth + 'm', false);
									if (tooltips != null) {
										for (var i in tooltips) {
											if (tooltips.hasOwnProperty(i)) {
												var t = tooltips[i];
												if (t.top <= depth && t.base > depth) {
													this.updateContent("<b>" + t.top + " - " + t.base + "m</b><br/>" + t.text, false);
												}
											}
										}
									}
								}
							}
						});
					} else if (tc.type == 'plot') { // build a plot track
						if (tc.url != null) {
							$.ajax({
								dataType: 'json',
								url: bind(tc.url, config),
								success: function(data, status) {
									// setup our track
									track.css({ height: "200px", border: "none", marginLeft: "-35px", marginRight: "-35px" });

									// build our data series
									var series = [];
									for (var s in data) {
										if (series.length < 2) {
											series.push(data[s]);
											data[s].yaxis = series.length;
										}
									}

									// build the plot
									var plot = $.plot(track, series, {
										series: {
											lines: { show: true},
											points: { show: true }
										},
										yaxis: { labelWidth: 30 },
										y2axis: { labelWidth: 30 },
										grid: {
											hoverable: true,
											clickable: true,
											borderWidth: 3,
											borderColor: '#CC0000'
										}
									});
									tc.plot = plot;

									// set up our visible bounds
									var bounds = $$.bounds();
									plot.getAxes().xaxis.datamin = bounds.visible.top;
									plot.getAxes().xaxis.datamax = bounds.visible.base;
									plot.setupGrid();
									plot.draw();
								}
							});
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
				if (!isNaN(val)) {
					offset = -scale(Math.min(val, -phys(maxOffset)));
					$$.redraw();
				}
			}
		});
	};

	$$.pan = function(value) {
		var bounds = $$.bounds();
		if (offset == 0 && bounds.top > 0 && value < 0) {
			window.location = bind($$.config.url, {
				top: Math.max(0, bounds.top - (bounds.base - bounds.top)),
				base: bounds.top,
				scale: scale(1),
				root: $$.config.root,
				id: $$.config.id
			}) + '#' + (-phys(maxOffset));
		} else if (offset == maxOffset && value > 0) {
			window.location = bind($$.config.url, {
				top: bounds.base,
				base: bounds.base + (bounds.base - bounds.top),
				scale: scale(1),
				root: $$.config.root,
				id: $$.config.id
			});
		} else {
			offset = Math.max(maxOffset, Math.min(0, offset - scale(value)));
			$$.redraw();
		}
	};

	$$.lookAt = function(value) {
		var bounds = $$.bounds();
		if (value >= bounds.visible.top && value <= bounds.visible.base) {	// in view
			$$.pan(value - bounds.visible.top);
		} else {
			window.location = bind($$.config.url, {
				top: value,
				base: value + 3,
				scale: scale(1),
				root: $$.config.root,
				id: $$.config.id
			});
		}
	};

	$$.redraw = function() {
		// update our link tool
		$('#linkTool a').attr('href', bind($$.config.url, $$.config) + '#' + round(phys(-offset)));

		// update our track offsets
		$(selector).each(function(j) {
			var tc = $$.config.tracks[this.id];
			if (tc != null) {
				if (tc.type == 'image') {
					if ($(this).hasClass('animated')) {
						$(this).css({ backgroundPosition: offset + 'px ' + Math.round(heights[this.id] * rotation * Math.PI) + 'px' });
					} else {
						$(this).css({ backgroundPosition: offset + 'px 0px' });
					}
				} else if (tc.type == 'plot') {
					var bounds = $$.bounds();
					var plot = tc.plot;
					if (plot != null) {
						plot.getAxes().xaxis.datamin = bounds.visible.top;
						plot.getAxes().xaxis.datamax = bounds.visible.base;
						plot.setupGrid();
						plot.draw();
					}
				}
			}
		});
	};
};