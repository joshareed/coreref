<html>
	<head>
		<title>${project.name} - Overview | CoreRef</title>
		<meta name="layout" content="main" />
		<g:javascript library="jquery.min" />
		<script type="text/javascript">
		$.ajax({
			dataType: 'json',
			url: '${createLink(controller:"recent", action:"searches", params: [project: project.id])}',
			success: function(data, status) {
				$.each(data, function(i, val) {
					$('<li class="recentSearch"></li>').append(
						$('<a></a>').attr('href', 'search?q=' + val.query).text("'" + val.query + "'")
					).appendTo($('#recent'));
				});
			}
		});
		</script>
	</head>
	<body>
		<div id="leftSidebar">
			<h3>Links</h3>
			<ul>
				<li class="active">
					<g:link controller="project" action="overview" params="[project: project.id]">Overview</g:link>
				</li>
				<li>
					<g:link controller="project" action="viewer" params="[project: project.id]">Core Viewer</g:link>
				</li>
				<li>
					<g:link controller="project" action="search" params="[project: project.id]">Search</g:link>
				</li>
				<g:if test="${project?.homepage}">
					<li>
						<a href="${project?.homepage}">Project Homepage</a>
					</li>
				</g:if>
			</ul>
		</div>
		<div id="main">
			<div id="rightNote">
				<table>
					<tr>
						<td><strong>Date</strong></td>
						<td>
							<g:if test="${project.startdate && project.enddate}">
								${project.startdate} to ${project.enddate}
							</g:if>
							<g:if test="${project.startdate && !project.enddate}">
								${project.startdate}
							</g:if>
							<g:if test="${!project.startdate && project.enddate}">
								${project.enddate}
							</g:if>
						</td>
					</tr>
					<tr>
						<td><strong>Lat/Long</strong></td>
						<td>
							<g:if test="${project.latitude && project.longitude}">
								${project.latitude}&deg;, ${project.longitude}&deg;
							</g:if>
							<g:else>
								<em>unspecified</em>
							</g:else>
						</td>
					</tr>
					<tr>
						<td><strong>Depth</strong></td>
						<td>
							<g:if test="${project.base}">
								${project.base} m
							</g:if>
							<g:else>
								<em>unspecified</em>
							</g:else>
						</td>
					</tr>
					<tr>
						<td><strong>Age</strong></td>
						<td>
							<g:if test="${project.age}">
								${project.age}
							</g:if>
							<g:else>
								<em>unspecified</em>
							</g:else>
						</td>
					</tr>
				</table>
			</div>
			<h1>${project.name}</h1>
			<g:if test="${project?.description}">
				<p>${project?.description}</p>
			</g:if>
			<div style="margin: 2em">
				<form action="${createLink(controller:'project', action:'search', params: [project: project.id])}" method="get" target="_blank">
					<input type="text" name="q" value="${q}" id="q"/><input type="submit" value="Search"> or
					<span style="font-size: 125%">
						<g:link controller="project" action="viewer" params="[project: project.id, depth: Math.random() * project.base]">Surprise Me</g:link>
					</span>
				</form>
				<h3 style="margin-top: 1em">Recent Searches</h3>
				<ul id="recent"></ul>
			</div>
		</div>
	</body>
</html>