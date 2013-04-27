package LinearArcPlotEditor;

import com.clcbio.api.base.util.State;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.workbench.WorkbenchManager;

public class LAPLayoutModel extends SidePanelModel {

	 private int zoomLevel = 100;
     private String LAPTitle = "Title";
     private boolean cgrVisible = true;

	    private static final String ZOOM_LEVEL = "100%";
	    
	//Here we define some keys for later use in usersettings of the sidepanel model.

	    
	    public LAPLayoutModel(WorkbenchManager manager) {
	        super("Infobox");
	        
	    }
	// Construction and initialization.

	    @Override
		public void loadModel(State model) {
	      
	    }

	    @Override
		public State saveModel() {
	        State model = new State();
	        model.put(ZOOM_LEVEL, zoomLevel);
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
	  		
	    @Override
		public String getId() {
	        return "LAPLayoutModel";
	    }
	//Returning usersettings id for model.

	    public void setToFactory() {
	        zoomLevel = 100;
	    }
	//The user has the option to apply some default settings on a sidepanel. When this is done this method is called, and here we reset the model to our chosen default value

		public void updateTitle(String newTitle){
			LAPTitle = newTitle;
			fireModelChanged();
		}
		
		public String getLapTitle(){
			return LAPTitle;
		}

		public boolean isCgrVisible() {
			return cgrVisible;
		}

		public void setCgrVisible(boolean cgrVisible) {
			if (cgrVisible == this.cgrVisible) {
	            return;
	        }
	        this.cgrVisible = cgrVisible;
	        fireModelChanged();
		}
		
		
}
