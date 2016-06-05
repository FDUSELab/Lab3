package littlemylyn;

import java.util.ArrayList;

public class TreeNode {
	Object element;
	Object parent;

	public TreeNode(Object element, Object parent) {
		this.element = element;
		this.parent = parent;
	}

	@Override
	public String toString() {
		if (element instanceof ArrayList<?>
				&& (((ArrayList<?>) element).size() == 0 || ((ArrayList<?>) element)
						.get(0) instanceof String)) {
			return "related classes(" + ((ArrayList<?>) element).size() + ")";
		} else {
			return element.toString();
		}
	}
	
	public Object getElement() {
		return element;
	}
	
	public Object getParent() {
		return parent;
	}
	
	public Object[] getChildren() {
		Object[] children = null;
		if (element instanceof Task) {
			children = new Object[3];
			children[0] = new TreeNode(((Task) element).getStatus(),this);
			children[1] = new TreeNode(((Task) element).getType(),this);
			children[2] = new TreeNode(((Task) element).getRelatedClass(),this);
			return children;
		} else if (element instanceof ArrayList<?>) {
			children = new Object[((ArrayList<?>) element).size()];
			for (int i = 0; i < children.length; i++) {
				children[i] = new TreeNode(((ArrayList<?>) element).get(i),this);
			}
			return children;
		}
		return children;
	}
	
	public boolean hasChildren() {
		if (element instanceof Task) {
			return true;
		} else if (element instanceof ArrayList<?>) {
			if (((ArrayList<?>) element).size() > 0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
}
