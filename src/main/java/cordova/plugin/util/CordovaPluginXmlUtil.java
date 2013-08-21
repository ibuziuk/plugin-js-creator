package cordova.plugin.util;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cordova.plugin.exception.PluginJsException;
import cordova.plugin.model.Plugin;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaPluginXmlUtil {
	private static final String ID = "id";
	private static final String SRC_ATTRIBUTE = "src";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String PLUGINS_DIR = "plugins";

	public static List<Plugin> getPluginsFromDocument(Document doc) throws PluginJsException {
		List<Plugin> plugins = new ArrayList<Plugin>();
		String pluginXmlId = getId(doc); // plugin.xml id, not plugin id
		
		try {
			XPathFactory xPathFactory = XPathFactory.newInstance();
			NodeList jsPlatformModuleElements = (NodeList) xPathFactory.newXPath().compile("//plugin/platform[@name=\"android\"]/js-module")
					.evaluate(doc, XPathConstants.NODESET);
			addModulesToPlugins(plugins, jsPlatformModuleElements, pluginXmlId);
			
			NodeList jsModuleElements = (NodeList) xPathFactory.newXPath().compile("//plugin/js-module")
					.evaluate(doc, XPathConstants.NODESET);
			addModulesToPlugins(plugins, jsModuleElements, pluginXmlId);
		} catch (XPathExpressionException e) {
			throw new PluginJsException(e);
		}
		
		return plugins;
	}

	private static void addModulesToPlugins(List<Plugin> plugins, NodeList jsModuleElements, String pluginXmlId) 
			throws PluginJsException {
		for (int i = 0; i < jsModuleElements.getLength(); i++) {
			Plugin plugin = createPlugin((Element) jsModuleElements.item(i), pluginXmlId);
			if (plugin != null) {
				plugins.add(plugin);
			}				
		}
	}
	
	public static List<Node> getChildrenByTagName(Element element, String tagName) {
		List<Node> children = new ArrayList<Node>();
		NodeList childNodes = element.getChildNodes();
		int childNodesCount = childNodes.getLength();
		for (int i = 0; i < childNodesCount; i++) {
			children.add(childNodes.item(i));
		}
		return children;
	}

	public static List<Plugin> getPluginsfromFiles(List<File> pluginXmlFiles) throws PluginJsException {
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
		return allPlugins;
	}

	private static Plugin createPlugin(Element jsModuleElement, String pluginXmlId) throws PluginJsException {
		Plugin plugin = null;
		String moduleName = jsModuleElement.getAttribute(NAME_ATTRIBUTE);
		String src = jsModuleElement.getAttribute(SRC_ATTRIBUTE);

		if ((moduleName != null) && (src != null)) {
			XPathFactory xPathFactory = XPathFactory.newInstance();
			NodeList clobbersTargets;
			NodeList mergesTargets;
			try {
				clobbersTargets = (NodeList) xPathFactory.newXPath().compile("clobbers/@target")
						.evaluate(jsModuleElement, XPathConstants.NODESET);
				mergesTargets = (NodeList) xPathFactory.newXPath().compile("merges/@target")
						.evaluate(jsModuleElement, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				throw new PluginJsException(e);
			}

			String pluginId = pluginXmlId + "." + moduleName; // plugin id = pligin.xml.id + moduleName
			String file = PLUGINS_DIR + "/" + pluginXmlId + "/" + src;

			if (clobbersTargets.getLength() > 0 || mergesTargets.getLength() > 0) {
				List<String> clobbersTargetValues = nodeListToValues(clobbersTargets);
				List<String> mergesTargetValues = nodeListToValues(mergesTargets);
				plugin = new Plugin(file, pluginId, clobbersTargetValues, mergesTargetValues);
			}
		}
		return plugin;
	}

	private static List<String> nodeListToValues(NodeList nodeList) {
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			String value = nodeList.item(i).getNodeValue();
			if (value != null) {
				values.add(value);
			}
		}
		
		return values;
	}

	private static String getId(Document doc) {
		Element element = doc.getDocumentElement();
		return element.getAttribute(ID);
	}

}