package LinearArcPlotEditor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelView;
import com.clcbio.api.free.gui.StandardLayout;

public class SequenceView extends SidePanelView{
	private StandardLayout panel;
	private JLabel sequenceTitle;
	private JCheckBox numbersCheckBox;
	private JCheckBox infoCheckBox;
	
	public SequenceView(SidePanelModel arg0) {
		super(arg0);
		createUI();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private void fillUI(){
		createUI();
		panel.addComp(numbersCheckBox);
		panel.addComp(infoCheckBox);
	}
	
	private void createUI(){
		if(panel == null){
			numbersCheckBox = new JCheckBox("Numbers on sequence");
			numbersCheckBox.setSelected(true);
			numbersCheckBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					((SequenceModel)getModel()).drawNumbers(numbersCheckBox.isSelected());
				}

			});
			
			infoCheckBox = new JCheckBox("Show infobox");
			infoCheckBox.setSelected(true);
			infoCheckBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					((SequenceModel)getModel()).showInfowBox(infoCheckBox.isSelected());
				}
				
			});
			
			panel = new StandardLayout();
			fillUI();
		}
	}
	
	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public void updateUI(SidePanelModel arg0) {
		createUI();
	}

}
