<html>
	<head>
		<title>${project.name} - Core Viewer | CoreRef</title>
		<meta name="layout" content="viewer" />
		<g:javascript library="jquery.min" />
		<g:javascript library="jquery.event.drag.min" />
		<g:javascript library="jquery.qtip-1.0.0-rc3.min" />
		<g:javascript library="coreviewer" />
		<script type="text/javascript">
			var viewer = new coreref.CoreViewer('.track');

			// setup our in-page controls
			$(function() {
				$('#panRight').click(function() {
					viewer.pan(0.25);
					return false;
				});

				$('#panLeft').click(function() {
					viewer.pan(-0.25);
					return false;
				});

				$('#jumpButton').click(function() {
					var depth = parseFloat($('#jumpField').val());
					if (depth != NaN && depth != null) {
						viewer.lookAt(depth);
					}
				});
			});
		</script>
		<script type="text/javascript" src="${createLinkTo(dir:'services')}/${project.project}/config/viewer?callback=viewer.configure"></script>
	</head>
	<body>
		<div class="tools">
			Jump to: <input type="text" id="jumpField"/> <button id="jumpButton">Go</button>
		</div>
		<h1 style="margin: 15px">
			<g:link controller="project" action="overview" params="[collection: project.project]">${project.name}</g:link>
		</h1>
		<table>
			<tr>
				<td class="left">
					<a id="panLeft" href="#">&laquo;</a>
				</td>
				<td class="center">
					<div class="animated track" id="whole"></div>
					<div class="track" id="split"></div>
					<div class="track" id="ruler"></div>
					<div class="track" id="lith"></div>
				</td>
				<td class="right">
					<a id="panRight" href="#">&raquo;</a>
				</td>
			</tr>
		</table>
	</body>
</html>