package LinearArcPlotEditor;

import com.clcbio.api.base.util.State;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;

public class SequenceModel extends SidePanelModel{
	private boolean drawNumbers;
	private boolean showInfoBox;
	private int startValue;
	private boolean showSub = false;
	
	public SequenceModel(String title) {
		super(title);
		drawNumbers = true;
		showInfoBox = true;
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
	
	public void drawNumbers(boolean drawNumbers){
		this.drawNumbers = drawNumbers;
		fireModelChanged();
	}
	
	public void setNumbers(String numbers){
		if(tryParseInt(numbers)){
			startValue = Integer.parseInt(numbers);
		}
		fireModelChanged();
	}
	
	public int getIntervalNumbers(){
		return startValue;
	}
	
	public boolean tryParseInt(String text){
		try  
	     {  
	         Integer.parseInt(text);  
	         return true;  
	      } catch(NumberFormatException nfe)  
	      {  
	          return false;  
	      }  
	}
	
	public boolean getDrawNumbers(){
		return drawNumbers;
	}
	
	public void showInfowBox(boolean showInfo){
		this.showInfoBox = showInfo;
		fireModelChanged();
	}
	
	public boolean getShowInfoBox(){
		return showInfoBox;
	}

	public void showSubSequence() {
		showSub=true;
		fireModelChanged();
		
		
	}

	public boolean isShowSub() {
		return showSub;
	}

	public void setShowSub(boolean showSub) {
		this.showSub = showSub;
	}

}
