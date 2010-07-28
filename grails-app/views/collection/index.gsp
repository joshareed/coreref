<html>
	<head>
		<title>${collection.name} | CoreRef</title>
		<meta name="layout" content="main" />
		<g:javascript library="jquery.min" />
		<style type="text/css" media="screen">
			div.entry {
				margin-top: 1em;
				padding: 1em 1em 0.0em 1em;
				border-top: 1px solid #AAA;
			}
		</style>
	</head>
	<body>
		<div id="leftSidebar">
			<h3>Contents</h3>
			<ul>
				<g:each in="${collection.projects}" var="program">
				<li>
					<a href="#${program.key}">${program.key.toUpperCase()}</a>
					<ul>
						<g:each in="${program.value}" var="leg">
						<li>
							<a href="#${program.key}-${leg.key}">${leg.key}</a>
						</li>
						</g:each>
					</ul>
				</li>
				</g:each>
			</ul>
		</div>
		<div id="main">
			<h1>${collection.name}</h1>
			<g:each in="${collection.projects}" var="program">
				<a name="${program.key}"></a>
				<g:each in="${program.value}" var="leg">
					<a name="${program.key}-${leg.key}"></a>
					<g:if test="${leg.value.size() > 0}">
						<div class="entry">
							<h2>${program.key.toUpperCase()} - ${leg.key}</h2>
							<p>
								${leg.value[0].description}
							</p>
							Holes:
							<ul class="horiz">
								<g:each in="${leg.value}" var="project">
								<li>
									<g:link controller="project" action="overview" params="[project: project.id]">${project.site}${project.hole}</g:link>
								</li>
								</g:each>
							</ul>
						</div>
					</g:if>
				</g:each>
			</g:each>
		</div>
	</body>
</html>