<html>
	<head>
		<title>${project.name} - Core Viewer | CoreRef</title>
		<meta name="layout" content="viewer" />
		<g:javascript library="jquery.min" />
		<g:javascript library="jquery.event.drag.min" />
		<g:javascript library="jquery.qtip-1.0.0-rc3.min" />
		<g:javascript library="jquery.flot.min" />
		<!--[if IE]><g:javascript library="excanvas.min" /><![endif]-->
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
			});
		</script>
		<script type="text/javascript" src="${createLinkTo(dir:'services')}/${project.id}/config/viewer?callback=viewer.configure"></script>
	</head>
	<body>
		<div id="tools">
			<form action="${createLink(controller:'project', action:'search', params: [project: project.id])}" method="get" target="_blank">
				<input type="text" name="q" value="${q}" id="q"/><input type="submit" value="Search">
			</form>
			<span id="linkTool" class="tool">
				<a href="${createLink(controller:'project', action:'viewer', params: [project: project.id, depth: depth])}">
					<img id="logo" src="${resource(dir:'images',file:'link.png')}" alt="Link to this page" /> Link
				</a>
			</span>
		</div>
		<h1 style="margin: 15px">
			<g:link controller="project" action="overview" params="[project: project.id]">${project.name}</g:link>
		</h1>
		<table id="viewer">
			<tr>
				<td class="left">
					<a id="panLeft" href="#">&laquo;</a>
				</td>
				<td class="center">
					<div class="animated track" id="whole"></div>
					<div class="track" id="split"></div>
					<div class="track" id="ruler"></div>
					<div class="track" id="lith"></div>
					<div class="track" id="data"></div>
				</td>
				<td class="right">
					<a id="panRight" href="#">&raquo;</a>
				</td>
			</tr>
		</table>
	</body>
</html>