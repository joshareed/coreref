<html>
	<head>
		<title>Home | CoreRef</title>
		<meta name="layout" content="splash" />
		<g:javascript library="jquery.min" />
		<script type="text/javascript">
		$(function() {
			$.ajax({
				dataType: 'json',
				url: '${createLink(controller:"recent", action:"searches")}',
				success: function(data, status) {
					$.each(data, function(i, val) {
						$('<li class="recentSearch"></li>').append(
							$('<a></a>').attr('href', 'projects/' + val.project + '/search?q=' + val.query).text("'" + val.query + "' in " + val.project)
						).appendTo($('#recent'));
					});
				}
			});
		});
		</script>
		<style type="text/css" media="screen">
			div.desc {
				margin-top: 1em;
				margin-bottom: 1em;
			}
		</style>
	</head>
	<body>
		<div id="leftSidebar">
			<h3>Popular Cores</h3>
			<ul>
				<li style="height: 16px">
					<g:link controller="project" action="viewer" params="[project: 'and1-1b', depth: 211]">ANDRILL MIS - Diatomite</g:link>
				</li>
				<li>
					<g:link controller="project" action="viewer" params="[project: 'and2-2a', depth: 430.4]">ANDRILL SMS - Pectin Shells</g:link>
				</li>
				<li>
					<g:link controller="project" action="viewer" params="[project: 'odp171-1049b', depth: 110.97]">ODP 1049 - K/T Boundary</g:link>
				</li>
			</ul>
			<h3 style="margin-top: 0.5em">Programs</h3>
			<ul>
				<li><g:link controller="collection" action="index" params="[collection: 'and']">ANDRILL</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'icdp']">ICDP</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'iodp']">IODP</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'odp']">ODP</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'all']">All</g:link></li>
			</ul>
			<h3 style="margin-top: 0.5em">Recent Searches</h3>
			<ul id="recent"></ul>
		</div>
		<div id="main">
			<h1>About CoreRef</h1>
			<p style="font-size: 1.5em; line-height: 1.3em">
				CoreRef provides web-based access to core imagery and data for over 1600 marine, antarctic, and continental drill holes.  
				You can browse projects by 
				<g:link controller="collection" action="index" params="[collection: 'all']">drilling program</g:link>
				or via a <a href="${resource(file:'map.gsp')}">map</a>.  Each project has an 
				<g:link controller="project" action="overview" params="[project: 'and1-1b']">overview page</g:link>, which provides a 
				description and related resources, and a 
				<g:link controller="project" action="viewer" params="[project: 'odp171-1049b', depth: 110.97]">core viewer page</g:link>, 
				which brings the core imagery and data directly into your web browser.
			</p>
			<div style="border: 1px solid #AAA; margin: 1em; padding: 10px; background-color: #EEE">
				Do you have images or data you'd like to make available?  <a href="${resource(file:'data.gsp')}">Let us know!</a> 
				We're always looking to partner with individuals or groups to improve existing projects or bring new projects online.
			</div>
		</div>
	</body>
</html>