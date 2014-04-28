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
import com.aerobal.data.serializers.GlobalGson
import com.aerobal.data.objects.Experiment
import com.aerobal.data.objects.Run
import com.aerobal.data.objects.Measurement

object Gets extends Controller {
	private val gson = GlobalGson.gson;
	def user = Action {
		request => 
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);
			val userOpt = getUser(token);
			val user = userOpt.getOrElse(throw new ForbiddenAccessException("Invalid token used."));
			Ok(user.toString);
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
		}
	}
	def session(id: Long) = Action { 
		request => 
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val sessionOpt = getSession(id, token);
			val session = sessionOpt.getOrElse(throw new NotFoundException("Id=" + id + " not found."));
			Ok(session.toString).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
		}
	}

	def experiment(id: Long) = Action {
		request => 
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val experimentOpt = getExperiment(id,token);
			val experiment = experimentOpt.getOrElse(throw new NotFoundException("Id=" + id + " not found."));
			Ok(experiment.toString).as("application/json");
		}
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
		}
	}
	def run(id: Long) = Action {	
		request => 
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val runOpt = getRun(id,token);
			val run = runOpt.getOrElse(throw new NotFoundException("Id=" + id + " not found."));
			Ok(run.toString).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
		}
	}

	def measurement(id: Long) = Action {
		request => 
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val measurementOpt = getMeasurement(id,token);
			val measurement = measurementOpt.getOrElse(throw new NotFoundException("Id=" + id + " not found."));
			Ok(measurement.toString).as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
		}
	}
	def sessions = Action {
		request =>
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val userOpt = getUser(token);
			val user = userOpt.getOrElse(throw new NotFoundException("No user found."));
			val sessions = getSessions(user.id,token,false);
			Ok("{\"payload\":"+gson.toJson(sessions.toArray)+"}").as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
		}

	}
	def experiments(sessionId: Long) = Action {
		request =>
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val sessionOpt = getSession(sessionId,token);
			if(sessionOpt.isEmpty) {
				throw new NotFoundException("Id=" + sessionId + " not found.")
			}
			val experiments = getExperiments(sessionId, token, false);
			Ok("{\"payload\":"+gson.toJson(experiments.toArray)+"}").as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
		}
	}
	def runs(experimentId: Long) = Action {
		request =>
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val experimentOpt = getExperiment(experimentId, token);
			if(experimentOpt.isEmpty) {
				throw new NotFoundException("Id=" + experimentId + " not found.")
			}
			val runs = getRuns(experimentId, token,false);
			Ok("{\"payload\":"+gson.toJson(runs.toArray)+"}").as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
		}

	}
	def measurements(runId: Long) = Action {
		request =>
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val runOpt = getRun(runId, token);
			if(runOpt.isEmpty) {
				throw new NotFoundException("Id=" + runId + " not found.")
			}
			val measurements = getMeasurements(runId, token,false);
			Ok("{\"payload\":"+gson.toJson(measurements.toArray)+"}").as("application/json");
		} 
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
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
	def fullExperiment(id: Long) = Action {
		request => 
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, List(""))(0);
			val experimentOpt = getDeepExperiment(id,token);
			val experiment = experimentOpt.getOrElse(throw new NotFoundException("Id=" + id + " not found."));
			Ok(experiment.toString).as("application/json");
		}
		catch {
		case e: NotFoundException => { NotFound(e.getMessage()) }
		case e: Exception => { e.printStackTrace(); InternalServerError("Something went wrong...") }
		}
	}
	def getUser(userId: Long): Option[UserDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM UserDto U WHERE U.id = :userId AND U.isActive = true";
			val query = session.createQuery(hql);
			query.setLong("userId", userId);
			val resultsList = query.list();
			session.close();
			if(!resultsList.isEmpty()) {
				Some(resultsList.get(0).asInstanceOf[UserDto]);
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
			val resultsList = query.list();
			session.close();
			if(!resultsList.isEmpty()) {
				Some(resultsList.get(0).asInstanceOf[UserDto]);
			}
			else {
				None
			}
	}
	def getSession(sessionId: Long, token: String): Option[SessionDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.id = :sessionId AND S.isActive = true " +  
					"AND (S.userId IN (select id from UserDto WHERE token = :token) OR (S.isPublic = true))";
			val query = session.createQuery(hql);
			query.setString(Constants.TOKEN_TEXT, token.trim());
			query.setLong("sessionId", sessionId);
			val resultsList = query.list();
			session.close();
			if(!resultsList.isEmpty()) {
				Some(resultsList.get(0).asInstanceOf[SessionDto]);
			}
			else {
				None
			}
	}
	def getExperiment(experimentId: Long, token: String): Option[ExperimentDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT E FROM ExperimentDto E, SessionDto S WHERE E.id = :experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR (S.isPublic = true)))";
			val query = session.createQuery(hql);
			query.setLong("experimentId", experimentId);
			query.setString(Constants.TOKEN_TEXT, token);
			val resultsList = query.list();
			session.close();
			if(!resultsList.isEmpty()) {
				Some(resultsList.get(0).asInstanceOf[ExperimentDto]);
			}
			else {
				None;
			}
	}
	def getDeepExperiment(experimentId: Long, token: String): Option[Experiment] = { 
			val experimentDto = getExperiment(experimentId, token).getOrElse(return None);
			val experiment = new Experiment(experimentDto);
			val runDtos = getRuns(experimentId,token, false);
			runDtos.foreach(runDto => {
				val runOpt = getDeepRun(runDto.id, token); 
				if(runOpt.isDefined) {
					experiment.runs.append(runOpt.get);
				}
			});
			Some(experiment);
	}

	def getDeepRun(runId: Long, token: String): Option[Run] = {
			val runDto = getRun(runId, token: String).getOrElse(return None);
			val run = new Run(runDto);
			val measurementDtos = getMeasurements(runId, token, false);
			measurementDtos.foreach(measurement =>  run.measurements.append(new Measurement(measurement)));
			Some(run);
	}
	def getRun(runId: Long,token: String): Option[RunDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT R FROM RunDto R, ExperimentDto E, SessionDto S WHERE R.id = :runId AND R.isActive = true AND " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR (S.isPublic = true))))";
			val query = session.createQuery(hql);
			query.setLong("runId", runId);
			query.setString(Constants.TOKEN_TEXT, token);
			val resultsList = query.list();
			session.close();
			if(!resultsList.isEmpty()) {
				Some(resultsList.get(0).asInstanceOf[RunDto]);
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
					"(SELECT id from UserDto WHERE token = :token) OR (S.isPublic = true)))))";
			val query = session.createQuery(hql);
			query.setLong("measurementId", measurementId);
			query.setString(Constants.TOKEN_TEXT, token);
			val resultsList = query.list();
			session.close();
			if(!resultsList.isEmpty()) {
				Some(resultsList.get(0).asInstanceOf[MeasurementDto]);
			}
			else {
				None
			}
	}
	def getUsers: List[UserDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM UserDto";
			val query = session.createQuery(hql);
			val resultsList = query.list();
			val tbrList = new ListBuffer[UserDto]();
			resultsList.foreach(x => tbrList.add(x.asInstanceOf[UserDto]));
			tbrList.toList;
	}
	def getSessions(userId: Long,token: String, showPublic: Boolean): List[SessionDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.userId = :userId AND S.isActive = true AND " + 
					" (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR (S.isPublic = true AND :showPublic = true))";
			val query = session.createQuery(hql);
			query.setLong("userId", userId);
			query.setBoolean("showPublic", showPublic);
			query.setString(Constants.TOKEN_TEXT, token);
			val resultsList = query.list();
			session.close();
			val tbrList = new ListBuffer[SessionDto]();
			resultsList.foreach(x => tbrList.add(x.asInstanceOf[SessionDto]));
			tbrList.toList;
	}

	def getExperiments(sessionId: Long, token: String, showPublic: Boolean): List[ExperimentDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT E FROM ExperimentDto E, SessionDto S WHERE E.sessionId = :sessionId AND E.isActive = true AND " +
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR (S.isPublic = true AND :showPublic = true)))";
			val query = session.createQuery(hql);
			query.setLong("sessionId", sessionId);
			query.setBoolean("showPublic", showPublic);
			query.setString(Constants.TOKEN_TEXT, token);
			val resultsList = query.list();
			session.close();
			val tbrList = new ListBuffer[ExperimentDto]();
			resultsList.foreach(x => tbrList.add(x.asInstanceOf[ExperimentDto]));
			tbrList.toList;
	}
	def getRuns(experimentId: Long, token: String, showPublic: Boolean): List[RunDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT R FROM RunDto R, ExperimentDto E, SessionDto S  WHERE R.experimentId = :experimentId  AND R.isActive = true AND " +
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR (S.isPublic = true AND :showPublic = true))))";
			val query = session.createQuery(hql);
			query.setLong("experimentId", experimentId);
			query.setBoolean("showPublic", showPublic);
			query.setString(Constants.TOKEN_TEXT, token);
			val resultsList = query.list();
			session.close();
			val tbrList = new ListBuffer[RunDto]();
			resultsList.foreach(x => tbrList.add(x.asInstanceOf[RunDto]));
			tbrList.toList;
	}
	def getMeasurements(runId: Long, token: String, showPublic: Boolean): List[MeasurementDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT M FROM MeasurementDto M, RunDto R, ExperimentDto E, SessionDto S WHERE M.runId = :runId AND M.isActive = true AND " +
					"(R.id = M.runId AND R.isActive = true AND " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR (S.isPublic = true AND :showPublic = true)))))";
			val query = session.createQuery(hql);
			query.setLong("runId", runId);
			query.setBoolean("showPublic", showPublic);
			query.setString(Constants.TOKEN_TEXT, token);
			val resultsList = query.list();
			session.close();
			val tbrList = new ListBuffer[MeasurementDto]();
			resultsList.foreach(x => tbrList.add(x.asInstanceOf[MeasurementDto]));
			tbrList.toList;
	}
	def getUserFromEmail(user: String): Option[UserDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM UserDto U WHERE U.email = :user";
			val query = session.createQuery(hql);
			query.setString("user", user.trim());
			val resultsList = query.list();
			session.close();
			if(resultsList.isEmpty()) {
				None
			} else {
				Some(resultsList(0).asInstanceOf[UserDto]);
			}
	}
	def getExperimentFromSessionIdAndName(sessionId: Long, name: String): Option[ExperimentDto] = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM ExperimentDto E WHERE E.sessionId = :sessionId AND E.name = :name";
			val query = session.createQuery(hql);
			query.setString("name", name);
			query.setLong("sessionId", sessionId);
			val resultsList = query.list();
			session.close();
			if(resultsList.isEmpty()) {
				None
			} else {
				Some(resultsList(0).asInstanceOf[ExperimentDto]);
			}
	}
}