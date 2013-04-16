package LinearArcPlotEditor;

import com.clcbio.api.base.util.State;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;

public class AlignmentModel extends SidePanelModel{

	private int sequenceNumber;
	
	public AlignmentModel(String arg0) {
		super(arg0);
		sequenceNumber = 0;
	}
	
	public void setSequenceNumber(int number){
		this.sequenceNumber = number-1;
		System.out.println(""+sequenceNumber);
		fireModelChanged();
	}
	
	public int getSequenceNumber(){
		return sequenceNumber;
	}

	@Override
	public String getId() {
		return "Alignment model";
	}

	@Override
	protected void loadModel(State arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected State saveModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
