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
			val sessionOpt = Gets.getSession(sessionId);
			if(sessionOpt.isDefined) {
				val session = sessionOpt.get;
				if(session.isActive) {
					val openSession = Application.sessionFactory.openSession();
					openSession.beginTransaction();
					session.isActive = false;
					openSession.update(session);
					openSession.getTransaction().commit();					
					true;
				} else {
					false;
				}
			}
			else {
				false;
			}
	}
	def deleteRun(runId: Long): Boolean = {
			val runOpt = Gets.getRun(runId);
			if(runOpt.isDefined) {
				val run = runOpt.get;
				if(run.isActive) {
					val openSession = Application.sessionFactory.openSession();
					openSession.beginTransaction();
					run.isActive = false;
					openSession.update(run);
					openSession.getTransaction().commit();
					true;
				} else {
					false;
				}
			}
			else {
				false;
			}
	}
	def deleteExperiment(experimentId: Long): Boolean = {
			val experimentOpt = Gets.getExperiment(experimentId);
			if(experimentOpt.isDefined) {
				val experiment = experimentOpt.get;
				if(experiment.isActive) {
					val openSession = Application.sessionFactory.openSession();
					openSession.beginTransaction();
					experiment.isActive = false;
					openSession.update(experiment);
					openSession.getTransaction().commit();
					true;
				} else {
					false;
				}
			}
			else {
				false;
			}
	}
}