package controllers

import org.hibernate.cfg.Configuration
import com.aerobal.data.dto.ExperimentDto
import com.aerobal.data.dto.MeasurementDto
import com.aerobal.data.dto.RunDto
import com.aerobal.data.dto.SessionDto
import play.api.mvc.Action
import play.api.mvc.Controller
import com.aerobal.data.dto.UserDto
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import com.google.gson.Gson
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import com.aerobal.data.dto.MeasurementDto

object Application extends Controller {
	//  val sf = JPA.em().getDelegate().asInstanceOf[Session].getSessionFactory();

	val configuration = new Configuration().configure();
	val serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
			configuration.getProperties()).build();
	val sessionFactory = { println("Starting to Build Session Facory"); 
	//	val tbr = new Configuration().configure().buildSessionFactory();
	val tbr = configuration.buildSessionFactory(serviceRegistry);
	println("Finished building session Factory");
	tbr }

	def index = Action {

		Ok(views.html.index("Aloha!"));
	}
	def newUser = Action {	implicit request => 
	val values = request.body.asFormUrlEncoded.get;
	val username = values.get("username").get(0);
	val name = values.get("name").get(0);
	val password = values.get("password").get(0);
	val email = values.get("email").get(0);
	val user = addUser(username, name, password, email);
	Ok(user.toString);
	}
	def newSession = Action {	implicit request => 
	val values = request.body.asFormUrlEncoded.get;
	val userId = values.get("userId").get(0).toLong;
	val name = values.get("name").get(0);
	val desc = values.get("desc").get(0);
	val isPublic = values.get("isPublic").get(0).toBoolean;
	val session = addSession(userId, name, desc,isPublic);
	Ok(session.toString);
	}
	def newRun = Action {	implicit request => 
	val values = request.body.asFormUrlEncoded.get;
	val experimentId = values.get("experimentId").get(0).toLong;
	val experiment = addRun(experimentId);
	Ok(experiment.toString);
	}
	def newExperiment = Action {	implicit request => 
	val values = request.body.asFormUrlEncoded.get;
	val sessionId = values.get("sessionId").get(0).toLong;
	val name = values.get("name").get(0);
	val amountOfValues = values.get("amountOfValues").get(0).toInt;
	val frequency = values.get("frequency").get(0).toInt;
	val windSpeed = values.get("windSpeed").get(0).toDouble;
	val experiment = addExperiment(sessionId, name, amountOfValues,frequency,windSpeed);
	Ok(experiment.toString);
	}
		def newMeasurement = Action {	implicit request => 
	val values = request.body.asFormUrlEncoded.get;
	val runId = values.get("runId").get(0).toLong;
	val typeOf = values.get("typeOf").get(0).toInt;
	val value = values.get("value").get(0).toDouble;
	val experiment = addMeasurement(runId, typeOf, value);
	Ok(experiment.toString);
	}
	def measurement(id: Long) = Action {
		val measurement = getMeasurement(id);
		if(measurement.isDefined) {
			NotFound("Id=" + id + " not found.");
		} else {
			Ok(measurement.get.toString);
		}

	}
	def experiment(id: Long) = Action {
		val experiment = getExperiment(id);
		if(experiment.isDefined) {
			Ok(experiment.get.toString);
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}
	def run(id: Long) = Action {
		val run = getRun(id);
		if(run.isDefined) {
			Ok(run.get.toString);
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}
	def session(id: Long) = Action {
		val session = getSession(id);
		if(session.isDefined) {
			Ok(session.get.toString);
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}
	def user(id: Long) = Action {
		val user = getUser(id);
		if(user.isDefined) {
			Ok(user.get.toString);
		} 
		else
		{
			NotFound("Id=" + id + " not found.");
		} 
	}
	def sessions(userId: Long) = Action {
		val sessions = getSessions(userId);
		if(!sessions.isEmpty) {
			Ok(new Gson().toJson(sessions.toArray));
		} 
		else
		{
			NotFound("Id=" + userId + " not found.");
		} 
	}
	def experiments(sessionId: Long) = Action {
		val experiments = getExperiments(sessionId);
		if(!experiments.isEmpty) {
			Ok(new Gson().toJson(experiments.toArray));
		} 
		else
		{
			NotFound("Id=" + sessionId + " not found.");
		} 
	}
	def runs(experimentId: Long) = Action {
		val runs = getRuns(experimentId);
		if(!runs.isEmpty) {
			Ok(new Gson().toJson(runs.toArray));
		} 
		else
		{
			NotFound("Id=" + experimentId + " not found.");
		} 
	}
	def measurements(runId: Long) = Action {
		val measurements = getMeasurements(runId);
		if(!measurements.isEmpty) {
			Ok(new Gson().toJson(measurements.toArray));
		} 
		else
		{
			NotFound("Id=" + runId + " not found.");
		} 
	}
	def getExperiment(experimentId: Long): Option[ExperimentDto] = {
			val session = sessionFactory.openSession();
			val hql = "FROM ExperimentDto E WHERE E.id = :experimentId";
			val query = session.createQuery(hql);
			query.setLong("experimentId", experimentId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[ExperimentDto]);
			}
			else {
				None
			}
	}

	def getMeasurement(measurementId: Long): Option[MeasurementDto] = {
			val session = sessionFactory.openSession();
			val hql = "FROM MeasurementDto M WHERE M.id = :measurementId";
			val query = session.createQuery(hql);
			query.setLong("measurementId", measurementId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[MeasurementDto]);
			}
			else {
				None
			}
	}
	def getRun(runId: Long): Option[RunDto] = {
			val session = sessionFactory.openSession();
			val hql = "FROM RunDto R WHERE R.id = :runId";
			val query = session.createQuery(hql);
			query.setLong("runId", runId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[RunDto]);
			}
			else {
				None
			}
	}
	def getSession(sessionId: Long): Option[SessionDto] = {
			val session = sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.id = :sessionId";
			val query = session.createQuery(hql);
			query.setLong("sessionId", sessionId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[SessionDto]);
			}
			else {
				None
			}
	}
	def getUser(userId: Long): Option[UserDto] = {
			val session = sessionFactory.openSession();
			val hql = "FROM UserDto U WHERE U.id = :userId";
			val query = session.createQuery(hql);
			query.setLong("userId", userId);
			val listResults = query.list();
			session.close();
			if(!listResults.isEmpty()) {
				Some(listResults.get(0).asInstanceOf[UserDto]);
			}
			else {
				None
			}
	}
	def getSessions(userId: Long): List[SessionDto] = {
			val sessions = sessionFactory.openSession();
			val hql = "FROM SessionDto S WHERE S.userId = :userId";
			val query = sessions.createQuery(hql);
			query.setLong("userId", userId);
			val listResults = query.list();
			sessions.close();
			val tbrList = new ListBuffer[SessionDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[SessionDto]));
			tbrList.toList;
	}

	def getExperiments(sessionId: Long): List[ExperimentDto] = {
			val experiments = sessionFactory.openSession();
			val hql = "FROM ExperimentDto E WHERE E.sessionId = :sessionId";
			val query = experiments.createQuery(hql);
			query.setLong("sessionId", sessionId);
			val listResults = query.list();
			experiments.close();
			val tbrList = new ListBuffer[ExperimentDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[ExperimentDto]));
			tbrList.toList;
	}
	def getRuns(experimentId: Long): List[RunDto] = {
			val runs = sessionFactory.openSession();
			val hql = "FROM RunDto R WHERE R.experimentId = :experimentId";
			val query = runs.createQuery(hql);
			query.setLong("experimentId", experimentId);
			val listResults = query.list();
			runs.close();
			val tbrList = new ListBuffer[RunDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[RunDto]));
			tbrList.toList;
	}
	def getMeasurements(runId: Long): List[MeasurementDto] = {
			val measurements = sessionFactory.openSession();
			val hql = "FROM MeasurementDto M WHERE M.runId = :runId";
			val query = measurements.createQuery(hql);
			query.setLong("runId", runId);
			val listResults = query.list();
			measurements.close();
			val tbrList = new ListBuffer[MeasurementDto]();
			listResults.foreach(x => tbrList.add(x.asInstanceOf[MeasurementDto]));
			tbrList.toList;
	}
	def addUser(username: String, name: String, password: String, email: String): UserDto = {
			val userDto = new UserDto();
			userDto.setUsername(username);
			userDto.setName(name);
			userDto.setSalt("someSalt");
			userDto.setPassword(password);
			userDto.setEmail(email);
			val openSession = sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(userDto);
			openSession.getTransaction().commit();
			val user = getUser(serial.toString.toLong);
			user.getOrElse(null);	
	}
	def addExperiment(sessionId: Long, name: String, amountOfValues: Int, frequency: Int, windSpeed: Double ): ExperimentDto = {
			val experimentDto = new ExperimentDto();
			experimentDto.sessionId = sessionId;
			experimentDto.name = name;
			experimentDto.amountOfValues = amountOfValues;
			experimentDto.frequency = frequency;
			experimentDto.windSpeed = windSpeed;
			val openSession = sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(experimentDto);
			openSession.getTransaction().commit();
			val experiment = getExperiment(serial.toString.toLong);
			experiment.getOrElse(null);
	}
	def addSession(userId: Long, name: String, description: String, isPublic: Boolean): SessionDto = {
			val sessionDto = new SessionDto();
			sessionDto.userId = userId;
			sessionDto.name = name;
			sessionDto.description = description;
			sessionDto.isPublic = isPublic;
			val openSession = sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(sessionDto);
			openSession.getTransaction().commit();
			val session = getSession(serial.toString.toLong);
			session.getOrElse(null);
	}
	def addRun(experimentId: Long): RunDto = {
			val runDto = new RunDto();
			runDto.experimentId = experimentId;
			val openSession = sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(runDto);
			openSession.getTransaction().commit();
			val run = getRun(serial.toString.toLong);
			run.getOrElse(null);	
	}
	def addMeasurement(runId: Long, measurementTypeId: Integer, value: Double): MeasurementDto = {
			val measurementDto = new MeasurementDto();
			measurementDto.runId = runId;
			measurementDto.measurementTypeId = measurementTypeId;
			measurementDto.value = value;
			val openSession = sessionFactory.openSession();
			openSession.beginTransaction();
			val serial = openSession.save(measurementDto);
			openSession.getTransaction().commit();
			val measurement = getMeasurement(serial.toString.toLong);
			measurement.getOrElse(null);	
	}
}