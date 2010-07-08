(function ($) {
	function init(viewer_, options) {
		function spacing(screen) {
			var scale = viewer_.getScale();
			var multiplier = Math.pow(10, Math.floor(Math.log(screen/scale) / Math.log(10)));
			var hash;
			if (1 * scale * multiplier > screen) {
				hash = 1 * multiplier;
			} else if (5 * scale * multiplier > screen) {
				hash = 5 * multiplier;
			} else {
				hash = 10 * multiplier;
			}
			return hash;
		};

		viewer_.hooks.draw.push(function(viewer, ctx, bounds) {
			// setup the fill and font
			ctx.fillStyle = "rgb(0,0,0)";
			ctx.font = "12px sans-serif";
			ctx.textAlign = 'center';
			ctx.textBaseline = 'middle';
			ctx.lineWidth = 2;

			// calculate our hash and label spacing
			var hashStep = spacing(10);
			var labelStep = spacing(40);

			// calculate our current view
			var isVertical = (viewer.getOptions().orientation == 'vertical');
			var scale = viewer.getScale();
			var top = Math.max(0, -(isVertical ? viewer.getOffset().y : viewer.getOffset().x)) / scale;
			var base = top + (isVertical ? viewer.height() : viewer.width()) / scale;

			// draw hashes
			for (var i = Math.floor(top / hashStep) * hashStep; i < base; i += hashStep) {
				ctx.beginPath();
				ctx.moveTo(bounds.x, bounds.y + scale * i);
				ctx.lineTo(bounds.x + 5, bounds.y + scale * i);
				ctx.moveTo(bounds.x + options.width - 5, bounds.y + scale * i);
				ctx.lineTo(bounds.x + options.width, bounds.y + scale * i);
				ctx.stroke();
			}

			// draw labels
			var digits = Math.ceil(-Math.log(labelStep) / Math.log(10));
			for (var i = Math.floor(top / labelStep) * labelStep; i < base; i += labelStep) {
				ctx.beginPath();
				ctx.moveTo(bounds.x, bounds.y + scale * i);
				ctx.lineTo(bounds.x + 10, bounds.y + scale * i);
				ctx.moveTo(bounds.x + options.width - 10, bounds.y + scale * i);
				ctx.lineTo(bounds.x + options.width, bounds.y + scale * i);
				ctx.stroke();

				// draw the label
				ctx.fillText('' + i.toFixed(digits), bounds.x + options.width / 2, bounds.y + i * scale);
			}

			// draw the border
			ctx.beginPath();
			ctx.moveTo(bounds.x, Math.max(bounds.y, bounds.y + (top * scale) - 10));
			ctx.lineTo(bounds.x, base * scale + 10);
			ctx.moveTo(bounds.x + options.width, Math.max(bounds.y, bounds.y + (top * scale) - 10));
			ctx.lineTo(bounds.x + options.width, base * scale + 10);
			ctx.stroke();

			return options.width;
		});
	}

	$.coreviewer.plugins.push({
		init: init,
		name: 'ruler',
		options: { width: 72 }
	});
})(jQuery);