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
import controllers.Gets;

public class GetsTest extends TestCase {

    @Before
	protected void setUp() throws Exception {
		
		Application.setTestConfigFile();
		Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb;user=sa;password=");
		IDatabaseConnection conn = new HsqldbConnection(connection, "public");
		DatabaseConfig conf = conn.getConfig();
		conf.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE);
//		conf.setFeature(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, Boolean.TRUE);
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("test/dbunit/dataset.xml"));
		DatabaseOperation.CLEAN_INSERT.execute(conn, dataSet);
	}
	@Test
	public void testGetUser() {
		UserDto user = Gets.getUser(1).get();
		assertEquals(1, user.getId());
		assertEquals("thisisatestname", user.getName());
		assertEquals("atestableandunrealtoken",user.getToken());
		assertEquals("fake@testmail.org",user.getEmail());
		assertTrue(user.isActive());
		assertFalse(Gets.getUser(2).isDefined());
	}
	@Test
	public void testGetSession() {
		SessionDto session = Gets.getSession(1).get();
		assertEquals(1, session.getId());
		assertEquals(1, session.getUserId());
		assertEquals("testingSession",session.getName());
		assertEquals("A description for testing.",session.getDescription());
		assertTrue(session.isActive());
		assertTrue(session.isPublic());
	}
	@Test
	public void testGetExperiment() {
		ExperimentDto experiment = Gets.getExperiment(1).get();
		assertEquals(1, experiment.getId());
		assertEquals(1, experiment.getSessionId());
		assertEquals("experimentname",experiment.getName());
		assertEquals(4,experiment.getAmountOfValues());
		assertEquals(7,experiment.getFrequency());
		assertEquals(34.5,experiment.getWindSpeed(),0.00001);
		assertTrue(experiment.isActive());
	}
	@Test
	public void testGetRun() {
		RunDto run = Gets.getRun(1).get();
		assertEquals(1, run.getId());
		assertTrue(run.isActive());
	}
	
	@Test
	public void testGetMeasurement() {
		MeasurementDto measurement = Gets.getMeasurement(1).get();
		assertEquals(1,measurement.getId());
		assertEquals(1,measurement.getRunId());
		assertEquals(1,(long)measurement.getMeasurementTypeId());
		assertEquals(1234.5, measurement.getValue(),0.00001);
	}
	
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSetBuilder().build(new FileInputStream("test/dbunit/dataset.xml"));
	}

}