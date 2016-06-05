package littlemylyn;

import java.util.ArrayList;

public class Task implements java.io.Serializable {
	public static final String TYPE_DEBUG = "debug";
	public static final String TYPE_NEW_FEATURE = "new feature";
	public static final String TYPE_REFACTOR = "refactor";
	
	public static final String STATUS_NEW = "New";
	public static final String STATUS_ACTIVATED = "Activated";
	public static final String STATUS_FINISHED = "Finished";
	
	private ArrayList<String> related = new ArrayList<String>();
	private String name;
	private String type;
	private String status;
	private String project;
	
	public Task(String name, String type, String status, String project) {
		this.name = name;
		this.type = type;
		this.status = status;
		this.project = project;
	}
	
	public ArrayList<String> getRelatedClass() {
		return related;
	}
	public void addClass(String name) {
		related.add(name);
	}
	
	public String getType() {
		return type;
	}
	public void setType(String i) {
		type = i;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String i) {
		status = i;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String s) {
		name = s;
	}
	
	public String getProject() {
		return project;
	}
	public void setProject(String s) {
		project = s;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
