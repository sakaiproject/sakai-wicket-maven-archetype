#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao.impl;

import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.apache.log4j.Logger;

import org.sakaiproject.component.cover.ServerConfigurationService;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import ${package}.dao.ProjectDao;
import ${package}.model.Thing;


/**
 * Implementation of ProjectDao
 * 
 * @author Steve Swinsburg (steve.swinsburg@anu.edu.au)
 *
 */
public class ProjectDaoImpl extends JdbcDaoSupport implements ProjectDao {

	private static final Logger log = Logger.getLogger(ProjectDaoImpl.class);
	
	private PropertiesConfiguration statements;
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Thing getThing(long id) {
		
		if(log.isDebugEnabled()) {
			log.debug("getThing(" + id + ")");
		}
		
		try {
			return (Thing) getJdbcTemplate().queryForObject(getStatement("select.thing"),
				new Object[]{Long.valueOf(id)},
				new ThingMapper()
			);
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public List<Thing> getThings() {
		if(log.isDebugEnabled()) {
			log.debug("getThings()");
		}
		
		try {
			return getJdbcTemplate().query(getStatement("select.things"),
				new ThingMapper()
			);
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public boolean addThing(Thing t) {
		
		if(log.isDebugEnabled()) {
			log.debug("addThing( " + t.toString() + ")");
		}
		
		try {
			getJdbcTemplate().update(getStatement("insert.thing"),
				new Object[]{t.getName()}
			);
			return true;
		} catch (DataAccessException ex) {
           log.error("Error executing query: " + ex.getClass() + ":" + ex.getMessage());
           return false;
		}
	}
	
	/**
	 * init
	 */
	public void init() {
		log.info("init()");
		
		//setup the vendor
		String vendor = ServerConfigurationService.getInstance().getString("vendor@org.sakaiproject.db.api.SqlService", null);
		
		//initialise the statements
		initStatements(vendor);
		
		//setup tables if we have auto.ddl enabled.
		boolean autoddl = ServerConfigurationService.getInstance().getBoolean("auto.ddl", true);
		if(autoddl) {
			initTables();
		}
	}
	
	/**
	 * Sets up our tables
	 */
	private void initTables() {
		try {
			getJdbcTemplate().execute(getStatement("create.table"));
		} catch (DataAccessException ex) {
			log.info("Error creating tables: " + ex.getClass() + ":" + ex.getMessage());
			return;
		}
	}
	
	/**
	 * Loads our SQL statements from the appropriate properties file
	 
	 * @param vendor	DB vendor string. Must be one of mysql, oracle, hsqldb
	 */
	private void initStatements(String vendor) {
		
		URL url = getClass().getClassLoader().getResource(vendor + ".properties"); 
		
		try {
			statements = new PropertiesConfiguration(); //must use blank constructor so it doesn't parse just yet (as it will split)
			statements.setReloadingStrategy(new InvariantReloadingStrategy());	//don't watch for reloads
			statements.setThrowExceptionOnMissing(true);	//throw exception if no prop
			statements.setDelimiterParsingDisabled(true); //don't split properties
			statements.load(url); //now load our file
		} catch (ConfigurationException e) {
			log.error(e.getClass() + ": " + e.getMessage());
			return;
		}
	}
	
	/**
	 * Get an SQL statement for the appropriate vendor from the bundle
	
	 * @param key
	 * @return statement or null if none found. 
	 */
	private String getStatement(String key) {
		try {
			return statements.getString(key);
		} catch (NoSuchElementException e) {
			log.error("Statement: '" + key + "' could not be found in: " + statements.getFileName());
			return null;
		}
	}
}
