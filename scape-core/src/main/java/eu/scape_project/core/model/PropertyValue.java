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

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import eu.scape_project.core.ScapeNamespaces;

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
 * attributes, use a {@link PropertyValue.Builder}:
 * <p>
 * {@code Property p = new Property.Builder(uri).unit(unit).build();}
 * </p>
 * <p>
 * Instances of this class are immutable and so can be shared.
 * </p>
 * 
 * @author Andrew Jackson
 * @author Fabian Steeg
 */
@XmlType(name = "propertyValue", namespace = ScapeNamespaces.DATATYPES_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public final class PropertyValue implements Serializable {

	@XmlElement(namespace = ScapeNamespaces.DATATYPES_NS)
	private PropertyDefinition definition;
	@XmlElement(namespace = ScapeNamespaces.DATATYPES_NS)
	private String value = null;

	/** For JAXB. */
	@SuppressWarnings("unused")
	private PropertyValue() {
		/** For JAXB */
	}

	/**
	 * Create a property with id, name and value. To create properties with less
	 * or more attributes, use a {@link PropertyValue.Builder} instead.
	 * 
	 * @param defId
	 *            The java.net.URI identifier for the PropertyDefinition
	 * @param value
	 *            The property value
	 */
	public PropertyValue(final URI defId, final String value) {
		this.definitionId = defId;
		this.value = value;
	}

	/**
	 * @return the java.net.URI identifier of the PropertyDefinition
	 */
	public final URI getDefinitionId() {
		return this.definitionId;
	}

	/**
	 * @return the Property Value
	 */
	public final String getValue() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s [%s] = '%s'", this.getClass().getSimpleName(),
				this.definitionId, this.value);
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
				+ (this.definitionId == null ? 0 : this.definitionId.hashCode());
		result = prime * result
				+ (this.value == null ? 0 : this.value.hashCode());
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
		PropertyValue other = (PropertyValue) obj;
		if (this.definitionId == null) {
			if (other.definitionId != null) {
				return false;
			}
		} else if (!this.definitionId.equals(other.definitionId)) {
			return false;
		}
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}
}
