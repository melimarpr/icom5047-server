package controllers

import org.hibernate.cfg.Configuration
import com.aerobal.data.dto._
import play.api.mvc.Action
import play.api.mvc.Controller
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import com.google.gson.Gson
import org.hibernate.boot.registry.StandardServiceRegistryBuilder

object Application extends Controller {
	//  val sf = JPA.em().getDelegate().asInstanceOf[Session].Gets.getSessionFactory();

	val configuration = new Configuration().configure();
	val serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
			configuration.getProperties()).build();
	val sessionFactory = { println("Starting to Build Session Factory"); 
	//	val tbr = new Configuration().configure().buildSessionFactory();
	val tbr = configuration.buildSessionFactory(serviceRegistry);
	println("Finished building session Factory");
	tbr }

	def index = Action {

		Ok(views.html.index("Aloha!"));
	}
	


}