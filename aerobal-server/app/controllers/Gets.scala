package controllers

import com.aerobal.data.dto._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import play.api.mvc.Action
import play.api.mvc.Controller
import com.google.gson.Gson
import exceptions.InternalServerErrorException
import exceptions.ForbiddenAccessException
import javassist.NotFoundException

object Gets extends Controller {
	def user = Action {
		request => 
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);
			val userOpt = getUser(token);
			val user = userOpt.getOrElse(throw new ForbiddenAccessException("Invalid token used."));
			Ok(user.toString);
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}
	def session(id: Long) = Action { 
		request => 
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val sessionOpt = getSession(id, token);
			val session = sessionOpt.getOrElse(throw new NotFoundException("Id=" + id + " not found."));
			Ok(session.toString).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}

	def experiment(id: Long) = Action {
		request => 
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val experimentOpt = getExperiment(id,token);
			val experiment = experimentOpt.getOrElse(throw new NotFoundException("Id=" + id + " not found."));
			Ok(experiment.toString).as("application/json");
		}
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}
	def run(id: Long) = Action {	
		request => 
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val runOpt = getRun(id,token);
			val run = runOpt.getOrElse(throw new NotFoundException("Id=" + id + " not found."));
			Ok(run.toString).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}

	def measurement(id: Long) = Action {
		request => 
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val measurementOpt = getMeasurement(id,token);
			val measurement = measurementOpt.getOrElse(throw new NotFoundException("Id=" + id + " not found."));
			Ok(measurement.toString).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}
	def sessions(userId: Long) = Action {
		request =>
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val userOpt = getUser(userId);
			if(userOpt.isEmpty) {
				throw new NotFoundException("Id=" + userId + " not found.")
			}
			val sessions = getSessions(userId,token);
			Ok(new Gson().toJson(sessions.toArray)).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { InternalServerError("Something went wrong...") }
		}

	}
	def experiments(sessionId: Long) = Action {
		request =>
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val sessionOpt = getSession(sessionId,token);
			if(sessionOpt.isEmpty) {
				throw new NotFoundException("Id=" + sessionId + " not found.")
			}
			val experiments = getExperiments(sessionId, token);
			Ok(new Gson().toJson(experiments.toArray)).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}
	def runs(experimentId: Long) = Action {
		request =>
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val experimentOpt = getExperiment(experimentId, token);
			if(experimentOpt.isEmpty) {
				throw new NotFoundException("Id=" + experimentId + " not found.")
			}
			val runs = getRuns(experimentId, token);
			Ok(new Gson().toJson(runs.toArray)).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { InternalServerError("Something went wrong...") }
		}

	}
	def measurements(runId: Long) = Action {
		request =>
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val runOpt = getExperiment(runId, token);
			if(runOpt.isEmpty) {
				throw new NotFoundException("Id=" + runId + " not found.")
			}
			val measurements = getMeasurements(runId, token);
			Ok(new Gson().toJson(measurements.toArray)).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { InternalServerError("Something went wrong...") }
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
			query.setString(Constants.TOKEN_TEXT, token);
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
			query.setString(Constants.TOKEN_TEXT, token.trim());
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
			query.setString(Constants.TOKEN_TEXT, token);
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
			query.setString(Constants.TOKEN_TEXT, token);
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
			query.setString(Constants.TOKEN_TEXT, token);
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
	def getSessions(userId: Long,token: String): List[SessionDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.userId = :userId AND S.isActive = true AND " + 
					" (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR S.isPublic = true)";
			val query = session.createQuery(hql);
			query.setLong("userId", userId);
			query.setString(Constants.TOKEN_TEXT, token);
			val listResults = query.list();
			session.close();
			val tbrList = new ListBuffer[SessionDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[SessionDto]));
			tbrList.toList;
	}

	def getExperiments(sessionId: Long, token: String): List[ExperimentDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT E FROM ExperimentDto E, SessionDto S WHERE E.sessionId = :sessionId AND E.isActive = true AND " +
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR S.isPublic = true))";
			val query = session.createQuery(hql);
			query.setLong("sessionId", sessionId);
			query.setString(Constants.TOKEN_TEXT, token);
			val listResults = query.list();
			session.close();
			val tbrList = new ListBuffer[ExperimentDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[ExperimentDto]));
			tbrList.toList;
	}
	def getRuns(experimentId: Long, token: String): List[RunDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT R FROM RunDto R, ExperimentDto E, SessionDto S  WHERE R.experimentId = :experimentId  AND R.isActive = true AND " +
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR S.isPublic = true)))";
			val query = session.createQuery(hql);
			query.setLong("experimentId", experimentId);
			query.setString(Constants.TOKEN_TEXT, token);
			val listResults = query.list();
			session.close();
			val tbrList = new ListBuffer[RunDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[RunDto]));
			tbrList.toList;
	}
	def getMeasurements(runId: Long, token: String): List[MeasurementDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT M FROM MeasurementDto M, RunDto R, ExperimentDto E, SessionDto S WHERE M.runId = :runId AND M.isActive = true AND " +
					"(R.id = M.runId AND R.isActive = true AND " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR S.isPublic = true))))";
			val query = session.createQuery(hql);
			query.setLong("runId", runId);
			query.setString(Constants.TOKEN_TEXT, token);
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