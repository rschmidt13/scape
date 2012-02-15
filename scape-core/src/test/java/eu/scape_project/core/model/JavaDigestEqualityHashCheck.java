/**
 * 
 */
package eu.scape_project.core.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.DataPoints;

import eu.scape_project.core.AllCoreTest;
import eu.scape_project.core.api.DigestValue.DigestAlgorithm;
import eu.scape_project.core.test.helpers.ObjectEqualityHashHelper;

/**
 * Test class that tests the .equals(Object obj), and .hashCode() methods for the JavaDigestValue class.
 * The tests are performed by extending the ObjectEqualityHashHelper theory class.
 *
 * @author  <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 *          <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT SourceForge</a>
 *          <a href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
 * @version 0.1
 *
 * @see eu.scape_project.core.test.helpers.ObjectEqualityHashHelper
 * 
 * Created Dec 20, 2011:4:37:46 PM
 */

public class JavaDigestEqualityHashCheck extends ObjectEqualityHashHelper {
	/**
	 * Method to create the DataPoints
	 * @return an array of DataPoints (JavaDigestValue objects).
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	@DataPoints
	public static JavaDigestValue[] data() throws FileNotFoundException, NoSuchAlgorithmException, IOException {
		List<JavaDigestValue> values = new ArrayList<JavaDigestValue>();
		List<File> testFiles = AllCoreTest
				.getFilesFromResourceDir(AllCoreTest.TEST_DATA_ROOT);
		for (File file : testFiles) {
			values.add(JavaDigestValue.getInstance(DigestAlgorithm.MD5, file));
		}
		return values.toArray(new JavaDigestValue[0]);
	}
}
