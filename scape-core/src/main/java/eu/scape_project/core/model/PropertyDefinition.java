/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 *
 */
package eu.scape_project.core.model;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.io.IOUtils;

import eu.scape_project.core.ScapeNamespaces;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Property representation using an URI as ID.
 * <p>
 * For the most common case (a property with ID, name and value), use the
 * {@link #Property(URI, String, String)} constructor:
 * </p>
 * <p>
 * {@code Property p = new Property(uri, name, value);}
 * </p>
 * Only the ID is actually required. To create properties with less or more
 * attributes, use a {@link PropertyDefinition.Builder}:
 * <p>
 * {@code Property p = new Property.Builder(uri).unit(unit).build();}
 * </p>
 * <p>
 * Instances of this class are immutable and so can be shared.
 * </p>
 * 
 * @author Andrew Jackson
 * @author Fabian Steeg
 * @author carlwilson-bl
 */
@XmlType(name = "PropertyDefinition", namespace = ScapeNamespaces.DATATYPES_NS)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public final class PropertyDefinition implements Serializable {

	@XmlElement(namespace = ScapeNamespaces.DATATYPES_NS)
	private URI identifier;
	@XmlElement(namespace = ScapeNamespaces.DATATYPES_NS)
	private String name = "";
	@XmlElement(namespace = ScapeNamespaces.DATATYPES_NS)
	private String unit = null;
	@XmlElement(namespace = ScapeNamespaces.DATATYPES_NS)
	private String description = "";
	@XmlElement(namespace = ScapeNamespaces.DATATYPES_NS)
	private String type = null;

	/** For JAXB. */
	private PropertyDefinition() {
		/** For JAXB. */
	}

	/**
	 * Create a property with id, name and value. To create properties with less
	 * or more attributes, use a {@link PropertyDefinition.Builder} instead.
	 * 
	 * @param identifier
	 *            The java.net.URI identifier of the PropertyDefinition
	 * @param name
	 *            The property name
	 */
	public PropertyDefinition(final URI identifier, final String name) {
		this.identifier = identifier;
		this.name = name;
	}

	/**
	 * @param builder
	 *            The builder to create a property from
	 */
	protected PropertyDefinition(final PropertyDefinition.Builder builder) {
		this.identifier = builder.identifier;
		this.name = builder.name;
		this.description = builder.description;
		this.unit = builder.unit;
		this.type = builder.type;
	}

	/**
	 * @return the uri
	 */
	public final URI getIdentifier() {
		return this.identifier;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * @return the unit
	 */
	public final String getUnit() {
		return this.unit;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return this.description;
	}

	/**
	 * @return the type
	 */
	public final String getType() {
		return this.type;
	}

	/**
	 * @param xml
	 *            The XML representation of an Container (as created from
	 *            calling toXml)
	 * @return An Container instance created from the given XML
	 */
	public static final PropertyDefinition of(final String xml) {
		try {
			/* Unmarshall with JAXB: */
			JAXBContext context = JAXBContext
					.newInstance(PropertyDefinition.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			Object object = unmarshaller.unmarshal(new StringReader(xml));
			PropertyDefinition unmarshalled = (PropertyDefinition) object;
			return unmarshalled;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return An XML representation of this Container (can be used to
	 *         instantiate an object using the static factory method)
	 */
	public final String toXml() {
		return toXml(false);
	}

	/**
	 * @return A formatted (pretty-printed) XML representation of this Container
	 */
	public final String toXmlFormatted() {
		return toXml(true);
	}

	@SuppressWarnings("boxing")
	private String toXml(boolean formatted) {
		try {
			/* Marshall with JAXB: */
			JAXBContext context = JAXBContext
					.newInstance(PropertyDefinition.class);
			Marshaller marshaller = context.createMarshaller();
			StringWriter writer = new StringWriter();
			marshaller.setProperty("jaxb.formatted.output", formatted);
			marshaller.marshal(this, writer);
			return writer.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param is an input stream to the XML ProperyDefinition
	 * @return the PropertyDefinition
	 */
	public static PropertyDefinition createFromInputStream(InputStream is) {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(is, writer);
			is.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return PropertyDefinition.of(writer.toString());
		
	}
	
	/**
	 * @param dir a directory containing property definition files
	 * @return a Set of PropertyDefinitions loaded from the files in the directory
	 */
	public final static Set<PropertyDefinition> loadSetFromDirectory(File dir) {
		Set<PropertyDefinition> defs = new HashSet<PropertyDefinition>();
		File[] files = dir.listFiles();
		for (File file : files) {
			if ((!file.isDirectory()) && (!file.isHidden())) {
				try {
					PropertyDefinition propDef = createFromInputStream(new FileInputStream(file));
					defs.add(propDef);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return Collections.unmodifiableSet(defs);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s [%s] '%s' (unit=%s type=%s description=%s)",
				this.getClass().getSimpleName(), this.identifier, this.name,
				this.unit, this.type, this.description);
	}

	/**
	 * Builder to create property instances with optional attributes.
	 * 
	 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
	 */
	public static final class Builder {
		/* URI is required: */
		final URI identifier;
		/* Defaults for optional values are set here: */
		String name = "";
		String description = "";
		String unit = null;
		String type = null;

		/**
		 * @param identifier
		 *            The java.net.URI identifier of the PropertyDefinition
		 * @throws IllegalArgumentException
		 *             if the given URI is null
		 */
		public Builder(final URI identifier) {
			if (identifier == null) {
				throw new IllegalArgumentException(
						"Property ID uri must not be null!");
			}
			this.identifier = identifier;
		}

		/**
		 * Create a new property by cloning and existing one.
		 * 
		 * @param p
		 *            The property to clone into this builder.
		 */
		public Builder(final PropertyDefinition p) {
			this.identifier = p.getIdentifier();
			this.name = p.getName();
			this.description = p.getDescription();
			this.unit = p.getUnit();
			this.type = p.getType();
		}

		/**
		 * @param nameVal
		 *            The property name
		 * @return This builder, for cascaded calls
		 */
		public Builder name(final String nameVal) {
			this.name = nameVal;
			return this;
		}

		/**
		 * @param desc
		 *            The property description
		 * @return This builder, for cascaded calls
		 */
		public Builder description(final String desc) {
			this.description = desc;
			return this;
		}

		/**
		 * @param unitVal
		 *            The property unit
		 * @return This builder, for cascaded calls
		 */
		public Builder unit(final String unitVal) {
			this.unit = unitVal;
			return this;
		}

		/**
		 * @param typeVal
		 *            The property type
		 * @return This builder, for cascaded calls
		 */
		public Builder type(final String typeVal) {
			this.type = typeVal;
			return this;
		}

		/**
		 * @return The finished immutable property instance
		 */
		public final PropertyDefinition build() {
			return new PropertyDefinition(this);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (this.description == null ? 0 : this.description.hashCode());
		result = prime * result
				+ (this.name == null ? 0 : this.name.hashCode());
		result = prime * result
				+ (this.type == null ? 0 : this.type.hashCode());
		result = prime * result
				+ (this.unit == null ? 0 : this.unit.hashCode());
		result = prime * result
				+ (this.identifier == null ? 0 : this.identifier.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PropertyDefinition other = (PropertyDefinition) obj;
		if (this.description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!this.description.equals(other.description)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!this.type.equals(other.type)) {
			return false;
		}
		if (this.unit == null) {
			if (other.unit != null) {
				return false;
			}
		} else if (!this.unit.equals(other.unit)) {
			return false;
		}
		if (this.identifier == null) {
			if (other.identifier != null) {
				return false;
			}
		} else if (!this.identifier.equals(other.identifier)) {
			return false;
		}
		return true;
	}
}
