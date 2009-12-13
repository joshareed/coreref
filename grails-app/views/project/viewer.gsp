<html>
	<head>
		<title>${project.name} | CoreRef</title>
		<meta name="layout" content="viewer" />
		<g:javascript library="jquery.min" />
		<g:javascript library="jquery.event.drag.min" />
		<g:javascript library="coreviewer" />
		<script type="text/javascript">
			$(function() {
				var config = {
					min: 0, max: 1300, scale: 1000,
					tracks: {
						split: { 
							path: function(opt) { 
								return "http://localhost:8080/coreref/services/track/${project.collection}/split/" + opt.top + "/" + opt.base + "/" + opt.scale
							}
						},
						lith: {
							path: function(opt) { 
								return "http://localhost:8080/coreref/services/track/${project.collection}/lith/" + opt.top + "/" + opt.base + "/" + opt.scale
							}
						},
						ruler: {
							path: function(opt) { 
								return "http://localhost:8080/coreref/services/track/${project.collection}/ruler/" + opt.top + "/" + opt.base + "/" + opt.scale
							}
						}
					}
				};
				var viewer = new coreref.CoreViewer(".track", config, 100);
				$('#jumpButton').click(function (e) {
					var depth = null;
					try {
						depth = parseFloat($('#jumpField').val());
					} catch (e) {}
					if (depth) {
						viewer.lookAt(depth);
					}
				});
				$('#zoomInButton').click(function (e) {
					config.scale += 500;
					viewer.center();
				});
				$('#zoomOutButton').click(function (e) {
					config.scale = Math.max(0, config.scale - 500);
					viewer.center();
				});
			});
		</script>
	</head>
	<body>
		<div class="tools">
			Jump to: <input id="jumpField" name="jumpField" type="text"/>
			<button id="jumpButton">Go</button> |
			<button id="zoomInButton">Z+</button>
			<button id="zoomOutButton">Z-</button>
		</div>
		<div class="track" id="split">
			<div class="tiles"></div>
		</div>
		<div class="track" id="lith">
			<div class="tiles"></div>
		</div>
		<div class="track" id="ruler">
			<div class="tiles"></div>
		</div>
		<div id="status"></div>
	</body>
</html>