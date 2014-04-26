package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import exceptions.ForbiddenAccessException

object Deletes extends Controller {

	def delete_session(sessionId: Long) = Action {
		request => 
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);
			if(Application.isSessionFromUser(sessionId, token)) {
				throw new ForbiddenAccessException("Session does not belong to user or does not exist.");
			}
			val success = deleteSession(sessionId, token);
			Ok("{\"success\":"+success+"}").as("application/json");
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}
	def delete_run(runId: Long) = Action {
		request => 
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);
			if(Application.isRunFromUser(runId, token)) {
				throw new ForbiddenAccessException("Session does not belong to user or does not exist.");
			}
			val success = deleteRun(runId, token);
			Ok("{\"success\":"+success+"}").as("application/json");
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}

	def delete_experiment(experimentId: Long) = Action {
	  request => 
		try {
			val values = request.headers.toMap;
			val token = values.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);
			if(Application.isExperimentFromUser(experimentId, token)) {
				throw new ForbiddenAccessException("Session does not belong to user or does not exist.");
			}
			val success = deleteExperiment(experimentId, token);
			Ok("{\"success\":"+success+"}").as("application/json");
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}

	def deleteSession(sessionId: Long, token: String): Boolean = {
			val session = Application.sessionFactory.openSession();	
			session.beginTransaction();
			deleteExperiments(session, sessionId, token);
			val hql = "UPDATE SessionDto S set isActive = false WHERE S.id = :sessionId AND S.isActive = true" + 
					"AND (S.userId IN (select id from UserDto WHERE token = :token) OR S.isPublic = true)";
			val query = session.createQuery(hql);
			query.setParameter("sessionId", sessionId);
			query.setString(Constants.TOKEN_TEXT, token);
			val result = query.executeUpdate();
			session.getTransaction().commit();
			session.close();
			result > 0
	}

	def deleteRun(runId: Long, token: String): Boolean = {
			val session = Application.sessionFactory.openSession();	
			session.beginTransaction();
			deleteMeasurements(session, runId, token);
			val hql = "UPDATE RunDto R set isActive = false WHERE R.id = :runId AND R.isActive = true " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR S.isPublic = true)))";
			val query = session.createQuery(hql);
			query.setParameter("runId", runId);
			query.setString(Constants.TOKEN_TEXT, token);
			val result = query.executeUpdate();
			session.getTransaction().commit();
			session.close();
			result > 0
	}
	def deleteExperiment(experimentId: Long, token: String): Boolean = {
			val session = Application.sessionFactory.openSession();	
			session.beginTransaction();
			deleteRuns(session, experimentId, token);
			val hql = "UPDATE ExperimentDto E set isActive = false WHERE E.id = :experimentId AND E.isActive = true " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR S.isPublic = true))";
			val query = session.createQuery(hql);
			query.setParameter("experimentId", experimentId);
			query.setString(Constants.TOKEN_TEXT, token);
			val result = query.executeUpdate();
			session.getTransaction().commit();
			session.close();
			result > 0
	}
	private def deleteExperiments(session: org.hibernate.Session, sessionId: Long, token: String) {
		val experiments = Gets.getExperiments(sessionId, token,false);
		experiments.foreach(experiment => deleteRuns(session, experiment.id, token));
		val hql = "UPDATE ExperimentDto E set isActive = false WHERE E.sessionId = :sessionId AND E.isActive = true";
		val query = session.createQuery(hql);
		query.setParameter("sessionId", sessionId);
		val result = query.executeUpdate();
	}
	private def deleteRuns(session: org.hibernate.Session, experimentId: Long, token: String) {
		val runs = Gets.getRuns(experimentId, token,false);
		runs.foreach(run => deleteMeasurements(session, run.id, token));
		val hql = "UPDATE RunDto R set isActive = false WHERE R.experimentId = :experimentId AND R.isActive = true";
		val query = session.createQuery(hql);
		query.setParameter("experimentId", experimentId);
		val result = query.executeUpdate();
	}
	private def deleteMeasurements(session: org.hibernate.Session, runId: Long, token: String) {
		val hql = "UPDATE MeasurementDto M set isActive = false WHERE M.runId = :runId AND M.isActive = true";
		val query = session.createQuery(hql);
		query.setParameter("runId", runId);
		val result = query.executeUpdate();
	}
}