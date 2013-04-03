package Engine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import LinearArcPlotEditor.AnnotationLayoutModel;
import LinearArcPlotEditor.AnnotationLayoutView;
import LinearArcPlotEditor.AnnotationTypeModel;
import LinearArcPlotEditor.AnnotationTypeView;
import LinearArcPlotEditor.LAPLayoutModel;
import LinearArcPlotEditor.LAPLayoutView;
import LinearArcPlotEditor.ResidueColorModel;
import LinearArcPlotEditor.ResidueColorView;
import LinearArcPlotEditor.SequenceModel;
import LinearArcPlotEditor.SequenceView;
import LinearArcPlotEditor.TextModel;
import LinearArcPlotEditor.TextView;
import ViewCanvas.TitleText;
import ViewCanvas.infoBox;

import com.clcbio.api.base.persistence.PersistenceException;
import com.clcbio.api.base.util.State;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructures;
import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.free.datatypes.ClcObject;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.framework.listener.ObjectEvent;
import com.clcbio.api.free.datatypes.framework.listener.ObjectListener;
import com.clcbio.api.free.datatypes.framework.listener.SelectionEvent;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelException;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelGroup;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelListener;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.event.SidePanelEvent;
import com.clcbio.api.free.framework.workspace.Workspace;
import com.clcbio.api.free.gui.icon.ClcIcon;
import com.clcbio.api.free.gui.icon.DefaultClcIcon;
import com.clcbio.api.free.workbench.WorkbenchManager;
import com.clcbio.api.clc.graphics.AbstractGraphicsEditor;
import com.clcbio.api.clc.graphics.components.ColorGradientManager;
import com.clcbio.api.free.editors.framework.MouseMode;


public class LAPEditor extends AbstractGraphicsEditor {

	public final static String PLUGIN_GROUP = "free";
	static{
        System.out.println("### LAPEditor LOADED SUCCESFULLY ###");
    }
	private LAP lap;
	private infoBox info;
	private ColorGradientModel colorGradientModel;
	
	private Sequence seq;
	private ObjectListener sequenceListener;
	
	private LAPLayoutView lapView;
	private LAPLayoutModel lapModel;
	
	private TextModel textModel;
	private TextView textView;
	
	private SequenceModel seqModel;
	private SequenceView seqView;
	
	private AnnotationLayoutModel annotationLayoutModel;
	private AnnotationLayoutView annotationLayoutView;	
	
	private AnnotationTypeModel annotationTypeModel;
	private AnnotationTypeView annotationTypeView;
	
	private ResidueColorModel colorModel;
    private ResidueColorView colorView;
	
	private Font font = new Font("Monospaced", Font.PLAIN, 12);
    private int[] sizeLookup = new int[] { 6, 9, 14, 18, 24 };
	
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
        super.initGraphicsEditorInstance(manager, models, ws);
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
        
        colorGradientModel = new ColorGradientModel(ColorGradientManager.getGradients());
        
        info = new infoBox();
        lap = new LAP(seq,colorGradientModel,"The title", this);
        
        getCanvas().addChild(lap);		
        getCanvas().addChild(info);

		info.addChild(info.getCgr());
		fillSidePanel();  
    }
	
	/*
	 * Fills the sidePanel and attaches listeners. 
	 */
	private void fillSidePanel(){
		lapModel = new LAPLayoutModel(manager);
        lapView = new LAPLayoutView(lapModel);
        
        // This states what happens (to the view) when the model changes.
        lapModel.addSidePanelListener(new SidePanelListener() {
            public void modelChanged(SidePanelModel model, SidePanelEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                    	if(lap!=null){
                    		lap.refresh(lapModel.getColorModel());
                    	}
                        if(info != null){
                        	info.getTitleText().setTitle(lapModel.getLapTitle());
                        }
                        repaint();
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
        
        seqModel = new SequenceModel("SequenceLayout");
        seqView = new SequenceView(seqModel);
        
        seqModel.addSidePanelListener(new SidePanelListener(){
			@Override
			public void modelChanged(SidePanelModel arg0, SidePanelEvent arg1) {
				SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(lap != null){
                        	lap.getBaseline().drawNumbers(seqModel.getDrawNumbers());
                        }
                        if(info != null){
                        	info.setVisible(seqModel.getShowInfoBox());
                        }
                    }
                });
			}
        });
        
        colorModel = new ResidueColorModel("Residue coloring");
        colorView = new ResidueColorView(colorModel);
        
        
        
        colorModel.addSidePanelListener(new SidePanelListener(){
        	@Override
        	public void modelChanged(SidePanelModel arg0, SidePanelEvent arg1){
        		SwingUtilities.invokeLater(new Runnable(){
        			public void run(){
        				
        			}
        		});
        	}
        });
        
        annotationTypeModel = new AnnotationTypeModel("Annotation Types", lap.getLv().getTypes());
        annotationTypeView = new AnnotationTypeView(annotationTypeModel);
        
        annotationTypeModel.addSidePanelListener(new SidePanelListener(){
        	@Override
        	public void modelChanged(SidePanelModel arg0, SidePanelEvent arg1){
        		SwingUtilities.invokeLater(new Runnable(){
        			public void run(){
        				if(lap != null){
        					if (annotationTypeModel.isLastUpdatedChanged()) lap.getLv().setTypeAcces(annotationTypeModel.getLastUpdated());
        					if (annotationTypeModel.isLabelChanged()) lap.getLv().setTypeColor(annotationTypeModel.getLastChangedLabelName(), annotationTypeModel.getLastChangedLabel());
        				}
        			}
        		});
        	}
        });
        
        annotationLayoutModel = new AnnotationLayoutModel("Annotation layout");
        annotationLayoutView = new AnnotationLayoutView(annotationLayoutModel);
        
        
        annotationLayoutModel.addSidePanelListener(new SidePanelListener(){
        	@Override
        	public void modelChanged(SidePanelModel arg0, SidePanelEvent arg1){
        		SwingUtilities.invokeLater(new Runnable(){
        			public void run(){
        				if(lap != null){
        					lap.getLv().setShowAnnotations(annotationLayoutModel.getshowAnnotations());
        					lap.getLv().setShowArrows(annotationLayoutModel.getshowArrows());
        					lap.getLv().setShowGradients(annotationLayoutModel.getshowGradients());
        				}
        			}
        		});
        	}
        });
        
        addSidePanelView(seqView);
        addSidePanelView(annotationLayoutView);
        addSidePanelView(annotationTypeView);
        addSidePanelView(textView);        
        addSidePanelView(lapView);
        addSidePanelView(colorView);
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

	public infoBox getInfo() {
		return info;
	}

	public void setInfo(infoBox info) {
		this.info = info;
	}

}
