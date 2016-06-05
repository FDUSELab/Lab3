package littlemylyn.views;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import littlemylyn.Task;
import littlemylyn.TreeNode;
import littlemylyn.actions.DetectChange;
import littlemylyn.views.SampleView.ViewContentProvider;
import littlemylyn.views.SampleView.ViewLabelProvider;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
//import org.eclipse.jface.dialogs.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

//import uimonopoly.Map;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "littlemylyn.views.SampleView";

	private TreeViewer viewer;
	private Action newTask;
	private Action toFinished;
	private Action toActivated;
	private ArrayList<Task> taskList;
	private ArrayList<TreeNode> nodeList;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	/**
	 * The constructor.
	 */
	@SuppressWarnings("unchecked")
	public SampleView() {
		try {
			File f = new File("list.tsk");
			if (f.exists()) {
				ObjectInputStream input = new ObjectInputStream(
						new BufferedInputStream(new FileInputStream("list.tsk")));
				taskList = (ArrayList<Task>) (input.readObject());
				input.close();
			} else {
				taskList = new ArrayList<Task>();
			}
		} catch (IOException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		nodeList = new ArrayList<TreeNode>();
		for (int i = 0; i < taskList.size(); i++) {
			nodeList.add(new TreeNode(taskList.get(i), null));
		}
		DetectChange dc = new DetectChange(taskList, this);
	}

	class ViewContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
		}

		@Override
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((TreeNode) parentElement).getChildren();
		}

		@Override
		public Object[] getElements(Object arg0) {
			return (Object[]) arg0;
		}

		@Override
		public Object getParent(Object arg0) {
			return ((TreeNode) arg0).getParent();
		}

		@Override
		public boolean hasChildren(Object parentElement) {
			return ((TreeNode) parentElement).hasChildren();
		}
	}

	class ViewLabelProvider extends LabelProvider implements ILabelProvider {
		// private ArrayList listeners= new ArrayList();
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}

		public String getText(Object element) {
			if (element instanceof ArrayList<?>
					&& (((ArrayList<?>) element).size() == 0 || ((ArrayList<?>) element)
							.get(0) instanceof String)) {
				return "related classes(" + ((ArrayList<?>) element).size()
						+ ")";
			} else {
				return super.getText(element);
			}
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		ViewContentProvider viewContentProvider = new ViewContentProvider();
		viewer.setContentProvider(viewContentProvider);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(nodeList.toArray());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "test.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void makeActions() {
		newTask = new Action() {
			public void run() {
				class NewTask {
					private Label nameLabel;
					private Text nameInput;
					private Group typeGroup;
					private Group statusGroup;

					private Button typeButton1;
					private Button typeButton2;
					private Button typeButton3;

					private Button statusButton1;
					private Button statusButton2;
					private Button statusButton3;

					private Combo projectCombo;
					private Button finishButton;
					private Button cancelButton;

					public void setNewTask() {

						Display display = Display.getDefault();
						Shell newTask = new Shell(display);
						
						Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
						int x = (screenSize.width - 450) / 2;
						int y = (screenSize.height - 250) / 2;
						newTask.setLocation(x, y);
						newTask.setText("New Task");
						newTask.setSize(450, 250);

						Color bgColor = new Color(display, 248,248,255);
						Color bgColor2 = new Color(display, 230,230,250);
						Color fontColor = new Color(display, 0, 0, 0);
						Font font1 = new Font(display, "微软雅黑", 10, SWT.NONE);
						newTask.setBackground(bgColor);

						newTask.open();

						nameLabel = new Label(newTask, SWT.NONE);
						nameLabel.setText("Task Name");
						nameLabel.setBounds(24, 18, 60, 24);
						nameLabel.setBackground(bgColor);
						nameLabel.setForeground(fontColor);
						nameLabel.setFont(font1);

						nameInput = new Text(newTask, SWT.BORDER);
						nameInput.setBounds(110, 18, 300, 22);

						typeGroup = new Group(newTask, SWT.NONE);
						typeGroup.setText("Task Type");
						typeGroup.setBounds(35, 60, 170, 120);
						typeGroup.setBackground(bgColor2);
						typeGroup.setFont(font1);

						typeButton1 = new Button(typeGroup, SWT.RADIO);
						typeButton1.setText("debug");
						typeButton1.setBounds(25, 10, 80, 20);
						typeButton1.setSelection(true);
					
						typeButton2 = new Button(typeGroup, SWT.RADIO);
						typeButton2.setText("new feature");
						typeButton2.setBounds(25, 40, 100, 20);
						
						typeButton3 = new Button(typeGroup, SWT.RADIO);
						typeButton3.setBounds(25, 70, 80, 20);
						typeButton3.setText("refactor");
						
						statusGroup = new Group(newTask, SWT.NONE);
						statusGroup.setText("Task Status");
						statusGroup.setBounds(245, 60, 170, 120);
						statusGroup.setBackground(bgColor2);
						statusGroup.setFont(font1);

						statusButton1 = new Button(statusGroup, SWT.RADIO);
						statusButton1.setText("New");
						statusButton1.setBounds(25, 10, 80, 20);
						statusButton1.setSelection(true);
						
						statusButton2 = new Button(statusGroup, SWT.RADIO);
						statusButton2.setText("Activated");
						statusButton2.setBounds(25, 40, 100, 20);
						
						statusButton3 = new Button(statusGroup, SWT.RADIO);
						statusButton3.setBounds(25, 70, 80, 20);
						statusButton3.setText("Finish");
						
						projectCombo = new Combo(newTask, SWT.NONE);
						IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
								.getProjects();
						String[] projectsName = new String[projects.length];
						for(int i = 0; i < projects.length; i++){
							projectsName[i] = projects[i].getName();
						}
						projectCombo.setItems(projectsName);
						projectCombo.select(0);
						projectCombo.setBounds(30, 190, 80, 20);
 						
						finishButton = new Button(newTask, SWT.None);
						finishButton.setBounds(150, 190, 70, 30);
						finishButton.setText("Finish");
						finishButton.setBackground(bgColor);

						cancelButton = new Button(newTask, SWT.None);
						cancelButton.setBounds(230, 190, 70, 30);
						cancelButton.setText("Cancel");
						cancelButton.setBackground(bgColor);

						/*
						 * statusButton2.addSelectionListener(new
						 * SelectionAdapter() { public void widgetSelected(final
						 * SelectionEvent e) { MessageBox msgBox = new
						 * MessageBox(newTask, SWT.ICON_INFORMATION); //
						 * TODO:whether there is already an Activated Task } });
						 */
						
						statusButton2.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(final SelectionEvent e) {
								for (Task task : taskList) {
									if (statusButton2.getSelection() && task.getStatus().equals(Task.STATUS_ACTIVATED)) {
										MessageBox msgBox = new MessageBox(newTask, SWT.ICON_INFORMATION);
										msgBox.setMessage("There is already an Activated Task!"+projectCombo.getText());
										msgBox.open();
										statusButton2.setSelection(false);
										statusButton1.setSelection(true);
										break;
									}
								}
							}
						});

						finishButton
								.addSelectionListener(new SelectionAdapter() {
									public void widgetSelected(
											final SelectionEvent e) {
										// TODO:create a new task
										String newTaskName = nameInput
												.getText();
										if (newTaskName.equals("")) {
											MessageBox msgBox = new MessageBox(
													newTask,
													SWT.ICON_INFORMATION);
											msgBox.setMessage("请输入任务名称");
											msgBox.open();
											return;
										}
										String newTaskType = "";
										if (typeButton1.getSelection()) {
											newTaskType = "debug";
										} else if (typeButton2.getSelection()) {
											newTaskType = "new feature";
										} else if (typeButton3.getSelection()) {
											newTaskType = "refactor";
										} else {
											MessageBox msgBox = new MessageBox(
													newTask,
													SWT.ICON_INFORMATION);
											msgBox.setMessage("请选择类型");
											msgBox.open();
											return;
										}

										String newTaskStatus = "";
										if (statusButton1.getSelection()) {
											newTaskStatus = "New";
										} else if (statusButton2.getSelection()) {
											newTaskStatus = "Activated";
										} else if (statusButton3.getSelection()) {
											newTaskStatus = "Finish";
										} else {
											MessageBox msgBox = new MessageBox(
													newTask,
													SWT.ICON_INFORMATION);
											msgBox.setMessage("请选择状态");
											msgBox.open();
											return;
										}
										
										String projectName = projectCombo.getText();
										Task t = new Task(newTaskName,
												newTaskType, newTaskStatus,projectName);
										taskList.add(t);
										nodeList.add(new TreeNode(t, null));
										refresh();
										newTask.setVisible(false);
									}
								});

						cancelButton
								.addSelectionListener(new SelectionAdapter() {
									public void widgetSelected(
											final SelectionEvent e) {
										newTask.setVisible(false);
									}
								});

						newTask.layout();
					}
				}
				NewTask t = new NewTask();
				t.setNewTask();
			}
		};
		newTask.setText("新建");
		newTask.setToolTipText("新建任务");

		toActivated = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				TreeNode element = (TreeNode) selection.getFirstElement();
				Display display = Display.getDefault();
				Shell warningShell = new Shell(display);
				boolean b = true;
				for (Task task : taskList) {
					if (task.getStatus().equals(Task.STATUS_ACTIVATED)
							&& ((Task) ((TreeNode) element.getParent()).getElement()) != task) {
						MessageBox msgBox = new MessageBox(warningShell, SWT.ICON_INFORMATION);
						msgBox.setMessage("There is already an Activated Task!");
						msgBox.open();
						b = false;
						break;
					}
				}
				if (b) {
					((Task) ((TreeNode) element.getParent()).getElement()).setStatus(Task.STATUS_ACTIVATED);
				}
				refresh();
			}
		};
		toActivated.setText("Activated");
		toActivated.setToolTipText("change to Activated");

		toFinished = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) viewer
						.getSelection();
				TreeNode element = (TreeNode) selection.getFirstElement();
				((Task) ((TreeNode) element.getParent()).getElement())
						.setStatus(Task.STATUS_FINISHED);
				refresh();
			}
		};
		toFinished.setText("Finished");
		toFinished.setToolTipText("change to Finished");
	}

	private void hookContextMenu() {
		MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				ISelection selection = viewer.getSelection();
				if (!selection.isEmpty()) {
					Object object = ((IStructuredSelection) selection)
							.getFirstElement();
					TreeNode element = (TreeNode) object;
					if (element.getElement().toString().equals(Task.STATUS_NEW)) {
						manager.add(toActivated);
						manager.add(toFinished);
					} else if (element.getElement().toString()
							.equals(Task.STATUS_ACTIVATED)) {
						manager.add(toFinished);
					} else if (element.getElement().toString()
							.equals(Task.STATUS_FINISHED)) {
						manager.add(toActivated);
					}
				}
			}
		});
		viewer.getControl().setMenu(mgr.createContextMenu(viewer.getControl()));
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				ISelection selection = viewer.getSelection();
				Object object = ((IStructuredSelection) selection)
						.getFirstElement();
				TreeNode element = (TreeNode) object;
				if (element.getParent() != null
						&& ((TreeNode) element.getParent()).getElement() instanceof ArrayList<?>) {
					open((String)element.getElement(),((Task)((TreeNode)((TreeNode)element.getParent()).getParent()).getElement()).getProject());
				} else if (element.hasChildren()) {
					if (viewer.getExpandedState(element))
						viewer.collapseToLevel(element, 1);
					else
						viewer.expandToLevel(element, 1);
				}
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(newTask);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void refresh() {
		Object[] expanded = viewer.getExpandedElements();
		viewer.getControl().setRedraw(false);
		viewer.setInput(nodeList.toArray());
		viewer.setExpandedElements(expanded);
		viewer.getControl().setRedraw(true);
	}

	public void open(String fName, String pName) {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(pName);
			if (!project.exists()) {
				MessageBox msgBox = new MessageBox(new Shell(Display.getDefault())
					,SWT.ICON_INFORMATION);
				msgBox.setMessage("该项目不存在");
				msgBox.open();
				return;
			}
			IFile java_file = project.getFile(new Path("/src/" + fName));
			if (!java_file.exists()) {
				MessageBox msgBox = new MessageBox(new Shell(Display.getDefault())
					,SWT.ICON_INFORMATION);
				msgBox.setMessage("该类不存在");
				msgBox.open();
				return;
			}
			IDE.openEditor(page, java_file);
		} catch (CoreException e) {
			System.out.println(e.getStackTrace());
		}
	}
	
	@Override
	public void dispose(){
		try{
			ObjectOutputStream output = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream("list.tsk")));
			output.writeObject(taskList);
			output.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		super.dispose();
	}
}