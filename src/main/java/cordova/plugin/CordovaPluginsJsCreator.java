package cordova.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import cordova.plugin.model.Plugin;
import cordova.plugin.util.DomUtil;
import cordova.plugin.util.FileUtil;

public class CordovaPluginsJsCreator {
	private static final String PLUGINS_SOURCE_FOLDER = System.getProperty("user.dir") + "/plugins";

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		List<File> pluginXmlFiles = FileUtil.getPluginXmlFiles(new File(PLUGINS_SOURCE_FOLDER));
		List<Plugin> allPlugins = new ArrayList<Plugin>();
		for (File file : pluginXmlFiles) {
			try {
				DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				List<Plugin> pluginsFromDocument = DomUtil.getPluginsFromDocument(doc);
				allPlugins.addAll(pluginsFromDocument);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String content = FileUtil.generateContent(allPlugins);
		long stop = System.currentTimeMillis();
		System.out.println(content);
		System.out.println("------------------");
		System.out.println("Time consumption: " + (stop - start));
	}
}