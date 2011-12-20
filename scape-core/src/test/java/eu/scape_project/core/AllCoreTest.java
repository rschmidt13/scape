package eu.scape_project.core;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import eu.scape_project.core.model.ByteStreamInfoTest;
import eu.scape_project.core.model.JavaDigestEqualityHashCheck;
import eu.scape_project.core.model.JavaDigestValueTest;
import eu.scape_project.core.utils.DigestUtilitiesTest;

/**
 * @author <a href="mailto:carl.wilson.bl@gmail.com">Carl Wilson</a> <a
 *         href="http://sourceforge.net/users/carlwilson-bl"
 *         >carlwilson-bl@SourceForge</a> <a
 *         href="https://github.com/carlwilson-bl">carlwilson-bl@github</a>
 */
@RunWith(Suite.class)
@SuiteClasses({ DigestUtilitiesTest.class, JavaDigestValueTest.class,
		ByteStreamInfoTest.class, JavaDigestEqualityHashCheck.class })
public class AllCoreTest {
	/** The root resource directory for the test data */
	public static String TEST_DATA_ROOT = "/eu/scape_project/test/data";

	/**
	 * @param dirName
	 * @return a java.util.List of java.io.File objects from the resource
	 *         directory
	 * @throws IllegalArgumentException
	 */
	public static List<File> getFilesFromResourceDir(String dirName) {
		// Check for a null directory name param
		if (dirName == null) {
			throw new NullPointerException(
					"dirName parameter should not be null.");
		}
		// OK get the resource directory as a file from the URL
		try {
			File dir = new File(AllCoreTest.class.getResource(dirName).toURI());
			if ((!dir.exists()) || (!dir.isDirectory())) {
				throw new IllegalArgumentException("dirName:" + dirName
						+ " is not an existing directory.");
			}
			return AllCoreTest.getFilesFromDir(dir);
		} catch (URISyntaxException excep) {
			throw new IllegalArgumentException(
					"Directory name does not translate to URI", excep);
		}
	}

	private static List<File> getFilesFromDir(File dir) {
		assert dir != null && dir.exists() && dir.isDirectory();
		// Get it's file children
		File[] files = dir.listFiles();
		assert files != null;
		ArrayList<File> retVal = new ArrayList<File>();
		// If not null then iterate and add non hidden files
		for (File file : files) {
			if ((!file.isDirectory()) || (file.isHidden())) {
				retVal.add(file);
			}
		}
		return retVal;
	}
}
