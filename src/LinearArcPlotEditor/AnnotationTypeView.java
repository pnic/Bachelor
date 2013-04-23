package LinearArcPlotEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.sidepanel.FeatureColorMap;
import com.clcbio.api.free.gui.components.ColorPickerLabel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelView;
import com.clcbio.api.free.gui.StandardLayout;

public class AnnotationTypeView extends SidePanelView{
	private StandardLayout panel;
	
	private List<String> types;
	
	private List<JCheckBox> checkTypes;
	private Map<String, ColorPickerLabel> labels;
	
	public AnnotationTypeView(SidePanelModel arg0) {
		super(arg0);
		checkTypes = new ArrayList<JCheckBox>();
		labels = new HashMap<String, ColorPickerLabel>();
		createUI();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private void fillUI(){
		createUI();
		for(JCheckBox jc : checkTypes){
			panel.addComps(labels.get(jc.getText()),jc);
			
		}
		/*
		for(ColorPickerLabel l : labels){
			panel.addComp(l);
			panel.add(l.getComponent(0));
		}*/
	}
	
	private void createUI(){
		if(panel == null){
			for(final String s : ((AnnotationTypeModel)getModel()).getBooltypes().keySet()){
				JCheckBox cur = new JCheckBox(s);
				cur.setSelected(true);
				cur.addItemListener(new ItemListener(){
					@Override
					public void itemStateChanged(ItemEvent arg0) {
						((AnnotationTypeModel)getModel()).changeType(s);
					}
				});
				checkTypes.add(cur);
				
				ColorPickerLabel curLab = new ColorPickerLabel(FeatureColorMap.getColor(s), s);
				
				curLab.addActionListener(new ActionListener(){
						
					@Override
					public void actionPerformed(ActionEvent arg0) {
						((AnnotationTypeModel)getModel()).labelChanged(s, labels.get(s).getColor());
						
					}
					
				});
				labels.put(cur.getText(), curLab);
			}			
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

