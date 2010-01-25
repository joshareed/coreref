<html>
	<head>
		<title>${project.name} - Downloads | CoreRef</title>
		<meta name="layout" content="main" />
		<style type="text/css" media="screen">
			.downloads { margin-bottom: 10px; }
			.downloads li {
				margin-left: 8px;
				padding: 3px;
			}
			.left { float: left; padding-right: 10px; }
		</style>
	</head>
	<body>
		<div id="leftSidebar">
			<h3>Links</h3>
			<ul>
				<g:if test="${project?.homepage}">
					<li>
						<a href="${project?.homepage}">Project Homepage</a>
					</li>
				</g:if>
				<li>
					<g:link controller="project" action="overview" params="[collection: project.id]">Overview</g:link>
				</li>
				<li>
					<g:link controller="project" action="viewer" params="[collection: project.id]">Core Viewer</g:link>
				</li>
				<li>
					<g:link controller="project" action="timeline" params="[collection: project.id]">Timeline</g:link>
				</li>
				<li class="active">
					<g:link controller="project" action="downloads" params="[collection: project.id]">Downloads</g:link>
				</li>
			</ul>
		</div>
		<div id="main">
			<h1>${project.name}</h1>
			<p>Use the form below to download imagery and data.</p>
			<div style="padding-bottom: 10px; margin-top: 15px">
				<span style="color: #CC0000; font-size: 120%; font-weight: bold">Depth Range</span>: <input type="text" size="4" value="0"/> - <input type="text" size="4" value="1300"/> m
			</div>
			<div class="left">
			<h3>Imagery</h3>
			<ul class="downloads">
				<li><input type="checkbox"> Slabbed (working)</li>
				<li><input type="checkbox"> Slabbed (archive)</li>
				<li><input type="checkbox"> Whole Round</li>
			</ul>
			</div>

			<div class="left">
			<h3>Data</h3>
			<ul class="downloads">
				<li><input type="checkbox"> Physical Properties</li>
				<li><input type="checkbox"> XRF Elements</li>
				<li><input type="checkbox"> Downhole Logs</li>
				<li><input type="checkbox"> Paleomag</li>
			</ul>
			</div>
			<div class="left">
			<h3>Files</h3>
			<ul class="downloads">
				<li><input type="checkbox"> Initial Report</li>
				<li><input type="checkbox"> Core Presentations</li>
				<li><input type="checkbox"> Detailed Logs</li>
				<li><input type="checkbox"> Summary Logs</li>
			</ul>
			</div>

			<div style="clear: both">
			<button style="padding-left: 5px; padding-right: 5px;">Download</button>
			</div>
		</div>
	</body>
</html>