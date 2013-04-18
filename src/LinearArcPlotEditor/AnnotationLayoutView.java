package LinearArcPlotEditor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelView;
import com.clcbio.api.free.gui.StandardLayout;

public class AnnotationLayoutView extends SidePanelView{
	private StandardLayout panel;
	
	private JCheckBox showAnnotationsCheckBox;
	private JCheckBox showArrowsCheckBox;
	private JCheckBox showGradientsCheckBox;
	
	private JRadioButton showAsArrows;
	private JRadioButton showAsLines;
	private ButtonGroup menu;
	
	public AnnotationLayoutView(SidePanelModel arg0) {
		super(arg0);
		createUI();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private void fillUI(){
		createUI();
		panel.addComp(showAnnotationsCheckBox);
		//panel.addComp(showArrowsCheckBox);
		//panel.addComp(showGradientsCheckBox);
		panel.addComp(showAsArrows);
		panel.addComp(showAsLines);
	}
	
	private void createUI(){
		if(panel == null){
			showAnnotationsCheckBox = new JCheckBox("Show annotations");
			showAnnotationsCheckBox.setSelected(true);
			showAnnotationsCheckBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					((AnnotationLayoutModel)getModel()).showAnnotations(showAnnotationsCheckBox.isSelected());
				}

			});
			
			showArrowsCheckBox = new JCheckBox("Show Arrows");
			showArrowsCheckBox.setSelected(true);
			showArrowsCheckBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					((AnnotationLayoutModel)getModel()).showArrows(showArrowsCheckBox.isSelected());
				}				
			});
			
			
			
			showGradientsCheckBox = new JCheckBox("Show gradients");
			showGradientsCheckBox.setSelected(true);
			showGradientsCheckBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					((AnnotationLayoutModel)getModel()).showGradients(showGradientsCheckBox.isSelected());
				}
			});
			menu = new ButtonGroup();
			
			showAsArrows = new JRadioButton("Show as arrows");
			showAsArrows.setSelected(true);
			showAsArrows.addItemListener(new ItemListener(){

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					((AnnotationLayoutModel)getModel()).setChosenView("arrows");					
				}				
			});
			
			
			showAsLines = new JRadioButton("Show as Lines");
			showAsLines.setSelected(false);
			showAsLines.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					((AnnotationLayoutModel)getModel()).setChosenView("lines");
				}
			});
						
			menu = new ButtonGroup();
			
			menu.add(showAsArrows);
			menu.add(showAsLines);
			
			
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
