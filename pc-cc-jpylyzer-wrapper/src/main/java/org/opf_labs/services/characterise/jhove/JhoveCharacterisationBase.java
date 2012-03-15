/**
 * 
 */
package org.opf_labs.services.characterise.jhove;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.JhoveException;
import edu.harvard.hul.ois.jhove.Module;
import edu.harvard.hul.ois.jhove.Property;
import edu.harvard.hul.ois.jhove.PropertyArity;
import edu.harvard.hul.ois.jhove.PropertyType;

/**
 * @author carl
 * 
 */
public abstract class JhoveCharacterisationBase {
	private static final String CONFIG_PATH = "C:/bin/jhove-1_6/jhove/conf/jhove.conf";
	private static final String APP_NAME = "OPF JHOVE Characterisation";
	private static final String RELEASE_ID = "0.1";
	private static final String USAGE = "";
	private static final String RIGHTS = "";
	private static final int[] DATE = { 2011, 01, 14 };

	protected App app = null;
	protected JhoveBase jhoveBase = null;

	/**
	 * 
	 */
	public JhoveCharacterisationBase() {
		// Create the Jhove app
		app = new App(APP_NAME, RELEASE_ID, DATE, USAGE, RIGHTS);
		// Create the JhoveBase object and initialise it from the config file
		try {
			jhoveBase = new JhoveBase();
			this.jhoveBase.init(CONFIG_PATH, null);
		} catch (JhoveException e) {
			throw new IllegalStateException("Couldn't initialise JHOVE", e);
		}
	}

	/**
	 * @return a map of string names / jhove modules
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Module> getModuleMap() {
		return this.jhoveBase.getModuleMap();
	}

	/**
	 * Method that processes a java.util.Collection of JHOVE properties. An
	 * individual property can itself be a list of properties.
	 * 
	 * @param properties
	 *            a java.util.Collection of JHOVE properties
	 */
	@SuppressWarnings("unchecked")
	protected void processPropertyCollection(Collection<Property> properties) {
		// OK first iterate through the collection of properties
		for (Property property : properties) {
			// OK if it's an array of properties
			if (property.getArity() == PropertyArity.ARRAY) {
				if (property.getType() == PropertyType.PROPERTY)
					this.processPropertyCollection(Arrays.asList((Property []) property.getValue()));
				else
					this.processValueCollection(property);
			} else if ((property.getArity() == PropertyArity.LIST) ||
					   (property.getArity() == PropertyArity.SET)){
				if (property.getType() == PropertyType.PROPERTY)
					this.processPropertyCollection((Collection<Property>) property.getValue());
				else
					this.processValueCollection(property);
			} else if (property.getArity() == PropertyArity.MAP) {
				if (property.getType() == PropertyType.PROPERTY) {
					Map<String, Property> moreProperties = (Map<String, Property>)property.getValue();
					this.processPropertyCollection(moreProperties.values());
				}
				else
					this.processValueCollection(property);
			} else {
				processProperty(property);
			}
		}
	}
	
	protected void processProperty(Property property) {
		System.out.println("propName:" + property.getName());
		System.out.println("propArr:" + property.getArity());
		System.out.println("propType:" + property.getType());
		if (property.getType() == PropertyType.PROPERTY)
			this.processProperty((Property)property.getValue());
		else
			System.out.println("propVal:" + property.getValue());
	}
	
	protected void processValueCollection(Property property) {
		System.out.println("collName:" + property.getName());
		System.out.println("collArr:" + property.getArity());
		System.out.println("collType:" + property.getType());
		System.out.println("collVal:" + property.getValue());
	}
}
