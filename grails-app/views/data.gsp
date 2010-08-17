<html>
	<head>
		<title>Data Submission | CoreRef</title>
		<meta name="layout" content="main" />
		<style type="text/css" media="screen">
			label {
				font-weight: bold;
				vertical-align: top;
				display: block;
				margin-bottom: 0.25em;
			}
			input[type='text'] {
				border: 1px solid #9A9A9A;
				width: 250px;
			}
			input[type='radio'] {
				margin: 0px 5px 0px 5px;
			}
			textarea {
				border: 1px solid #9A9A9A;
				width: 400px;
				padding: 5px;
			}
			.row { margin: 1em; }
			.hint {
				color: #AAAAAA;
			}
		</style>
		<g:javascript library="jquery.min" />
		<script type="text/javascript">
		$(function() {
			$('input.hint, textarea.hint').focus(function(){
				if ($(this).val() == $(this).attr('title')) {
					$(this).val('');
					$(this).removeClass('hint');
				}
			}).blur(function() {
				if ($(this).val() == '' && $(this).attr('title') != '') {
					$(this).val($(this).attr('title'));
					$(this).addClass('hint');
				}
			}).each(function() {
				if ($(this).attr('title') == '') { return; }
				if ($(this).val() == '') { 
					$(this).val($(this).attr('title')); 
				} else { 
					$(this).removeClass('hint'); 
				}
			});
			if (location.hash != '') {
				$("input[name='project']")[1].checked = true;
				$('#existing').val(location.hash.substring(1));
			}
		});
		</script>
	</head>
	<body>
		<p>
			Give us some details about the data and/or imagery you'd like to make available and one of our data managers will 
			follow up with you via the email address your provide.
		</p>
		<form action="${createLink(controller:'feedback', action:'data')}" method="post">
			<div class="row">
				<label for="name">Name:</label>
				<input type="text" name="name" id="name"/>
			</div>
			<div class="row">
				<label for="email">Email:</label>
				<input type="text" name="email" id="email"/>
			</div>
			<div class="row">
				<label for="project">Project:</label>
				<input type="radio" name="project" value="new">New</input> or 
				<input type="radio" name="project" value="existing" id="exisingRadio">Existing</input>
				<input type="text" name="existing" id="existing" style="width: 115px; margin-left: 5px"/>
			</div>
			<div class="row">
				<label for="message">Details:</label>
				<textarea name="message" id="message" rows="12" title="What type of data and images?  Is it under moratorium?  Is it currently archived anywhere?" class="hint"></textarea>
			</div>
			<div class="row">
				<input type="submit" value="Submit"></input>
			</div>
		</form>
	</body>
</html>