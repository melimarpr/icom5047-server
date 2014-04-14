package controllers

import com.aerobal.data.dto._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import play.api.mvc.Action
import play.api.mvc.Controller
import com.google.gson.Gson

object Puts extends Controller {

	def update_experiment = Action { request =>
	val values = request.body.asFormUrlEncoded.get;
	val experimentId = values.get("experimentId").get(0).toLong;
	val nameOpt = values.get("name");
	val name = if(nameOpt.isDefined) {
	  nameOpt.get(0).toString;
	} else null;
	val experiment = updateExperiment(experimentId, Some(name));
	Ok(experiment.toString);
	}
	def update_session = Action { request => 
	val values = request.body.asFormUrlEncoded.get;
	val sessionId = values.get("sessionId").get(0).toLong;
	val nameOpt = values.get("name");
	val name = if(nameOpt.isDefined) {
	  nameOpt.get(0).toString;
	} else null;
	val descriptionOpt = values.get("desc");
	val description = if(descriptionOpt.isDefined) {
	  descriptionOpt.get(0).toString;
	} else null;
	val session = updateSession(sessionId, Some(name), Some(description));
	Ok(session.toString);
	}
	def update_user = Action { request =>
	val values = request.body.asFormUrlEncoded.get;
	val userId = values.get("userId").get(0).toLong;
	val nameOpt = values.get("name");
	val name = if(nameOpt.isDefined) {
	  nameOpt.get(0).toString;
	} else null;
	val emailOpt = values.get("email");
	val email = if(emailOpt.isDefined) {
	  emailOpt.get(0).toString;
	} else null;
	val experiment = updateUser(userId, Some(name), Some(email));
	Ok(experiment.toString);
	}
	def updateSession(sessionId: Long, name: Option[String], desc: Option[String]): SessionDto =  {
			val opt = Gets.getSession(sessionId);
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
			val updatedSession = Gets.getSession(sessionId);
			if(updatedSession.isDefined)
				updatedSession.get;
			else
				throw new Exception();
	}
	def updateExperiment(experimentId: Long, name: Option[String]): ExperimentDto = {
			val opt = Gets.getExperiment(experimentId);
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
			val updatedExperiment = Gets.getExperiment(experimentId);
			if(updatedExperiment.isDefined)
				updatedExperiment.get;
			else
				throw new Exception();
	}
	def updateUser(userId: Long, name: Option[String], email: Option[String]): UserDto = {
			val opt = Gets.getUser(userId);
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
			val updatedUser = Gets.getUser(userId);
			if(updatedUser.isDefined)
				updatedUser.get;
			else
				throw new Exception();
	}
}