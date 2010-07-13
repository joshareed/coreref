<html>
	<head>
		<title>${project.name} - Core Viewer | CoreRef</title>
		<meta name="layout" content="viewer" />
		<g:javascript library="jquery.min" />
		<g:javascript library="jquery.cookie.min" />
		<g:javascript library="jquery.event.drag.min" />
		<g:javascript library="jquery.qtip-1.0.0-rc3.min" />
		<g:javascript library="jquery.flot.min" />
		<g:javascript library="jquery.form.min" />
		<!--[if IE]><g:javascript library="excanvas.min" /><![endif]-->
		<g:javascript library="coreviewer" />
		<script type="text/javascript">
			var viewer = new coreref.CoreViewer('.track');

			// setup our in-page controls
			$(function() {
				$('#panRight').click(function() {
					viewer.pan(0.25);
					return false;
				});

				$('#panLeft').click(function() {
					viewer.pan(-0.25);
					return false;
				});

				$('#settingsTool').click(function(e) {
					// position the settings panel
					var p = $('#settingsTool').position();
					var w = $('#settingsTool').width();
					var settings = $('#coreviewerSettings');
					settings.css({
						top: p.top + 30,
						left: p.left + w - settings.width()
					});

					// hide/show the panel
					if (settings.is(':visible')) {
						settings.hide();
					} else {
						settings.show();
					}

					return false;
				});

				$('#reportIssueTool').click(function(e) {
					var issues = $('#reportIssue');
					if (issues.is(':visible')) {
						issues.hide();
					} else {
						issues.show();
						$('#depth').val(viewer.bounds().visible.top);
						$('#browser').val(navigator.userAgent);
						$('#url').val(window.location);
						$('#user').val($.cookie('username'));
					}
				});

				$('#reportIssue form').ajaxForm({
					success: function(responseText, statusText, xhr, $form) {
						alert('Your issue was successfully reported!');
						$('#reportIssue').fadeOut('slow');
						$('#details').val('');
						$.cookie('username', $('#user').val(), { expires: 30 });
					}
				});
			});
		</script>
		<script type="text/javascript" src="${createLinkTo(dir:'services')}/${project.id}/config/viewer?callback=viewer.configure"></script>
		<style type="text/css" media="screen">
			#reportIssue {
				border: 3px solid gold;
				background-color: #FFFF7F;
				width: 550px;
				margin: 0 auto;
				-moz-border-radius: 10px;
				-webkit-border-radius: 10px;
				display: none;
			}
			#reportIssue label {
				font-weight: bold;
				vertical-align: top;
				display: block;
				margin-bottom: 0.25em;
			}
			#reportIssue input[type='text'] {
				border: 1px solid #9A9A9A;
				width: 200px;
			}
			#reportIssue select { width: 200px; }
			#reportIssue textarea {
				border: 1px solid #9A9A9A;
				width: 300px;
			}
			#reportIssue .row { margin: 1em; }
			#reportIssue .box { float: left; }
		</style>
	</head>
	<body>
		<div id="tools">
			<form action="${createLink(controller:'project', action:'search', params: [project: project.id])}" method="get" target="_blank">
				<input type="text" name="q" value="${q}" id="q"/><input type="submit" value="Search">
			</form>
			<span id="linkTool" class="tool">
				<a href="${createLink(controller:'project', action:'viewer', params: [project: project.id, depth: depth])}">
					<img src="${resource(dir:'images',file:'link.png')}" alt="Link to this page" /> Link
				</a>
			</span>
			<span id="settingsTool" class="tool">
				<a href="javascript:void()">Settings</a>
			</span>
			<span id="reportIssueTool" class="tool">
				<a href="javascript:void()">
					<img src="${resource(dir:'images',file:'help.png')}" alt="Report Issue" /> Report Issue
				</a>
			</span>
			<span class="tool">
				<a href="${createLinkTo(dir:'help')}" target="_blank">Help</a>
			</span>
			<span class="tool">
				<g:link controller="project" action="experimental" params="[project: project.id, depth: params.depth]">*</g:link>
			</span>
		</div>
		<h1 style="margin: 15px">
			<g:link controller="project" action="overview" params="[project: project.id]">${project.name}</g:link>
		</h1>

		<div id="reportIssue">
			<form action="${createLink(controller:'feedback', action:'issue')}" method="post">
				<input type="hidden" name="project" id="project" value="${project.id}" />
				<input type="hidden" name="depth" id="depth" value="" />
				<input type="hidden" name="browser" id="browser" value="" />
				<input type="hidden" name="url" id="url" value="" />

				<div class="box">
					<div class="row">
						<label for="type">Issue:</label>
						<select name="type" id="type">
							<option value="alignment">Images not aligned</option>
							<option value="missing">Images or data missing</option>
							<option value="other">Other</option>
						</select>
					</div>
					<div class="row">
						<label for="user">Reported By:</label>
						<input type="text" name="user" id="user"/>
					</div>
				</div>
				<div class="box">
					<div class="row">
						<label for="details">Details:</label>
						<textarea name="details" id="details" rows="4"></textarea>
					</div>
				</div>
				<div style="text-align: right; margin: 0.5em;">
					<input type="submit" value="Report Issue"/>
				</div>
			</form>
			<div style="clear: both"></div>
		</div>

		<table id="viewer">
			<tr>
				<td class="left">
					<a id="panLeft" href="javascript:void()">&laquo;</a>
				</td>
				<td class="center" style="padding-left: 35px; padding-right: 35px">
					<div class="animated track" id="whole"></div>
					<div class="track" id="split"></div>
					<div class="track" id="ruler"></div>
					<div class="track" id="lith"></div>
					<div class="track" id="data"></div>
				</td>
				<td class="right">
					<a id="panRight" href="javascript:void()">&raquo;</a>
				</td>
			</tr>
		</table>
		<div id="coreviewerSettings">
			<p>Turn tracks on and off using the controls below:</p>
		</div>
	</body>
</html>