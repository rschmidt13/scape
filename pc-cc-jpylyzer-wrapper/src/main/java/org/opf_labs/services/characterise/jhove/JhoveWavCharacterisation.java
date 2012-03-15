package org.opf_labs.services.characterise.jhove;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import edu.harvard.hul.ois.jhove.JhoveException;
import edu.harvard.hul.ois.jhove.Module;
import edu.harvard.hul.ois.jhove.Property;
import edu.harvard.hul.ois.jhove.RepInfo;

/**
 * @author <a href="mailto:carl.wilson.bl@gmail.com">Carl Wilson</a>
 *	   <a href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl@SourceForge</a>
 *	   <a href="https://github.com/carlwilson-bl">carlwilson-bl@github</a>
 *
 */
public class JhoveWavCharacterisation extends JhoveCharacterisationBase {
	protected static final String HUL_NAME = "wave-hul";

	/**
	 * @throws Exception
	 */
	public JhoveWavCharacterisation() throws Exception {
		super();
		if (!this.hasWavModule()) throw new JhoveException("The WAVE-HUL Jhove module could not be found");
	}
	
	private boolean hasWavModule() {
		return this.getModuleMap().containsKey(HUL_NAME);
	}
	
	/**
	 * @param stream
	 * @return
	 * @throws Exception
	 */
	public boolean isWav(InputStream stream) throws Exception {
		Module wavModule = this.getModuleMap().get(HUL_NAME);
		@SuppressWarnings("unchecked")
		List<String> features = wavModule.getFeatures();
		for (String feature : features) {
			System.out.println("feature:" + feature);
		}
		RepInfo repInfo = new RepInfo("");
		
		int reportIndex = 0;
		while (reportIndex == 0) {
			wavModule.parse(stream, repInfo, reportIndex);
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
	
}
