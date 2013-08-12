package cordova.plugin.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cordova.plugin.model.Plugin;

public class DomUtil {
	private static final String ID = "id";
	private static final String JS_MODULE = "js-module";
	private static final String SRC_ATTRIBUTE = "src";
	private static final String NAME_ATTRIBUTE = "name";	
	private static final String MERGES = "merges";
	private static final String CLOBBERS = "clobbers";
	private static final String TARGET_ATTRIBUTE = "target";
	private static final String ANDROID = "android";

	public static List<Plugin> getPluginsFromDocument(Document doc) {
		List<Plugin> plugins = new ArrayList<Plugin>();
		String pluginXmlId = getId(doc); // plugin.xml id, not plugin id
		NodeList nodeList = doc.getElementsByTagName(JS_MODULE);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (isAndroidJsModule(node)) {
					Element jsModuleElement = (Element) node;
					Plugin plugin = createPlugin(jsModuleElement, pluginXmlId);
					if (plugin != null) {
						plugins.add(plugin);
					}
				}
			}
		}
		return plugins;
	}
	
	private static Plugin createPlugin(Element jsModuleElement, String pluginXmlId) {
		Plugin plugin = null;
		String moduleName = jsModuleElement.getAttribute(NAME_ATTRIBUTE);
		String src = jsModuleElement.getAttribute(SRC_ATTRIBUTE);

		if ((moduleName != null) && (src != null)) {
			NodeList clobberList = jsModuleElement.getElementsByTagName(CLOBBERS);
			NodeList mergeList = jsModuleElement.getElementsByTagName(MERGES);
			
			List<String> clobbers = getMappers(clobberList);
			List<String> merges = getMappers(mergeList);
			
			String pluginId = pluginXmlId + "." + moduleName; // plugin id = pligin.xml.id + moduleName
			String file = "plugins/" + pluginXmlId + "/" + src;
			
			if (clobbers.size() > 0 || merges.size() > 0) {
				plugin = new Plugin(file, pluginId, clobbers, merges);
			}
		}
		return plugin;
	}
	
	
	private static List<String> getMappers(NodeList clobberList) {
		List<String> mappers = new ArrayList<String>();
		for (int j = 0; j < clobberList.getLength(); j++) {
			Node mapperNode = clobberList.item(j);
			if (mapperNode.getNodeType() == Node.ELEMENT_NODE) {
				Element cloberElement = (Element) mapperNode;
				String clober = cloberElement.getAttribute(TARGET_ATTRIBUTE);
				mappers.add(clober);
			}
		}
		return mappers;
	}

	private static boolean isAndroidJsModule(Node node) {
		Node parentNode = node.getParentNode();
		if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
			Element parentElement = (Element) parentNode;
			String platformName = parentElement.getAttribute(NAME_ATTRIBUTE);
			if (platformName == null || platformName.trim().equals("") || platformName.equals(ANDROID)) { // HACK - need to do this better
				return true;
			}
		}
		return false;
	}

	private static String getId(Document doc) {
		Element element = doc.getDocumentElement();
		return element.getAttribute(ID);
	}
}