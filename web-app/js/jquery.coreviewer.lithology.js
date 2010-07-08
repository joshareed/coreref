(function ($) {
	function init(viewer_, options) {
		var cache = {};
		var intervals = [];
		var schemeBase = '';
		var scheme;

		viewer_.hooks.setData.push(function(viewer, data) {
			intervals = [];
			if (data.intervals) {
				if (data.intervals.items)	intervals = data.intervals.items;
				if (data.intervals.scheme) {
					var url = data.intervals.scheme;
					$.ajax({
						dataType: 'json',
						url: url,
						success: function(data, status) {
							scheme = data;
							if (url.indexOf('/') > -1) {
								schemeBase = url.substring(0, url.lastIndexOf('/') + 1);
							}
						}
					});
				}
			}
		});

		// draw function
		viewer_.hooks.draw.push(function(viewer, ctx, bounds) {
			function lookup(code) {
				if (scheme && scheme.items && scheme.items[code]) {
					var item = scheme.items[code];
					var color = item.color;
					var image = item.pattern;
					if (image.indexOf(':') == -1 && image.indexOf('/') != 0) { image = schemeBase + image; }
					return {
						'color': color,
						'image': image
					};
				} else {
					return {};
				}
			};

			function drawLithology(key, interval, scale) {
				if (key.color) {
					ctx.fillStyle = key.color;
					ctx.fillRect(bounds.x, bounds.y + (interval.top * scale), options.width, (interval.base - interval.top) * scale);
				}
				if (key.image && key.image.width > 0 && (!key.image.readyState || key.image.readyState == 'complete')) {
					var pattern = ctx.createPattern(key.image, 'repeat');
					ctx.fillStyle = pattern;
					ctx.fillRect(bounds.x, bounds.y + (interval.top * scale), options.width, (interval.base - interval.top) * scale);
				}
				ctx.strokeRect(bounds.x, bounds.y + (interval.top * scale), options.width, (interval.base - interval.top) * scale);
			};

			// save our scale and create our loading pattern
			var isVertical = (viewer.getOptions().orientation == 'vertical');
			var scale = viewer.getScale();

			// calculate our current view
			var top = Math.max(0, -(isVertical ? viewer.getOffset().y : viewer.getOffset().x)) / scale;
			var base = top + (isVertical ? viewer.height() : viewer.width()) / scale;
			var width = (isVertical ? viewer.height() : viewer.width()) / scale;

			// walk through the intervals and draw them
			ctx.lineWidth = 3;
			for (var i = 0; i < intervals.length; i++) {
				var interval = intervals[i];
				if (interval.top <= (base + width) && interval.base >= (top - width)) {
					var code = interval.lithology;
					if (cache[code]) {
						drawLithology(cache[code], interval, scale);
					} else {
						// draw our rectangle
						ctx.strokeRect(bounds.x, bounds.y + (interval.top * scale), options.width, (interval.base - interval.top) * scale);

						if (scheme) {
							// load our pattern
							var item = lookup(code);
							cache[code] = {
								'color': item.color,
								'image': new Image()
							};
							if (item.image) {
								cache[code].image.onload = function() { viewer.redraw(); };
								cache[code].image.src = item.image;
							}
						}
					}
				}
			}
			return options.width;
		});
	}

	$.coreviewer.plugins.push({
		init: init,
		name: 'lithology',
		options: {
			'width': 72
		}
	});
})(jQuery);