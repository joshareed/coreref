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

					$('.jump input').keyup(function(e) {
						if (e.keyCode == '13') {
							e.preventDefault();
							var depth = $('#jumpField').val();
							if (!isNaN(depth - 0)) {
								viewer.lookAt(-depth * viewer.getScale(), viewer.height() / 2);
							}
						}
					});

					var depth = ${params.depth};
					if (depth) {
						viewer.pan(-depth * viewer.getScale(), 0);
					}
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
					margin-right: 5px;
				}
				.jump input {
					border: 1px solid #9A9A9A;
				}
			</style>
		</head>
		<body>
			<div id="tools">
				<div class="jump">
					Jump to: <input type="text" id="jumpField"/>
				</div>
				<span class="tool">
					<g:link controller="project" action="viewer" params="[project: project.id, depth: params.depth]">Standard Viewer</g:link>
				</span>
				<span class="tool">
					<a href="${createLinkTo(dir:'help')}" target="_blank">Help</a>
				</span>
			</div>
			<h1 style="margin: 15px">
				<g:link controller="project" action="overview" params="[project: project.id]">${project.name}</g:link>
			</h1>
			<div class="controls">
				<div class="center-column">
					&nbsp;
				</div>
				<div class="left-column">
					<a href="javascript:void(0);" class="panLeft">&laquo;</a>
				</div>
				<div class="right-column">
					<a href="javascript:void(0);" class="panRight">&raquo;</a>
				</div>
			</div>
			<div id="viewer" style="height: 600px; border: 3px solid #CC0000; clear: both">
				Your browser does not support the core viewer. Sorry.
			</div>
		</body>
</html>