package LinearArcPlotEditor;

import javax.swing.ButtonModel;

import com.clcbio.api.base.util.State;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;

public class AnnotationLayoutModel extends SidePanelModel{
	private boolean showAnnotations;
	private boolean showArrows;
	private boolean showGradients;
	private String selected;
	
	//private static final String[] positions = new String[]{"Next", "Small", "Medium", "Large", "Huge"};
	 
	public AnnotationLayoutModel(String title) {
		super(title);
		selected = "arrows";
		showAnnotations = true;
		showArrows = true;
		showGradients = true;
	}

	@Override
	public String getId() {
		return "Sequence layout model";
	}

	@Override
	protected void loadModel(State arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected State saveModel() {
		State model = new State();
		
		return model;
	}
	
	public void showAnnotations(boolean showAnnotations){
		this.showAnnotations = showAnnotations;
		fireModelChanged();
	}
	
	public boolean getshowAnnotations(){
		return showAnnotations;
	}
	
	public void showArrows(boolean showInfo){
		this.showArrows = showInfo;
		fireModelChanged();
	}
	
	public boolean getshowArrows(){
		return showArrows;
	}

	public void showGradients(boolean showInfo){
		this.showGradients = showInfo;
		fireModelChanged();
	}
	
	public boolean getshowGradients(){
		return showGradients;
	}

	public void setChosenView(String selected) {
		this.setSelected(selected);
		fireModelChanged();		
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getSelected() {
		return selected;
	}
	
}
