package controllers

import play.api.mvc.Controller
import play.api.mvc.Action

/**
 * Created by enrique on 4/19/14.
 */
object Website extends Controller{


  def browse = Action {
    //Return Default Browse Session HTML
    Ok(views.html.browse())
  }

  def no_session = Action{

    Ok(views.html.nosession())
  }

  def session_search = Action{


    Ok(views.html.result())
  }


  def index = Action { request =>
    val token = request.session.get("token")
    //Is Empty Redirect to Login
    if(token.isEmpty){
      Redirect(routes.Website.login)
    } else{
      Redirect(routes.Website.panel)
    }
  }

  def login = Action {
    Ok(views.html.login())
  }

  def panel = Action {

    Ok(views.html.panel())
  }

  def settings = Action {
    Ok(views.html.settings())
  }



  def sessions = Action{
    Ok(views.html.mysessions())
  }

  def main = Action{
    Ok(views.html.main())
  }

  def result =  Action {
     Ok(views.html.result())

  }

  def logout = Action{

    Ok("logout")
  }


}
