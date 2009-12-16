<html>
    <head>
        <title><g:layoutTitle default="CoreRef" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
    </head>
    <body>
		<div id="container">
			<div id="header">
				<a href="${createLink(controller:'/')}">
					<img id="logo" src="${resource(dir:'images',file:'logo.png')}" alt="CoreRef Logo" />
				</a>
			</div>
			<div id="content">
	        	<g:layoutBody />			
			</div>
			<div id="footer">
				<div class="providedBy">hosting and services provided by <a href="http://andrill.org">ANDRILL</a></div>
			</div>
		</div>
    </body>
</html>