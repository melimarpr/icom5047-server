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
import exceptions.ForbiddenAccessException
import exceptions.InternalServerErrorException

object Posts extends Controller {
	val TOKEN_TEXT = "token";
	val USER_NAME_TEXT = "name";
	val USER_PASSWORD_TEXT = "password";
	val USER_EMAIL_TEXT = "email";

	val SESSION_NAME_TEXT = "name";
	val SESSION_DESCRIPTION_TEXT = "desc";
	val SESSION_IS_PUBLIC_TEXT = "isPublic";

	val EXPERIMENT_SESSION_ID_TEXT = "sessionId";
	val EXPERIMENT_NAME_TEXT = "name";
	val EXPERIMENT_AMOUNT_OF_VALUES_TEXT = "amountOfValues";
	val EXPERIMENT_FREQUENCY_TEXT = "frequency";
	val EXPERIMENT_WIND_SPEED_TEXT = "windSpeed";

	val RUN_EXPERIMENT_ID_TEXT = "experimentId";

	val MEASUREMENT_RUN_ID_TEXT = "runId";
	val MEASUREMENT_TYPE_OF_TEXT = "typeOf";
	val MEASUREMENT_VALUE_TEXT = "value";

	val AUTH_USER_TEXT = "user";
	val AUTH_PASSWORD_TEXT = "password";

	val emptyList = List("");
	def newUser = Action { 
		request => 
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val name = values.get(USER_NAME_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + USER_NAME_TEXT+ "\' is missing."))(0);
			val password = values.get(USER_PASSWORD_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + USER_PASSWORD_TEXT+ "\' is missing."))(0);
			val email = values.get(USER_EMAIL_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + USER_EMAIL_TEXT+ "\' is missing."))(0);
			val userOpt = addUser(name, password, email);
			if(userOpt.isDefined) {
				Ok(userOpt.get.toString).as("application/json");
			}
			else {
				BadRequest("Something went wrong...");
			}
		}
		catch {
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}
	def newSession = Action { 
		request => 
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val token = values.get(TOKEN_TEXT).getOrElse(throw new ForbiddenAccessException("No token passed."))(0);
			val user = Gets.getUser(token).getOrElse(throw new ForbiddenAccessException("Invalid token."));
			val name = values.get(SESSION_NAME_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + SESSION_NAME_TEXT + "\' is missing."))(0);
			val desc = values.get(SESSION_DESCRIPTION_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + SESSION_DESCRIPTION_TEXT + "\' is missing."))(0);
			val isPublic = values.get(SESSION_IS_PUBLIC_TEXT).getOrElse(List("false"))(0).toBoolean;;
			val sessionOpt = addSession(name, desc, isPublic, token);
			val session = sessionOpt.getOrElse(throw new InternalServerErrorException("Something went wrong..."));
			Ok(session.toString).as("application/json");
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}
	}

	def newExperiment = Action { 
		request => 
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val token = values.get(TOKEN_TEXT).getOrElse(throw new ForbiddenAccessException("No token passed."))(0);
			val sessionId = values.get(EXPERIMENT_SESSION_ID_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + EXPERIMENT_SESSION_ID_TEXT + "\' is missing."))(0).toLong;
			if(!Application.isSessionFromUser(sessionId, token)) { 
				throw new ForbiddenAccessException("Session does not belong to user.")
			}
			val name = values.get(EXPERIMENT_NAME_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + EXPERIMENT_NAME_TEXT + "\' is missing."))(0);
			val amountOfValues = values.get(EXPERIMENT_AMOUNT_OF_VALUES_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + EXPERIMENT_AMOUNT_OF_VALUES_TEXT + "\' is missing."))(0).toInt;
			val frequency = values.get(EXPERIMENT_FREQUENCY_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + EXPERIMENT_FREQUENCY_TEXT + "\' is missing."))(0).toInt;
			val windSpeed = values.get(EXPERIMENT_WIND_SPEED_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + EXPERIMENT_WIND_SPEED_TEXT + "\' is missing."))(0).toDouble;
			val experimentOpt = addExperiment(sessionId, name, amountOfValues, frequency, windSpeed, token);
			val experiment = experimentOpt.getOrElse(throw new InternalServerErrorException("Something went wrong..."));
			Ok(experiment.toString).as("application/json");


		}		
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}
	}
	def newRun = Action { 
		request => 
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val token = values.get(TOKEN_TEXT).getOrElse(throw new ForbiddenAccessException("No token passed."))(0);
			val experimentId = values.get(RUN_EXPERIMENT_ID_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + RUN_EXPERIMENT_ID_TEXT + "\' is missing."))(0).toLong;
			if(!Application.isExperimentFromUser(experimentId, token)) {
				throw new ForbiddenAccessException("Run does not belong to user.");
			}
			val runOpt = addRun(experimentId, token);
			val run = runOpt.getOrElse(throw new InternalServerErrorException("Something went wrong..."));
			Ok(runOpt.toString).as("application/json");
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}

	}
	def newMeasurement = Action { 
		request => 
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val runId = values.get(MEASUREMENT_RUN_ID_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + MEASUREMENT_RUN_ID_TEXT + "\' is missing."))(0).toLong;
			val token = values.get(TOKEN_TEXT).getOrElse(throw new ForbiddenAccessException("No token passed."))(0);
			if(!Application.isRunFromUser(runId, token)) {
				throw new ForbiddenAccessException("Run does not belong to user.");
			}
			val typeOf = values.get(MEASUREMENT_TYPE_OF_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + MEASUREMENT_TYPE_OF_TEXT + "\' is missing."))(0).toInt;
			val value = values.get(MEASUREMENT_VALUE_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + MEASUREMENT_VALUE_TEXT + "\' is missing."))(0).toDouble;
			val measurementOpt = addMeasurement(runId, typeOf, value, token);
			val measurement = measurementOpt.getOrElse(throw new InternalServerErrorException("Something went wrong..."));
			Ok(measurementOpt.get.toString).as("application/json");
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}
	}

	def auth = Action {
		request => 
		val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
		val user = values.get(AUTH_USER_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + AUTH_USER_TEXT + "\' is missing."))(0);
		val password = values.get(AUTH_PASSWORD_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + AUTH_PASSWORD_TEXT + "\' is missing."))(0);
		val auth = authenticate(user, password)
				Ok("{\"token\":\"" + auth + "\"}").as("application/json");
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
