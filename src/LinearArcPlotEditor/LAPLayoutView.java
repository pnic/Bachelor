package LinearArcPlotEditor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import LinearArcPlotEditor.LAPLayoutModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelView;
import com.clcbio.api.free.gui.StandardLayout;

public class LAPLayoutView extends SidePanelView {

	private StandardLayout panel = null;

    private JTextField Title;
    private JLabel titleLabel;
    private JButton setTitle;
    private JCheckBox setCgrVisible;
    private StandardLayout titlePanel;
    
//Here have the three Swing components representing our model properties. Furthermore we use the CLC component StandardLayout to do the layout of our UI.
    
    public LAPLayoutView(final LAPLayoutModel lapModel) {
        super(lapModel);
        createUI();
    }
    
    @Override
    public void setEnabled(boolean val){
    	//Set all components to the required values
    
    	
    }

    private void createUI() {
    	final LAPLayoutModel lapModel = (LAPLayoutModel) getModel();
    	
        if (panel == null) {
            titlePanel = new StandardLayout();
            titlePanel.setLayout(new FlowLayout());
            
            titleLabel = new JLabel("Set title");
            Title = new JTextField("");
            Title.setPreferredSize(new Dimension(100, 24));
            Title.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					((LAPLayoutModel) getModel()).updateTitle(Title.getText());	
				}
			});
            
            setCgrVisible = new JCheckBox("Show reliabilities");
            setCgrVisible.setSelected(true);
            setCgrVisible.addChangeListener(new ChangeListener(){

				@Override
				public void stateChanged(ChangeEvent e) {
					((LAPLayoutModel)getModel()).setCgrVisible(setCgrVisible.isSelected());					
				}
            	
            });
            
            
            titlePanel.add(titleLabel);
            titlePanel.add(Title);
            titlePanel.addComp(setCgrVisible);
            
            panel = new StandardLayout();
            
            fillPanel();
        }
    }
//Here we create all the UI components. Notice how the listeners on the different components manipulates the model and hence acts as the controllers of our design.

    private void fillPanel() {
        createUI();
        panel.removeAll();  
        panel.addComp(titlePanel);
        panel.addComp(setCgrVisible);
    }
//And here we do the layout of the components in our StandardLayout. Notice how the fontNameBox and isBoldBox are only added if their respective "include" flags are set.

    @Override
	public JComponent getComponent() {
        createUI();
        return panel;
    }
//And this call back should return a Container holding all our UI.

    @Override
	public void updateUI(SidePanelModel m) {
        createUI();        
    }
//This method is ensured to be called whenever the model changes. Thus it should update the view according to the model.



}
