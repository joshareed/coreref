<html>
	<head>
		<title>Home | CoreRef</title>
		<meta name="layout" content="splash" />
		<g:javascript library="jquery.min" />
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script> 
		<script type="text/javascript">
		$(function() {
			// create a map
			var map = new google.maps.Map(document.getElementById("map"), {
				zoom: 1,
				center: new google.maps.LatLng(0, 0),
				mapTypeId: google.maps.MapTypeId.HYBRID
			});
			
			var PROGRAMS = {
				and: 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=A|FF0000|000000',
				odp: 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=O|0000FF|000000',
				iodp: 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=I|0000FF|000000',
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
			<h3 style="margin-top: 0.5em">Programs</h3>
			<ul>
				<li><g:link controller="collection" action="index" params="[collection: 'and']">ANDRILL</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'icdp']">ICDP</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'iodp']">IODP</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'odp']">ODP</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'all']">All</g:link></li>
			</ul>
		</div>
		<div id="main">
			<p>
			Below is a map of all the project sites available in CoreRef.  There are over 1600 projects so the map takes a bit of time to load.  Please be patient.
			</p>
			<div id="map" style="width: 100%; height: 600px;"></div>
		</div>
	</body>
</html>