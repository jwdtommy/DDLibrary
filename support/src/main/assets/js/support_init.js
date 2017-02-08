function include(jsurl) {
    if (jsurl == null || typeof(jsurl) != 'string') return;
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.charset = 'utf-8';
    script.src = jsurl;
    document.head.appendChild(script);
}
include("js/jquery.js");
include("js/support.js");