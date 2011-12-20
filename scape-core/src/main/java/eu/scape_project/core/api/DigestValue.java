/**
 * 
 */
package eu.scape_project.core.api;

import java.net.URI;

import eu.scape_project.core.Constants;

/**
 * Interface that represents a digest / cryptographic hash value. The interface also defines an enum that covers the
 * most commonly used digest algorithms, and their Java names.
 * 
 * @author <a href="mailto:carl.wilson.bl@gmail.com">Carl Wilson</a><br/>
 *         <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl@SourceForge</a><br/>
 *         <a href="https://github.com/carlwilson-bl">carlwilson-bl@github</a>
 * @version 0.1 Created Nov 21, 2011:1:54:25 PM
 */

public interface DigestValue {
	/**
	 * Enum to identify the java supported digest algorithms. The javaName field is used to identify the algorithm for
	 * java.security.MessageDigest. The list isn't supposed to be authoritative but was grabbed by the code in the
	 * DigestUtilities class that lists the providers and algorithms.
	 * 
	 * java.security.MessageDigest algorithms are identified by string names, which are a little brittle. This enum can
	 * be used to ensure that the right name is used when creating a digest, e.g. for an MD5 digest:
	 * <p/>
	 * {@code DigestAlgorithm alg = DigestAlgorithm.MD5;}<br/>
	 * {@code MessageDigest digest = new MessageDigest.getInstance(alg.getJavaName());}
	 * 
	 * @author <a href="mailto:carl.wilson.bl@gmail.com">Carl Wilson</a><br/>
	 *         <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl@SourceForge</a><br/>
	 *         <a href="https://github.com/carlwilson-bl">carlwilson-bl@github</a>
	 */
	public enum DigestAlgorithm {
		/** MD5 algorithm identifier */
		MD2("MD2"),
		/** MD5 algorithm identifier */
		MD5("MD5"),
		/** SHA algorithm identifier */
		SHA("SHA"),
		/** SHA1 algorithm identifier */
		SHA1("SHA-1"),
		/** SHA256 algorithm identifier */
		SHA256("SHA-256"),
		/** SHA384 algorithm identifier */
		SHA384("SHA-384"),
		/** SHA512 algorithm identifier */
		SHA512("SHA-512");

		/** SCAPE algorithm id URI scheme prefix */
		public final static String ALGID_URI_PREFIX = Constants.SCAPE_URI_SCHEME
				+ "digest/";
		private final String javaName; // java.security.MessageDigest string
										// identifier

		DigestAlgorithm(String javaName) {
			this.javaName = javaName;
		}

		/**
		 * Get the <code>java.lang.String</code> name for the digest algorithm. Can be used as a paramater for:<br/>
		 * <code>java.security.MessageDigest.getInstance(String)</code>.
		 * 
		 * @return the java name for the enum instance
		 */
		public final String getJavaName() {
			return this.javaName;
		}

		/**
		 * Get the SCAPE URI that uniquely identifies the digest algorithm.
		 * 
		 * @return the <code>java.net.URI</code> SCAPE digest algorithm identifier
		 */
		public final URI getId() {
			return URI.create(ALGID_URI_PREFIX + this.javaName);
		}
	}

	/**
	 * The {@link eu.scape_project.core.api.DigestValue.DigestAlgorithm} enum that identifies the digest algorithm.
	 * 
	 * @return the <code>enum DigestAlgorithm</code> for the digest algorithm
	 */
	public DigestAlgorithm getAlgorithm();

	/**
	 * Get the SCAPE URI that uniquely identifies the digest algorithm.
	 * 
	 * @return the <code>java.net.URI</code> SCAPE digest algorithm identifier
	 */
	public URI getAlgorithmId();

	/**
	 * Get the hex <code>java.lang.String</code> representation of the digest value.
	 * 
	 * @return the hex value of the digest as a <code>java.lang.String</code>
	 */
	public String getHexDigest();

	/**
	 * Get the digest value as a <code>byte[]</code>.
	 * 
	 * @return the digest value as a <code>byte[]</code>
	 */
	public byte[] getDigest();
}
