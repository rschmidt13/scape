package eu.scape_project.core;

/**
 * @author  <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *          <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT SourceForge</a>
 *          <a href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
 * @version 0.1
 * 
 * Created Dec 21, 2011:2:52:32 PM
 */
public class ScapeNamespaces {

	/**
	 * Root namespace for the SCAPE project
	 */
    public static final String SCAPE_NS = "http://scape-project.eu";


    /**
     * The namespace for the planets digital objects, and all the
     * contained datastructures
     * @see #SCAPE_NS
     */
    public static final String OBJECTS_NS = SCAPE_NS +"/objects";


    /**
     * The namespace for all planets services, and service interfaces
     * and datastructures like ServiceDescription and ServiceReport
     * @see #SCAPE_NS
     */
    public static final String SERVICES_NS = SCAPE_NS + "/services";

    /**
     *  A namespace for the tool elements for wrapped tools
     * @see #SERVICES_NS
     */
    public static final String TOOLS_NS = SERVICES_NS + "/tools";


    /**
     * The namespace for datatypes not belonging to objects or services
     *
     * @see #OBJECTS_NS
     * @see #SERVICES_NS
     */
    public static final String DATATYPES_NS = SERVICES_NS+"/datatypes";


    /**
     * The Dublin Core namespace, used for certain terms
     */
    public static final String DC_TERMS_NS = "http://purl.org/dc/terms/";
}
