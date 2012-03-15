/**
 * 
 */
package eu.scape_project.pc.cc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;

/**
 * @author  <a href="mailto:carl.wilson.bl@gmail.com">Carl Wilson</a>
 *          <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT SourceForge</a>
 *          <a href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
 * @version 0.1
 * 
 * Created Jan 16, 2012:3:11:30 PM
 */

public class ContainerUnpacker {
	private static int BUFFER_SIZE = 1024 * 64;
	private static final String JAVA_TEMP_DIR_PROPERTY = "java.io.tmpdir";
	private static final File SYSTEM_TEMP_DIR = new File(System.getProperty(JAVA_TEMP_DIR_PROPERTY));

	private ZipFile zipFile;
	private String zipBaseName;
	private File destDir;
	private Map<String, File> unpackedEntries = new HashMap<String, File>();

	@SuppressWarnings("unused")
	private ContainerUnpacker() {/** Disable default constructor */}

	ContainerUnpacker(String zipFilePath) throws IOException {
		if ((!SYSTEM_TEMP_DIR.exists()) || (!SYSTEM_TEMP_DIR.isDirectory())) {
			throw new IllegalStateException("Couldn't find the temp directory:" + SYSTEM_TEMP_DIR.getAbsolutePath());
		}
		File testFile = new File(zipFilePath);
		
		if ((testFile == null) || (!testFile.exists()) || (!testFile.isFile())) {
			throw new IllegalArgumentException("String argument " + zipFilePath + " should be an existing file.");
		}
		
		this.zipFile = new ZipFile(zipFilePath);
		this.zipBaseName = FilenameUtils.getBaseName(zipFilePath);
		this.destDir = new File(SYSTEM_TEMP_DIR, this.getZipName());
		if (this.destDir.exists()) {
			ContainerUnpacker.cleanup(this.destDir);
		}
		if (!this.destDir.mkdir()) {
			throw new IllegalStateException("Couldn't create the temp directory:" + this.destDir.getAbsolutePath());
		}
		this.unpackZipToTempFolder();
	}
	
	/**
	 * @return the base name of the zip file
	 */
	public String getZipName() {return this.zipBaseName;}
	
	/**
	 * @param entryName the name of the zip entry
	 * @return the unpacked file for the zip entry
	 */
	public File getUnzippedFile(String entryName) {
		return this.unpackedEntries.get(entryName);
	}
	
	/**
	 * @return the set of entry names unpacked from the zip
	 */
	public Set<String> getEntryNames() {
		return this.unpackedEntries.keySet();
	}
	
	/**
	 * Cleans up the temp files and dirs
	 */
	public void cleanup() {
		ContainerUnpacker.cleanup(this.destDir);
	}

	/**
	 * @param zipPath path to the zip to unpack
	 * @return a new instance to unpack the zip container at path
	 * @throws IOException
	 */
	public static ContainerUnpacker getInstance(String zipPath) throws IOException {
		return new ContainerUnpacker(zipPath);
	}
	/**
	 * Cleans up the temp files and dirs
	 * @param dir 
	 */
	private static void cleanup(File dir) {
		if (dir.isDirectory())
			for (File file : dir.listFiles()) {
				ContainerUnpacker.cleanup(file);
			}
		dir.delete();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		for (String zipPath : args) {
			ContainerUnpacker unpacker = new ContainerUnpacker(zipPath);
			for (String entryName : unpacker.getEntryNames()) {
				System.out.println(entryName + "@" + unpacker.getUnzippedFile(entryName).getAbsolutePath());
			}
			unpacker.cleanup();
		}
	}

	private void unpackZipToTempFolder() {
		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) this.zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			try {
				InputStream entryStream = this.zipFile.getInputStream(entry);
				File dest = new File(this.destDir, entry.getName());
				if (!dest.getParentFile().exists()) {
					dest.getParentFile().mkdirs();
				}
				copyStreamToFile(entryStream, dest);
				this.unpackedEntries.put(entry.getName(), dest);
				entryStream.close();
			} catch (IOException excep) {
				// TODO Auto-generated catch block
				excep.printStackTrace();
			}
		}
	}
	
	private static void copyStreamToFile(InputStream source, File dest) throws IOException {
		byte buffer[] = new byte[BUFFER_SIZE];
		FileOutputStream fos = new FileOutputStream(dest);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		BufferedInputStream bis = new BufferedInputStream(source);
		int bytesRead;
		while ((bytesRead = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}
		bos.flush();
		bos.close();
		bis.close();
		fos.close();
	}
}
