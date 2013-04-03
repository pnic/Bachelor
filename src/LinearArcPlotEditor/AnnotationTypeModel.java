package LinearArcPlotEditor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ViewCanvas.LAPFeatureType;

import com.clcbio.api.base.util.State;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.editors.framework.sidepanel.event.SidePanelEvent;

public class AnnotationTypeModel extends SidePanelModel{
	
	//For (un)checking a type
	private String lastUpdated;
	private boolean lastUpdatedChanged = false;
	
	//For changing color
	private String lastChangedLabelName;
	private Color lastChangedLabel;
	private boolean labelChanged = false;
	
	
	private Map<String, Boolean> booltypes;
	
	public AnnotationTypeModel(String title, List<LAPFeatureType> types) {
		super(title);
		booltypes = new HashMap<String, Boolean>();
		
		buildMap(types);
	}

	private void buildMap(List<LAPFeatureType> types) {
		for(LAPFeatureType l : types){
			booltypes.put(l.getName(), true);
		}
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

	public Map<String, Boolean> getBooltypes() {
		return booltypes;
	}

	public void setBooltypes(Map<String, Boolean> booltypes) {
		this.booltypes = booltypes;
	}

	
	public void changeType(String type){
		lastUpdated = type;
		booltypes.put(type, !booltypes.get(type));
		labelChanged = false;
		lastUpdatedChanged = true;
		fireModelChanged();
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public void labelChanged(String s, Color color) {
		this.lastChangedLabelName = s;
		this.lastChangedLabel = color;
		lastUpdatedChanged = false;
		labelChanged = true;
		fireModelChanged();
	}

	public Color getLastChangedLabel() {
		return lastChangedLabel;
	}

	public void setLastChangedLabel(Color lastChangedLabel) {
		this.lastChangedLabel = lastChangedLabel;
	}

	public void setLastChangedLabelName(String lastChangedLabelName) {
		this.lastChangedLabelName = lastChangedLabelName;
	}

	public String getLastChangedLabelName() {
		return lastChangedLabelName;
	}

	public boolean isLastUpdatedChanged() {
		return lastUpdatedChanged;
	}

	public void setLastUpdatedChanged(boolean lastUpdatedChanged) {
		this.lastUpdatedChanged = lastUpdatedChanged;
	}

	public boolean isLabelChanged() {
		return labelChanged;
	}

	public void setLabelChanged(boolean labelChanged) {
		this.labelChanged = labelChanged;
	}

}
