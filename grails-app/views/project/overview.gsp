<html>
	<head>
		<title>${project.name} - Overview | CoreRef</title>
		<meta name="layout" content="main" />
		<g:javascript library="jquery.min" />
		<style type="text/css" media="screen">
			#holeOverview img {
				cursor: pointer;
			}
		</style>
		<script type="text/javascript">
		$(function() {
			var overview = new Image();
			$(overview).load(function() {
				$('#holeOverview').fadeIn();
			}).click(function(e) {
				var root = '${createLink(controller:"project", action:"viewer", params: [project: project.id])}';
				var offset = $(this).offset();
				var depth = (e.pageY - offset.top) / 500 * 10 + Math.floor((e.pageX - offset.left) / 15) * 10;
				window.location = root + '/' + depth;
			}).appendTo($('#holeOverview'));
			overview.src = '${createLink(controller:'admin', action:'updateHoleView', params: [project: project.id])}';
		});
		</script>
	</head>
	<body>
		<div id="leftSidebar">
			<h3>Tools</h3>
			<ul style="margin-bottom: 1em">
				<li class="active">
					<g:link controller="project" action="overview" params="[project: project.id]">Overview</g:link>
				</li>
				<li>
					<g:link controller="project" action="viewer" params="[project: project.id]">Core Viewer</g:link>
				</li>
				<li>
					<g:link controller="project" action="search" params="[project: project.id]">Search</g:link>
				</li>
			</ul>
			<g:if test="${project?.links || project?.homepage}">
				<h3>Related Links</h3>
				<ul style="margin-bottom: 1em">
					<g:if test="${project?.homepage}">
						<li>
							<a href="${project?.homepage}">Project Homepage</a>
						</li>
					</g:if>
					<g:each in="${project.links}" var="link">
						<li>
							<a href="${link.value}" target="_blank">${link.key}</a>
						</li>
					</g:each>
				</ul>
			</g:if>
		</div>
		<div id="main">
			<div id="rightNote">
				<table>
					<g:if test="${project?.startdate || project?.enddate}">
					<tr>
						<td><strong>Date</strong></td>
						<td>
							<g:if test="${project?.startdate && project?.enddate}">
								${project.startdate} to ${project.enddate}
							</g:if>
							<g:if test="${project?.startdate && !project?.enddate}">
								${project.startdate}
							</g:if>
							<g:if test="${!project?.startdate && project?.enddate}">
								${project.enddate}
							</g:if>
						</td>
					</tr>
					</g:if>
					<g:if test="${project?.latitude && project?.longitude}">
					<tr>
						<td><strong>Lat/Long</strong></td>
						<td>${project.latitude}&deg;, ${project.longitude}&deg;</td>
					</tr>
					</g:if>
					<g:if test="${project?.waterdepth}">
					<tr>
						<td><strong>Water Depth</strong></td>
						<td>${project.waterdepth} m</td>
					</tr>
					</g:if>
					<g:if test="${project?.base}">
					<tr>
						<td><strong>Total Depth</strong></td>
						<td>${project.base} m</td>
					</tr>
					</g:if>
					<g:if test="${project?.corerecovered}">
					<tr>
						<td><strong>Core Recovered</strong></td>
						<td>${project.corerecovered} m</td>
					</tr>
					</g:if>
					<g:if test="${project?.age}">
					<tr>
						<td><strong>Age</strong></td>
						<td>${project.age}</td>
					</tr>
					</g:if>
				</table>
			</div>
			<h1>${project.name}</h1>
			<g:if test="${project?.description}">
				<p>${project?.description}</p>
			</g:if>
			<g:if test="${related}">
				<strong>Related:</strong>
				<ul class="horiz">
					<g:each in="${related}" var="project">
					<li>
						<g:link controller="project" action="overview" params="[project: project.id]">${project.site}${project.hole}</g:link>
					</li>
					</g:each>
				</ul>
			</g:if>
			<div style="margin-top: 2em; display: none" id="holeOverview">
				<h3>Hole Overview</h3>
				<p>
					Below is a graphical representation of all core recovered from this hole.  Each column represents 10 meters of core.  Bright blue areas denote sections of no core recovery.  
					Clicking on the image will allow you to quickly jump to that specific depth in the hole.
				</p>
			</div>
		</div>
	</body>
</html>