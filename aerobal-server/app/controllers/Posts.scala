package controllers

import com.aerobal.data.dto.ExperimentDto
import com.aerobal.data.dto.MeasurementDto
import com.aerobal.data.dto.RunDto
import com.aerobal.data.dto.SessionDto
import com.aerobal.data.dto.UserDto
import play.api.mvc.Action
import play.api.mvc.Controller
import cryptography.PasswordHash
import views.html.defaultpages.badRequest

object Posts extends Controller {

	def newUser = Action { 
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val name = values.get("name").get(0);
		val password = values.get("password").get(0);
		val email = values.get("email").get(0);
		val userOpt = addUser(name, password, email);
		if(userOpt.isDefined) {
			Ok(userOpt.get.toString).as("application/json");
		}
		else {
			BadRequest("Something went wrong...");
		}
	}
	def newSession = Action { 
		request => 
		val values = request.body.asFormUrlEncoded.getOrElse(null);
		val token = values.get("token").get(0);
		val userOpt = Gets.getUser(token);
		if(userOpt.isDefined) {
			val name = values.get("name").get(0);
			val desc = values.get("desc").get(0);
			val isPublic = values.get("isPublic").getOrElse(List("false"))(0).toString().toBoolean;;
			val sessionOpt = addSession(name, desc, isPublic, token);
			if(sessionOpt.isDefined) {
				Ok(sessionOpt.get.toString).as("application/json");
			}
			else {
				BadRequest("Something went wrong...");
			} 
		}
		else {
			Forbidden("Invalid token or not found.");
		}
	}
	def newRun = Action { 
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val experimentId = values.get("experimentId").get(0).toLong;
		val token = values.get("token").get(0);
		if(Application.isExperimentFromUser(experimentId, token)) {
			val runOpt = addRun(experimentId, token);

			if(runOpt.isDefined) {
				Ok(runOpt.toString).as("application/json");
			}
			else {
				BadRequest("Something went wrong...");
			}
		} else {
		  val experimentOpt = Gets.getExperiment(experimentId, token);
			Forbidden("Invalid token or not found.")
		  
		}
	}
	def newExperiment = Action { 
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val token = values.get("token").get(0);
		val sessionId = values.get("sessionId").get(0).toLong;
		if(Application.isSessionFromUser(sessionId, token)) {
			val name = values.get("name").get(0);
			val amountOfValues = values.get("amountOfValues").get(0).toInt;
			val frequency = values.get("frequency").get(0).toInt;
			val windSpeed = values.get("windSpeed").get(0).toDouble;
			val experimentOpt = addExperiment(sessionId, name, amountOfValues,frequency,windSpeed, token);

			if(experimentOpt.isDefined) {
				Ok(experimentOpt.toString).as("application/json");
			}
			else {
				BadRequest("Something went wrong...");
			}
		}
		else {
			Forbidden("Invalid token or not found.")
		}
	}
	def newMeasurement = Action { 
		request => 
		val values = request.body.asFormUrlEncoded.get;
		val runId = values.get("runId").get(0).toLong;
		val token = values.get("token").get(0);
		if(Application.isRunFromUser(runId, token)) {
			val typeOf = values.get("typeOf").get(0).toInt;
			val value = values.get("value").get(0).toDouble;
			val measurementOpt = addMeasurement(runId, typeOf, value, token);
			if(measurementOpt.isDefined) {
				Ok(measurementOpt.get.toString).as("application/json");
			}
			else {
				BadRequest("Something went wrong...");
			}
		}
		else {
			Forbidden("Invalid token or not found.")
		}
	}

	def auth = Action {
		request => val values = request.body.asFormUrlEncoded.get;
		val user = values.get("user").get(0);
		val password = values.get("password").get(0);
		val auth = authenticate(user, password)
				Ok("{\"token\":\"" + auth + "\"}");
	}
	def addUser(name: String, password: String, email: String): Option[UserDto] = {
			val userDto = new UserDto();
			userDto.setName(name);
			userDto.setHash(PasswordHash.createHash(password));
			userDto.setEmail(email);
			userDto.setToken(Application.generateTokenString);
			val openSession = Application.sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(userDto);
			openSession.getTransaction().commit();
			val user = Gets.getUser(serial.toString.toLong);
			Some(user.getOrElse(null));	
	}
	def addSession(name: String, description: String, isPublic: Boolean, token: String): Option[SessionDto] = {
			val user = Gets.getUser(token);
			if(user.isDefined) {
				val sessionDto = new SessionDto();
				sessionDto.userId = user.get.id;
				sessionDto.name = name;
				sessionDto.description = description;
				sessionDto.isPublic = isPublic;
				val openSession = Application.sessionFactory.openSession();
				openSession.beginTransaction();
				val serial = openSession.save(sessionDto);
				openSession.getTransaction().commit();
				val session = Gets.getSession(serial.toString.toLong,token);
				Some(session.getOrElse(null));
			}
			else {
				None
			}
	}
	def addExperiment(sessionId: Long, name: String, amountOfValues: Int, frequency: Int, windSpeed: Double, token: String): Option[ExperimentDto] = {
			val session = Gets.getSession(sessionId, token);
			if(session.isDefined) {
				val experimentDto = new ExperimentDto();
				experimentDto.sessionId = sessionId;
				experimentDto.name = name;
				experimentDto.amountOfValues = amountOfValues;
				experimentDto.frequency = frequency;
				experimentDto.windSpeed = windSpeed;
				val openSession = Application.sessionFactory.openSession();
				openSession.beginTransaction();
				val serial = openSession.save(experimentDto);
				openSession.getTransaction().commit();
				val experiment = Gets.getExperiment(serial.toString.toLong,token);
				Some(experiment.getOrElse(null));
			}
			else {
				None
			}
	}
	def addRun(experimentId: Long, token: String): Option[RunDto] = {
			val experiment = Gets.getExperiment(experimentId, token);
			if(experiment.isDefined) {
				val runDto = new RunDto();
				runDto.experimentId = experimentId;
				val openSession = Application.sessionFactory.openSession();
				openSession.beginTransaction();
				val serial = openSession.save(runDto);
				openSession.getTransaction().commit();
				val run = Gets.getRun(serial.toString.toLong, token);
				Some(run.getOrElse(null)); 
			}
			else {
				None;
			}
	}
	def addMeasurement(runId: Long, measurementTypeId: Integer, value: Double,token: String): Option[MeasurementDto] = {
			val run = Gets.getRun(runId, token);
			if(run.isDefined) {
				val measurementDto = new MeasurementDto();
				measurementDto.runId = runId;
				measurementDto.measurementTypeId = measurementTypeId;
				measurementDto.value = value;
				val openSession = Application.sessionFactory.openSession();
				openSession.beginTransaction();
				val serial = openSession.save(measurementDto);
				openSession.getTransaction().commit();
				val measurement = Gets.getMeasurement(serial.toString.toLong, token);
				Some(measurement.getOrElse(null));
			}
			else None
	}
	def authenticate(user: String, password: String): String = {
			val userOpt = Gets.getUserFromEmail(user);
			if(userOpt.isDefined) {
				val user = userOpt.get;
				if(PasswordHash.validatePassword(password, user.getHash)) {
					user.token;
				} else {
					"INCORRECTPASSWORD";
				}

			}
			else {
				"NOTFOUND"
			}
	}


}