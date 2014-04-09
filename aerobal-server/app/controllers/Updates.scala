package controllers

import com.aerobal.data.dto._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import play.api.mvc.Action
import play.api.mvc.Controller
import com.google.gson.Gson

object Updates extends Controller {
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
			val updatedExperiment = Gets.getExperiment(serial.toString.toLong);
			if(updatedExperiment.isDefined)
				updatedExperiment.get;
			else
				throw new Exception();
	}
	def updateUser(userId: Long, email: Option[String], name: Option[String]): UserDto = {
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
			val updatedUser = Gets.getUser(serial.toString.toLong);
			if(updatedUser.isDefined)
				updatedUser.get;
			else
				throw new Exception();
	}
}