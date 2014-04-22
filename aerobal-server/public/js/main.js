var main_website = "http://localhost:9000/";


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


function setDataToContainer(data){
    $("#container").html(data);
}


function loadWaitingElement(){
    $("#container").html('<div class="loader">Loading</div>');
}










//Static Content Generation
