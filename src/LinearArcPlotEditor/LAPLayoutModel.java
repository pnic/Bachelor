package LinearArcPlotEditor;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import com.clcbio.api.base.util.State;
import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.clc.graphics.components.ColorGradientManager;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.workbench.WorkbenchManager;

public class LAPLayoutModel extends SidePanelModel {

	 private int zoomLevel = 100;
     private State colorstate;
     private String LAPTitle = "Title";
     private boolean titleIsVisible = true;
	    
	    private ColorGradientModel colormodel = new ColorGradientModel(ColorGradientManager.getGradients()); 

	    private static final String ZOOM_LEVEL = "100%";
	    private static final String KEY_COLORSTATE = "colorstate";
	    
	//Here we define some keys for later use in usersettings of the sidepanel model.


	    public ColorGradientModel getColorModel(){
	    	return colormodel;
	    }
	    
	    public LAPLayoutModel(WorkbenchManager manager) {
	        super("Infobox");

	    }
	// Construction and initialization.

	    public void loadModel(State model) {
	        colormodel.setState(model.getChild(KEY_COLORSTATE));
	    }

	    public State saveModel() {
	        State model = new State();
	        model.put(ZOOM_LEVEL, zoomLevel);
	        model.put(KEY_COLORSTATE, colormodel.getState());
	        return model;
	    }
	    
	    //The loadModel(..) and saveModel() methods, that has to be overwritten are used to support usersettings. We simply store and restore our properties in a State.
	    public int zoomLevel() {
	        return zoomLevel;
	    }

	    public void setZoom(int zoom) {
	        this.zoomLevel = zoom;
	        fireModelChanged();
	    }
	  		
	    public String getId() {
	        return "LAPLayoutModel";
	    }
	//Returning usersettings id for model.

	    public void setToFactory() {
	        zoomLevel = 100;
	    }
	//The user has the option to apply some default settings on a sidepanel. When this is done this method is called, and here we reset the model to our chosen default value

		public void updateColors(ColorGradientModel colmodel) {
			// TODO Auto-generated method stub
			this.colormodel = colmodel;
			fireModelChanged();
		}

		public void updateTitle(String newTitle){
			LAPTitle = newTitle;
			fireModelChanged();
		}
		
		public String getLapTitle(){
			return LAPTitle;
		}
}
