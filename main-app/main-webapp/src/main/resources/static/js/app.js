(function () {

    var stompClient = null;

    function displayUserInfo(e) {
        console.log(e);

        jQuery.ajax({
            url: "account/user/" + "6cc2e517-e133-4708-bbc0-8725ed30c354",
            method: "GET",
            dataType: "html"
        }).done(function (html) {
            console.log(html);
        })

    }

    //common behaviour
    jQuery(document).ready(function () {
        //init username-link

        var userLink = jQuery(".username-link");
        userLink.mouseover(function (ev) {
            displayUserInfo(ev);
        });
        userLink.click(function (ev) {
            displayUserInfo(ev);
        });

        initSockJS();
    });


    function initSockJS() {
        var socket = new SockJS('/app-websockets-main-endpoint');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {

            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/review-results', function (msg) {
                console.log(msg);
                $.toast({
                    text: msg,
                    icon: 'info',
                    allowToastClose: true,
                    hideAfter: 5000,
                    position: 'top-right',
                });
            });
        });
    }
})();
