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
	private static final String CLOBBERS = "clobbers";
	private static final String TARGET_ATTRIBUTE = "target";
	private static final String ANDROID = "android";

	public static List<Plugin> getPluginsFromDocument(Document doc) {
		List<Plugin> plugins = new ArrayList<Plugin>();
		String id = getId(doc); // plugin.xml id, not plugin id
		NodeList nodeList = doc.getElementsByTagName(JS_MODULE);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (isAndroidJsModule(node)) {
					Element element = (Element) node;
					String moduleName = element.getAttribute(NAME_ATTRIBUTE);
					String src = element.getAttribute(SRC_ATTRIBUTE);
	
					if ((moduleName != null) && (src != null)) {
						List<String> clobbers = new ArrayList<String>();
	
						NodeList clobbersList = element.getElementsByTagName(CLOBBERS);
						for (int j = 0; j < clobbersList.getLength(); j++) {
							Node clobberNode = clobbersList.item(j);
							if (clobberNode.getNodeType() == Node.ELEMENT_NODE) {
								Element cloberElement = (Element) clobberNode;
								String clober = cloberElement.getAttribute(TARGET_ATTRIBUTE);
								clobbers.add(clober);
							}
						}
	
						if (clobbers.size() > 0) {
							String pluginId = id + "." + moduleName; // plugin id = pligin.xml.id + moduleName
							String file = "plugins/" + id + "/" + src;
							plugins.add(new Plugin(file, pluginId, clobbers));
						}
					}
				}
			}
		}
		return plugins;
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