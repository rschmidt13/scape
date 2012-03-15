package org.opf_labs.services.characterise.jhove;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import edu.harvard.hul.ois.jhove.Module;
import edu.harvard.hul.ois.jhove.Property;
import edu.harvard.hul.ois.jhove.RepInfo;
import eu.scape_project.pc.cc.ContainerUnpacker;

/**
 * @author <a href="mailto:carl.wilson.bl@gmail.com">Carl Wilson</a>
 *	   <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl@SourceForge</a>
 *	   <a href="https://github.com/carlwilson-bl">carlwilson-bl@github</a>
 *
 */
public class JhoveJP2kCharacterisation extends JhoveCharacterisationBase {
	protected static final String HUL_NAME = "jpeg2000-hul";

	/**
	 * @throws Exception
	 */
	public JhoveJP2kCharacterisation() {
		super();
		if (!this.hasJp2kModule()) throw new IllegalStateException("The WAVE-HUL Jhove module could not be found");
	}
	
	private boolean hasJp2kModule() {
		return this.getModuleMap().containsKey(HUL_NAME);
	}
	
	/**
	 * @param stream
	 * @return true if a valid jp2k stream
	 * @throws IOException 
	 * @throws Exception
	 */
	public boolean isJp2k(File jp2kToCheck) throws IOException {
		Module wavModule = this.getModuleMap().get(HUL_NAME);
		wavModule.setBase(this.jhoveBase);
		@SuppressWarnings("unchecked")
		List<String> features = wavModule.getFeatures();
		for (String feature : features) {
			System.out.println("feature:" + feature);
		}
		RepInfo repInfo = new RepInfo("");
		try {
			this.jhoveBase.processFile(this.app, wavModule, true, jp2kToCheck, repInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("mime:" + repInfo.getMimeType());
		System.out.println("URI:" + repInfo.getUri());
		System.out.println("valid:" + repInfo.getValid());
		System.out.println("wellformed:" + repInfo.getWellFormed());
		
		@SuppressWarnings("unchecked")
		Map<String, Property> properties = repInfo.getProperty();
		this.processPropertyCollection(properties.values());
		return false;
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		JhoveJP2kCharacterisation jp2k = new JhoveJP2kCharacterisation();
		for (String zipPath : args) {
			ContainerUnpacker unpacker = ContainerUnpacker.getInstance(zipPath);
			for (String entryName : unpacker.getEntryNames()) {
				jp2k.isJp2k(unpacker.getUnzippedFile(entryName));
			}
		}
	}
}
