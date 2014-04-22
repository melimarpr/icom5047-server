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

object Application extends Controller {
	//  val sf = JPA.em().getDelegate().asInstanceOf[Session].Gets.getSessionFactory();
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


}
