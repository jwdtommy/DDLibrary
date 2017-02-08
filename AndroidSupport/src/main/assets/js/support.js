//document ready
$(document).ready(function() {
	$("img").each(function(index, el) {
		var src = $(el).attr("src");
        var w = $(el).width();
        var h = $(el).height();

		$(el).attr('image-attach-src', src);
		$(el).attr('src', "hybird://method/image_load?url=" + encodeURIComponent(src) + "&w=" + w + "&h=" + h);
		$(el).bind('click', function(event) {
            var w = $(this).width();
            var h = $(this).height();
		    var left = $(this).offset().left;
		    var top = $(this).offset().top;
			window.location.href = "hybird://method/image_show?url=" + encodeURIComponent($(this).attr('image-attach-src')) + "&w=" + w + "&h=" + h + "&t=" + top + "&l=" + left;
		});
	});
});

//replace images
function showImage(rawurl, localpath) {
	$("[image-attach-src='"+rawurl+"']").attr('src', localpath);
	alert(localpath);
}

function include(jsurl) {
    if (jsurl == null || typeof(jsurl) != 'string') return;
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.charset = 'utf-8';
    script.src = jsurl;
    document.head.appendChild(script);
    alert("include" + jsurl);
}