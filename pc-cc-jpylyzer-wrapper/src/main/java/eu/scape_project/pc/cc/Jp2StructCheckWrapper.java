/**
 * 
 */
package eu.scape_project.pc.cc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * @author carl
 *
 */
public class Jp2StructCheckWrapper {
	// TODO: this is the work of a hard code hero, better handling of path needed
	private static final String DEFAULT_JP2STRUCTCHECK_PATH = "C:/bin/jp2kstructcheck/jp2StructCheck.exe";
	
	private File fileToStructCheck;

	private String jp2StructCheckPath = DEFAULT_JP2STRUCTCHECK_PATH;
	private boolean allBoxes = false;
	private boolean endOfCodeStream = false;

	private Jp2StructCheckWrapper(String pathToJp2StructChecker) {
		this.jp2StructCheckPath = pathToJp2StructChecker;
	}
	
	/**
	 * @param path string path to the file to check
	 * @return true if the struct checker finds the file is valid JP2K, otherwise false
	 * @throws FileNotFoundException
	 */
	public boolean structCheckJp2kFile(String path) throws FileNotFoundException {
		File jp2kFile = new File(path);
		return this.structCheckJp2kFile(jp2kFile);
	}
	
	/**
	 * @param jp2kFile
	 * @return true if the JP2K is valid, false otherwise
	 * @throws FileNotFoundException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public boolean structCheckJp2kFile(File jp2kFile) throws FileNotFoundException {
		if (jp2kFile == null) throw new IllegalArgumentException("File argument cannot be null");
		if (!jp2kFile.exists() || !jp2kFile.isFile()) {
			throw new FileNotFoundException("Couldn't find file " + jp2kFile.getAbsolutePath());
		}
		this.fileToStructCheck = jp2kFile;
		this.runJp2StructCheck();

		return this.isValid();
	}
	/**
	 * Is the image a valid JP2K?
	 * 
	 * @return true if a valid JP2K, false otherwise.
	 */
	public boolean isValid() {return this.allBoxes && this.endOfCodeStream;}

	/**
	 * @return a new instance of the struct checker using the default hard code string
	 */
	public static Jp2StructCheckWrapper getInstance() {return new Jp2StructCheckWrapper(DEFAULT_JP2STRUCTCHECK_PATH);}

	/**
	 * @param pathToJp2StructChecker path to the struct checker exe
	 * @return a new instance
	 */
	public static Jp2StructCheckWrapper getInstance(String pathToJp2StructChecker) {return new Jp2StructCheckWrapper(pathToJp2StructChecker);}

	/**
	 * @param args
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) {
		Jp2StructCheckWrapper structChecker = Jp2StructCheckWrapper.getInstance();
		for (String jp2kpath : args) {
			try {
				structChecker.structCheckJp2kFile(new File(jp2kpath));
				System.out.println(jp2kpath);
				System.out.println(String.valueOf(structChecker.isValid()));
			} catch (FileNotFoundException excep) {
				// TODO Auto-generated catch block
				excep.printStackTrace();
			}
		}
	}
	
	private void runJp2StructCheck() {
		String[] command = {jp2StructCheckPath, this.fileToStructCheck.getAbsolutePath()};

		try {
			Process pr = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			reader.readLine();
			this.allBoxes = reader.readLine().contains("True");
			this.endOfCodeStream = reader.readLine().contains("True");
		} catch (IOException excep) {
			// TODO Auto-generated catch block
			excep.printStackTrace();
		}
	}
}
