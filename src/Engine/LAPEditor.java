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
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import LinearArcPlotEditor.AnnotationLayoutModel;
import LinearArcPlotEditor.AnnotationLayoutView;
import LinearArcPlotEditor.AnnotationTypeModel;
import LinearArcPlotEditor.AnnotationTypeView;
import LinearArcPlotEditor.LAPLayoutModel;
import LinearArcPlotEditor.LAPLayoutView;
import LinearArcPlotEditor.RasmolColorInfoProvider;
import LinearArcPlotEditor.SequenceModel;
import LinearArcPlotEditor.SequenceView;
import LinearArcPlotEditor.StructureValueInfoProvider;
import LinearArcPlotEditor.TextModel;
import LinearArcPlotEditor.TextView;
import ViewCanvas.ColorGradientRectangle;
import ViewCanvas.TitleText;
import ViewCanvas.infoBox;

import com.clcbio.api.base.persistence.PersistenceException;
import com.clcbio.api.base.session.FactoryManager;
import com.clcbio.api.base.util.CreateList;
import com.clcbio.api.base.util.State;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructure;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructures;
import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.clc.editors.graphics.sequence.sidepanel.SequenceInfoView;
import com.clcbio.api.free.datatypes.ClcObject;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.NucleotideSequence;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.Alignment;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.AlignmentBuilder;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.AlignmentFactory;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alphabet.AlphabetTools;
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
import com.clcbio.api.clc.gui.framework.ParameterPanel;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.SequenceTypeEditor;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.info.AbstractInfoProvider;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.info.InfoListener;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.info.InfoProvider;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.sidepanel.SequenceInfoModel;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.sidepanel.SubSequenceInfoModel;
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
	private boolean inputIsAlignment;
	private Alignment alignment;
	
	private Font font = new Font("Monospaced", Font.PLAIN, 12);
    private int[] sizeLookup = new int[] { 6, 9, 14, 18, 24 };	
	public ClcObject[] models;
	
	@Override
    public void initGraphicsEditorInstance(WorkbenchManager manager, ClcObject[] models, Workspace ws) {
		super.initGraphicsEditorInstance(manager, models, ws);
		// This method is called whenever an Editor of this type is instantiated. As seen in the Action example, we are fed the the global context object WorkbenchManager. In this method we set up an observer on the sequence we want to edit in order to update the Editor whenever the sequence changes. This is fundamental part of the Model View Controller (MVC) design pattern.
        //Assume a sequence is selected (otherwise we wouldn't be here) 
        //Set up model listener
        
        if(inputIsAlignment){
        	alignment = (Alignment)models[0];
        }
        else{
        	AlignmentFactory alignmentFactory = FactoryManager.getInstance().getAlignmentFactory();
        	alignment = alignmentFactory.createAlignment((Sequence)(models[0]));
        }
        
        colorGradientModel = new ColorGradientModel(ColorGradientManager.getGradients());
        
        info = new infoBox("David", colorGradientModel);
        lap = new LAP(alignment,colorGradientModel,"The title", this);
        
        getCanvas().addChild(lap);		
        getCanvas().addChild(info);

		fillSidePanel();  
    }
	
	/*
	 * Fills the sidePanel and attaches listeners. 
	 */
	private void fillSidePanel(){
		final LAPLayoutModel lapModel = new LAPLayoutModel(manager);
        LAPLayoutView lapView = new LAPLayoutView(lapModel);
        
        // This states what happens (to the view) when the model changes.
        lapModel.addSidePanelListener(new SidePanelListener() {
            public void modelChanged(SidePanelModel model, SidePanelEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(info != null){

                        	info.getTitleText().setTitle(lapModel.getLapTitle());
                        }
                        System.out.println("1");
                        repaint();
                    }
                });
            }
        });
        
        final TextModel textModel = new TextModel(manager);
        TextView textView = new TextView(textModel);
        
        textModel.addSidePanelListener(new SidePanelListener(){
			@Override
			public void modelChanged(SidePanelModel arg0, SidePanelEvent arg1) {
				SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(lap != null){
                        	System.out.println("2");
                        	lap.getBaseline().setBold(textModel.isBold());
                        	lap.getBaseline().setFontSize(sizeLookup[textModel.getTextSize()]);
                        	lap.getBaseline().setFontName(textModel.getFontName());
                        	lap.getBaseline().updateFont();
                        }
                    }
                });
			}
        });
        
        final SequenceModel seqModel = new SequenceModel("SequenceLayout");
        SequenceView seqView = new SequenceView(seqModel);
        
        seqModel.addSidePanelListener(new SidePanelListener(){
			@Override
			public void modelChanged(SidePanelModel arg0, SidePanelEvent arg1) {
				SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                    	System.out.println("3");
                        if(lap != null){
                        	lap.getBaseline().drawNumbers(seqModel.getDrawNumbers());
                        	lap.getBaseline().setIndexNumber(seqModel.getIntervalNumbers());
                        }
                        if(info != null){
                        	info.setVisible(seqModel.getShowInfoBox());
                        }
                    }
                });
			}
        });
        
        
        
        final AnnotationTypeModel annotationTypeModel = new AnnotationTypeModel("Annotation Types", lap.getLv().getTypes());
        AnnotationTypeView annotationTypeView = new AnnotationTypeView(annotationTypeModel);
        
        annotationTypeModel.addSidePanelListener(new SidePanelListener(){
        	@Override
        	public void modelChanged(SidePanelModel arg0, SidePanelEvent arg1){
        		SwingUtilities.invokeLater(new Runnable(){
        			public void run(){
        				System.out.println("5");
        				if(lap != null){
        					if (annotationTypeModel.isLastUpdatedChanged()) lap.getLv().setTypeAcces(annotationTypeModel.getLastUpdated());
        					if (annotationTypeModel.isLabelChanged()) lap.getLv().setTypeColor(annotationTypeModel.getLastChangedLabelName(), annotationTypeModel.getLastChangedLabel());
        				}
        			}
        		});
        	}
        });
        
        final AnnotationLayoutModel annotationLayoutModel = new AnnotationLayoutModel("Annotation layout");
        AnnotationLayoutView annotationLayoutView = new AnnotationLayoutView(annotationLayoutModel);
        
        
        annotationLayoutModel.addSidePanelListener(new SidePanelListener(){
        	@Override
        	public void modelChanged(SidePanelModel arg0, SidePanelEvent arg1){
        		SwingUtilities.invokeLater(new Runnable(){
        			public void run(){
        				System.out.println("6");
        				if(lap != null){
        					lap.getLv().setShowAnnotations(annotationLayoutModel.getshowAnnotations());
        					lap.getLv().setShowView(annotationLayoutModel.getSelected());
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

        
        final RasmolColorInfoProvider RasmosColors = new RasmolColorInfoProvider(manager, "Rasmol colors");
        RasmosColors.addInfoListener(new InfoListener(){
			@Override
			public void changeEnding() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void changeStarting() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void colorsChanged() {	
				System.out.println("7");
				lap.getBaseline().showRasmolColors(RasmosColors.getForeground(), 0);
				lap.getBaseline().showRasmolColors(RasmosColors.getBackground(), 1);
			}

			@Override
			public void graphChanged(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        InfoProvider StructureValue = new StructureValueInfoProvider(manager, "Structure values", colorGradientModel);
        StructureValue.addInfoListener(new InfoListener(){

			@Override
			public void changeEnding() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void changeStarting() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void colorsChanged() {
				System.out.println("8");
				lap.refresh2();
				info.refresh();
				repaint();
			}

			@Override
			public void graphChanged(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        SubSequenceInfoModel subSequenceInfoModel = new SubSequenceInfoModel(new SequenceInfoModel(new InfoProvider[] {RasmosColors, StructureValue }, null), "Residue Coloring", CreateList.of(RasmosColors, StructureValue));
        
        addSidePanelView(new SequenceInfoView(subSequenceInfoModel));	
        
		}
	
		// This method returns a title for the sidepanel. More about this in the next section, which is about side panels
	    @Override
	    public void doPopup(MouseEvent e) {
	        super.doPopup(e);
	        getContextGroup().getPopup().show((Component) e.getSource(), e.getX(), e.getY());
	    }
	// This method is invoked when the user triggers popup on the editor. In this case we use a standard popup the API offers

	@Override
    public State getState() {
        State s = super.getState();
        System.out.println("getState");
        return s;
    }
// In this case the user state of the editor is the state of the sidepanel. More about this later

    @Override
    public void setState(State s) {
        super.setState(s);
        System.out.println("setState");
    }
// And vice versa, when setting the state, we simply load it into the sidepanel state

    
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
    
    /*
     * Show the rasmol colors on a sequence.
     * @show = show yes/no
     * @ground: 1 = foreground, 2 = background. 
     */
    public void showRasmolColors(boolean show, int ground){
    	
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
    	return new DefaultClcIcon("arc-icon");
    }
    
    @Override
    public String getSideTitle() {
        return "Linear Arcplot";
    }
	
    @Override
    protected boolean validateInit(WorkbenchManager arg0, ClcObject[] arg1){
    	if(Alignment.class.isAssignableFrom(arg1[0].getClass())){
    		inputIsAlignment = true;
    		return true;
    	}
    	
    	NucleotideSequence sequence = (NucleotideSequence)arg1[0];
    	if(RnaStructures.getStructures(sequence) == null || RnaStructures.getStructures(sequence).getStructureCount() < 1){
    		setErrorView("This is not a secondary structure");
    		return false;
    	}
    	
    	System.out.println("Den g�r igennem");
    	
    	return true;
    }
	//Human readable text string, that will appear in a "View" submenu. The concatenated string will then be "View As Simple Text"
    
	public boolean canEdit(Class[] types) {
		if (types == null || types.length != 1) {
            return false;
        }
        if ((NucleotideSequence.class.isAssignableFrom(types[0])) || Alignment.class.isAssignableFrom(types[0])) {
            return true;
        }
        
        return false;
    }


}
