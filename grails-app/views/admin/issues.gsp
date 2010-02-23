<html>
	<head>
		<title>Issues | CoreRef</title>
		<meta name="layout" content="main" />
		<style type="text/css" media="screen">
			table {
				width: 100%;
				border-collapse: collapse;
			}
			td, th {
				border: 1px solid #C00;
				padding: 0.25em;
			}
			#issues {
				margin: 1em;
			}
		</style>
	</head>
	<body>
		<div id="issues">
			<table>
				<tr>
					<th>Project</th>
					<th>Type</th>
					<th>Depth</th>
					<th>Details</th>
					<th>&nbsp;</th>
				</tr>
			<g:each in="${issues}" var="issue">
				<tr>
					<td>${issue.project}</td>
					<td>${issue.type}</td>
					<td>
						<a href="${createLink(controller: 'project', action: 'viewer', params: [project: issue.project, depth: issue.depth])}">${issue.depth}</a>
					</td>
					<td>${issue.details}</td>
					<td style="text-align: center">
						<a href="${createLink(controller: 'admin', action: 'closeIssue', params: [project: issue.project, opt: issue.'_id'])}">Close</a>
					</td>
				</tr>
			</g:each>
			</table>
		</div>
	</body>
</html>