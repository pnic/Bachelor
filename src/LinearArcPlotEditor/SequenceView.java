package LinearArcPlotEditor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelView;
import com.clcbio.api.free.gui.StandardLayout;

public class SequenceView extends SidePanelView{
	private StandardLayout panel;
	private JLabel sequenceTitle;
	private JCheckBox numbersCheckBox;
	private JCheckBox infoCheckBox;
	private JButton showSub;
	private StandardLayout indexNumberPanel;
	
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
		panel.addComp(indexNumberPanel);
		panel.addComp(infoCheckBox);
		//panel.addComp(showSub);
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
			FlowLayout layout = new FlowLayout();
			indexNumberPanel = new StandardLayout();
			indexNumberPanel.setLayout(layout);
			
			
			JLabel indexText = new JLabel("Relative to");
			final JTextField indexField = new JTextField();
			indexField.setPreferredSize(new Dimension(100, 24));
			
			indexField.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					((SequenceModel)getModel()).setNumbers(indexField.getText());
				}
			});
			indexField.setText("0");
			
			
			indexNumberPanel.add(indexText);
			indexNumberPanel.add(indexField);
			
			infoCheckBox = new JCheckBox("Show infobox");
			infoCheckBox.setSelected(true);
			infoCheckBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					((SequenceModel)getModel()).showInfowBox(infoCheckBox.isSelected());
				}
				
			});
			
			showSub = new JButton("Subsequence");
			showSub.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					((SequenceModel)getModel()).showSubSequence();
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
