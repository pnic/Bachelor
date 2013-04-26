package LinearArcPlotEditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

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
	private HashMap<String, Integer> NamesDictionary;
	private String[] names;
	
	public AlignmentView(SidePanelModel arg0, String[] names) {
		super(arg0);
		this.alignmentLength = names.length;
		this.names = names;
		NamesDictionary = new HashMap<String, Integer>();
		
		for(int i=0; i<names.length; i++){
			System.out.println("names[" + i + "] " + names[i]);
			NamesDictionary.put(names[i], i+1);
		}
		
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
			
			final JComboBox alignmentNumber = new JComboBox(names);
			alignmentNumber.setPreferredSize(new Dimension(150, 25));
			
			alignmentNumber.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String s = (String)alignmentNumber.getSelectedItem();
					int newValue = NamesDictionary.get(s);

					if(!(newValue==oldValue)){
						((AlignmentModel)getModel()).setSequenceNumber(newValue);
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
