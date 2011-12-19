/**
 * 
 */
package eu.scape_project.core.model;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.scape_project.core.api.DigestValue.DigestAlgorithm;

/**
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a> <a
 *         href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT SourceForge</a> <a
 *         href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
 * @version 0.1
 * 
 *          Created Dec 5, 2011:2:34:00 PM
 */
@XmlRootElement
public final class ByteStreamInfo {
	private static final int BUFFER_SIZE = (32 * 1024);
	/** The default MIME type string */
	public static final String DEFAULT_MIME = "application/octet-stream";
	@XmlAttribute
	private final long length;
	@XmlAttribute
	private final String mimeType;
	@XmlElement
	private final Set<JavaDigestValue> digests;

	@SuppressWarnings("unused")
	private ByteStreamInfo() {
		this.length = 0L;
		this.mimeType = null;
		this.digests = null;
	}

	ByteStreamInfo(long length, Set<JavaDigestValue> digests) {
		this(length, digests, DEFAULT_MIME);
	}

	ByteStreamInfo(long length, Set<JavaDigestValue> digests, String mime) {
		this.length = length;
		this.digests = Collections.unmodifiableSet(digests);
		this.mimeType = mime;
	}

	/**
	 * @return the size of the byte stream in bytes
	 */
	public final long getLength() {
		return this.length;
	}

	/**
	 * @return the set of digests known for this byte sequence
	 */
	public final Set<JavaDigestValue> getDigests() {
		return this.digests;
	}

	/**
	 * @return the recorded MIME type of the file
	 */
	public final String getMimeType() {
		return this.mimeType;
	}

	/**
	 * @return a String xml representation of the object
	 * @throws JAXBException
	 */
	public String toXml() throws JAXBException {
		JAXBContext jbc = JAXBContext.newInstance(ByteStreamInfo.class);
		Marshaller m = jbc.createMarshaller();
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		m.marshal(this, sw);
		return sw.toString();
	}

	@Override
	public String toString() {
		String retVal = String.valueOf(this.length) + "|" + this.mimeType + "|";
		for (JavaDigestValue checksum : this.digests) {
			retVal += "[" + checksum.toString() + "]";
		}
		return retVal += "|";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.digests == null) ? 0 : this.digests.hashCode());
		result = prime * result + (int) (this.length ^ (this.length >>> 32));
		result = prime * result
				+ ((this.mimeType == null) ? 0 : this.mimeType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// Check if it's the same object
		if (this == obj)
			return true;
		if (!(obj instanceof ByteStreamInfo))
			return false;
		ByteStreamInfo other = (ByteStreamInfo) obj;
		if (this.digests == null) {
			if (other.digests != null)
				return false;
		} else if (!this.digests.equals(other.digests))
			return false;
		if (this.mimeType == null) {
			if (other.mimeType != null)
				return false;
		} else if (!this.mimeType.equals(other.mimeType))
			return false;
		return (this.length == other.length);
	}

	/**
	 * @param xml
	 * @return a new JavaDigestValue object serialized from XML
	 * @throws JAXBException
	 */
	public static ByteStreamInfo getInstance(String xml) throws JAXBException {
		ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
		JAXBContext jbc = JAXBContext.newInstance(ByteStreamInfo.class);
		Unmarshaller um = jbc.createUnmarshaller();
		return (ByteStreamInfo) um.unmarshal(input);
	}

	/**
	 * @param length the length of the Byte Sequence in bytes
	 * @param digest
	 * @return a new ByteStreamInfo object
	 */
	public static ByteStreamInfo getInstance(long length, JavaDigestValue digest) {
		Set<JavaDigestValue> digests = new HashSet<JavaDigestValue>();
		digests.add(digest);
		return new ByteStreamInfo(length, digests);
	}
	/**
	 * @param length
	 *        the length of the byte sequence in bytes
	 * @param digests
	 *        a set of digest algorithms and values
	 * @return a new ByteStreamInfo instance created from the params
	 */
	public static ByteStreamInfo getInstance(long length,
			Set<JavaDigestValue> digests) {
		return new ByteStreamInfo(length, digests);
	}

	/**
	 * @param length
	 *        the length of the byte sequence in bytes
	 * @param digests
	 *        details of the checksums
	 * @param mime
	 *        the mime type of the item
	 * @return a new ByteStreamInfo instance created from the params
	 */
	public static ByteStreamInfo getInstance(long length,
			Set<JavaDigestValue> digests, String mime) {
		return new ByteStreamInfo(length, digests, mime);
	}

	/**
	 * @param algorithms
	 * @param is
	 * @return A ByteStreamInfo object containin the byte sequence length and the requested digest values calculated
	 *         from the byte sequence from the java.io.InputStream paramenter.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static ByteStreamInfo getInstance(DigestAlgorithm[] algorithms,
			InputStream is) throws IOException, NoSuchAlgorithmException {
		InputStream lastStream = is; // OK last stream is the first stream
		Map<DigestAlgorithm, MessageDigest> digests = new HashMap<DigestAlgorithm, MessageDigest>();
		// iterate through the passed algorithms
		for (DigestAlgorithm algorithm : algorithms) {
			// Skip it if it's in the map already
			if (digests.containsKey(algorithm))
				continue;
			MessageDigest digest = MessageDigest.getInstance(algorithm
					.getJavaName()); // Create a digest
			// Create a stream, add digest to set and record last stream
			DigestInputStream dis = new DigestInputStream(lastStream, digest);
			digests.put(algorithm, digest);
			lastStream = dis;
		}
		// Wrap it all in a Buffered Input Stream, and calculate the digests
		BufferedInputStream bis = new BufferedInputStream(lastStream);
		byte[] buff = new byte[BUFFER_SIZE];
		long length = 0L;
		int count;
		while ((count = bis.read(buff, 0, BUFFER_SIZE)) != -1) {
			length += count;
		}
		bis.close();
		Set<JavaDigestValue> results = new HashSet<JavaDigestValue>();
		for (DigestAlgorithm algorithm : digests.keySet()) {
			results.add(new JavaDigestValue(algorithm, digests.get(algorithm)
					.digest()));
		}
		return new ByteStreamInfo(length, results);
	}

}
