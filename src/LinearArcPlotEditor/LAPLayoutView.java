package LinearArcPlotEditor;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
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

	private JLabel colorChoose;
    private GradientChooser gradchooser;
    

    private JTextField Title;
    private JLabel titleLabel;
    private JButton setTitle;
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
            
            titlePanel.add(titleLabel);
            titlePanel.add(Title);
            
            colorChoose = new JLabel("Color of arcs");
            panel = new StandardLayout();
            
            fillPanel();
        }
    }
//Here we create all the UI components. Notice how the listeners on the different components manipulates the model and hence acts as the controllers of our design.

    private void fillPanel() {
        createUI();
        panel.removeAll();  
        panel.addComp(titlePanel);
        panel.addComp(colorChoose);
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
