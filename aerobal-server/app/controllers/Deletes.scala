package controllers

import play.api.mvc.Controller
import play.api.mvc.Action

object Deletes extends Controller {

	def delete_session(sessionId: Long) = Action {
		Ok(deleteSession(sessionId).toString);
	}
	def delete_run(runId: Long) = Action {
		Ok(deleteRun(runId).toString);
	}
	def delete_experiment(experimentId: Long) = Action {
		Ok(deleteExperiment(experimentId).toString);
	}
	def deleteSession(sessionId: Long): Boolean = {
			val openSession = Application.sessionFactory.openSession();
			val hql = "DELETE FROM SessionDto S WHERE S.id = :sessionId";
			val query = openSession.createQuery(hql);
			query.setLong("sessionId", sessionId);
			val results = query.executeUpdate();
			openSession.close();
			results > 0
	}
	def deleteRun(runId: Long): Boolean = {
			val openSession = Application.sessionFactory.openSession();
			val hql = "DELETE FROM RunDto R WHERE R.id = :runId";
			val query = openSession.createQuery(hql);
			query.setLong("runId", runId);
			val results = query.executeUpdate();
			openSession.close();
			results > 0
	}
	def deleteExperiment(experimentId: Long): Boolean = {
			val openSession = Application.sessionFactory.openSession();
			val hql = "DELETE FROM ExperimentDto E WHERE E.id = :experimentId";
			val query = openSession.createQuery(hql);
			query.setLong("experimentId", experimentId);
			val results = query.executeUpdate();
			openSession.close();
			results > 0
	}
}