<html>
	<head>
		<title>Home | CoreRef</title>
		<meta name="layout" content="splash" />
		<g:javascript library="jquery.min" />
		<script type="text/javascript">
		$.ajax({
			dataType: 'json',
			url: '${createLink(controller:"recent", action:"searches")}',
			success: function(data, status) {
				$.each(data, function(i, val) {
					if (i < 10) {
						$('<li class="recentSearch"></li>').append(
							$('<a></a>').attr('href', 'projects/' + val.project + '/search?q=' + val.query).text("'" + val.query + "' in " + val.project)
						).appendTo($('#recent'));
					}
				});
			}
		});
		</script>
	</head>
	<body>
		<div id="leftSidebar">
			<h3>Recent Searches</h3>
			<ul id="recent"></ul>
		</div>
		<div id="main">
			<h1 style="margin-bottom: 0.25em">Featured Project</h1>
			<a href="${createLink(controller:'project', action:'overview', params: [project: 'and1-1b'])}">
				<img id="featured" src="${resource(dir:'images',file:'featured.png')}" alt="Featured Project" />
			</a>
		</div>
	</body>
</html>