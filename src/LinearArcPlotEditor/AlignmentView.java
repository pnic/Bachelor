package LinearArcPlotEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelView;
import com.clcbio.api.free.gui.StandardLayout;

public class AlignmentView extends SidePanelView {

	private static final long serialVersionUID = 1L;
	private StandardLayout panel;
	private int alignmentLength;
	private int oldValue;
	
	public AlignmentView(SidePanelModel arg0, String[] names) {
		super(arg0);
		this.alignmentLength = names.length;
		oldValue = 1;
		fillPanel();
	}
	
	private void fillPanel(){
		if(panel == null){
			JLabel showAnnotations = new JLabel("Show alignment nr");
			String[] numbers = new String[alignmentLength];
			for(int i=0; i<numbers.length; i++){
				numbers[i] = ""+(i+1);
			}
			
			
			final JComboBox alignmentNumber = new JComboBox(numbers);
			
			alignmentNumber.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int newValue = Integer.parseInt((String)alignmentNumber.getSelectedItem());
					
					if(!(newValue==oldValue)){
						System.out.println("De er ikke ens, new: " + newValue + " old: " + oldValue);
						((AlignmentModel)getModel()).setSequenceNumber(Integer.parseInt((String)alignmentNumber.getSelectedItem()));
						oldValue = newValue;
					}
				}
				
			});
			
			final JCheckBox showAlignments = new JCheckBox("Show alignments");
			showAlignments.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					((AlignmentModel)getModel()).setShowAlignments(showAlignments.isSelected());
				}
			});
			
			panel = new StandardLayout();
			panel.addComp(showAlignments);
			panel.addComps(showAnnotations, alignmentNumber);
		}
	}


	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public void updateUI(SidePanelModel arg0) {
		fillPanel();
	}

}
