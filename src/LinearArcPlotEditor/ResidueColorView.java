package LinearArcPlotEditor;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.clcbio.api.free.editors.framework.sidepanel.SidePanelGroup;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelView;

public class ResidueColorView extends SidePanelView{
	private JPanel panel;
	
	public ResidueColorView(SidePanelModel model) {
		super(model);
		createUI();
	}
	
	public void fillPanel(){
		createUI();
	}

	private void createUI(){
		if(panel == null){
			panel = new JPanel();
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
			JPanel jj = new JPanel();
			JLabel da = new JLabel("David");
			DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("Child 1");
			root.add(child1);
			DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("Child 2");
			root.add(child2);
			JTree tree = new JTree(root);
			panel.add(tree);
			fillPanel();
		}
	}
	@Override
	public JComponent getComponent() {
		
		return panel;
	}

	@Override
	public void updateUI(SidePanelModel arg0) {
		// TODO Auto-generated method stub
		
	}

}
