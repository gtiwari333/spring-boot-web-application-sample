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

    //TODO: implement auto reconnect on disconnect

        var socket = new SockJS('/app-websockets-main-endpoint');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {

            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/global-messages', function (msg) {
                console.log(msg);
                $.toast({
                    text: msg.body,
                    icon: 'info',
                    allowToastClose: true,
                    hideAfter: 5000,
                    position: 'top-right',
                });
            });

            //this is used to send receive messages meant for specific user
            stompClient.subscribe('/user/topic/review-results', function (msg) {
                console.log("User messages" + msg);
                $.toast({
                    text: msg.body,
                    icon: 'info',
                    allowToastClose: true,
                    hideAfter: 5000,
                    position: 'top-right',
                });
            });
        });
    }
})();
