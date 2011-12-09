/*
 * Copyright 2011 The SCAPE Project Consortium
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.scape_project.xa.tw.cli;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.xa.tw.Constants;
import eu.scape_project.xa.tw.conf.Configuration;
import eu.scape_project.xa.tw.gen.DeploymentCreator;
import eu.scape_project.xa.tw.gen.GeneratorException;
import eu.scape_project.xa.tw.gen.PropertiesSubstitutor;
import eu.scape_project.xa.tw.gen.ServiceCodeCreator;
import eu.scape_project.xa.tw.gen.ServiceDef;
import eu.scape_project.xa.tw.gen.WsdlCreator;
import eu.scape_project.xa.tw.tmpl.ServiceCode;
import eu.scape_project.xa.tw.tmpl.ServiceXml;
import eu.scape_project.xa.tw.toolspec.Operation;
import eu.scape_project.xa.tw.toolspec.Service;
import eu.scape_project.xa.tw.toolspec.Toolspec;
import eu.scape_project.xa.tw.util.FileUtil;

/**
 * Command line interface for the SCAPE toolwrapper.
 * Generates a maven project that can be used to
 * deploy a soap/rest web service.
 * 
 * <p>The tool is invoked java -jar toolwrapper.jar [OPTIONS].</p>
 * <p>Options are<br/>
 * <ul>
 * <li>{@code -h --help            Prints a usage message}</li>
 * <li>{@code -d --default         Generates a default}</li>
 * <li>{@code -p --print s         Print project setting properties to stdout}</li>
 * <li>{@code -p --print t         Print default toolsec to stdout}</li>
 * <li>{@code -q --quiet           Minimal output, default off}</li>
 * <li>{@code -s --settings <FILE> Path to project settings Java properties file}</li>
 * <li>{@code -t --toolspec <FILE> Path to tool specfication XML file}</li>
 * <li>{@code -v --version         Print toolwrapper version}</li>
 * </ul>
 * </p>
 *
 * @author shsdev https://github.com/shsdev
 * @version 0.3
 */
public class ToolWrapperCLI {
	// Logger instance
	private static Logger logger = LoggerFactory.getLogger(ToolWrapperCLI.class
			.getName());
	private static Configuration ioc = new Configuration(); // and the config
	private static final String SYNTAX = "java -jar toolwrapper.jar";

	/**
	 * Default constructor
	 */
	public ToolWrapperCLI() {
	}

	/**
	 * Main method of the command line application
	 * 
	 * @param args
	 *            Arguments of the command line application
	 * @throws GeneratorException
	 *             Exception if project generation fails
	 */
	public static void main(String[] args) throws GeneratorException {

		CommandLineParser cmdParser = new PosixParser();
		try {
			// Parse the command line arguments
			CommandLine cmd = cmdParser.parse(MyOptions.OPTIONS, args);
			// If no args or help selected
			if (cmd.getOptions().length == 0 | cmd.hasOption(MyOptions.HELP_OPT)) {
				// OK help needed
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(SYNTAX, MyOptions.OPTIONS, true);
				System.exit(0);
			}

			if (cmd.hasOption(MyOptions.VERSION_OPT)) {
				// Print version
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(SYNTAX, MyOptions.OPTIONS, true);
				System.exit(0);
			}

			String toolspecPath = cmd.getOptionValue(MyOptions.TOOLSPEC_OPT,
					Constants.DEFAULT_TOOLSPEC);
			File toolspecFile = new File(toolspecPath);
			if (MyOptions.OPTIONS.hasOption(MyOptions.SETTINGS_OPT)) {
				ioc.setProjConf(new File(cmd.getOptionValue(MyOptions.SETTINGS_OPT)));
			} else {
				ioc.setProjConf(getDefaultPropertiesResourceFile());
			}
			ioc.setXmlConf(toolspecFile);

		} catch (ParseException excep) {
			// Problem parsing the command line args, just print the message and
			// help
			logger.error("Problem parsing command line arguments.", excep);
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(SYNTAX, MyOptions.OPTIONS, true);
			System.exit(1);
		}

		if (!ioc.hasConfig()) {
			throw new GeneratorException("No configuration available.");
		}
		JAXBContext context;
		try {
			context = JAXBContext
					.newInstance("eu.scape_project.xa.tw.toolspec");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Toolspec toolspec = (Toolspec) unmarshaller.unmarshal(new File(ioc
					.getXmlConf()));
			// general tool specification properties
			logger.info("Toolspec model: " + toolspec.getModel());
			logger.info("Tool id: " + toolspec.getId());
			logger.info("Tool name: " + toolspec.getName());
			logger.info("Tool version: " + toolspec.getVersion());

			// List of services for the tool
			List<Service> services = toolspec.getServices().getService();
			// For each service a different maven project will be generated
			for (Service service : services) {
				createService(service, toolspec.getVersion());
			}
		} catch (IOException ex) {
			logger.error("An IOException occurred", ex);
		} catch (JAXBException ex) {
			logger.error("JAXBException", ex);
			throw new GeneratorException(
					"Unable to create XML binding for toolspec");
		}
	}

	/**
	 * Create a service. Each service is packaged in a separate web application
	 * archive (*.war) and can be deployed to different web application
	 * container (e.g. Apache Tomcat).
	 * 
	 * @param service
	 *            Service
	 * @param toolVersion
	 *            Version of the tool
	 * @throws GeneratorException
	 * @throws IOException
	 */
	private static void createService(Service service, String toolVersion)
			throws GeneratorException, IOException {
		logger.info("Service id: " + service.getSid());
		logger.info("Service name: " + service.getName());
		logger.info("Service type: " + service.getType());
		// Properties substitutor is created for each service
		PropertiesSubstitutor st = new PropertiesSubstitutor(ioc.getProjConf());
		// Service name is composed of Service Name and Tool Version
		ServiceDef sdef = new ServiceDef(service.getName(), toolVersion);
		st.setServiceDef(sdef);
		st.addVariable("tool_version", sdef.getVersion());
		st.addVariable("project_title", sdef.getName());
		st.addVariable("global_package_name", service.getServicepackage());
		String cpp = service.getContextpathprefix();
		st.addVariable("contextpath_prefix", ((cpp == null) ? "" : cpp));
		st.deriveVariables();
		logger.info(st.getTemplateDir());
		File dir = new File(st.getTemplateDir());
		st.processDirectory(dir);
		String generatedDir = st.getGenerateDir();
		String projMidfix = st.getProjectMidfix();
		String projDir = st.getProjectDirectory();

		// target service wsdl
		String wsdlSourcePath = FileUtil.makePath("tmpl") + "Template.wsdl";
		logger.debug("Source WSDL file: " + wsdlSourcePath);

		// target service wsdl
		String wsdlTargetPath = FileUtil.makePath(generatedDir, projDir, "src",
				"main", "webapp") + projMidfix + ".wsdl";
		logger.debug("Target WSDL file: " + wsdlTargetPath);

		List<Operation> operations = service.getOperations().getOperation();
		WsdlCreator wsdlCreator = new WsdlCreator(st, wsdlSourcePath,
				wsdlTargetPath, operations);
		wsdlCreator.insertDataTypes();

		st.processFile(new File(wsdlTargetPath));

		// service code
		String sjf = FileUtil.makePath(generatedDir, projDir, "src", "main",
				"java", st.getProjectPackagePath()) + projMidfix + ".java";
		logger.debug("Initialising service file: " + sjf);
		String serviceTmpl = st.getProp("project.template.service");
		// service operation java code template
		ServiceCode sc = new ServiceCode(serviceTmpl);
		// service xml
		ServiceXml sxml = new ServiceXml("tmpl/servicexml.vm");
		sxml.put(st.getContext());
		ServiceCodeCreator scc = new ServiceCodeCreator(st, sc, sxml,
				operations);
		scc.createOperations();

		logger.debug("Writing service file: " + sjf);
		// add project properties velocity context
		sc.put(st.getContext());
		sc.put("operations", sc.getOperations());
		sc.create(sjf);
		String sxmlFile = FileUtil.makePath(generatedDir, projDir,
				"src/main/webapp/WEB-INF/services", st.getProjectMidfix(),
				"META-INF")
				+ "services.xml";

		// adding operations to services.xml
		sxml.put("servxmlops", sxml.getOperations());
		logger.debug("Writing services.xml file: " + sxmlFile);
		sxml.put("url_filter", st.getProp("url.filter"));
		sxml.create(sxmlFile);

		// pom.xml (maven project definition)
		String pomPath = FileUtil.makePath(generatedDir, projDir) + "pom.xml";
		DeploymentCreator pomCreator = new DeploymentCreator(pomPath, service,
				st);
		pomCreator.createPom();

		logger.info("Project created in in \""
				+ FileUtil.makePath(generatedDir, projDir) + "\"");
	}

	/**
	 * @return the default properties resource file packaged with the jar
	 */
	public static File getDefaultPropertiesResourceFile() {
		try {
			return new File(ClassLoader.getSystemResource(Constants.RESOURCE_PACKAGE + Constants.DEFAULT_PROJECT_PROPERTIES).toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException("Could not read packaged properties file.", e);
		}
	}
	/**
	 * Helper class that is used to set up the commons-cli options object.
	 * 
	 * @author <a href="mailto:carl.wilson.bl@gmail.com">Carl Wilson</a>
	 *	   <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl@SourceForge</a>
	 *	   <a href="https://github.com/carlwilson-bl">carlwilson-bl@github</a>
	 *
	 */
	private static final class MyOptions {
 		// Statics to set up command line arguments
		private static final String HELP_FLG = "h";
		private static final String HELP_OPT = "help";
		private static final String HELP_OPT_DESC = "print this message.";
		private static final String DEFAULT_FLG = "d";
		private static final String DEFAULT_OPT = "def";
		private static final String DEFAULT_OPT_DESC = "generate default simple copy project, overrides other options.";
		private static final String PRINT_FLG = "p";
		private static final String PRINT_OPT = "print";
		private static final String PRINT_OPT_ARG = "OPT";
		private static final String PRINT_OPT_DESC = "OPT t or no arg prints default toolspec to stdout. OPT t prints project settings to stdout.";
		private static final String SETTINGS_FLG = "s";
		private static final String SETTINGS_OPT = "settings";
		private static final String SETTINGS_OPT_ARG = "FILE";
		private static final String SETTINGS_OPT_DESC = "path to project settings Java properties file.";
		private static final String TOOLSPEC_FLG = "t";
		private static final String TOOLSPEC_OPT = "toolspec";
		private static final String TOOLSPEC_OPT_ARG = "FILE";
		private static final String TOOLSPEC_OPT_DESC = "path to tool specification XML file.";
		private static final String VERSION_FLG = "v";
		private static final String VERSION_OPT = "version";
		private static final String VERSION_OPT_DESC = "print toolwrapper version.";

		/**
		 * Apache commons-cli options, used to set up and parse the command line args
		 */
		final static Options OPTIONS = new Options();
		static {
			// Option for help
			OPTIONS.addOption(HELP_FLG, HELP_OPT, false, HELP_OPT_DESC);
			// Add the default option
			OptionBuilder.withLongOpt(DEFAULT_OPT);
			OptionBuilder.withDescription(DEFAULT_OPT_DESC);
			OPTIONS.addOption(OptionBuilder.create(DEFAULT_FLG));
			// Add option for printing defaults
			OptionBuilder.hasArg();
			OptionBuilder.withLongOpt(PRINT_FLG);
			OptionBuilder.withArgName(PRINT_OPT);
			OptionBuilder.withDescription(PRINT_OPT_ARG);
			OPTIONS.addOption(OptionBuilder.create(PRINT_OPT_DESC));
			// Add option for settings file path
			OptionBuilder.hasArg();
			OptionBuilder.withLongOpt(SETTINGS_FLG);
			OptionBuilder.withArgName(SETTINGS_OPT);
			OptionBuilder.withDescription(SETTINGS_OPT_ARG);
			OPTIONS.addOption(OptionBuilder.create(SETTINGS_OPT_DESC));
			// Add option for toolspec file path
			OptionBuilder.hasArg();
			OptionBuilder.withLongOpt(TOOLSPEC_OPT);
			OptionBuilder.withArgName(TOOLSPEC_OPT_ARG);
			OptionBuilder.withDescription(TOOLSPEC_OPT_DESC);
			OPTIONS.addOption(OptionBuilder.create(TOOLSPEC_FLG));
			// Option for version
			OPTIONS.addOption(VERSION_FLG, VERSION_OPT, false, VERSION_OPT_DESC);
		}
	}
}