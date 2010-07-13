<html>
	<head>
		<title>Home | CoreRef</title>
		<meta name="layout" content="splash" />
		<g:javascript library="jquery.min" />
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script> 
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

			// create a map
			var map = new google.maps.Map(document.getElementById("map"), {
				zoom: 1,
				center: new google.maps.LatLng(0, 0),
				mapTypeId: google.maps.MapTypeId.HYBRID
			});
			
			var PROGRAMS = {
				andrill: 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=A|FF0000|000000',
				odp: 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=O|0000FF|000000',
				icdp: 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=C|00FF00|000000'
			};
			
			// add our markers
			$.ajax({
				dataType: 'json',
				url: '${createLink(controller:"project", action:"list")}',
				success: function(data, status) {
					var currentlyOpen;
					var root = "${createLinkTo(dir: 'projects', absolute: true)}/";
					$.each(data, function(i, val) {
						if (!isNaN(val.latitude - 0) && !isNaN(val.longitude)) {
							var lat = parseFloat(val.latitude);
							var lng = parseFloat(val.longitude);
							var desc = '<div><h3>' + val.name + '</h3><div class="desc">' + val.description + '</div><div>' +
								'<a href="' + root + val.id + '/overview">Overview</a> | ' + 
								'<a href="' + root + val.id + '/viewer">Core Viewer</a> | ' + 
								'<a href="' + root + val.id +'/search">Search</a>' +
								'</div></div>';
							var info = new google.maps.InfoWindow({ content: desc });
							var icon = 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=A|FFFF00|000000';
							var marker = new google.maps.Marker({
								position: new google.maps.LatLng(lat, lng),
								map: map,
								title: val.name,
								icon: PROGRAMS[val.program]
							});
							google.maps.event.addListener(marker, 'click', function() {
								if (currentlyOpen) {
									currentlyOpen.close();
								}
								info.open(map, marker);
								currentlyOpen = info;
							});
							google.maps.event.addListener(info, 'closeclick', function() {
								info.close();
								currentlyOpen = null;
							});
						}
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
					<g:link controller="project" action="viewer" params="[project: 'odp-1049B', depth: 110.97]">ODP 1049 - K/T Boundary</g:link>
				</li>
			</ul>
			<h3 style="margin-top: 0.5em">Programs</h3>
			<ul>
				<li><g:link controller="collection" action="index" params="[collection: 'andrill']">ANDRILL</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'icdp']">ICDP</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'odp']">ODP</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'all']">All</g:link></li>
			</ul>
			<h3 style="margin-top: 0.5em">Recent Searches</h3>
			<ul id="recent"></ul>
		</div>
		<div id="main">
			<div id="map" style="width: 100%; height: 600px;"></div>
		</div>
	</body>
</html>