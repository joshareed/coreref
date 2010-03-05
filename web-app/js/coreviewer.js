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
			if (config.base == null) { config.base = config.top + 5; }

			// build our settings DOM
			var settings = $('#coreviewerSettings');
			if (settings.size() > 0) {
				$.each(config.tracks, function(key, val) {
					// build the settings DOM
					$('<div></div>').attr({ id: key + 'Settings', 'class': 'trackSettings'}).append(
						$('<input type="checkbox"></input>').attr({ id: 'id' + key, name: key }).click(changeTrackVisibility)
					).append(
						$('<label>' + key.charAt(0).toUpperCase() + key.substring(1) + '</label>').attr('for', 'id' + key)
					).appendTo(settings);

					// set the checkbox
					var visible = $.cookie(key + '.visibility');
					if (visible == null || visible == 'true' || visible == true) {
						$('#id' + key).attr('checked', 'checked');
					}
				});
			}

			// build the tracks
			$('.track').each(function(i) {
				var track = $(this);
				var tc = config.tracks[this.id];
				if (tc == null) {
					track.hide();
				} else {
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
									{ root: config.root, height: Math.round(height) })
								);
							}
							$$.redraw();

							// pre-cache next and prev images
							var next = bind(tc.url, {
								top: config.base,
								base: config.base + 5,
								scale: config.scale,
								root: config.root,
								id: config.id
							});
							$(new Image()).attr('src', next).appendTo($('body')).css({display: 'none'});

							var prev = bind(tc.url, {
								top: config.top - 5,
								base: config.top,
								scale: config.scale,
								root: config.root,
								id: config.id
							});
							$(new Image()).attr('src', prev).appendTo($('body')).css({display: 'none'});
						}).attr('src', url).appendTo($('body')).css({display: 'none'});

						// add our tooltips
						track.qtip({
							position: { target: 'mouse' },
							style: {
								name: 'light',
								width: 400
							},
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
							// add our series controls
							var trackSettings = $('#' + track.attr('id') + 'Settings');
							if (trackSettings.size() > 0) {
								$.each(tc.series, function(i, key) {
									var foo = key.replace(/\ /, '-');
									$('<div></div>').attr({ id: track.attr('id') + '_' + foo, 'class': 'seriesSettings'}).append(
										$('<input type="radio"></input>').attr({ id: foo + '_' + track.attr('id') + '_y1', name: track.attr('id') + '_y1' }).click(function() { changeSeries(track.attr('id')); })
									).append(
										$('<input type="radio"></input>').attr({ id: foo + '_' + track.attr('id') + '_y2', name: track.attr('id') + '_y2' }).click(function() { changeSeries(track.attr('id')); })
									).append(
										$('<label>' + key + '</label>')
									).appendTo(trackSettings);
								});

								// check some of them
								var selected = $.cookie(track.attr('id') + '.series');
								if (selected == null || selected == "") {
									if (tc.series.length > 0) { $('#' + tc.series[0].replace(/\ /, '-') + '_' + track.attr('id') + '_y1').attr('checked', 'checked'); }
									if (tc.series.length > 1) { $('#' + tc.series[1].replace(/\ /, '-') + '_' + track.attr('id') + '_y2').attr('checked', 'checked'); }
								} else {
									var split = selected.split(',');
									if (split.length > 0) { $('#' + split[0].replace(/\ /, '-') + '_' + track.attr('id') + '_y1').attr('checked', 'checked'); }
									if (split.length > 1) { $('#' + split[1].replace(/\ /, '-') + '_' + track.attr('id') + '_y2').attr('checked', 'checked'); }
								}
							}
							changeSeries(track.attr('id'));
						}
					}
				}

				// set visibility
				var visible = $.cookie(this.id + '.visibility');
				if (visible != null && (visible == 'false' || visible == false)) {
					track.hide();
				}
			});

			function changeTrackVisibility() {
				$('.trackSettings input').each(function (i) {
					var track = $(this).attr('name');
					if ($(this).attr('checked') == true) {
						$('#' + track).show();
						$.cookie(track + '.visibility', true, { expires: 30 });
					} else {
						$('#' + track).hide();
						$.cookie(track + '.visibility', false, { expires: 30 });
					}
				});
			}

			function changeSeries(track) {
				var selected = [];
				$('#' + track + 'Settings .seriesSettings input').each(function(i){
					if ($(this).attr('checked')) {
						var id = $(this).attr('id');
						selected.push(id.substring(0, id.indexOf('_')).replace(/-/, ' '));
					}
				});

				// save the series
				$.cookie(track + ".series", selected.join(','), { expires: 30 });

				// load the data
				var bounds = $$.bounds();
				var tc = config.tracks[track];
				$.ajax({
					dataType: 'json',
					url: bind(tc.url, {
						top: bounds.top,
						base: bounds.base,
						series: selected.join(','),
						root: config.root,
						id: config.id
					}),
					success: function(data, status) {
						// setup our track
						$('#' + track).css({ height: "200px", border: "none", marginLeft: "-35px", marginRight: "-35px" });

						var options = {
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
						};

						// build our data series
						var series = [];
						for (var i = 0; i < selected.length; i++) {
							for (var s in data[selected[i]]) {
								if (data[selected[i]].hasOwnProperty(s)) {
									series.push(data[selected[i]][s]);
								}
							}

							// set max/min
							if (config.stats != null && config.stats[selected[i]] != null) {
								var stats = config.stats[selected[i]];
								var yaxis = ((i == 0) ? options.yaxis : options.y2axis);
								yaxis.max = stats.max;
								yaxis.min = stats.min;
							}
						}

						// build the plot
						var plot = $.plot($('#' + track), series, options);
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