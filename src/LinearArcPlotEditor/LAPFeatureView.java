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
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;
//import com.ppfold.algo.ExportTools;
//import com.ppfold.algo.MatrixTools;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LAPFeatureView {
	
	Sequence seq;
	List<LAPFeatureType> types;
	List<LAPFeature> features;
	private int typeHeight = 50;
	private int typeWidth;
	
	
	public LAPFeatureView(Sequence seq, LAP root){
		this.seq = seq;
		features = new ArrayList<LAPFeature>();
		typeWidth = seq.getLength();
		buildFeatureTypes(root);
	}
	

public void buildFeatureTypes(LAP root){
	
		//Get an iterator over the features and only proceed if at least one feature exists.
		
		Iterator<Feature> featureIter = seq.getFeatureIterator();
		
		
		if(featureIter.hasNext()) types = new ArrayList<LAPFeatureType>();
		
		//Build LAPFeatureType elements for each different type of feature in the sequence.
		buildTypes(seq.getFeatureTypes(),root);
		
		//Get type iterator
		Iterator<LAPFeatureType> typeIter = types.iterator();
		LAPFeatureType cur = typeIter.next();
		
		
		int curOverlaps = 0;
		int offset = 30;
				
		cur.setX(0);
		cur.setTypeOffset(offset);
		cur.setWidth(typeWidth);
		cur.setHeight(typeHeight);
		
		while(featureIter.hasNext()){
			Feature fet = featureIter.next(); 
			System.out.println(fet.getType() + " is the type of the feature and " + cur.getName() + " is the name of the type");
			if(fet.getType().compareToIgnoreCase(cur.getName()) != 0){
				System.out.println("New Type");
				cur = typeIter.next();
				
				cur.setX(0);
				cur.setTypeOffset(offset);
				cur.setWidth(typeWidth);
				cur.setHeight(typeHeight);
				
				offset += typeHeight;
			}
			
			if(fet.getName().length() > 20) continue;
						
			Region fetregion = fet.getRegion();
			int fetstart = fetregion.getFirstPos().getMin(); 
			int fetstop = fetregion.getLastPos().getMin(); 
			
			LAPFeature tmp = new LAPFeature(fet.getName(), fetstart, fetstop, offset, root);
			
			Iterator<Interval> II = fetregion.getIntervalIterator();
			
			while(II.hasNext()){
				Interval in = II.next();
				LAPFeatureInterval li = new LAPFeatureInterval(in.getFirstPos().getMin(),in.getLastPos().getMax(),offset, root);
				tmp.addFeatureInterval(li);
				//System.out.println("Interval \n First Position: " + in.getFirstPos() + " \n Last Position: " + in.getLastPos());
				//System.out.println("Interval \n First Position.getMin: " + in.getFirstPos().getMin() + " \n Last Position.getMin: " + in.getLastPos().getMin());
				//System.out.println("Interval \n First Position.getMax: " + in.getFirstPos().getMax() + " \n Last Position.getMax: " + in.getLastPos().getMax());
				cur.addInterval(li);
			}
			//features.add(tmp);
			
			offset += 20;	
		}			
		
		sortTypes();
		
		
		
		
		
}
	
private void sortTypes() {
	for(LAPFeatureType t : types){
		java.util.Collections.sort(t.getIntervals());
	}	
}


private void buildTypes(Set<String> s, LAP root){
	for(String t : s){
		types.add(new LAPFeatureType(t,root));
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


public List<LAPFeatureType> getTypes() {
	return types;
}


public void setTypes(List<LAPFeatureType> types) {
	this.types = types;
}

}
