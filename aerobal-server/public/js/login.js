'use strict';

$.ajaxSetup({ cache: false });

//Back Stack Vars
var back = "main-cont";
var curr;



//Content Gen On Clicks
$("#btn-login").click(function(){

    //Erase Cl
    $("#main-cont").css("display", "none");
    back = "main-cont";
    curr = "login-cont";
    $("#login-cont").css("display", "inline");

});

$("#btn-forgot").click(function(){
    $("#login-cont").css("display", "none");
    back = "login-cont";
    curr = "forgot-cont";
    $("#forgot-cont").css("display", "inline");

});

$("#btn-register").click(function(){
    //Erase Cl
    $("#main-cont").css("display", "none");
    back = "main-cont";
    curr = "register-cont";
    $("#register-cont").css("display", "inline");
});

$("#btn-complete-register").click(function(){
    //Erase Cls
    $("#register-cont").css("display", "none");
    back = "main-cont";
    curr = "complete-cont";
    $("#complete-cont").css("display", "inline");
});


//Back Function Button
$(".btn-back").click(function(){

    $("#"+curr).css("display", "none");
    $("#"+back).css("display", "inline");
    curr=back;
    back = "main-cont";

})





//HTTP Clicks





