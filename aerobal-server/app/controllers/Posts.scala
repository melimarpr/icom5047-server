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
import javassist.NotFoundException
import exceptions.InvalidPasswordException
import com.aerobal.data.serializers.GlobalGson
import com.aerobal.data.objects.Experiment
import com.google.gson.GsonBuilder
import com.aerobal.data.serializers.ExperimentSerializer
import scala.collection.mutable.ListBuffer
import com.aerobal.data.objects.Run
import com.aerobal.data.objects.Measurement
import exceptions.InvalidRequestException

object Posts extends Controller {

	def newUser = Action { 
		request => 
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val name = values.get(Constants.USER_NAME_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.USER_NAME_TEXT+ "\' is missing."))(0);
			val password = values.get(Constants.USER_PASSWORD_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.USER_PASSWORD_TEXT+ "\' is missing."))(0);
			val email = values.get(Constants.USER_EMAIL_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.USER_EMAIL_TEXT+ "\' is missing."))(0);
			val userOpt = addUser(name, password, email);
			val user = userOpt.getOrElse(throw new InternalServerErrorException("Something went wrong..."));
			Ok(user.toString).as("application/json");
		}
		catch {
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") }
		}
	}
	def newSession = Action { 
		request => 
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val user = Gets.getUser(token).getOrElse(throw new ForbiddenAccessException("Invalid token."));
			val name = values.get(Constants.SESSION_NAME_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.SESSION_NAME_TEXT + "\' is missing."))(0);
			val desc = values.get(Constants.SESSION_DESCRIPTION_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.SESSION_DESCRIPTION_TEXT + "\' is missing."))(0);
			val isPublic = values.get(Constants.SESSION_IS_PUBLIC_TEXT).getOrElse(List("false"))(0).toBoolean;;
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
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val sessionId = values.get(Constants.EXPERIMENT_SESSION_ID_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.EXPERIMENT_SESSION_ID_TEXT + "\' is missing."))(0).toLong;
			if(!Application.isSessionFromUser(sessionId, token)) { 
				throw new ForbiddenAccessException("Session does not belong to user.")
			}
			val name = values.get(Constants.EXPERIMENT_NAME_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.EXPERIMENT_NAME_TEXT + "\' is missing."))(0);
			val amountOfValues = values.get(Constants.EXPERIMENT_AMOUNT_OF_VALUES_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.EXPERIMENT_AMOUNT_OF_VALUES_TEXT + "\' is missing."))(0).toInt;
			val frequency = values.get(Constants.EXPERIMENT_FREQUENCY_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.EXPERIMENT_FREQUENCY_TEXT + "\' is missing."))(0).toInt;
			val windSpeed = values.get(Constants.EXPERIMENT_WIND_SPEED_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.EXPERIMENT_WIND_SPEED_TEXT + "\' is missing."))(0).toDouble;
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
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val experimentId = values.get(Constants.RUN_EXPERIMENT_ID_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.RUN_EXPERIMENT_ID_TEXT + "\' is missing."))(0).toLong;
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
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val runId = values.get(Constants.MEASUREMENT_RUN_ID_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.MEASUREMENT_RUN_ID_TEXT + "\' is missing."))(0).toLong;
			if(!Application.isRunFromUser(runId, token)) {
				throw new ForbiddenAccessException("Run does not belong to user.");
			}
			val typeOf = values.get(Constants.MEASUREMENT_TYPE_OF_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.MEASUREMENT_TYPE_OF_TEXT + "\' is missing."))(0).toInt;
			val value = values.get(Constants.MEASUREMENT_VALUE_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.MEASUREMENT_VALUE_TEXT + "\' is missing."))(0).toDouble;
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

	def submit_experiment = Action {
		request => 
		try {
			val headersMap = request.headers.toMap;
			val token = headersMap.getOrElse(Constants.TOKEN_TEXT, throw new NoSuchElementException("No token found."))(0);	
			val sessionId = headersMap.getOrElse(Constants.EXPERIMENT_SESSION_ID_TEXT, throw new NoSuchElementException("No Session ID provided in header."))(0).toLong;
			val values = request.body.asJson.getOrElse(throw new NoSuchElementException("No body supplied."));
			val gson = new GsonBuilder().registerTypeAdapter(classOf[Experiment], ExperimentSerializer).create();
			val experiment = gson.fromJson(values.toString, classOf[Experiment]);
			val experimentExistsOpt = Gets.getExperimentFromSessionIdAndName(sessionId, experiment.name);
			if(experimentExistsOpt.isDefined && experiment.id < 1) {
				throw new InvalidRequestException("Name \'" + experiment.name + "' already exists for session with ID = " + sessionId);
			}
			val newExperiment = submitExperiment(sessionId, experiment, token).getOrElse(throw new InternalServerErrorException("Something went wrong..."));
			Ok(newExperiment.toString);
		}
		catch {
		case e: ForbiddenAccessException => { Forbidden(e.getMessage()) };
		case e: InvalidRequestException => { BadRequest(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}
	}



	def auth = Action {
		request => 
		try {
			val values = request.body.asFormUrlEncoded.getOrElse(throw new NoSuchElementException("No Form URL Encoded body supplied."));
			val user = values.get(Constants.AUTH_USER_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.AUTH_USER_TEXT + "\' is missing."))(0);
			val password = values.get(Constants.AUTH_PASSWORD_TEXT).getOrElse(throw new NoSuchElementException("Parameter \'" + Constants.AUTH_PASSWORD_TEXT + "\' is missing."))(0);
			val auth = authenticate(user, password);
			if(auth.equals(Constants.AUTH_USER_NOT_FOUND_TEXT)) {
				throw new NotFoundException("User not found.");
			} else if(auth.equals(Constants.AUTH_INCORRECT_PASSWORD_TEXT)) {
				throw new InvalidPasswordException("Invalid password for user.");
			}
			Ok("{\"token\":\"" + auth + "\"}").as("application/json");
		}
		catch {
		case e: InvalidPasswordException => { BadRequest(e.getMessage()) };
		case e: NotFoundException => { NotFound(e.getMessage()) };
		case e: NoSuchElementException => { BadRequest(e.getMessage()) };
		case e: InternalServerErrorException => { InternalServerError(e.getMessage()) };
		case e: Exception => { InternalServerError("Something went wrong...") };
		}

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
			val experimentDto = new ExperimentDto();
			experimentDto.sessionId = sessionId;
			experimentDto.name = name;
			experimentDto.amountOfValues = amountOfValues;
			experimentDto.frequency = frequency;
			experimentDto.windSpeed = windSpeed;
			addExperiment(experimentDto, token);
	}
	def addExperiment(experimentDto: ExperimentDto, token: String): Option[ExperimentDto] = {
			val session = Gets.getSession(experimentDto.sessionId, token);
			if(session.isDefined) {
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

			val runDto = new RunDto();
			runDto.experimentId = experimentId;
			addRun(runDto, token);
	}
	def addRun(runDto: RunDto, token: String): Option[RunDto] = {
			val experiment = Gets.getExperiment(runDto.experimentId, token);
			if(experiment.isDefined) {

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
			val measurementDto = new MeasurementDto();
			measurementDto.runId = runId;
			measurementDto.measurementTypeId = measurementTypeId;
			measurementDto.value = value;
			addMeasurement(measurementDto, token);
	}
	def addMeasurement(measurementDto: MeasurementDto, token: String): Option[MeasurementDto] = {
			val run = Gets.getRun(measurementDto.runId, token);
			if(run.isDefined) {
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
		def submitExperiment(sessionId: Long, experiment: Experiment, token: String): Option[Experiment] = {
			val newExperiment = 
					if(experiment.id > 0) {
						val experimentOpt = Gets.getExperiment(experiment.id, token);
						if(experimentOpt.isDefined) {
							new Experiment(experimentOpt.get);;
						}
						else {
							return None
						}
					} else {
						val experimentDto = new ExperimentDto();
						experimentDto.sessionId = sessionId;
						experimentDto.name = experiment.name;
						experimentDto.amountOfValues = experiment.amountOfValues;
						experimentDto.frequency = experiment.frequency;
						experimentDto.windSpeed = experiment.windSpeed;
						new Experiment(addExperiment(experimentDto, token).getOrElse(return None))
					}

			val list = new ListBuffer[Option[Run]]();
			experiment.runs.foreach(run => list.append(submitRun(newExperiment.id, run, token)));
			list.foreach(runOpt => if(runOpt.isDefined) { newExperiment.runs.append(runOpt.get) });
			Some(newExperiment);
	}
		def submitRun(experimentId: Long, run: Run, token: String): Option[Run] = {
			val newRun = 
					if(run.id > 0) {
						val runOpt = Gets.getRun(run.id, token);
						if(runOpt.isDefined) {
							new Run(runOpt.get);;;;
						}
						else {
							return None
						}
					} else {
						val runDto = new RunDto();
						runDto.experimentId = experimentId;
						new Run(addRun(runDto, token).getOrElse(return None));
					}
			val list = new ListBuffer[Option[Measurement]]();
			run.measurements.foreach(measurement => list.append(submitMeasurement(newRun.id, measurement,token)));
			list.foreach(measurementOpt => if(measurementOpt.isDefined) { newRun.measurements.append(measurementOpt.get) });
			Some(newRun);
	}
		def submitMeasurement(runId: Long, measurement: Measurement, token: String): Option[Measurement] = {
			val newMeasurement = 
					if(measurement.id > 0) {
						val measurementOpt = Gets.getMeasurement(measurement.id, token);
						if(measurementOpt.isDefined) {
							new Measurement(measurementOpt.get);;
						}
						else {
							return None
						}
					} else {
						val measurementDto = new MeasurementDto();
						measurementDto.runId = runId;
						measurementDto.measurementTypeId = measurement.typeOf.id;
						measurementDto.value = measurement.value;
						measurementDto.timestamp = measurement.timestamp;
						new Measurement(addMeasurement(measurementDto, token).getOrElse(return None));
					}

			Some(newMeasurement);
	}

}
