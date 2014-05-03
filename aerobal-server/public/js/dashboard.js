'use strict';

//Global Setup
$.ajaxSetup({ cache: false });

//Load Main On Ready
$(document).ready(function(){

    $.get(
            main_website+"main",
            function(data){
                setDataToContainer(data);
            }
     );

});

/*
* Navabar OnClicks
*/
function navbarSettings(){
    loadWaitingElement();
     $.get(
            main_website+"settings",//Url
            function(data){
                setDataToContainer(data);
            }
     );

}

function navbarBrowse(){
    loadWaitingElement();
    $.get(
        main_website+"browse",
        function(data){
            setDataToContainer(data);
        }
    );
}

//Search Bar
$("#navbar-search").keyup(function (e) {
    if (e.keyCode == 13) {
        loadWaitingElement();
         var query = $("#navbar-search").val();

         paging_browse_start = 0;
         paging_browse_end = 10;

         var data = {
             "query": query,
             "order":"Ascending",
             "email":"",
             "start": paging_browse_start,
             "end": paging_browse_end
         }

         loadWaitingElement();
         $.get(
             main_website+"search_sessions",
             data,
             function(data){
                 paging_browse_start += 10;
                 paging_browse_end   += 10;
                 setDataToContainer(data)
             }
         ).fail(function(jqXHR, textStatus, errorThrown){
                  console.log(jqXHR);
         });
    }
});




/*
* Helper Functions
*/

function setDataToContainer(data){
    $("#container").html(data);
}

function loadWaitingElement(){
    $("#container").html('<div id="loader" class="white-panel center"><div class="loader" style="margin-bottom:200px;">Loading</div></div>');
}


/* Settings OnClick */
function updatePassword(){
    var currPass = $("#settings-curr-password").val();
    var newPass = $("#settings-new-password").val();
    var newConfPass = $("#settings-conf-new-password").val();


    if(newPass !== newConfPass){
        toggleSettingsError("settings-password", "Passwords Don't Match", "error");
        return;
    }


    toggleSettingsSpinner("on", "settings-password")
     $.post(
            main_website+"update_password_website ",
            {
                "current":currPass,
                "new": newPass
            },
            function(){
                 $("#settings-curr-password").val("");
                 $("#settings-new-password").val("");
                 $("#settings-conf-new-password").val("");
                 toggleSettingsSpinner("off", "settings-password");
                 toggleSettingsError("settings-password", "Successful Change", "success");
            }
     ).fail(function(jqXHR, textStatus, errorThrown){
         console.log(jqXHR);
         $("#settings-curr-password").val("");
         $("#settings-new-password").val("");
         $("#settings-conf-new-password").val("");
         toggleSettingsSpinner("off", "settings-password");
         toggleSettingsError("settings-password", "Server Error", "error");
     });
}

function updateProfile(){
     var email = $("#settings-email").val();
    var name = $("#settings-name").val();
    toggleSettingsSpinner("on", "settings-profile")
     $.post(
            main_website+"update_profile",
            {
                "name":name,
                "email":email
            },
            function(){
                 toggleSettingsSpinner("off", "settings-profile");
                 toggleSettingsError("settings-profile", "Successful Change", "success");
            }
     ).fail(function(jqXHR, textStatus, errorThrown){
         toggleSettingsSpinner("off", "settings-profile");
         toggleSettingsError("settings-profile", jqXHR.responseText, "error");
     });



}

function toggleSettingsSpinner(state, id){
    if (state === "on"){
        $("#"+id+"-submit").hide();
        $("#"+id+"-loading").show();

    }else{ //off
        $("#"+id+"-loading").hide();
        $("#"+id+"-submit").show();

    }
}

function toggleSettingsError(id, msg, type){
        if(type === "error"){
            $("#"+id+"-error").removeClass();
            $("#"+id+"-error").addClass("error");


        } else if(type === "success"){
            $("#"+id+"-error").removeClass();
            $("#"+id+"-error").addClass("success");


        }

        $("#"+id+"-error").html(msg);
        $("#"+id+"-error").show().delay(delay_time).fadeOut();

}

/* Browse Screen */



//Main Content On Click
$("#main-settings").click(function(){

    //Set To Waiting Mode
    loadWaitingElement();
    $.get(
        main_website+"settings",//Url
        function(data){
            setDataToContainer(data);
        }
    );
});
$("#main-menu").click(function(){
    loadWaitingElement();
    $.get(
        main_website+"main",
        function(data){
            setDataToContainer(data);
        }
    );
});

$("#main-sessions").click(function(){
    loadWaitingElement();
    $.get(
        main_website+"my_sessions",
        function(data){
            setDataToContainer(data);
        }
    );
});



//Browser On-Click
function browseSubmit(){
    //Get Data
    var query = $("#browse-session-query").val();
    var email = $("#browse-email").val();
    var order = $( "#browse-order option:selected" ).text();


    var data = {
        "query": query,
        "order":order,
        "email": email,
        "start": paging_browse_start,
        "end": paging_browse_end
    }

     paging_browse_start = 0;
     paging_browse_end = 10;

    loadWaitingElement();
    $.get(
        main_website+"search_sessions",
        data,
        function(data){
            paging_browse_start += 10;
            paging_browse_end   += 10;
            setDataToContainer(data)
        }
    ).fail(function(jqXHR, textStatus, errorThrown){
        setDataToContainer(jqXHR.responseText);
    });
}



//Static Content Generation
