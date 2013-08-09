package cordova.plugin.model;

import java.util.List;

public class Plugin {
	private String file;
	private String id;
	private List<String> clobbers;

	public Plugin(String file, String id, List<String> clobbers) {
		this.file = file;
		this.id = id;
		this.clobbers = clobbers;
	}

	public String getFile() {
		return file;
	}

	public String getId() {
		return id;
	}

	public List<String> getClobbers() {
		return clobbers;
	}

	@Override
	public String toString() {
		return file + ", " + id + ", " + clobbers.toString();
	}
}