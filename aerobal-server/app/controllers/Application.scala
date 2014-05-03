package controllers

import org.hibernate.cfg.Configuration
import com.aerobal.data.dto._
import play.api.mvc.Action
import play.api.mvc.Controller
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import com.google.gson.Gson
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import java.security.SecureRandom
import java.math.BigInteger
import com.github.sendgrid.SendGrid
import credentials.SendGridCredentials

object Application extends Controller {
	//  val sf = JPA.em().getDelegate().asInstanceOf[Session].Gets.getSessionFactory();
	var token = "";
	var configFile = "hibernate.cfg.xml";
	val random = new SecureRandom();
	lazy val sessionFactory = { 
			val configuration = new Configuration().configure(configFile);
			val serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
					configuration.getProperties()).build();
			println("Starting to Build Session Factory"); 
			val tbr = configuration.buildSessionFactory(serviceRegistry);
			println("Finished building session Factory");
			tbr 
	}


	def setTestConfigFile() {
		configFile = "hibernatetest.cfg.xml";
		sessionFactory.toString();
	}
	def generateTokenString: String = {
			new BigInteger(130, random).toString(128);
	}
	def generateSaltString: String = {
			return new BigInteger(130, random).toString(128);
	}

	def isSessionFromUser(sessionId: Long, token: String): Boolean = {
			val session = Application.sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.id = :sessionId AND S.isActive = true " +  
					"AND (S.userId IN (select id from UserDto WHERE token = :token))";
			val query = session.createQuery(hql);
			query.setString("token", token);
			query.setLong("sessionId", sessionId);
			val listResults = query.list();
			session.close();
			!listResults.isEmpty()
	}
	def isExperimentFromUser(experimentId: Long, token: String): Boolean = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT E FROM ExperimentDto E, SessionDto S WHERE E.id = :experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(select id from UserDto WHERE token = :token)))";
			val query = session.createQuery(hql);
			query.setLong("experimentId", experimentId);
			query.setString("token", token);
			val listResults = query.list();
			session.close();
			!listResults.isEmpty()
	}
	def isRunFromUser(runId: Long,token: String): Boolean = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT R FROM RunDto R, ExperimentDto E, SessionDto S WHERE R.id = :runId AND R.isActive = true AND " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token))))";
			val query = session.createQuery(hql);
			query.setLong("runId", runId);
			query.setString("token", token);
			val listResults = query.list();
			session.close();
			!listResults.isEmpty();
	}

	def isMeasurementFromUser(measurementId: Long,token: String): Boolean = {
			val session = Application.sessionFactory.openSession();
			val hql = "SELECT M FROM MeasurementDto M, RunDto R, ExperimentDto E, SessionDto S WHERE M.id = :measurementId AND M.isActive = true AND" + 
					"(R.id = M.runId AND R.isActive = true AND " + 
					"(E.id = R.experimentId AND E.isActive = true AND " + 
					"(S.id = E.sessionId AND S.isActive = true and (S.userId IN " + 
					"(SELECT id from UserDto WHERE token = :token)))))";
			val query = session.createQuery(hql);
			query.setLong("measurementId", measurementId);
			query.setString("token", token);
			val listResults = query.list();
			session.close();
			!listResults.isEmpty();
	}
	def sendRegisterEmail(address: String) {
		val sendGrid = new SendGrid(SendGridCredentials.UserName, SendGridCredentials.Password);
		sendGrid.setFrom("aerobal@ece.uprm.edu");
		sendGrid.setFromName("AeroBal");
		sendGrid.addTo(address.trim());
		sendGrid.setSubject("AeroBal Web App Registration");
		sendGrid.setText("Welcome to AeroBal!\n\nYou have just been registered in AeroBal with this email address. " +
				"This account can be used to both browse our web repository and to commit experiments into it. " + 
				"We hope you enjoy using our automated Wind Tunnel system at UPRM!\n\nSincerely,\nThe AeroBal Team");
		sendGrid.send();
	}
	def sendForgotPasswordEmail(address: String) {
		val sendGrid = new SendGrid(SendGridCredentials.UserName, SendGridCredentials.Password);
		val user = Gets.getUserFromEmail(address).getOrElse(throw new NoSuchElementException("No user with this email."));
		sendGrid.setFrom("aerobal@ece.uprm.edu");
		sendGrid.setFromName("AeroBal");
		sendGrid.addTo(address);
		sendGrid.setSubject("AeroBal Forgotten Pasword");
		sendGrid.setHtml("<p>You or someone else have asked to reset your password. If you would like to reset your pass, " + 
				"click <a href=\"http://162.243.4.162/resetPassword?resetCode=" + user.getToken + "\">here</a>: \n\n" + 
				"\n\n Sincerely, \nThe AeroBal Team</p>");
		sendGrid.send();
	}
}
