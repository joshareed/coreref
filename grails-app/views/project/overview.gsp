<html>
	<head>
		<title>${project.name} | CoreRef</title>
		<meta name="layout" content="main" />
	</head>
	<body>
		<div id="leftSidebar">
			<h3>Links</h3>
			<ul>
				<g:if test="${project?.homepage}">
					<li><a href="${project?.homepage}">Project Homepage</a></li>
				</g:if>
				<li>
					<g:link controller="project" action="viewer" params="[collection: project.collection]">Core Viewer</g:link>
				</li>
				<li>Downloads</li>
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
		</div>
	</body>
</html>