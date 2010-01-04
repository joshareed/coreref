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
				<a href="${createLinkTo(dir:'/')}">
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
		<!-- Google Analytics -->
		<script type="text/javascript">
			var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
			document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
		</script>
		<script type="text/javascript">
			try {
				var pageTracker = _gat._getTracker("UA-12175996-1");
				pageTracker._trackPageview();
			} catch(err) {}
		</script>
    </body>
</html>