(function () {


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
    });


})();
