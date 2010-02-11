<html>
	<head>
		<title>Contact | CoreRef</title>
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
				width: 200px;
			}
			textarea {
				border: 1px solid #9A9A9A;
				width: 300px;
			}
			.row { margin: 1em; }
		</style>
	</head>
	<body>
		<p>Contact us using the form below:</p>
		<form action="${createLink(controller:'feedback', action:'contact')}" method="post">
			<div class="row">
				<label for="name">Name:</label>
				<input type="text" name="name" id="name"/>
				<span>(optional)</span>
			</div>
			<div class="row">
				<label for="email">Email:</label>
				<input type="text" name="email" id="email"/>
				<span>(optional)</span>
			</div>
			<div class="row">
				<label for="message">Message:</label>
				<textarea name="message" id="message" rows="8"></textarea>
			</div>
			<div class="row">
				<input type="submit" value="Submit"></input>
			</div>
		</form>
	</body>
</html>