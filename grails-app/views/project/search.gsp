<html>
	<head>
		<title>${project.name} - Search | CoreRef</title>
		<meta name="layout" content="main" />
		<style type="text/css" media="screen">
			#q {
				border: 1px solid #9A9A9A;
				width: 250px;
				margin: 1em;
				padding: 0.25em;
			}
			#results {
				margin-top: 1em;
				border-top: 1px solid #9A9A9A;
			}
			#total {
				color: #CC0000;
				background-color: #EFEFEF;
				padding: 0.25em;
				text-align: right;
			}
			.item {
				margin: 0.75em;
			}
			.link {
				font-size: 120%;
				text-decoration: underline;
			}
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
					<g:link controller="project" action="overview" params="[project: project.id]">Overview</g:link>
				</li>
				<li>
					<g:link controller="project" action="viewer" params="[project: project.id]">Core Viewer</g:link>
				</li>
				<li>
					<g:link controller="project" action="timeline" params="[project: project.id]">Timeline</g:link>
				</li>
				<li>
					<g:link controller="project" action="downloads" params="[project: project.id]">Downloads</g:link>
				</li>
			</ul>
		</div>
		<div id="main">
			<h1>${project.name}</h1>
			<form action="search" method="get">
				<input type="text" name="q" value="${q}" id="q"/><input type="submit" value="Search">
			</form>
			<g:if test="${results}">
				<div id="results">
					<div id="total">
						<strong>${results.size()}</strong> result${results.size() != 1 ? 's' : ''} for <strong>${q}</strong>
					</div>
					<g:each in="${results}">
					<div class="item">
						<div class="link">
							<a href="${it.link}">${it.top}m${it.base ? ' - ' + it.base + 'm' : ''}: ${it.title}</a>
						</div>
						<div class="text">${it.text}</div>
					</div>
					</g:each>
				</div>
			</g:if>
			<g:else>
				No results found for <strong>${q}</strong>
			</g:else>
		</div>
	</body>
</html>