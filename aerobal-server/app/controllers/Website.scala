package controllers

import play.api.mvc.Controller
import play.api.mvc.Action

/**
 * Created by enrique on 4/19/14.
 */
object Website extends Controller{



  def index = Action {
    Ok("Test");
  }

  def login = Action {
    Ok(views.html.login());
  }

  def panel = Action {

    Ok(views.html.panel());
  }


}
