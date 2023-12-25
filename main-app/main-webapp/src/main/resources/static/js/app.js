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

    jQuery(document).ready(function () {
        //init username-link

        var userLink = jQuery(".username-link");
        userLink.mouseover(function (ev) {
            displayUserInfo(ev);
        });
        userLink.click(function (ev) {
            displayUserInfo(ev);
        });

        initStompJs();
    });


    function initStompJs() {
        stompClient = new StompJs.Client({
            brokerURL: 'ws://localhost:8081/app-websockets-main-endpoint',
            debug: function (str) {
                console.log(str);
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        stompClient.onConnect = function (frame) {

            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/global-messages', function (msg) {
                console.log("Global message" + msg);
                $.toast({
                    text: msg.body,
                    icon: 'info',
                    allowToastClose: true,
                    hideAfter: 5000,
                    position: 'top-right',
                });
            });

            //this (/user/* path) is used to send/receive messages meant for specific user
            stompClient.subscribe('/user/topic/review-results', function (msg) {
                console.log("User message" + msg);
                $.toast({
                    text: msg.body,
                    icon: 'info',
                    allowToastClose: true,
                    hideAfter: 5000,
                    position: 'top-right',
                });
            });
        }

        stompClient.onStompError = function (frame) {
            console.log('Broker reported error: ' + frame.headers['message']);
            console.log('Additional details: ' + frame.body);
        };

        stompClient.activate();
    }
})();
