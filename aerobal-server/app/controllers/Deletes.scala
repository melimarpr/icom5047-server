package controllers

import play.api.mvc.Controller
import play.api.mvc.Action

object Deletes extends Controller {

	def delete_session(sessionId: Long) = Action {
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val token = values.get("token").getOrElse(List(""))(0);
		val success = deleteSession(sessionId, token);
		Ok("{\"success\":"+success+"}").as("application/json");

	}
	def delete_run(runId: Long) = Action {
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val token = values.get("token").getOrElse(List(""))(0);
		val success = deleteRun(runId, token);
		Ok("{\"success\":"+success+"}").as("application/json");
	}

	def delete_experiment(experimentId: Long) = Action {
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val token = values.get("token").getOrElse(List(""))(0);
		val success = deleteExperiment(experimentId, token);
		Ok("{\"success\":"+success+"}").as("application/json");
	}
	//	def delete_measurement(measurementId: Long) = Action {
	//	  Ok(deleteMeasurement(measurementId).toString);
	//	}
	//	def delete_experiments(sessionId: Long) = Action {
	//		Ok(deleteExperiments(sessionId).toString);
	//	}
	def deleteSession(sessionId: Long, token: String): Boolean = {
			val session = Application.sessionFactory.openSession();	
			session.beginTransaction();
			deleteExperiments(session, sessionId);
			val hql = "UPDATE SessionDto S set isActive = false WHERE S.id = :sessionId AND S.isActive = true" + 
					"AND (S.userId IN (select id from UserDto WHERE token = :token) OR S.isPublic = true)";
			val query = session.createQuery(hql);
			query.setParameter("sessionId", sessionId);
			query.setString("token", token);
			val result = query.executeUpdate();
			session.getTransaction().commit();
			session.close();
			result > 0
	}

	def deleteRun(runId: Long, token: String): Boolean = {
			val session = Application.sessionFactory.openSession();	
			session.beginTransaction();
			deleteMeasurements(session, runId);
			val hql = "UPDATE RunDto R set isActive = false WHERE R.id = :runId AND R.isActive = true " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token) OR S.isPublic = true)))";
			val query = session.createQuery(hql);
			query.setParameter("runId", runId);
			query.setString("token", token);
			val result = query.executeUpdate();
			session.getTransaction().commit();
			session.close();
			result > 0
	}
	def deleteExperiment(experimentId: Long, token: String): Boolean = {
			val session = Application.sessionFactory.openSession();	
			session.beginTransaction();
			deleteRuns(session, experimentId);
			val hql = "UPDATE ExperimentDto E set isActive = false WHERE E.id = :experimentId AND E.isActive = true " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(select id from UserDto WHERE token = :token) OR S.isPublic = true))";
			val query = session.createQuery(hql);
			query.setParameter("experimentId", experimentId);
			query.setString("token", token);
			val result = query.executeUpdate();
			session.getTransaction().commit();
			session.close();
			result > 0
	}
	//	def deleteMeasurement(measurementId: Long): Boolean = {
	//		val session = Application.sessionFactory.openSession();	
	//	  val hql = "DELETE FROM MeasurementDto M "  + 
	//					"WHERE M.id = :measurementId";
	//			val query = session.createQuery(hql);
	//			query.setParameter("measurementId", measurementId);
	//			session.beginTransaction();
	//			val result = query.executeUpdate();
	//			session.getTransaction().commit();
	//			session.close();
	//			println("Rows affected: " + result);
	//			result > 0
	//	}

	private def deleteExperiments(session: org.hibernate.Session, sessionId: Long) {
		val experiments = Gets.getExperiments(sessionId);
		experiments.foreach(experiment => deleteRuns(session, experiment.id));
		val hql = "UPDATE ExperimentDto E set isActive = false WHERE E.sessionId = :sessionId AND E.isActive = true";
		val query = session.createQuery(hql);
		query.setParameter("sessionId", sessionId);
		val result = query.executeUpdate();
	}
	private def deleteRuns(session: org.hibernate.Session, experimentId: Long) {
		val runs = Gets.getRuns(experimentId);
		runs.foreach(run => deleteMeasurements(session, run.id));
		val hql = "UPDATE RunDto R set isActive = false WHERE R.experimentId = :experimentId AND R.isActive = true";
		val query = session.createQuery(hql);
		query.setParameter("experimentId", experimentId);
		val result = query.executeUpdate();
	}
	private def deleteMeasurements(session: org.hibernate.Session, runId: Long) {
		val hql = "UPDATE MeasurementDto M set isActive = false WHERE M.runId = :runId AND M.isActive = true";
		val query = session.createQuery(hql);
		query.setParameter("runId", runId);
		val result = query.executeUpdate();
	}
}