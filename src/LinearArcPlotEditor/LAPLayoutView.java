package LinearArcPlotEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.xmlbeans.impl.values.JavaIntegerHolder;

import LinearArcPlotEditor.LAPLayoutModel;
import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.clc.editors.graphics.components.GradientChooser;
import com.clcbio.api.clc.editors.graphics.components.GradientModelListener;
import com.clcbio.api.clc.graphics.components.ColorGradientManager;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelView;
import com.clcbio.api.free.gui.StandardLayout;

public class LAPLayoutView extends SidePanelView {

	private StandardLayout panel = null;
    private JTextField zoom; //Reverse the sequence? (Clockwise/anticlockwise) 
   

    private GradientChooser gradchooser;
    
    
    private JSpinner zoomlevel; //to zoom 
    
//Here have the three Swing components representing our model properties. Furthermore we use the CLC component StandardLayout to do the layout of our UI.
    
    public LAPLayoutView(final LAPLayoutModel lapModel) {
        super(lapModel);
        createUI();
    }
    
    @Override
    public void setEnabled(boolean val){
    	//Set all components to the required values
    	zoom.setEnabled(val);
    	
    }

    private void createUI() {
    	
    	final LAPLayoutModel lapModel = (LAPLayoutModel) getModel();
    	
        if (panel == null) {
            zoom = new JTextField("Zoom In");
            
            zoom.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    lapModel.setZoom(Integer.parseInt(zoom.getText()));
                }
            });
           //zoom.setSelected((lapModel).zoomLevel());
            
          
            final ColorGradientModel gradmodel = lapModel.getColorModel();
            gradchooser = new GradientChooser(gradmodel);
            gradmodel.addListener(new GradientModelListener(){
				public void changeEnding() {
					((LAPLayoutModel) getModel()).updateColors(gradmodel);
				}
				public void changeStarting() {
					((LAPLayoutModel) getModel()).updateColors(gradmodel);
				}
				public void gradientChanged() {
					((LAPLayoutModel) getModel()).updateColors(gradmodel);
				}
				public void limitChanged() {
					((LAPLayoutModel) getModel()).updateColors(gradmodel);
				}
				public void stringsChanged() {}
            });
         
            panel = new StandardLayout();
            
            fillPanel();
        }
    }
//Here we create all the UI components. Notice how the listeners on the different components manipulates the model and hence acts as the controllers of our design.

    private void fillPanel() {
        createUI();
        panel.removeAll();
        panel.addComps(new JLabel("Zoom Level"), zoom);         
        panel.addComp(gradchooser);
        
    }
//And here we do the layout of the components in our StandardLayout. Notice how the fontNameBox and isBoldBox are only added if their respective "include" flags are set.

    public JComponent getComponent() {
        createUI();
        return panel;
    }
//And this call back should return a Container holding all our UI.

    public void updateUI(SidePanelModel m) {
        createUI();        
    }
//This method is ensured to be called whenever the model changes. Thus it should update the view according to the model.



}
