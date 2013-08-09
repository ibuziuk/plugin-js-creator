package cordova.plugin.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cordova.plugin.model.Plugin;

public class FileUtil {
	private static final String FILE = "\"file\": ";
	private static final String ID = "\"id\": ";
	private static final String CLOBBERS = "\"clobbers\": [\n";
	private static final String PLUGIN_XML = "plugin.xml";
	private static final String BEGINING = "cordova.define('cordova/plugin_list', function(require, exports, module) { \n module.exports = [\n";
	private static final String END = "]\n});";

	public static List<File> getPluginXmlFiles(File pluginsDir) {
		List<File> pluginXmlFiles = new ArrayList<File>();
		File[] pluginDirs = pluginsDir.listFiles();
		for (File pluginDir : pluginDirs) {
			if (pluginDir.isDirectory()) {
				File pluginXmlFile = new File(pluginDir, PLUGIN_XML);
				if (pluginDir.exists()) {
					pluginXmlFiles.add(pluginXmlFile);
				}
			}
		}

		return pluginXmlFiles;
	}

	public static String generateContent(List<Plugin> plugins) {
		String pluginContent = "";
		Iterator<Plugin> pluginIterator = plugins.iterator();
		while (pluginIterator.hasNext()) {
			Plugin plugin = pluginIterator.next();
			pluginContent += "\n\t{\n";
			pluginContent += "\t\t" + FILE + "\"" + plugin.getFile() + "\",\n";
			pluginContent += "\t\t" + ID + "\"" + plugin.getId() + "\",\n";
			pluginContent += "\t\t" + CLOBBERS;
			Iterator<String> clobberIterator = plugin.getClobbers().iterator();
			while (clobberIterator.hasNext()) {
				String clobber = clobberIterator.next();
				pluginContent += "\t\t\t\"" + clobber + "\"";
				if (clobberIterator.hasNext()) {
					pluginContent += ",\n";
				} else {
					pluginContent += "\n\t\t]";
				}
			}
			if (pluginIterator.hasNext()) {
				pluginContent += "\n\t},";
			} else {
				pluginContent += "\n\t}\n";
			}
		}
		return BEGINING + pluginContent + END;
	}

}