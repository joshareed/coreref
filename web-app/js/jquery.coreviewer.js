(function($) {
	function Coreviewer(placeholder, data, options, plugins) {
		var viewer = this,
			canvas = null,
			ctx = null,
			hooks = {
				setData: [],
				draw: []
			},
			drag = null,
			offsetX = 0,
			offsetY = 0,
			scale = 1;

		// public functions and attributes
		viewer.setData = setData;
		viewer.redraw = function() { setTimeout(draw, 100); };
		viewer.width = function() { return placeholder.width(); };
		viewer.height = function() { return placeholder.height(); };
		viewer.getOptions = function() { return options; };
		viewer.getData = function() { return data; };
		viewer.getScale = function() { return scale; };
		viewer.getOffset = function() { return { x: offsetX, y: offsetY }; };
		viewer.pan = function(dx, dy) {
			offsetX += dx;
			if (options.orientation == 'vertical') {
				offsetY += dy;
			} else {
				offsetY -= dy;
			}
			draw();
			return viewer;
		};
		viewer.lookAt = function(x, y) {
			offsetX = x - placeholder.width()/2;
			offsetY = y - placeholder.height()/2;
			draw();
			return viewer;
		};
		viewer.zoomIn = function() { viewer.setZoom(scale * 1.2); return viewer; };
		viewer.zoomOut = function() { viewer.setZoom(scale * 0.8); return viewer; };
		viewer.setZoom = function(newScale) {
			// save our old scale
			var oldScale = scale;

			// set the new scale
			if (options.maxScale && newScale > options.maxScale) {
				scale = options.maxScale;
			} else if (options.minScale && newScale < options.minScale) {
				scale = options.minScale;
			} else {
				scale = newScale;
			}

			// translate back to center
			if (options.orientation == 'vertical') {
				var before = -offsetY / oldScale + placeholder.height() / 2 / oldScale;
				var after = -offsetY / scale + placeholder.height() / 2 / scale;
				viewer.pan(0, (after - before) * scale);
			} else {
				var before = -offsetX / oldScale + placeholder.width() / 2 / oldScale;
				var after = -offsetX / scale + placeholder.width() / 2 / scale;
				viewer.pan((after - before) * scale, 0);
			}

			draw();
			return viewer;
		};
		viewer.setOrientation = function(orientation) { options.orientation = orientation; return viewer; };
		viewer.hooks = hooks;

		function setData(data) {
			for (var i = 0; i < hooks.setData.length; i++) {
				hooks.setData[i].apply(this, [viewer, data]);
			}
			draw();
			return viewer;
		}

		function draw() {
			// clear and setup our transform
			ctx.setTransform(1, 0, 0, 1, 0, 0);
			ctx.clearRect(0, 0, viewer.width(), viewer.height());
			if (options.orientation == 'horizontal') {
				ctx.rotate(-Math.PI / 2);
				ctx.translate(-viewer.height(), 0);
			}

			// draw tracks
			var x = 0;
			for (var i = 0; i < hooks.draw.length; i++) {
				var bounds;
				if (options.orientation == 'vertical') {
					bounds = { 'x': x + offsetX, 'y': offsetY, 'w': placeholder.width(), 'h': placeholder.height() };
				} else {
					bounds = { 'x': x - offsetY, 'y': offsetX, 'w': placeholder.height(), 'h': placeholder.width() };
				}

				ctx.save();
				var dx = hooks.draw[i].apply(this, [viewer, ctx, bounds]);
				if (dx) {
					x += (dx + 5);
				}
				ctx.restore();
			}
		}

		// initialize the viewer
		createCanvas();
		parseOptions();
		bindEvents();
		initTracks();
		setData(data);
		draw();

		// private functions
		function createCanvas() {
			// get our width and height
			var width = placeholder.width();
			var height = placeholder.height();
			if (width <= 0 || height <= 0) {
				throw "Invalid dimensions for viewer, width = " + width + ", height = " + height;
			}

			// clear our placeholder
			placeholder.html('');

			// IE excanvas hack
			if ($.browser.msie && window.G_vmlCanvasManager) {
				window.G_vmlCanvasManager.init_(document);
			}

			// create the canvas object
			var c = document.createElement('canvas');
			c.width = width;
			c.height = height;
			if ($.browser.msie) { // IE excanvas hack
				c = window.G_vmlCanvasManager.initElement(c);
			}

			// set our canvas and context objects
			canvas = $(c).appendTo(placeholder).get(0);
			ctx = canvas.getContext('2d');
		};

		function parseOptions() {
			if (options.offsetX) {
				offsetX = options.offsetX;
			}
			if (options.offsetY) {
				offsetY = options.offsetY;
			}
			if (options.scale) {
				scale = options.scale;
			}
		};

		function bindEvents() {
			if (options.draggable) {
				$(canvas).bind('dragstart', function(event) {
					drag = event;
				}).bind('drag', function(event) {
					var dx = event.offsetX - drag.offsetX;
					var dy = (options.orientation == 'vertical' ? event.offsetY - drag.offsetY : drag.offsetY - event.offsetY);
					viewer.pan(dx, dy);
					drag = event;
				});
			}
			if (options.zoomable) {
				$(canvas).bind('mousewheel', function(event, delta) {
					var diff = delta / 10;
					viewer.setZoom(scale * (1 + diff));
				});
			}
		};

		function initTracks() {
			for (var i = 0; i < options.tracks.length; i++) {
				var track = options.tracks[i];
				var name = track, options_ = {};

				// if it was an object get the first key as name and value as options
				// otherwise assume it was a string and no additional options
				if (typeof(track) == 'object') {
					for (var key in track) {
						name = key;
						options_ = track[name];
					}
				}

				// initialize the track
				for (var j = 0; j < plugins.length; j++) {
					var plugin = plugins[j];
					if (plugin.name == name) {
						plugin.init(viewer, $.extend({}, plugin.options, options_));
						found = true;
					}
				}
			}
		};
	};

	$.coreviewer = function(placeholder, data, options) {
		var opts = $.extend({}, $.coreviewer.defaults, options);
		return new Coreviewer($(placeholder), data, opts, $.coreviewer.plugins);
	};

	$.coreviewer.defaults = {
		tracks: ['ruler', 'image'],
		draggable: true,
		zoomable: true,
		orientation: 'horizontal',
		scale: 1000
	};
	$.coreviewer.plugins = [];
})(jQuery);