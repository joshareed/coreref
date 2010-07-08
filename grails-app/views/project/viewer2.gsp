<meta http-equiv="X-UA-Compatible" content="chrome=1">
<html>
	<head>
		<title>${project.name} - Core Viewer | CoreRef</title>
		<meta name="layout" content="viewer" />
		<g:javascript library="jquery.min" />
		<g:javascript library="jquery.event.drag.min" />
		<g:javascript library="jquery.mousewheel.min" />
		<g:javascript library="jquery.coreviewer" />
		<g:javascript library="jquery.coreviewer.ruler" />
		<g:javascript library="jquery.coreviewer.image" />
		<g:javascript library="jquery.coreviewer.lithology" />
		<!--[if IE]><g:javascript library="excanvas.min" /><![endif]-->
		<g:javascript library="canvas.text" />
			<script type="text/javascript" charset="utf-8">
				$(function() {
					var viewer = $.coreviewer($('#viewer'), {}, {
						tracks: [
							{ 'ruler': { width: 72 }},
							{ 'image': { type: 'split' }},
							{ 'lithology': {}}
						],
						orientation: 'horizontal',
						scale: 2000, maxScale: 10000, minScale: 1000
					});

					$.ajax({
						dataType: 'json',
						url: '${createLinkTo(dir:'services')}/${project.id}/config/canvas',
						success: function(data, status) { viewer.setData(data); }
					});

					// add our controls
					$('.panLeft').click(function() {
						viewer.pan(200, 0);
						return false;
					});

					$('.panRight').click(function() {
						viewer.pan(-200, 0);
						return false;
					});

					$('.zoom input').click(function() {
						viewer.setZoom($(this).attr('value') * 2000);
					});

					$('.jump button').click(function() {
						var depth = $('#jumpField').val();
						viewer.lookAt(-depth * viewer.getScale(), viewer.height() / 2);
					});
				});
			</script>
			<style type="text/css" media="screen">
				html, body {
					margin: 0;
					padding: 0;
				}
				.controls {
					margin: 0;
					padding: 0;
					padding-left: 100px;
					padding-right: 100px;
				}
				.center-column, .left-column, .right-column {
					position: relative;
					float: left;
				}
				.center-column {
					width: 100%;
					margin-top: 16px;
					text-align: center;
				}
				.left-column {
					width: 100px;
					margin-left: -100%;
					right: 100px;
				}
				.right-column {
					width: 95px;
					margin-right: -100px;
					text-align: right;
				}
				.panLeft, .panRight {
					font-size: 48px;
					text-decoration: none;
				}
				.zoom {
					display: inline;
				}
				.jump {
					display: inline;
				}
			</style>
		</head>
		<body>
			<div class="controls">
				<div class="center-column">
					<div class="zoom">
						-
						<input type="radio" name="zoom" value="1" id="zoom1" checked="checked"/>
						<input type="radio" name="zoom" value="2" id="zoom2"/>
						<input type="radio" name="zoom" value="3" id="zoom3"/>
						<input type="radio" name="zoom" value="4" id="zoom4"/>
						<input type="radio" name="zoom" value="5" id="zoom5"/>
						+
					</div>
					<span style="margin: 10px">|</span>
					<div class="jump">
						Jump to:
						<input type="text" id="jumpField"/>
						<button>Go</button>
					</div>
				</div>
				<div class="left-column">
					<a href="javascript:void(0);" class="panLeft">&laquo;</a>
				</div>
				<div class="right-column">
					<a href="javascript:void(0);" class="panRight">&raquo;</a>
				</div>
			</div>
			<div id="viewer" style="height: 400px; border: 1px solid black; clear: both">
				Your browser does not support the core viewer. Sorry.
			</div>
		</body>
</html>