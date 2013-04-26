package LinearArcPlotEditor;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelView;
import com.clcbio.api.free.gui.StandardLayout;

public class TextView extends SidePanelView{

	private StandardLayout panel = null;
    private JCheckBox isBoldBox;
    private JComboBox fontNameBox;
    private JComboBox textSizeBox;
    private JTextArea minBorder;
    private JTextArea maxBorder;
    private JButton showSub;
    
    
    private boolean includeFont = true;
    private boolean includeBold = true;

    
	public TextView(SidePanelModel arg0) {
		super(arg0);
		createUI();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setBoldText(String text){
		isBoldBox.setText(text);
	}
	
	public void setIncludeBold(boolean includeBold){
		this.includeBold = includeBold;
		fillPanel();
	}
	
	public void setIncludeFont(boolean includeFont){
		this.includeFont = includeFont;
		fillPanel();
	}
	
	@Override
    public void setEnabled(boolean val){
    	//Set all components to the required values	
    }
	
	private void fillPanel() {
		createUI();
		panel.removeAll();
		panel.addComps(new JLabel("Text size"), textSizeBox);
		if(includeFont){
			panel.addComp(new JLabel("Font"));
			panel.addComp(fontNameBox);
		}
		if(includeBold){
			panel.addComp(isBoldBox);
		}
		
	}

	@Override
	public JComponent getComponent() {
		createUI();
		return panel;
	}

	@Override
	public void updateUI(SidePanelModel arg0) {
		createUI();
		isBoldBox.setSelected(((TextModel) getModel()).isBold());
        fontNameBox.setSelectedIndex(((TextModel) getModel()).getFontNumber());
        //textSizeBox.setSelectedIndex(((TextModel) getModel()).getTextSize());
	}
	
	private void createUI(){
		if(panel == null){
			isBoldBox = new JCheckBox("Bold");
			isBoldBox.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent arg0) {
					((TextModel)getModel()).setBold(isBoldBox.isSelected());
				}
			});
			isBoldBox.setSelected(true);
			
			fontNameBox = new JComboBox(((TextModel)getModel()).getFontNames());
			fontNameBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					((TextModel)getModel()).setFontNumber(fontNameBox.getSelectedIndex());
				}
				
			});
			fontNameBox.setSelectedIndex(221);
			fontNameBox.setPreferredSize(new Dimension(150, fontNameBox.getPreferredSize().height));
			
			
			textSizeBox = new JComboBox(((TextModel)getModel()).getTextSizeNames());
			textSizeBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent even){
					((TextModel)getModel()).setTextSize(textSizeBox.getSelectedIndex());
				}
			});
			
			textSizeBox.setSelectedIndex(2);
			
			panel = new StandardLayout();
			fillPanel();
		}
	}
}
