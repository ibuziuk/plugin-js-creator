/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package cordova.plugin;

import java.io.File;
import java.util.List;

import cordova.plugin.exception.PluginJsException;
import cordova.plugin.model.Plugin;
import cordova.plugin.util.CordovaFileUtil;
import cordova.plugin.util.CordovaPluginXmlUtil;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class PluginJsCreator {
	private static final String PLUGINS_SOURCE_FOLDER = System.getProperty("user.dir") + "/plugins";

	public static void main(String[] args) throws PluginJsException {
		long start = System.currentTimeMillis();
		List<File> pluginXmlFiles = CordovaFileUtil.getPluginXmlFiles(new File(PLUGINS_SOURCE_FOLDER));
		List<Plugin> allPlugins = CordovaPluginXmlUtil.getPluginsfromFiles(pluginXmlFiles, CordovaPluginXmlUtil.PLATFORM_ANDROID);
		String content = CordovaFileUtil.generateCordovaPluginsJsContent(allPlugins);
		long stop = System.currentTimeMillis();
		System.out.println(content);
		System.out.println("------------------");
		System.out.println("Number of plugins: " + allPlugins.size());
		System.out.println("Time consumption: " + (stop - start));
	}
	
}