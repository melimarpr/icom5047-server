package controllers

import com.aerobal.data.dto._
import scala.collection.mutable.ListBuffer
import play.api.mvc.Action
import play.api.mvc.Controller
import com.google.gson.Gson

object Posts extends Controller {

	def newUser = Action { request => 
	val values = request.body.asFormUrlEncoded.get;
	val username = values.get("username").get(0);
	val name = values.get("name").get(0);
	val password = values.get("password").get(0);
	val email = values.get("email").get(0);
	val user = addUser(username, name, password, email);
	Ok(user.toString);
	}
	def newSession = Action { request => 
	val values = request.body.asFormUrlEncoded.get;
	val userId = values.get("userId").get(0).toLong;
	val name = values.get("name").get(0);
	val desc = values.get("desc").get(0);
	val isPublic = values.get("isPublic").get(0).toBoolean;
	val session = addSession(userId, name, desc,isPublic);
	Ok(session.toString);
	}
	def newRun = Action { request => 
	val values = request.body.asFormUrlEncoded.get;
	val experimentId = values.get("experimentId").get(0).toLong;
	val experiment = addRun(experimentId);
	Ok(experiment.toString);
	}
	def newExperiment = Action { request => 
	val values = request.body.asFormUrlEncoded.get;
	val sessionId = values.get("sessionId").get(0).toLong;
	val name = values.get("name").get(0);
	val amountOfValues = values.get("amountOfValues").get(0).toInt;
	val frequency = values.get("frequency").get(0).toInt;
	val windSpeed = values.get("windSpeed").get(0).toDouble;
	val experiment = addExperiment(sessionId, name, amountOfValues,frequency,windSpeed);
	Ok(experiment.toString);
	}
	def newMeasurement = Action { request => 
	val values = request.body.asFormUrlEncoded.get;
	val runId = values.get("runId").get(0).toLong;
	val typeOf = values.get("typeOf").get(0).toInt;
	val value = values.get("value").get(0).toDouble;
	val experiment = addMeasurement(runId, typeOf, value);
	Ok(experiment.toString);
	}

	def addUser(username: String, name: String, password: String, email: String): UserDto = {
			val userDto = new UserDto();
			userDto.setUsername(username);
			userDto.setName(name);
			userDto.setSalt(Application.generateSaltString);
			userDto.setPassword(password);
			userDto.setEmail(email);
			userDto.setToken(Application.generateTokenString);
			val openSession = Application.sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(userDto);
			openSession.getTransaction().commit();
			val user = Gets.getUser(serial.toString.toLong);
			user.getOrElse(null);	
	}
	def addExperiment(sessionId: Long, name: String, amountOfValues: Int, frequency: Int, windSpeed: Double ): ExperimentDto = {
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
			val experiment = Gets.getExperiment(serial.toString.toLong);
			experiment.getOrElse(null);
	}
	def addSession(userId: Long, name: String, description: String, isPublic: Boolean): SessionDto = {
			val sessionDto = new SessionDto();
			sessionDto.userId = userId;
			sessionDto.name = name;
			sessionDto.description = description;
			sessionDto.isPublic = isPublic;
			val openSession = Application.sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(sessionDto);
			openSession.getTransaction().commit();
			val session = Gets.getSession(serial.toString.toLong);
			session.getOrElse(null);
	}
	def addRun(experimentId: Long): RunDto = {
			val runDto = new RunDto();
			runDto.experimentId = experimentId;
			val openSession = Application.sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(runDto);
			openSession.getTransaction().commit();
			val run = Gets.getRun(serial.toString.toLong);
			run.getOrElse(null);	
	}
	def addMeasurement(runId: Long, measurementTypeId: Integer, value: Double): MeasurementDto = {
			val measurementDto = new MeasurementDto();
			measurementDto.runId = runId;
			measurementDto.measurementTypeId = measurementTypeId;
			measurementDto.value = value;
			val openSession = Application.sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(measurementDto);
			openSession.getTransaction().commit();
			val measurement = Gets.getMeasurement(serial.toString.toLong);
			measurement.getOrElse(null);	
	}

}