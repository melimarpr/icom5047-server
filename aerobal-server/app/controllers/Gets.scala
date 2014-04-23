package controllers

import com.aerobal.data.dto._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import play.api.mvc.Action
import play.api.mvc.Controller
import com.google.gson.Gson

object Gets extends Controller {
	def user = Action {
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val token = values.get("token").getOrElse(List(""))(0);
		val user = getUser(token);
		if(user.isDefined) {
			Ok(user.get.toString).as("application/json");
		} 
		else
		{
			Forbidden("Invalid Token");
		} 
	}
	def session(id: Long) = Action { 
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val name = values.get("token");
		val token = name.getOrElse(List(""))(0);
		val session = getSession(id, token);
		if(session.isDefined) {
			Ok(session.get.toString).as("application/json");
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}

	def experiment(id: Long) = Action {
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val name = values.get("token");
		val token = name.getOrElse(List(""))(0);
		val experiment = getExperiment(id,token);
		if(experiment.isDefined) {
			Ok(experiment.get.toString).as("application/json");
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}
	def run(id: Long) = Action {
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val name = values.get("token");
		val token = name.getOrElse(List(""))(0);
		val experiment = getExperiment(id,token);
		val run = getRun(id,token);
		if(run.isDefined) {
			Ok(run.get.toString).as("application/json");
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}

	def measurement(id: Long) = Action {
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val name = values.get("token");
		val token = name.getOrElse(List(""))(0);
		val measurement = getMeasurement(id,token);
		if(measurement.isDefined) {
			Ok(measurement.get.toString).as("application/json");			
		} else {
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
	def users = Action {
		val users = getUsers;
		if(!users.isEmpty) {
			Ok(new Gson().toJson(users.toArray)).as("application/json");
		} 
		else
		{
			NotFound("No users found.");
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
	def getUser(token: String): Option[UserDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM UserDto U WHERE U.token = :token AND U.isActive = true";
			val query = session.createQuery(hql);
			query.setString("token", token);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[UserDto]);
			}
			else {
				None
			}
	}
	def getSession(sessionId: Long, token: String): Option[SessionDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.id = :sessionId AND S.isActive = true " +  
					"AND (S.userId IN (select id from UserDto WHERE token = :token) OR S.isPublic = true)";
			val query = session.createQuery(hql);
			query.setString("token", token);
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
	def getExperiment(experimentId: Long, token: String): Option[ExperimentDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT E FROM ExperimentDto E, SessionDto S WHERE E.id = :experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR S.isPublic = true))";
			val query = session.createQuery(hql);
			query.setLong("experimentId", experimentId);
			query.setString("token", token);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[ExperimentDto]);
			}
			else {
				None
			}
	}
	def getRun(runId: Long,token: String): Option[RunDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT R FROM RunDto R, ExperimentDto E, SessionDto S WHERE R.id = :runId AND R.isActive = true AND " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR S.isPublic = true)))";
			val query = session.createQuery(hql);
			query.setLong("runId", runId);
			query.setString("token", token);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[RunDto]);
			}
			else {
				None
			}
	}

	def getMeasurement(measurementId: Long,token: String): Option[MeasurementDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT M FROM MeasurementDto M, RunDto R, ExperimentDto E, SessionDto S WHERE M.id = :measurementId AND M.isActive = true AND" + 
					"(R.id = M.runId AND R.isActive = true AND " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR S.isPublic = true))))";
			val query = session.createQuery(hql);
			query.setLong("measurementId", measurementId);
			query.setString("token", token);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[MeasurementDto]);
			}
			else {
				None
			}
	}
	def getUsers: List[UserDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM UserDto";
			val query = session.createQuery(hql);
			val results = query.list();
			val tbrList = new ListBuffer[UserDto]();
			results.foreach(x => tbrList.add(x.asInstanceOf[UserDto]));
			tbrList.toList;
	}
	def getSessions(userId: Long): List[SessionDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.userId = :userId AND S.isActive = true AND " + 
					" (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR S.isPublic = true)";
			val query = session.createQuery(hql);
			query.setLong("userId", userId);
			query.setString("token", Application.token);
			val listResults = query.list();
			session.close();
			val tbrList = new ListBuffer[SessionDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[SessionDto]));
			tbrList.toList;
	}

	def getExperiments(sessionId: Long): List[ExperimentDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT E FROM ExperimentDto E, SessionDto S WHERE E.sessionId = :sessionId AND E.isActive = true AND " +
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR S.isPublic = true))";
			val query = session.createQuery(hql);
			query.setLong("sessionId", sessionId);
			query.setString("token", Application.token);
			val listResults = query.list();
			session.close();
			val tbrList = new ListBuffer[ExperimentDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[ExperimentDto]));
			tbrList.toList;
	}
	def getRuns(experimentId: Long): List[RunDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT R FROM RunDto R, ExperimentDto E, SessionDto S  WHERE R.experimentId = :experimentId  AND R.isActive = true AND " +
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR S.isPublic = true)))";
			val query = session.createQuery(hql);
			query.setLong("experimentId", experimentId);
			query.setString("token", Application.token);
			val listResults = query.list();
			session.close();
			val tbrList = new ListBuffer[RunDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[RunDto]));
			tbrList.toList;
	}
	def getMeasurements(runId: Long): List[MeasurementDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT M FROM MeasurementDto M, RunDto R, ExperimentDto E, SessionDto S WHERE M.runId = :runId AND M.isActive = true AND " +
					"(R.id = M.runId AND R.isActive = true AND " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR S.isPublic = true))))";
			val query = session.createQuery(hql);
			query.setLong("runId", runId);
			query.setString("token", Application.token);
			val listResults = query.list();
			session.close();
			val tbrList = new ListBuffer[MeasurementDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[MeasurementDto]));
			tbrList.toList;
	}

	def getUserFromEmail(user: String): Option[UserDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM UserDto U WHERE U.email = :user";
			val query = session.createQuery(hql);
			query.setString("user", user.trim());
			val results = query.list();
			session.close();
			if(results.isEmpty()) {
				None
			} else {
				Some(results(0).asInstanceOf[UserDto]);
			}
	}
}