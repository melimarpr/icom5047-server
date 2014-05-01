package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import javassist.NotFoundException
import exceptions.{InternalServerErrorException, InvalidPasswordException}
import play.mvc.Http.Session


object Website extends Controller{


  /*======================== Validates Checks ========================*/

  /**
   * Index Checks Session
   */
  def index = Action { request =>
    val tokenOpt = request.session.get(Constants.WEB_SESSION_TOKEN_KEY)
    if(validateToken(tokenOpt)){
      Redirect(routes.Website.panel)
    }
    else{
      Redirect(routes.Website.login).withNewSession
    }
  }

  /**
   * Returns Login Html
   * @return
   */
  def login = Action { request =>
    val tokenOpt = request.session.get(Constants.WEB_SESSION_TOKEN_KEY)

    if(validateToken(tokenOpt)){
      println("Login Token Pass")
      Redirect(routes.Website.panel)
    }
    else{
      Ok(views.html.login()).withNewSession
    }
  }

  /**
   * Auth for Website Sets Session
   * @return
   */
  def authWebsite = Action {
    request =>
      try {
        val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
        val user = values.get(Constants.AUTH_USER_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.AUTH_USER_TEXT + "\' is missing."))(0);
        val password = values.get(Constants.AUTH_PASSWORD_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.AUTH_PASSWORD_TEXT + "\' is missing."))(0);
        val auth = Posts.authenticate(user, password);
        if(auth.equals(Constants.AUTH_USER_NOT_FOUND_TEXT)) {
          throw new NotFoundException("User not found.");
        } else if(auth.equals(Constants.AUTH_INCORRECT_PASSWORD_TEXT)) {
          throw new InvalidPasswordException("Invalid password for user.");
        }
        //Valid Login Redirect
        Ok("Log-in").withSession(Constants.WEB_SESSION_TOKEN_KEY -> auth);
      }
      catch {
        case e: InvalidPasswordException => { BadRequest(e.getMessage()) };
        case e: NotFoundException => { NotFound(e.getMessage()) };
        case e: NoSuchElementException => { BadRequest(e.getMessage()) };
        case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
        case e: Exception => { InternalServerError("Something went wrong...") };
      }
  }

  /**
   * Auth Panel Access
   * @return
   */
  def panel = Action {
    request => {

      val token = request.session.get(Constants.WEB_SESSION_TOKEN_KEY)
      if (validateToken(token)) {
         Ok(views.html.panel())
      } else {
        Redirect(routes.Website.login).withNewSession
      }
    }
  }

  /**
   * Verifies Tokens from Request
   * @param tokenOption Token Option Recover from request.session
   * @return true: Valid Token, false: Not Valid Token
   */
  def validateToken(tokenOption:Option[String]):Boolean = {
    if(tokenOption.isDefined) {
      val userOpt = Gets.getUser(tokenOption.get)
      if(userOpt.isDefined){
        true
      }else{
        false
      }
    } else {
     false
    }
  }

  /**
   * Redirects to Login with New Session
   * @return Redirect to Login
   */
  def logout = Action{
    Redirect(routes.Website.login).withNewSession
  }







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




}
