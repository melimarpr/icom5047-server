var main_website = "http://localhost:9000/";

//Load Main On Ready
$(document).ready(function(){

    $.get(
            main_website+"main",
            function(data){
                setDataToContainer(data);
            }
     );

});

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
$("#main-browse").click(function(){
    loadWaitingElement();
    $.get(
        main_website+"browse",
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

$("#main-search").keyup(function (e) {
    if (e.keyCode == 13) {
        loadWaitingElement();
        console.log("Este Cabron")
    }
});



function setDataToContainer(data){
    $("#container").html(data);
}

function loadWaitingElement(){
    $("#container").html('<div class="panel center-loader"><div class="loader" style="margin-bottom:200px;">Loading</div></div>');
}




//Browser On-Click

function browseSubmit(){

    //Get Data
    var query = $("#browse-session-query").val();
    var email = $("#browse-email").val();
    var order = $( "#browse-order option:selected" ).text();
    loadWaitingElement();
    $.get(
        main_website+"search_sessions",
        {
            "query": query,
            "email": email,
            "order": order
        }
    ).done( function(data, status){
        //Do Stuff
    });
}



//Static Content Generation
