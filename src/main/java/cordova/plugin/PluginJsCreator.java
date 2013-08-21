package cordova.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import cordova.plugin.exception.PluginJsException;
import cordova.plugin.model.Plugin;
import cordova.plugin.util.CordovaPluginXmlUtil;
import cordova.plugin.util.FileUtil;

public class PluginJsCreator {
	private static final String PLUGINS_SOURCE_FOLDER = System.getProperty("user.dir") + "/plugins";

	public static void main(String[] args) throws PluginJsException {
		long start = System.currentTimeMillis();
		List<File> pluginXmlFiles = FileUtil.getPluginXmlFiles(new File(PLUGINS_SOURCE_FOLDER));
		List<Plugin> allPlugins = new ArrayList<Plugin>();
		for (File file : pluginXmlFiles) {
			try {
				DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				List<Plugin> pluginsFromDocument = CordovaPluginXmlUtil.getPluginsFromDocument(doc);
				allPlugins.addAll(pluginsFromDocument);
			} catch (ParserConfigurationException e) {
				throw new PluginJsException(e);
			} catch (IOException e) {
				throw new PluginJsException(e);
			} catch (SAXException e) {
				throw new PluginJsException(e);
			}
		}
		String content = FileUtil.generateContent(allPlugins);
		long stop = System.currentTimeMillis();
		System.out.println(content);
		System.out.println("------------------");
		System.out.println("Time consumption: " + (stop - start));
	}
	
}