<html>
	<head>
		<title>${project.name} | CoreRef</title>
		<meta name="layout" content="viewer" />
		<g:javascript library="jquery.min" />
		<g:javascript library="jquery.event.drag.min" />
		<g:javascript library="jquery.qtip-1.0.0-rc3.min" />
		<g:javascript library="coreviewer" />
		<script type="text/javascript">
		<%
			// HACK
			def min = 0
			def max = 5
			if (params?.depth) {
				def split = params.depth.split("-")
				if (split.length == 1) {
					min = split[0] as BigDecimal
					max = min + 5
				} else if (split.length == 2) {
					min = split[0] as BigDecimal
					max = split[1] as BigDecimal
				}
			}
		%>
			$(function() {
				var config = {
					top: ${min}, base: ${max}, scale: 2000, root: '${createLink(controller:"/")}',
					tracks: {
						whole: {
							path: function(opt) {
								return "${createLink(controller:'/')}/services/track/${project.collection}/whole/" + opt.top + "/" + opt.base + "/" + opt.scale;
							}
						},
						split: {
							path: function(opt) {
								return "${createLink(controller:'/')}/services/track/${project.collection}/split/" + opt.top + "/" + opt.base + "/" + opt.scale;
							}
						},
						lith: {
							path: function(opt) {
								return "${createLink(controller:'/')}/services/track/${project.collection}/lith/" + opt.top + "/" + opt.base + "/" + opt.scale;
							}
						},
						ruler: {
							path: function(opt) {
								return "${createLink(controller:'/')}/services/track/${project.collection}/ruler/" + opt.top + "/" + opt.base + "/" + opt.scale;
							}
						}
					},
					path: function(opt) {
						return "${createLink(controller:'/')}/projects/${project.collection}/viewer/" + opt.top;
					},
					descriptions: function(opt) {
						return "${createLink(controller:'/')}/services/search/${project.collection}/type/Description/" + opt.top + "/" + opt.base;
					}
				};
				new coreref.CoreViewer(".track", config);
			});
		</script>
	</head>
	<body>
		<div class="tools">
			Jump to: <input type="text" id="jumpField"/> <button id="jumpButton">Go</button>
		</div>
		<h1>
			${project.name}
		</h1>
		<table>
			<tr>
				<td class="left">
					<a id="moveLeft" href="#">&laquo;</a>
				</td>
				<td class="center">
					<div class="animated track" id="whole"></div>
					<div class="track" id="split"></div>
					<div class="track" id="ruler"></div>
					<div class="track" id="lith"></div>
				</td>
				<td class="right">
					<a id="moveRight" href="#">&raquo;</a>
				</td>
			</tr>
		</table>
	</body>
</html>