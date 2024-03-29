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
import com.aerobal.data.dto.SessionDto;
import com.aerobal.data.dto.UserDto;
import com.aerobal.data.objects.OptionWrapper;

import controllers.Application;
import controllers.Puts;

public class UpdatesTest extends TestCase {
	String PLACEHOLDER_MUST_FIX = "";
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
	public void testUpdateSession() {
		Long sessionId = 1L;
		String name = "updatedNameWhoo";
		String description = "updatedDescYeah";
		SessionDto session = Puts.updateSession(sessionId, OptionWrapper.some(name), OptionWrapper.some(description),PLACEHOLDER_MUST_FIX).get();
		assertEquals(sessionId + 0,session.getId());
    	assertEquals(name,session.getName());
    	assertEquals(description,session.getDescription());
    	assertTrue(session.isPublic());
	}
	@Test
	public void testUpdateExperiment() {
		Long experimentId = 1L;
		String name = "updatedNameWhoo";
		ExperimentDto experiment = Puts.updateExperiment(experimentId, OptionWrapper.some(name), PLACEHOLDER_MUST_FIX).get();
		assertEquals(experimentId + 0,experiment.getId());
    	assertEquals(name,experiment.getName());
	}

	@Test
	public void testUpdateUser() {
		String name = "updatedNameWhoo";
		String email = "updated@mailupdates.edu";
		UserDto user = Puts.updateUser(OptionWrapper.some(name), OptionWrapper.some(email), PLACEHOLDER_MUST_FIX).get();
    	assertEquals(name,user.getName());
    	assertEquals(email,user.getEmail());
	}


}
