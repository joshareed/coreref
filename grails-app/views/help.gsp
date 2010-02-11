<html>
	<head>
		<title>Help | CoreRef</title>
		<meta name="layout" content="main" />
	</head>
	<body>
		<div id="leftSidebar">
			<ul>
				<li><a href="#no-3d-core">Why is the 3D whole core track missing in some areas?</a></li>
				<li><a href="#image-no-lith">Why is there a core image but no lithology?</a></li>
				<li><a href="#lith-no-image">Why is there lithology but no core image?</a></li>
				<li><a href="#alignment">Why do the images not match up in some areas?</a></li>
				<li><a href="#smaller-images">Why do the core images get smaller deeper in the hole?</a></li>
			</ul>
		</div>
		<div id="main">
			<a name="top"></a>
			<h1>Frequently Asked Questions</h1>
			<p>Below are the answers to frequently asked questions about the CoreRef site and core viewer.</p>
			<div class="question">
				<a name="no-3d-core"></a>
				<h3>Why is the 3D whole core track missing in some areas?</h3>
				<p>
					Whole-round imaging involves placing the core on a set of rollers and rolling it so a camera can take a picture of its circumference.
					In some sections, the core was too soft or broken to image safely.
					<br/>
					<img src="${resource(dir:'images/help',file:'no-3d-core.png')}" />
				</p>
				<a href="#top" class="topLink">&uarr; Top</a>
			</div>
			<div class="question">
				<a name="image-no-lith"></a>
				<h3>Why is there a core image but no lithology?</h3>
				<p>
					The core may have been sampled between the time the core was imaged and when it was described. This is common in the case of
					time-sensitive samples such as interstitial water. The tooltips will usually contain more information.
					<br/>
					<img src="${resource(dir:'images/help',file:'image-no-lith.png')}" />
				</p>
				<a href="#top" class="topLink">&uarr; Top</a>
			</div>
			<div class="question">
				<a name="lith-no-image"></a>
				<h3>Why is there lithology but no image?</h3>
				<p>
					Core was recovered and described but could not be imaged. This may be because of drilling-related issues such as washed cores.
					The tooltips will usually contain more information.
					<br/>
					<img src="${resource(dir:'images/help',file:'lith-no-image.png')}" />
				</p>
				<a href="#top" class="topLink">&uarr; Top</a>
			</div>
			<div class="question">
				<a name="alignment"></a>
				<h3>Why do the images not match up in some areas?</h3>
				<p>
					The whole round images are taken before the cores are transported from the drill site to the science lab and may shift in their containers
					during transit.	 If you spot an alignment issue, please use the <span style="font-weight: bold; color: #C00">
					<img src="${resource(dir:'images',file:'help.png')}" /> Report Issue</span> link on the core viewer page to let us know so we
					can correct the issue.
					<br/>
					<img src="${resource(dir:'images/help',file:'alignment.png')}" />
				</p>
				<a href="#top" class="topLink">&uarr; Top</a>
			</div>
			<div class="question">
				<a name="smaller-images"></a>
				<h3>Why do the core images get smaller as you get deeper in the hole?</h3>
				<p>
					Two major considerations in drilling are core diameter and drilling depth.	It requires more power to drill large diameter cores
					than small diameter cores.	Similarly, it requires more power to drill at 1000m than it does at 100m.  To reach target depth,
					we must reduce the diameter of drill string as we get deeper in the hole resulting in narrower cores to image.
				</p>
				<div>
					<img src="${resource(dir:'images/help',file:'smaller-images-1.png')}" style="vertical-align: middle"/>
					<img src="${resource(dir:'images/help',file:'smaller-images-2.png')}" style="vertical-align: middle"/>
					<img src="${resource(dir:'images/help',file:'smaller-images-3.png')}" style="vertical-align: middle"/>
				</div>
				<a href="#top" class="topLink">&uarr; Top</a>
			</div>
		</div>
	</body>
</html>