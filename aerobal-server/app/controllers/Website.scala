package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import javassist.NotFoundException
import exceptions.{ForbiddenAccessException, InternalServerErrorException, InvalidPasswordException}
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

  /**
   * Settings Route Token Exists
   * @return Settings HTML With Data
   */
  def settings = Action { request =>
    val tokenOption = request.session.get(Constants.WEB_SESSION_TOKEN_KEY)
    if(tokenOption.isDefined) {
      val userOpt = Gets.getUser(tokenOption.get)
      if(userOpt.isDefined){
        val user = userOpt.get
        Ok(views.html.settings(user.getName, user.getEmail))

      }else{
        Redirect(routes.Website.login).withNewSession
      }
    } else {
      Redirect(routes.Website.login).withNewSession
  }

  }

  def main = Action{ request =>
    val tokenOption = request.session.get(Constants.WEB_SESSION_TOKEN_KEY)
    val token = tokenOption.getOrElse("");
    if(!token.isEmpty){
      Ok(views.html.main())
    }
    else {
      Redirect(routes.Website.login).withNewSession
    }
  }

  /*======================== Puts ========================*/

  def update_profile = Action { request =>
    try {
      val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
      val token = request.session.get(Constants.WEB_SESSION_TOKEN_KEY)
      if(validateToken(token)){
        val nameOpt = values.get(Constants.USER_NAME_TEXT);
        val name = nameOpt.getOrElse(null)(0);
        val emailOpt = values.get(Constants.USER_EMAIL_TEXT);
        val email = emailOpt.getOrElse(null)(0);
        val updetedUserOpt = Puts.updateUser(Some(name), Some(email), token.get);
        if (updetedUserOpt.isDefined){
          Ok("ok");
        }
        else{
          BadRequest("Not Valid User")
        }
      } else{
        BadRequest("Not Valid Token")
      }
    }
    catch {
      case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
      case e: NoSuchElementException => { BadRequest(e.getMessage()) };
      case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
      case e: Exception => { InternalServerError("Something went wrong...") };
    }
  }

  def update_password = Action { request =>
    try {
      val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
      val token = request.session.get(Constants.WEB_SESSION_TOKEN_KEY)
      if(validateToken(token)){
        val currentPassword = values.get(Constants.UPDATE_PASSWORD_CURRENT_PASSWORD_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.UPDATE_PASSWORD_CURRENT_PASSWORD_TEXT + "\' is missing."))(0);
        val newPassword = values.get(Constants.UPDATE_PASSWORD_NEW_PASSWORD_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.UPDATE_PASSWORD_NEW_PASSWORD_TEXT + "\' is missing."))(0);
        val newHashOpt = Puts.updatePassword(currentPassword, newPassword, token.get);
        if (newHashOpt.isDefined){
          Ok("ok");
        }
        else{
          BadRequest("Not Valid Password")
        }
      } else{
        BadRequest("Not Valid Token")
      }
    }
    catch {
      case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
      case e: NoSuchElementException => { BadRequest(e.getMessage()) };
      case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
      case e: Exception => { InternalServerError("Something went wrong...") };
    }
  }


  def search_sessions(query: String, email: String ,order: String, start: Int, end: Int ) = Action { request =>
    try {

      //Token
      val tokenOption = request.session.get(Constants.WEB_SESSION_TOKEN_KEY)

      if (validateToken(tokenOption)) {

        val userOp = Gets.getUser(tokenOption.get)
        val isPublic = true
        val sessions = Gets.getSessions(tokenOption.get, isPublic )

        println(sessions);
        val querySession = sessions.filter(x => x.getName().contains(query) || x.getDescription().contains(email) ||  Gets.getUser(x.getId()).get.getEmail.contains(email));
        val finalSession = querySession.slice(if ( start < querySession.length) {
          start
        } else {
          0
        }, if(end < querySession.length) {
          end
        } else {
          querySession.length;
        })

        var sortedSessions = finalSession.sortWith( (x,y) => x.getName > y.getName)
        if(order.equals("Descending")){
          sortedSessions = finalSession.sortWith( (x,y) => x.getName < y.getName)

        }

        if (sortedSessions.length == 0){
          Ok(views.html.exclamation("No Sessions Found"));
        }
        else{

          Ok(views.html.exclamation("Placeholder"))
        }

      } else {
        BadRequest(views.html.exclamation("Invalid Access To Session "));
      }

    }catch {
      case e: NoSuchElementException => { BadRequest(e.getMessage())};
    }
  }








  //Old Stuff
  def browse = Action {
    //Return Default Browse Session HTML
    Ok(views.html.browse())
  }








  def sessions = Action{
    Ok(views.html.mysessions())
  }





}
