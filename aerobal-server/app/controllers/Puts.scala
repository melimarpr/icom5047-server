package controllers

import com.aerobal.data.dto._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import play.api.mvc.Action
import play.api.mvc.Controller
import com.google.gson.Gson
import cryptography.PasswordHash

object Puts extends Controller {

	def update_experiment = Action { 
		request =>
		val values = request.body.asFormUrlEncoded.get;
		val experimentId = values.get("experimentId").get(0).toLong;
		val token = values.get("token").get(0);
		if(Application.isExperimentFromUser(experimentId, token)) {

			val nameOpt = values.get("name");
			val name = if(nameOpt.isDefined) {
				nameOpt.get(0).toString;
			} else null;
			val experiment = updateExperiment(experimentId, Some(name), token);
			Ok(experiment.toString).as("application/json");
		}
		else {
			Forbidden("Invalid token or not found.");
		}
	}
	def update_session = Action { 
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val sessionId = values.get("sessionId").get(0).toLong;
		val token = values.get("token").get(0);
		if(Application.isSessionFromUser(sessionId, token)) {
			val nameOpt = values.get("name");
			val name = if(nameOpt.isDefined) {
				nameOpt.get(0).toString;
			} else null;
			val descriptionOpt = values.get("desc");

			val description = if(descriptionOpt.isDefined) {
				descriptionOpt.get(0).toString;
			} else null;
			val session = updateSession(sessionId, Some(name), Some(description), token);
			Ok(session.toString).as("application/json");
		}
		else {
			Forbidden("Invalid token or not found.");
		}
	}
	def update_user = Action { 
		request =>
		val values = request.body.asFormUrlEncoded.get;
		val token = values.get("token").get(0);
		val userOpt = Gets.getUser(token);
		if(userOpt.isDefined) {

			val nameOpt = values.get("name");
			val name = if(nameOpt.isDefined) {
				nameOpt.get(0).toString;
			} else null;
			val emailOpt = values.get("email");
			val email = if(emailOpt.isDefined) {
				emailOpt.get(0).toString;
			} else null;
			val experiment = updateUser(Some(name), Some(email), token);
			Ok(experiment.toString).as("application/json");
		}
		else {
			Forbidden("Invalid token or not found.");
		}
	}
	def update_password = Action {
	  		request =>
		val values = request.body.asFormUrlEncoded.get;
		val token = values.get("token").get(0);
		val userOpt = Gets.getUser(token);
		if(userOpt.isDefined) {

			val currentPassword = values.get("current").get(0);

			val newPassword = values.get("new").get(0);
			val newHashOpt = updatePassword(currentPassword, newPassword, token);
			Ok("{\"success\":"+newHashOpt.isDefined+"}").as("application/json");
		}
		else {
			Forbidden("Invalid token.");
		}
	}
	def updateSession(sessionId: Long, name: Option[String], desc: Option[String], token: String): SessionDto =  {
			val opt = Gets.getSession(sessionId, token);
			val session = if(opt.isDefined) {
				opt.get;
			} else {
				throw new Exception();
			}

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
			if(updatedSession.isDefined)
				updatedSession.get;
			else
				throw new Exception();
	}
	def updateExperiment(experimentId: Long, name: Option[String], token: String): ExperimentDto = {
			val opt = Gets.getExperiment(experimentId, token);
			val experiment = if(opt.isDefined) {
				opt.get;
			} else {
				throw new Exception();
			}

			if(name.isDefined) {
				experiment.name = name.get;
			}
			val openSession = Application.sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.update(experiment);
			openSession.getTransaction().commit();
			val updatedExperiment = Gets.getExperiment(experimentId, token);
			if(updatedExperiment.isDefined)
				updatedExperiment.get;
			else
				throw new Exception();
	}
	def updateUser(name: Option[String], email: Option[String], token: String): UserDto = {
			val opt = Gets.getUser(token);
			val user = if(opt.isDefined) {
				opt.get;
			} else {
				throw new Exception();
			}
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
			if(updatedUser.isDefined)
				updatedUser.get;
			else
				throw new Exception();
	}
	def updatePassword(currentPassword: String, newPassword: String, token: String): Option[String] = {
			val userOpt = Gets.getUser(token);
			if(userOpt.isDefined) {
				val user = userOpt.get;
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
			} else {
			  None
			}
	}
}