package LinearArcPlotEditor;


import com.clcbio.api.base.io.ClcFileFilter;
import com.clcbio.api.base.io.ExportPlugin;
import com.clcbio.api.base.persistence.PersistenceException;
import com.clcbio.api.base.process.Activity;
import com.clcbio.api.base.util.iterator.MovableIntegerIterator;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructures;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.annotation.RnaStructureAnnotation;
import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.free.datatypes.ClcObject;
import com.clcbio.api.free.datatypes.ClcString;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.Alignment;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.feature.Feature;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.interval.Interval;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.region.Region;
import com.clcbio.api.free.gui.dialog.ClcMessages;
//import com.ppfold.algo.ExportTools;
//import com.ppfold.algo.MatrixTools;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LAPFeatureView {
	
	Sequence seq;
	List<LAPFeature> features;
	
	public LAPFeatureView(Sequence seq, LAP root){
		this.seq = seq;
		buildFeatures(root);
	}
	
public void buildFeatures(LAP root){
	
		
		Iterator<Feature> featureIter = seq.getFeatureIterator();
		if(featureIter.hasNext()) features = new ArrayList<LAPFeature>();
		
		int offset = 50;
		
		while(featureIter.hasNext()){
			Feature fet = featureIter.next(); 
			if(fet.getName().length() > 20) continue;
			
			
			Region fetregion = fet.getRegion();
			int fetstart = fetregion.getFirstPos().getMin(); 
			int fetstop = fetregion.getLastPos().getMin(); 
			
			LAPFeature tmp = new LAPFeature(fet.getName(), fetstart, fetstop, offset, root);
			
			
			System.out.println("Feature: " + fet.getName() + "\n " + fetstart + "\n " + fetstop); 
			
			Iterator<Interval> II = fetregion.getIntervalIterator();
			
			while(II.hasNext()){
				Interval in = II.next();
				tmp.addFeatureInterval(new LAPFeatureInterval(in.getFirstPos().getMin(),in.getLastPos().getMax(),offset, root));
				System.out.println("Interval \n First Position: " + in.getFirstPos() + " \n Last Position: " + in.getLastPos());
				//System.out.println("Interval \n First Position.getMin: " + in.getFirstPos().getMin() + " \n Last Position.getMin: " + in.getLastPos().getMin());
				//System.out.println("Interval \n First Position.getMax: " + in.getFirstPos().getMax() + " \n Last Position.getMax: " + in.getLastPos().getMax());
			}
			features.add(tmp);
			offset += 20;			
		}			
}

public List<LAPFeature> getFeatures(){
	return features;
}

public double getVersion() {
return 1.0;
}
//Version of the plugin

public String getName() {
return "Sequence Feqtures";
}
//Human readable name of the plugin

public String getClassKey() {
return "alignment_annotations_exporter";
}
//Class identifier of the plugin

}
