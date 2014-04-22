package dbunit;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.hsqldb.HsqldbConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import com.aerobal.data.dto.ExperimentDto;
import com.aerobal.data.dto.MeasurementDto;
import com.aerobal.data.dto.RunDto;
import com.aerobal.data.dto.SessionDto;
import com.aerobal.data.dto.UserDto;

import controllers.Application;
import controllers.Posts;


public class PostsTest extends TestCase {

    @Before
	protected void setUp() throws Exception {
		Application.setTestConfigFile();
		Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb;user=sa;password=");
		IDatabaseConnection conn = new HsqldbConnection(connection, "public");
		DatabaseConfig conf = conn.getConfig();
		conf.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE);
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("test/dbunit/dataset.xml"));
		DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
	}
    
    @Test
    public void testAddUser() {
    	String username = "testUserNameYeah";
    	String name = "somename";
    	String hash = "somehash";
    	String email = "some@email.com";
    	UserDto user = Posts.addUser(username, name, hash, email);
    	assertEquals(username,user.getUsername());
    	assertEquals(name,user.getName());
    	assertEquals(hash,user.getHash());
    	assertEquals(email,user.getEmail());
    	assertTrue(user.getId() > 0);
    }
    @Test
    public void testAddSession() {
    	Long userId = 1L;
    	String name = "atestname";
    	String description = "This is a description.";
    	Boolean isPublic = true;
    	SessionDto session = Posts.addSession(userId, name, description, isPublic);
    	assertEquals(userId + 0,session.userId());
    	assertEquals(name,session.getName());
    	assertEquals(description,session.getDescription());
    	assertTrue(session.isPublic());
    	assertTrue(session.getId() > 0);
    }
    @Test
    public void testAddExperiment() {
    	Long sessionId = 1L;
    	String name = "atestname";
    	Integer amountOfValues = 13;
    	Integer frequency = 4;
    	Double windSpeed = 5423.332342;
    	ExperimentDto experiment = Posts.addExperiment(sessionId, name, amountOfValues, frequency, windSpeed);
    	assertEquals(sessionId + 0,experiment.sessionId());
    	assertEquals(name,experiment.getName());
    	assertEquals((long)amountOfValues,experiment.getAmountOfValues());
    	assertEquals((long)frequency, experiment.getFrequency());
    	assertEquals(windSpeed,experiment.getWindSpeed(),0.000001);
    	assertTrue(experiment.isActive());
    	assertTrue(experiment.getId() > 0);
    }
    @Test
    public void testAddRun() {
    	Long experimentId = 1L;
    	RunDto run = Posts.addRun(experimentId);
    	assertEquals(1,run.getExperimentId());
    	assertTrue(run.getId()  > 0);
    }
    @Test
    public void testAddMeasurement() {
    	Long runId = 1L;
    	Integer measurementTypeId = 1;
    	Double value = 2354834.23534;
    	MeasurementDto measurement = Posts.addMeasurement(runId, measurementTypeId, value);
    	assertEquals(runId + 0,measurement.getRunId());
    	assertEquals(measurementTypeId,measurement.getMeasurementTypeId());
    	assertEquals(value,measurement.getValue());
    	assertTrue(measurement.getId() > 0);
    }
    
}
