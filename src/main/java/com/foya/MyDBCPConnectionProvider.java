package com.foya;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import pl.kernelpanic.dbmonster.connection.ConnectionProvider;
import pl.kernelpanic.dbmonster.connection.LogEnabled;

public class MyDBCPConnectionProvider implements ConnectionProvider, LogEnabled {

	/**
	 * JDBC driver name.
	 */
	private String driver = System.getProperty("dbmonster.jdbc.driver");

	/**
	 * JDBC url.
	 */
	private String url = System.getProperty("dbmonster.jdbc.url");

	/**
	 * JDBC user name.
	 */
	private String username = System.getProperty("dbmonster.jdbc.username");

	/**
	 * JDBC user's password.
	 */
	private String password = System.getProperty("dbmonster.jdbc.password");

	/**
	 * Properties used to establish connection.
	 */
	private Properties properties = null;

	/**
	 * A datasource.
	 */
	private DataSource dataSource = null;

	/**
	 * Logger.
	 */
	private Log logger = null;

	/**
	 * Connection pool handler.
	 */
	private ObjectPool pool;

	private boolean autoCommit = false;

	/**
	 * Creates new SimpleConnectionProvider.
	 *
	 * @throws Exception
	 *             if driver class could not be found.
	 */
	public MyDBCPConnectionProvider() throws Exception {
		initDriver();
		setupPool();
	}

	/**
	 * Creates new DBCPConnectionProvider with given connection info.
	 *
	 * @param jdbcDriver
	 *            JDBC driver
	 * @param jdbcUrl
	 *            JDBC url
	 * @param jdbcUsername
	 *            JDBC user name
	 * @param jdbcPassword
	 *            JDBC password
	 *
	 * @throws Exception
	 *             if the driver class could not be found.
	 */
	public MyDBCPConnectionProvider(String jdbcDriver, String jdbcUrl, String jdbcUsername, String jdbcPassword)
			throws Exception {
		driver = jdbcDriver;
		url = jdbcUrl;
		username = jdbcUsername;
		password = jdbcPassword;
		initDriver();
		setupPool();
	}

	/**
	 * Creates new DBCPConnectionProvider with properties (usefull for
	 * interbase).
	 *
	 * @param jdbcDriver
	 *            JDBC driver
	 * @param jdbcUrl
	 *            JDBC url
	 * @param props
	 *            properties
	 *
	 * @throws Exception
	 *             if driver class could not be found.
	 */
	public MyDBCPConnectionProvider(String jdbcDriver, String jdbcUrl, Properties props) throws Exception {
		driver = jdbcDriver;
		url = jdbcUrl;
		properties = props;
		initDriver();
		setupPool();
	}

	/**
	 * @see ConnectionProvider#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		if (dataSource == null) {
			throw new SQLException("Data source is null!");
		}
		Connection conn = dataSource.getConnection();
		conn.setAutoCommit(autoCommit);
		return conn;
	}

	/**
	 * @see ConnectionProvider#testConnection()
	 */
	public void testConnection() throws SQLException {
		Connection conn = getConnection();
		DatabaseMetaData dbmd = conn.getMetaData();
		if (logger != null && logger.isInfoEnabled()) {
			logger.info("Today we are feeding: " + dbmd.getDatabaseProductName() + " "
					+ dbmd.getDatabaseProductVersion());
		}
		conn.close();
		dbmd = null;
		conn = null;
	}

	/**
	 * Sets the logger.
	 *
	 * @param log
	 *            a logger.
	 */
	public void setLogger(Log log) {
		logger = log;
	}

	/**
	 * Shutdown this connection provider.
	 */
	public void shutdown() {
		try {
			pool.close();
		} catch (Exception e) {
		} finally {
			pool = null;
		}
		dataSource = null;
		driver = null;
		logger = null;
		password = null;
		properties = null;
		url = null;
		username = null;
	}

	/**
	 * Initializes the JDBC driver.
	 *
	 * @throws ClassNotFoundException
	 *             if driver class could not be found.
	 */
	private void initDriver() throws ClassNotFoundException {
		Class.forName(driver);
	}

	/**
	 * Sets the pool up.
	 *
	 * @throws Exception
	 *             if the pool cannot be set up
	 */
	private void setupPool() throws Exception {
		pool = new GenericObjectPool(null);
		ConnectionFactory connFactory = null;
		if (properties != null) {
			connFactory = new DriverManagerConnectionFactory(url, properties);
		} else {
			connFactory = new DriverManagerConnectionFactory(url, username, password);
		}
		new PoolableConnectionFactory(connFactory, pool, null, null, false, true);
		dataSource = new PoolingDataSource(pool);
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
}