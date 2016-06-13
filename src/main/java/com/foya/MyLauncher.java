package com.foya;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import pl.kernelpanic.dbmonster.DBMonster;
import pl.kernelpanic.dbmonster.ProgressMonitor;
import pl.kernelpanic.dbmonster.SchemaGrabber;
import pl.kernelpanic.dbmonster.schema.Schema;
import pl.kernelpanic.dbmonster.schema.SchemaUtil;

public class MyLauncher {

	/**
	 * Run time properties.
	 */
	private Properties properties = new Properties();

	/**
	 * Command line options.
	 */
	private Options options = new Options();

	/**
	 * Constructs new launcher.
	 */
	public MyLauncher() {

	}

	/**
	 * Runs DBMonster.
	 *
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		try {
			new MyLauncher().run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs launcher.
	 *
	 * @param args
	 *            command line
	 *
	 * @throws Exception
	 *             on error
	 */
	public void run(String[] args) throws Exception {

		CommandLine line = prepareCommandLine(args);
		if (line.getOptions().length == 0 || line.hasOption('h')) {
			HelpFormatter hf = new HelpFormatter();
			System.out.println("dbMonster v." + DBMonster.getVersion());
			hf.printHelp("java pl.kernelpanic.dbmonster.Launcher"
					+ " \n[-s file1 file2 ...] [-c configfile] [-h] [-n num]" + " \n[-f file] [-l file]"
					+ " \n[--grab] [-t name1 name2 ...] [-o output file]", options);
			return;
		}

		loadProperties();

		if (line.hasOption('c')) {
			readProperties(new File(line.getOptionValue('c')));
		}

		exportProperties();

		MyDBCPConnectionProvider connProvider = new MyDBCPConnectionProvider();

		if (line.hasOption("grab")) {
			SchemaGrabber grabber = new SchemaGrabber();
			grabber.setProperties(properties);
			grabber.setConnectionProvider(connProvider);
			if (line.hasOption('o')) {
				grabber.setOutputFile(line.getOptionValue('o'));
			}
			if (line.hasOption('t')) {
				String[] names = line.getOptionValues('t');
				if (names != null) {
					for (int i = 0; i < names.length; i++) {
						grabber.addTable(names[i]);
					}
				}
			}
			grabber.doTheJob();
			return;
		}

		ProgressMonitor progressMonitor = null;
		String progressMonitorClass = properties.getProperty("dbmonster.progress.monitor", null);
		if (progressMonitorClass != null) {
			try {
				Class clazz = Class.forName(progressMonitorClass);
				progressMonitor = (ProgressMonitor) clazz.newInstance();
			} catch (Exception e) {
				System.err.println("Unable to instanciate progress monitor class: " + e.getMessage());
			}
		}

		// Start
		DBMonster dbm = new DBMonster();
		if (progressMonitor != null) {
			dbm.setProgressMonitor(progressMonitor);
		}
		connProvider.setLogger(dbm.getLogger());
		dbm.setConnectionProvider(connProvider);
		dbm.setProperties(properties);

		if (line.hasOption('f')) {
			dbm.setPreScript(new File(line.getOptionValue('f')));
		}

		if (line.hasOption('l')) {
			dbm.setPostScript(new File(line.getOptionValue('l')));
		}

		if (line.hasOption('n')) {
			int transactionSize = 0;
			try {
				transactionSize = Integer.parseInt(System.getProperty("dbmonster.jdbc.transaction.size"));
			} catch (Exception e) {
			}
			if (transactionSize == 0) {
				try {
					transactionSize = Integer.parseInt(line.getOptionValue('n'));
				} catch (Exception e) {
				}
			}
			dbm.setTransactionSize(transactionSize);
		}

		String[] schemas = line.getOptionValues('s');
		if (schemas != null) {
			for (int i = 0; i < schemas.length; i++) {
				Schema schema = SchemaUtil.loadSchema(schemas[i], dbm.getLogger());
				dbm.addSchema(schema);
			}
			dbm.doTheJob();
		}
	}

	/**
	 * Loads properties from ~/dbmonster.properties and ./dbmonster.properties
	 *
	 * @throws Exception
	 *             if properties cannot be loaded
	 */
	private void loadProperties() throws Exception {
		String userHome = System.getProperty("user.home");
		File f = new File(userHome, "dbmonster.properties");
		readProperties(f);
		f = new File("dbmonster.properties");
		readProperties(f);
	}

	/**
	 * Reads properties from a file.
	 *
	 * @param file
	 *            the .properties file
	 *
	 * @throws Exception
	 *             if file cannot be read.
	 */
	private void readProperties(File file) throws Exception {
		if (file.exists() && file.canRead() && file.isFile()) {
			InputStream fis = new BufferedInputStream(new FileInputStream(file));
			properties.load(fis);
		}
	}

	/**
	 * Exports some global properties to System properties.
	 */
	private void exportProperties() {
		Enumeration propertyKeys = properties.propertyNames();
		while (propertyKeys.hasMoreElements()) {
			String key = (String) propertyKeys.nextElement();
			if (key.startsWith("dbmonster.jdbc.")) {
				System.setProperty(key, properties.getProperty(key));
			}
		}
	}

	/**
	 * Prepares the command line.
	 *
	 * @param args
	 *            command line
	 *
	 * @return command line
	 *
	 * @throws Exception
	 *             if command line is wrong
	 */
	private CommandLine prepareCommandLine(String[] args) throws Exception {

		CommandLineParser parser = new PosixParser();

		Option conf = OptionBuilder.withArgName("config file").hasArg()
				.withDescription("use additional configuration file").withLongOpt("config").create('c');

		Option schema = OptionBuilder.withArgName("schema file[s]").hasArgs().withDescription("schema files")
				.withLongOpt("schema").create('s');

		Option help = OptionBuilder.withDescription("display help (this screen)").withLongOpt("help").create('h');

		Option grab = OptionBuilder.withDescription("start Schema Grabber only").withLongOpt("grab").create();

		Option tables = OptionBuilder.withArgName("table[s] name[s]").hasArgs()
				.withDescription("specify tables you want to grab").withLongOpt("tables").create('t');

		Option output = OptionBuilder.withArgName("output file").hasArg()
				.withDescription("output file for Schema Grabber").withLongOpt("out").create('o');

		Option prescript = OptionBuilder.withArgName("pre-generation script").hasArg()
				.withDescription("execute statements from this file before data generation").withLongOpt("first")
				.create('f');

		Option postscript = OptionBuilder.withArgName("post-generation script").hasArg()
				.withDescription("execute statements from this file after data generation").withLongOpt("last")
				.create('l');

		Option transactionSize = OptionBuilder.withArgName("num").hasArg()
				.withDescription("number of insert statements per transaction").withLongOpt("transaction-size")
				.create('n');

		options.addOption(conf);
		options.addOption(schema);
		options.addOption(help);
		options.addOption(prescript);
		options.addOption(postscript);
		options.addOption(grab);
		options.addOption(tables);
		options.addOption(output);
		options.addOption(transactionSize);

		CommandLine line = parser.parse(options, args);

		return line;
	}
}
