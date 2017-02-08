function showImage(rawurl, localpath) {
	var elements = document.getElementsByTagName("img");
	for(var i= 0; i< elements.length; i++){
		var url = elements[i].getAttribute("image-attach-src");
		if (rawurl == url) {
			elements[i].setAttribute("src", localpath)
		}
	}
}

function getElementLeft(element){
	var actualLeft = element.offsetLeft;
	var current = element.offsetParent;
	while (current !== null){
		actualLeft += current.offsetLeft;
		current = current.offsetParent;
	}
	return actualLeft;
}

function getElementTop(element){
	var actualTop = element.offsetTop;
	var current = element.offsetParent;
	while (current !== null){
		actualTop += current.offsetTop;
		current = current.offsetParent;
	}
	return actualTop;
}

function doInject() {
    var images = document.getElementsByTagName("img");
    for(var i=0; i< images.length; i++){
    	var src = images[i].getAttribute("src");
    	var w = images[i].clientWidth;
    	var h = images[i].clientHeight;
    	images[i].setAttribute("image-attach-src", src);
    	images[i].setAttribute("src", "hybird://method/image_load?url=" + encodeURIComponent(src) + "&w=" + w + "&h=" + h);
    	images[i].onclick = function() {
    		var w = this.clientWidth;
		    var h = this.clientHeight;
		    var left = getElementLeft(this);
		    var top = getElementTop(this);
		    var src = this.getAttribute("image-attach-src");
		    window.location.href = "hybird://method/image_show?url=" + encodeURIComponent(src) + "&w=" + w + "&h=" + h + "&t=" + top + "&l=" + left;
    	}
    }
    alert('success');
}
doInject();
