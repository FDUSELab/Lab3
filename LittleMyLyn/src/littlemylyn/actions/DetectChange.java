package littlemylyn.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;

import java.util.ArrayList;

import littlemylyn.Task;
import littlemylyn.views.SampleView;

public class DetectChange implements IResourceChangeListener,IResourceDeltaVisitor {
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	int i = 0;
	ArrayList<Task> taskList;
	SampleView sampleView;
	public DetectChange(ArrayList<Task> taskList, SampleView sampleView) {
		System.out.println("ss");
		workspace.addResourceChangeListener(this);
		this.taskList = taskList;
		this.sampleView = sampleView;
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if(delta != null) {
			switch (delta.getKind()) {
				case IResourceDelta.ADDED :
					//System.out.println("added");
					break;
				case IResourceDelta.REMOVED:
					System.out.println("removed");
					break;
				case IResourceDelta.CHANGED : {
					System.out.println("CHANGED");
					try {
						IResourceDelta[] children;
						do {
							children = delta.getAffectedChildren();
							delta = children[0];
						} while (delta.getAffectedChildren().length != 0);
						delta.accept(this);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				default :
					break;
			}
		}
	}
	
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource res = delta.getResource();
		new Thread(new Runnable() {
			public void run() {
				try { Thread.sleep(1000); } catch (Exception e) { }
			    	Display.getDefault().asyncExec(new Runnable() {
			        	public void run() {
			            	String dispTxt = "";
			            	switch (delta.getKind()) {
			       	   			case IResourceDelta.ADDED :
				       	   			dispTxt = res.getFullPath() + "";
				       				System.out.println(dispTxt + "added");
				       				if(dispTxt.endsWith("java")) {
				       					String[] dimensions = dispTxt.split("/");
				       					String file = dimensions[dimensions.length - 1];
				       					String project = dimensions[1];
				       					Task activatedTask = getActivatedTask(taskList);
				       					//System.out.println(activatedTask);
				       					if(activatedTask != null&&activatedTask.getProject().equals(project)) {
				       						addRelatedClass(file, activatedTask);
				       					}
				       				}
				       				break;
			       	   			case IResourceDelta.REMOVED:
				       	   			dispTxt = res.getFullPath() + "";
				       	   			System.out.println(dispTxt + "removed");
				       				if(dispTxt.endsWith("java")) {
				       					String[] dimensions = dispTxt.split("/");
				       					String project = dimensions[1];
				       					String file = dimensions[dimensions.length - 1];
				       					removeRelatedClass(file);
				       				}
				       				else if(dispTxt.endsWith("classpath")) {
				       					String[] dimensions = dispTxt.split("/");
				       					String project = dimensions[1];
				       					removeAllRelatedClass(project);
				       				}
				       	   			break;
				       	   		case IResourceDelta.CHANGED :
				       				dispTxt = res.getFullPath() + "";
				       				System.out.println(dispTxt + "changed");
				       				if(dispTxt.endsWith("java")) {
				       					String[] dimensions = dispTxt.split("/");
				       					String project = dimensions[1];
				       					String file = dimensions[dimensions.length - 1];
				       					Task activatedTask = getActivatedTask(taskList);
				       					System.out.println("ss"+activatedTask.getProject());
				       					System.out.println("ss2"+project);
				       					if(activatedTask != null&&activatedTask.getProject().equals(project)) {
				       						addRelatedClass(file, activatedTask);
				       					}
				       					
				       				}
				       	   			break;
				         		}
			            	}
			            });
			   		}
			   	}).start();
  		return true;
	}

	
	public Task getActivatedTask(ArrayList<Task> taskList) {
		for(int i = 0; i < taskList.size(); i++) {
			//System.out.println(taskList.get(i).getStatus());
			if(taskList.get(i).getStatus().equals(Task.STATUS_ACTIVATED)) {
				return taskList.get(i);
			}
		}
		return null;
	}
	
	public void addRelatedClass(String file, Task task) {
		boolean match = task.getRelatedClass().stream().anyMatch(item -> item.equals(file));
		if(!match) task.getRelatedClass().add(file);
		sampleView.refresh();
		System.out.println(task.getRelatedClass().get(task.getRelatedClass().size()-1));
	}
	
	public void removeRelatedClass(String file) {
		for(int i = 0; i < taskList.size(); i++) {
			boolean match = taskList.get(i).getRelatedClass().stream().anyMatch(item -> item.equals(file));
			if(match) taskList.get(i).getRelatedClass().remove(file);
		}
		sampleView.refresh();
	}
	
	public void removeAllRelatedClass(String project) {
		for(int i = 0; i < taskList.size(); i++) {
			if(taskList.get(i).getProject().equals(project)) {
				taskList.get(i).getRelatedClass().clear();
			}
		}
		sampleView.refresh();
	}
}
