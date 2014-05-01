'use strict';

//Global Variables
var main_website = "http://localhost:9000/";
$.ajaxSetup({ cache: false });


//Back Stack Vars
var back = "main-cont";
var curr;


function login(){
    //Get Data
    var user = $("#login-email").val();
    var pass = $("#login-password").val();

    //Show Loading Screen
    showLoading("#login-cont");
    $.post(
        main_website+"auth_website",//Url
        {
            "user": user,
            "password":pass
        },
        function(data ,status){
            if(status === "success"){
                window.location.replace(main_website+"panel");
            }
        }
    ).fail(function(jqXHR, textStatus, errorThrown){
        if(jqXHR.status == 404) {
            //Get Text
            $("#login-error").html(jqXHR.responseText);
            hideLoading("#login-cont");
            $("#login-error").show().delay(1500).fadeOut();
        }
    });
}


function showLoading(id){
    $(id).hide();
    $("#loading-cont").show();
}

function hideLoading(id){
      $("#loading-cont").hide();
      $(id).show();
}

//Forget
function forgotPass(){
    var email = $("#forgot-email").val();
    showLoading("#forgot-cont");
    $.get(
            main_website+"forgot_password",
            {
                "email": email
            }

     ).done(function(){
          //Success
          $("#loading-cont").css("display", "none");
          back = "login-cont";
          curr = "complete-cont";
          $("#success-message").html("An email has been send with your password");
          $("#complete-cont").css("display", "inline");
     });
}

function register(){
    var email = $("#register-email").val();
    var name = $("#register-name").val();
    var pass = $("#register-password").val();
    var passConf = $("#register-confirm-password").val()

    if(pass != passConf){
        $("#register-error").html("Passwords Don't Match");
        $("#register-error").show().delay(1500).fadeOut();
        return;
    }

    showLoading("#register-cont");
    $.post(
        main_website+"new_user",
        {
            "name":name,
            "email":email,
            "password":pass
        },
        function(){
             //Success
              $("#loading-cont").css("display", "none");
              back = "login-cont";
              curr = "complete-cont";
              $("#success-message").html("An email has been sent for account confirmation.");
              $("#complete-cont").css("display", "inline");

        }).fail(function(jqXHR, textStatus, errorThrown){
                  hideLoading("#register-cont");
                  $("#register-error").html("Passwords Don't Match");
                  $("#register-error").show().delay(1500).fadeOut();
         });




}



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




//Back Function Button
$(".btn-back").click(function(){

    $("#"+curr).css("display", "none");
    $("#"+back).css("display", "inline");
    curr=back;
    back = "main-cont";

})


//Dialog functions
$(function() {
    $( "#dialog" ).dialog({
      autoOpen: false,
      width: 600,
      modal: true,
      draggable: false,
      resizable: false,
      show: {
        effect: "fade",
        duration: 1000
      },
      hide: {
        effect: "fade",
        duration: 1000
      }
    });


 });

 $( "#terms" ).click(function() {
       $( "#dialog" ).dialog( "open" );
 });







