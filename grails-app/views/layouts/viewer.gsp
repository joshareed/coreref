<html>
    <head>
        <title><g:layoutTitle default="CoreRef" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="stylesheet" href="${resource(dir:'css',file:'viewer.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="application" />
    </head>
    <body>
		<div id="container">
			<div id="content">
	        	<g:layoutBody />			
			</div>
			<div id="footer">
				<div class="providedBy">hosting and services provided by <a href="http://andrill.org">ANDRILL</a></div>
				<a href="${createLink(controller:'/')}">
					<img id="logo" height="25" src="${resource(dir:'images',file:'logo.png')}" alt="CoreRef Logo" />
				</a>
			</div>
		</div>
    </body>
</html>