package controllers

import com.aerobal.data.dto._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import play.api.mvc.Action
import play.api.mvc.Controller
import com.google.gson.Gson
import cryptography.PasswordHash
import exceptions.ForbiddenAccessException
import exceptions.InternalServerErrorException

object Puts extends Controller {

	def update_experiment = Action { 
		request => 
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val token = values.get(Constants.TOKEN_TEXT).getOrElse(throw new ForbiddenAccessException("No token passed."))(0);
			val experimentId = values.get(Constants.EXPERIMENT_SELF_ID_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.EXPERIMENT_SELF_ID_TEXT+ "\' is missing."))(0).toLong;
			if(!Application.isExperimentFromUser(experimentId, token)) {
				throw new ForbiddenAccessException("User does not have permission to modifiy this experiment.");
			}
			val name = values.get(Constants.EXPERIMENT_NAME_TEXT).getOrElse(null)(0);
			val experimentOpt = updateExperiment(experimentId, Some(name), token);
			val experiment = experimentOpt.getOrElse(throw new InternalServerErrorException("Something went wrong..."));
			Ok(experiment.toString).as("application/json");
		}		
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}
	}
	def update_session = Action { 
		request => 
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val token = values.get(Constants.TOKEN_TEXT).getOrElse(throw new ForbiddenAccessException("No token passed."))(0);
			val sessionId = values.get(Constants.SESSION_SELF_ID_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.SESSION_SELF_ID_TEXT + "\' is missing."))(0).toLong;
			if(!Application.isSessionFromUser(sessionId, token)) {
				throw new ForbiddenAccessException("User does not have permission to modifiy this session.");
			}
			val nameOpt = values.get(Constants.SESSION_NAME_TEXT);
			val name = if(nameOpt.isDefined) {
				nameOpt.get(0).toString;
			} else null;
			val descriptionOpt = values.get(Constants.SESSION_DESCRIPTION_TEXT);
			val description = if(descriptionOpt.isDefined) {
				descriptionOpt.get(0).toString;
			} else null;
			val sessionOpt = updateSession(sessionId, Some(name), Some(description), token);
			val session = sessionOpt.getOrElse(throw new InternalServerErrorException("Something went wrong..."));
			Ok(session.toString).as("application/json");
		}		
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}
	}
	def update_user = Action { 
		request =>
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val token = values.get(Constants.TOKEN_TEXT).getOrElse(throw new ForbiddenAccessException("No token passed."))(0);
			val userOpt = Gets.getUser(token);
			val user = userOpt.getOrElse(throw new ForbiddenAccessException("Invalid token."));
			val nameOpt = values.get(Constants.USER_NAME_TEXT);
			val name = nameOpt.getOrElse(null)(0); 
			val emailOpt = values.get(Constants.USER_EMAIL_TEXT);
			val email = emailOpt.getOrElse(null)(0);
			val updetedUserOpt = updateUser(Some(name), Some(email), token);
			val updatedUser = userOpt.getOrElse(throw new InternalServerErrorException("Something went wrong..."));
			Ok(updatedUser.toString).as("application/json");
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}
	}
	def update_password = Action {
		request =>
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val token = values.get(Constants.TOKEN_TEXT).getOrElse(throw new ForbiddenAccessException("No token passed."))(0);
			val userOpt = Gets.getUser(token);
			val user = userOpt.getOrElse(throw new ForbiddenAccessException("Invalid token."));
			val currentPassword = values.get(Constants.UPDATE_PASSWORD_CURRENT_PASSWORD_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.UPDATE_PASSWORD_CURRENT_PASSWORD_TEXT + "\' is missing."))(0);
			val newPassword = values.get(Constants.UPDATE_PASSWORD_NEW_PASSWORD_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.UPDATE_PASSWORD_NEW_PASSWORD_TEXT + "\' is missing."))(0);
			val newHashOpt = updatePassword(currentPassword, newPassword, token);
			Ok("{\"success\":"+newHashOpt.isDefined+"}").as("application/json");
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}
	}
	def updateSession(sessionId: Long, name: Option[String], desc: Option[String], token: String): Option[SessionDto] =  {
			val sessionOpt = Gets.getSession(sessionId, token);
			val session = sessionOpt.getOrElse(return None);
			if(name.isDefined) {
				session.name = name.get;
			}
			if(desc.isDefined) {
				session.description =  desc.get;
			}
			val openSession = Application.sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.update(session);
			openSession.getTransaction().commit();
			val updatedSession = Gets.getSession(sessionId, token);
			updatedSession;
	}
	def updateExperiment(experimentId: Long, name: Option[String], token: String): Option[ExperimentDto] = {
			val experimentOpt = Gets.getExperiment(experimentId, token);
			val experiment = experimentOpt.getOrElse(return None);

			if(name.isDefined) {
				experiment.name = name.get;
			}
			val openSession = Application.sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.update(experiment);
			openSession.getTransaction().commit();
			val updatedExperiment = Gets.getExperiment(experimentId, token);
			updatedExperiment;
	}
	def updateUser(name: Option[String], email: Option[String], token: String): Option[UserDto] = {
			val userOpt = Gets.getUser(token);
			val user = userOpt.getOrElse(return None);
			if(name.isDefined) {
				user.name = name.get;
			}
			if(email.isDefined) {
				user.email = email.get;
			}
			val openSession = Application.sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.update(user);
			openSession.getTransaction().commit();
			val updatedUser = Gets.getUser(token);
			updatedUser;
	}
	def updatePassword(currentPassword: String, newPassword: String, token: String): Option[String] = {
			val userOpt = Gets.getUser(token);
			val user = userOpt.getOrElse(return None);
			if(PasswordHash.validatePassword(currentPassword, user.hash)) {
				val newHash = PasswordHash.createHash(newPassword);
				user.hash = newHash;
				val openSession = Application.sessionFactory.openSession();
				openSession.beginTransaction();
				val serial = openSession.update(user);
				openSession.getTransaction().commit();
				val updatedUser = Gets.getUser(token);
				Some(user.hash);
			} else {
				None
			}
	}
}