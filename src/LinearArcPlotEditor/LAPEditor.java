package LinearArcPlotEditor;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.clcbio.api.base.persistence.PersistenceException;
import com.clcbio.api.base.util.State;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructures;
import com.clcbio.api.free.datatypes.ClcObject;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.NucleotideSequence;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.framework.listener.ObjectEvent;
import com.clcbio.api.free.datatypes.framework.listener.ObjectListener;
import com.clcbio.api.free.datatypes.framework.listener.SelectionEvent;
import com.clcbio.api.free.editors.framework.AbstractEditor;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelListener;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.event.SidePanelEvent;
import com.clcbio.api.free.framework.navigation.NavigationManager;
import com.clcbio.api.free.framework.workspace.Workspace;
import com.clcbio.api.free.gui.StandardLayout;
import com.clcbio.api.free.gui.components.JTextAreaNotPasteable;
import com.clcbio.api.free.gui.components.SelectFeaturePanel;
import com.clcbio.api.free.gui.focus.ClcFocusScrollPane;
import com.clcbio.api.free.gui.icon.ClcIcon;
import com.clcbio.api.free.gui.icon.DefaultClcIcon;
import com.clcbio.api.free.gui.icon.EmptyIcon;
import com.clcbio.api.free.workbench.WorkbenchManager;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructures;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.annotation.RnaStructureAnnotation;
import com.clcbio.api.clc.graphics.AbstractGraphicsEditor;
import com.clcbio.api.clc.graphics.framework.ClcScrollPane;
import com.clcbio.api.free.editors.framework.MouseMode;
import com.clcbio.api.free.datatypes.ClcObject;
import com.clcbio.api.free.datatypes.ClcString;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.NucleotideSequence;


public class LAPEditor extends AbstractGraphicsEditor {

	public final static String PLUGIN_GROUP = "free";
	static{
        System.out.println("### LAPEditor LOADED SUCCESFULLY ###");
    }
	private LAP lap;
	
	private Sequence seq;
	private ObjectListener sequenceListener;
	
	private LAPLayoutView lapView;
	private LAPLayoutModel lapModel;
	
	private TextModel textModel;
	private TextView textView;
	
	private Font font = new Font("Monospaced", Font.PLAIN, 12);
    private int[] sizeLookup = new int[] { 6, 9, 12, 18, 24 };
	
	public boolean canEdit(Class[] types) {
        if (types == null || types.length != 1) {
            return false;
        }
        if (!(Sequence.class.isAssignableFrom(types[0]))) {
            return false;
        }
        return true;
    }
	
	@Override
    public void initGraphicsEditorInstance(WorkbenchManager manager, ClcObject[] models, Workspace ws) {
        //super.initGraphicsEditorInstance(manager, models, ws);
// This method is called whenever an Editor of this type is instantiated. As seen in the Action example, we are fed the the global context object WorkbenchManager. In this method we set up an observer on the sequence we want to edit in order to update the Editor whenever the sequence changes. This is fundamental part of the Model View Controller (MVC) design pattern.
        //Assume a sequence is selected (otherwise we wouldn't be here) 
        //Set up model listener
        seq = (Sequence) models[0];
        sequenceListener = new ObjectListener() {
            public void eventOccurred(ObjectEvent event) {
                if (event instanceof SelectionEvent) {
                    return;
                }
                update();
            }
        };
        seq.addListener(sequenceListener);
        
        
        lapModel = new LAPLayoutModel(manager);
        lapView = new LAPLayoutView(lapModel);
        
        // This states what happens (to the view) when the model changes.
        lapModel.addSidePanelListener(new SidePanelListener() {
            public void modelChanged(SidePanelModel model, SidePanelEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                    	if(lap!=null){
                    		lap.refresh(lapModel.getColorModel());
                    		lap.setTitle(lapModel.getLapTitle());
                    		repaint();
                    	}
                    }
                });
            }
        });
        
        textModel = new TextModel(manager);
        textView = new TextView(textModel);
        
        textModel.addSidePanelListener(new SidePanelListener(){
			@Override
			public void modelChanged(SidePanelModel arg0, SidePanelEvent arg1) {
				SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(lap != null){
                        	lap.setBaseLineText(textModel.isBold(), sizeLookup[textModel.getTextSize()], textModel.getFontName());
                        }
                    }
                });
			}
        });
        
        addSidePanelView(textView);        
        addSidePanelView(lapView);

        //lapView.setEnabled(true);
        //textView.setEnabled(true);
        //New linear arc plot
        lap = new LAP(seq,lapModel.getColorModel(),"The title");
		getCanvas().addChild(lap);
    }
	
	
	
	 @Override
	    public String getSideTitle() {
	        return "Linear Arcplot";
	    }
	// This method returns a title for the sidepanel. More about this in the next section, which is about side panels

	//Human readable text string, that will appear in a "View" submenu. The concatenated string will then be "View As Simple Text"

	    @Override
	    public void doPopup(MouseEvent e) {
	        super.doPopup(e);
	        getContextGroup().getPopup().show((Component) e.getSource(), e.getX(), e.getY());
	    }
	// This method is invoked when the user triggers popup on the editor. In this case we use a standard popup the API offers

	    private void update() {
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	if(RnaStructures.getStructures(seq)==null||RnaStructures.getStructures(seq).getStructureCount() != 1){
	            		lapView.setEnabled(false);
	            	}
	            	else{
	            		lapView.setEnabled(false);
	            	}
	            }
	        });
	    }
	

	@Override
    public State getState() {
        State s = super.getState();
        s.putAll(lapModel.save());
        s.putAll(textModel.save());
        return s;
    }
// In this case the user state of the editor is the state of the sidepanel. More about this later

    @Override
    public void setState(State s) {
        super.setState(s);
        lapModel.load(s);
        textModel.load(s);
    }
// And vice versa, when setting the state, we simply load it into the sidepanel state

    public ClcObject[] getEditingObjects(boolean isDragging) {
        return new ClcObject[] { seq };
    }
// Here we return an array of the object being edited. The parameter should almost always be ignored

    @Override
    public String toString() {
        return getName();
    }

    public double getVersion() {
        return 1.0;
    }

    public String getClassKey() {
        return "lap_editor";
    }

    @Override
    public ClcIcon getMenuIcon() {
    	//TODO: Use the proper icon! 
    	//ImageIcon menuIcon = createImageIcon("res/arcplotIcon.png","arcplot");
    	//return new DefaultClcIcon(menuIcon);
        //return EmptyIcon.getInstance();
    	return new DefaultClcIcon("editors/linear");
    }
// Here we return an icon for the editor. This will appear both in menus and on the editor tab in the view. The icon is loaded from the resources of the plugin. These resource should be built into the final plugin jar when deploying.
    protected ImageIcon createImageIcon(String path,
            String description) {
    	File dir1 = new File ("."); 
    	try {
    		System.out.println(dir1.getCanonicalPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't open directory ");
			e1.printStackTrace();
		}
    	java.net.URL imgURL = getClass().getResource(path);
    	if (imgURL != null) {
    		return new ImageIcon(imgURL, description);
    	} else {
    		
    		System.err.println("Couldn't find file: " + path);
    		return null;
    	}
    }

	public boolean canDoMode(MouseMode arg0) {
		// TODO Auto-generated method stub
		//pan & zoom enabled
		return true;
	}

	@Override
	protected void setScales(boolean arg0) {
	}



	@Override
	public String getName() {
		
		return "As Linear ArcPlot";
	}

}
