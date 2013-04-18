package LinearArcPlotEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	
	public AlignmentView(SidePanelModel arg0, int alignmentLength) {
		super(arg0);
		this.alignmentLength = alignmentLength;
		oldValue = 1;
		fillPanel();
	}
	
	private void fillPanel(){
		if(panel == null){
			JLabel showAnnotations = new JLabel("Show annotations");
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
			panel = new StandardLayout();
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
