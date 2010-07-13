(function ($) {
	function init(viewer_, options) {
		// local variables
		var images = [];
		var cache = {};
		var baseURL = '';
		var trackWidth = -72;

		viewer_.hooks.setData.push(function(viewer, data) {
			images = [];	// clear the images array

			if (data.images) {
				if (data.images.items) {
					for (var i = 0; i < data.images.items.length; i++) {
						var item = data.images.items[i];
						if (!options.type || (options.type == item.type)) {
							images.push(item);
						}
					}
				}
				// save our base url
				if (data.images.base) { baseURL = data.images.base; };
			}
		});

		// draw function
		viewer_.hooks.draw.push(function(viewer, ctx, bounds) {
			// our drawing function
			function drawImage(key, scale) {
				var data = key.data;
				var image = key.image;
				var dpm = data.dpm ? data.dpm : (image.height / (data.base - data.top));
				var width = image.width / dpm;
				trackWidth = Math.max(trackWidth, width);
				var offset = (trackWidth - width) / 2;
				ctx.drawImage(image, bounds.x + (offset * scale), bounds.y + (data.top * scale), width * scale, (data.base - data.top) * scale);
			}

			// save our scale and create our loading pattern
			var isVertical = (viewer.getOptions().orientation == 'vertical');
			var scale = viewer.getScale();
			var pattern = 'rgb(180,180,180)';

			// calculate our current view
			var top = Math.max(0, -(isVertical ? viewer.getOffset().y : viewer.getOffset().x)) / scale;
			var base = top + (isVertical ? viewer.height() : viewer.width()) / scale;
			var width = (isVertical ? viewer.height() : viewer.width()) / scale;

			// walk through the images and load them
			for (var i = 0; i < images.length; i++) {
				var image = images[i];
				if (image.top <= (base + width) && image.base >= (top - width)) {
					var url = (baseURL + image.url).replace('/r0/', '/r2/');
					if (cache[url] && cache[url].image.width > 0) {
						drawImage(cache[url], scale);
					} else {
						// draw our loading pattern
						ctx.fillStyle = pattern;
						ctx.fillRect(bounds.x, bounds.y + (image.top * scale), (trackWidth < 0 ? -trackWidth : trackWidth * scale), (image.base - image.top) * scale);
						ctx.strokeRect(bounds.x, bounds.y + (image.top * scale), (trackWidth < 0 ? -trackWidth : trackWidth * scale), (image.base - image.top) * scale);

						// load our image
						cache[url] = {
							'data': image,
							'image': new Image()
						};
						cache[url].image.onload = function() { viewer.redraw(); };
						cache[url].image.src = url;
					}
				}
			}
			return (trackWidth < 0 ? -trackWidth : trackWidth * scale);
		});
	}

	$.coreviewer.plugins.push({
		init: init,
		name: 'image',
		options: {}
	});
})(jQuery);