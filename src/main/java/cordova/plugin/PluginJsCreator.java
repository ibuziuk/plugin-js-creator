package cordova.plugin;

import java.io.File;
import java.util.List;

import cordova.plugin.exception.PluginJsException;
import cordova.plugin.model.Plugin;
import cordova.plugin.util.CordovaFileUtil;
import cordova.plugin.util.CordovaPluginXmlUtil;

public class PluginJsCreator {
	private static final String PLUGINS_SOURCE_FOLDER = System.getProperty("user.dir") + "/plugins";

	public static void main(String[] args) throws PluginJsException {
		long start = System.currentTimeMillis();
		List<File> pluginXmlFiles = CordovaFileUtil.getPluginXmlFiles(new File(PLUGINS_SOURCE_FOLDER));
		List<Plugin> allPlugins = CordovaPluginXmlUtil.getPluginsfromFiles(pluginXmlFiles, CordovaPluginXmlUtil.PLATFORM_ANDROID);
		String content = CordovaFileUtil.generateContent(allPlugins);
		long stop = System.currentTimeMillis();
		System.out.println(content);
		System.out.println("------------------");
		System.out.println("Number of plugins: " + allPlugins.size());
		System.out.println("Time consumption: " + (stop - start));
	}
	
}