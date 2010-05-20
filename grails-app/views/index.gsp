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
					$('<li class="recentSearch"></li>').append(
						$('<a></a>').attr('href', 'projects/' + val.project + '/search?q=' + val.query).text("'" + val.query + "' in " + val.project)
					).appendTo($('#recent'));
				});
			}
		});
		</script>
	</head>
	<body>
		<div id="leftSidebar">
			<h3>Collections</h3>
			<ul>
				<li><g:link controller="collection" action="index" params="[collection: 'north-atlantic']">North Atlantic</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'equatorial-pacific']">Equatorial Pacific</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'paleoceanographic']">Paleoceanographic</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'highres-paleoceanographic']">High Res Paleoceanographic</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'new-jersey-margin']">New Jersey Margin</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'k-t-boundary']">K/T Boundary</g:link></li>
				<li><g:link controller="collection" action="index" params="[collection: 'coral-reef']">Coral Reef</g:link></li>
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
			<h1 style="margin-bottom: 0.25em">Featured Projects</h1>
			<ul>
				<li>
					ANDRILL
					<ul>
						<li>
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'and1-1b'])}">McMurdo Ice Shelf (MIS)</a>
						</li>
						<li>
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'and2-2a'])}">Southern McMurdo Sound (SMS)</a>
							<img id="secure" src="${resource(dir:'images',file:'secure.png')}" alt="Requires a Password" />
						</li>
					</ul>
				</li>
				<li>
					Lake El'gygytgyn Drilling Project
					<ul>
						<li>
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'icdp5011-1a'])}">Hole A</a>
							<img id="secure" src="${resource(dir:'images',file:'secure.png')}" alt="Requires a Password" />
						</li>
						<li>
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'icdp5011-1b'])}">Hole B</a>
							<img id="secure" src="${resource(dir:'images',file:'secure.png')}" alt="Requires a Password" />
						</li>
						<li>
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'icdp5011-1c'])}">Hole C</a>
							<img id="secure" src="${resource(dir:'images',file:'secure.png')}" alt="Requires a Password" />
						</li>
					</ul>
				</li>
				<li>
					ODP Leg 113
					<ul>
						<li>
							689 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0689A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0689B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0689C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0689D'])}">D</a>
						</li>
						<li>
							690 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0690A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0690B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0690C'])}">C</a>
						</li>
						<li>
							691 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0691A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0691B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0691C'])}">C</a>
						</li>
						<li>
							692 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0692A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0692B'])}">B</a>
						</li>
						<li>
							693 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0693A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0693B'])}">B</a>
						</li>
						<li>
							694 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0694A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0694B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0694C'])}">C</a>
						</li>
						<li>
							695 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0695A'])}">A</a>
						</li>
						<li>
							696 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0696A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0696B'])}">B</a>
						</li>
						<li>
							697 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0697A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0697B'])}">B</a>
						</li>
					</ul>
				</li>
				<li>
					ODP Leg 114
					<ul>
						<li>
							698 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0698A'])}">A</a>
						</li>
						<li>
							699 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0699A'])}">A</a>
						</li>
						<li>
							700 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0700A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0700B'])}">B</a>
						</li>
						<li>
							701 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0701A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0701B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0701C'])}">C</a>
						</li>
						<li>
							702 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0702A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0702B'])}">B</a>
						</li>
						<li>
							703 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0703A'])}">A</a>
						</li>
						<li>
							704 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0704A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0704B'])}">B</a>
						</li>
					</ul>
				</li>
				<li>
					ODP Leg 119
					<ul>
						<li>
							736 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0736A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0736B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0736C'])}">C</a>
						</li>
						<li>
							737 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0737A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0737B'])}">B</a>
						</li>
						<li>
							738 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0738A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0738B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0738C'])}">C</a>
						</li>
						<li>
							739 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0739A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0739B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0739C'])}">C</a>
						</li>
						<li>
							740 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0740A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0740B'])}">B</a>
						</li>
						<li>
							741 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0741A'])}">A</a>
						</li>
						<li>
							742 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0742A'])}">A</a>
						</li>
						<li>
							743 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0743A'])}">A</a>
						</li>
						<li>
							744 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0744A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0744B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0744C'])}">C</a>
						</li>
						<li>
							745 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0745A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0745B'])}">B</a>
						</li>
						<li>
							746 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0746A'])}">A</a>
						</li>
					</ul>
				</li>
				<li>
					ODP Leg 120
					<ul>
						<li>
							747 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0747A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0747B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0747C'])}">C</a>
						</li>
						<li>
							748 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0748A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0748B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0748C'])}">C</a>
						</li>
						<li>
							749 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0749A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0749B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0749C'])}">C</a>
						</li>
						<li>
							750 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0750A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0750B'])}">B</a>
						</li>
						<li>
							751 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-0751A'])}">A</a>
						</li>
					</ul>
				</li>
				<li>
					ODP Leg 177
					<ul>
						<li>
							1088 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1088A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1088B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1088C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1088D'])}">D</a>
						</li>
						<li>
							1089 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1089A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1089B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1089C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1089D'])}">D</a>
						</li>
						<li>
							1090 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1090A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1090B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1090C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1090D'])}">D</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1090E'])}">E</a>
						</li>
						<li>
							1091 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1091A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1091B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1091C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1091D'])}">D</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1091E'])}">E</a>
						</li>
						<li>
							1092 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1092A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1092B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1092C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1092D'])}">D</a>
						</li>
						<li>
							1093 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1093A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1093B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1093C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1093D'])}">D</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1093E'])}">E</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1093F'])}">F</a>
						</li>
						<li>
							1094 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1094A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1094B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1094C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1094D'])}">D</a>
						</li>
					</ul>
				</li>
				<li>
					ODP Leg 178
					<ul>
						<li>
							1095 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1095A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1095B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1095C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1095D'])}">D</a>
						</li>
						<li>
							1096 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1096A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1096B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1096C'])}">C</a>
						</li>
						<li>
							1097 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1097A'])}">A</a>
						</li>
						<li>
							1098 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1098A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1098B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1098C'])}">C</a>
						</li>
						<li>
							1099 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1099A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1099B'])}">B</a>
						</li>
						<li>
							1100 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1100A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1100B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1100C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1100D'])}">D</a>
						</li>
						<li>
							1101 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1101A'])}">A</a>
						</li>
						<li>
							1102 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1102A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1102B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1102C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1102D'])}">D</a>
						</li>
						<li>
							1103 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1103A'])}">A</a>
						</li>
					</ul>
				</li>
				<li>
					ODP Leg 188
					<ul>
						<li>
							1165 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1165A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1165B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1165C'])}">C</a>
						</li>
						<li>
							1166 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1166A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1166B'])}">B</a>
						</li>
						<li>
							1167 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1167A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1167B'])}">B</a>
						</li>						
					</ul>
				</li>
				<li>
					ODP Leg 189
					<ul>
						<li>
							1168 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1168A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1168B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1168C'])}">C</a>
						</li>
						<li>
							1169 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1169A'])}">A</a>
						</li>
						<li>
							1170 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1170A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1170B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1170C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1170D'])}">D</a>
						</li>
						<li>
							1171 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1171A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1171B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1171C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1171D'])}">D</a>
						</li>
						<li>
							1172 - <a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1172A'])}">A</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1172B'])}">B</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1172C'])}">C</a>,
							<a href="${createLink(controller:'project', action:'overview', params: [project: 'odp-1172D'])}">D</a>
						</li>
					</ul>
				</li>
			</ul>
		</div>
	</body>
</html>