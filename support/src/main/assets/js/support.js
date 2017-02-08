//document ready
$(document).ready(function() {
	$("img").each(function(index, el) {
		var src = $(el).attr("src");
		var w = $(el).attr("width");
		var h = $(el).attr("height");

		$(el).attr('image-attach-src', src);
		$(el).attr('src', "hybird://method/image_load?url=" + encodeURIComponent(src))+ "&w=" + w + "&h=" + h;
		$(el).bind('click', function(event) {
			window.location.href = "hybird://method/image_show?url=" + encodeURIComponent($(this).attr('image-attach-src'));
		});
	});
});

//replace images
function showImage(rawurl, localpath) {
	$("[image-attach-src='"+rawurl+"']").attr('src', localpath);
}