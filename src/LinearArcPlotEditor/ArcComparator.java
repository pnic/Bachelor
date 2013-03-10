package LinearArcPlotEditor;

import java.util.Comparator;

public class ArcComparator implements Comparator<Arc>{

	@Override
	public int compare(Arc a1, Arc a2) {
		double value_1 = Math.abs(1-a1.getContainValue());
		double value_2 = Math.abs(1-a2.getContainValue());
		
		if(value_1 < value_2)return -1;
		if(value_1 > value_2) return 1;
		
		return 0;
	}

}
