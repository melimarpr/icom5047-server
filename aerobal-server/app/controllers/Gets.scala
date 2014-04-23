package controllers

import com.aerobal.data.dto._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import play.api.mvc.Action
import play.api.mvc.Controller
import com.google.gson.Gson

object Gets extends Controller {
  
	def measurement(id: Long) = Action {
		val measurement = getMeasurement(id);
		if(measurement.isDefined) {
			Ok(measurement.get.toString).as("application/json");			
		} else {
			NotFound("Id=" + id + " not found.");
		}

	}
	def experiment(id: Long) = Action {
		val experiment = getExperiment(id);
		if(experiment.isDefined) {
			Ok(experiment.get.toString).as("application/json");
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}
	def run(id: Long) = Action {
		val run = getRun(id);
		if(run.isDefined) {
			Ok(run.get.toString).as("application/json");
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}
	def session(id: Long) = Action {
		val session = getSession(id);
		if(session.isDefined) {
			Ok(session.get.toString).as("application/json");
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}
	def user(id: Long) = Action {
		val user = getUser(id);
		if(user.isDefined) {
			Ok(user.get.toString).as("application/json");
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}
	def sessions(userId: Long) = Action {
		val sessions = getSessions(userId);
		if(!sessions.isEmpty) {
			Ok(new Gson().toJson(sessions.toArray)).as("application/json");
		} 
		else
		{
			NotFound("Id=" + userId + " not found.");
		} 
	}
	def experiments(sessionId: Long) = Action {
		val experiments = getExperiments(sessionId);
		if(!experiments.isEmpty) {
			Ok(new Gson().toJson(experiments.toArray)).as("application/json");
		} 
		else
		{
			NotFound("Id=" + sessionId + " not found.");
		} 
	}
	def runs(experimentId: Long) = Action {
		val runs = getRuns(experimentId);
		if(!runs.isEmpty) {
			Ok(new Gson().toJson(runs.toArray)).as("application/json");
		} 
		else
		{
			NotFound("Id=" + experimentId + " not found.");
		} 
	}
	def measurements(runId: Long) = Action {
		val measurements = getMeasurements(runId);
		if(!measurements.isEmpty) {
			Ok(new Gson().toJson(measurements.toArray)).as("application/json");
		} 
		else
		{
			NotFound("Id=" + runId + " not found.");
		} 
	}
	def getExperiment(experimentId: Long): Option[ExperimentDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM ExperimentDto E WHERE E.id = :experimentId AND E.isActive = true";
			val query = session.createQuery(hql);
			query.setLong("experimentId", experimentId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[ExperimentDto]);
			}
			else {
				None
			}
	}

	def getMeasurement(measurementId: Long): Option[MeasurementDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM MeasurementDto M WHERE M.id = :measurementId AND M.isActive = true";
			val query = session.createQuery(hql);
			query.setLong("measurementId", measurementId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[MeasurementDto]);
			}
			else {
				None
			}
	}
	def getRun(runId: Long): Option[RunDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM RunDto R WHERE R.id = :runId AND R.isActive = true";
			val query = session.createQuery(hql);
			query.setLong("runId", runId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[RunDto]);
			}
			else {
				None
			}
	}
	def getSession(sessionId: Long): Option[SessionDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.id = :sessionId AND S.isActive = true";
			val query = session.createQuery(hql);
			query.setLong("sessionId", sessionId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[SessionDto]);
			}
			else {
				None
			}
	}
	def getUser(userId: Long): Option[UserDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM UserDto U WHERE U.id = :userId AND U.isActive = true";
			val query = session.createQuery(hql);
			query.setLong("userId", userId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[UserDto]);
			}
			else {
				None
			}
	}
	def getSessions(userId: Long): List[SessionDto] = {
			val sessions = Application.sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.userId = :userId AND S.isActive = true";
			val query = sessions.createQuery(hql);
			query.setLong("userId", userId);
			val listResults = query.list();
			sessions.close();
			val tbrList = new ListBuffer[SessionDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[SessionDto]));
			tbrList.toList;
	}

	def getExperiments(sessionId: Long): List[ExperimentDto] = {
			val experiments = Application.sessionFactory.openSession();
			val hql = "FROM ExperimentDto E WHERE E.sessionId = :sessionId AND E.isActive = true";
			val query = experiments.createQuery(hql);
			query.setLong("sessionId", sessionId);
			val listResults = query.list();
			experiments.close();
			val tbrList = new ListBuffer[ExperimentDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[ExperimentDto]));
			tbrList.toList;
	}
	def getRuns(experimentId: Long): List[RunDto] = {
			val runs = Application.sessionFactory.openSession();
			val hql = "FROM RunDto R WHERE R.experimentId = :experimentId  AND R.isActive = true";
			val query = runs.createQuery(hql);
			query.setLong("experimentId", experimentId);
			val listResults = query.list();
			runs.close();
			val tbrList = new ListBuffer[RunDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[RunDto]));
			tbrList.toList;
	}
	def getMeasurements(runId: Long): List[MeasurementDto] = {
			val measurements = Application.sessionFactory.openSession();
			val hql = "FROM MeasurementDto M WHERE M.runId = :runId AND M.isActive = true";
			val query = measurements.createQuery(hql);
			query.setLong("runId", runId);
			val listResults = query.list();
			measurements.close();
			val tbrList = new ListBuffer[MeasurementDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[MeasurementDto]));
			tbrList.toList;
	}
	def getUserByUsernameOrEmail(user: String): Option[UserDto] = {
	  val session = Application.sessionFactory.openSession();
	  val hql = "FROM UserDto U WHERE U.username = :user OR U.email = :user";
	  val query = session.createQuery(hql);
	  query.setString("user", user.trim());
	  val results = query.list();
	  if(results.isEmpty()) {
	    None
	  } else {
	    Some(results(0).asInstanceOf[UserDto]);
	  }
	}
}