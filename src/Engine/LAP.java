package Engine;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ViewCanvas.Arc;
import ViewCanvas.Baseline;
import ViewCanvas.LAPFeatureInterval;
import ViewCanvas.LAPFeatureType;

import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructure;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructureElement;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructureTools;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructures;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.annotation.RnaStructureAnnotation;
import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.clc.graphics.framework.ClcCanvas;
import com.clcbio.api.clc.graphics.framework.ClcScrollPane;
import com.clcbio.api.clc.graphics.framework.RootDrawingNode;
import com.clcbio.api.clc.graphics.framework.ViewBounds;
import com.clcbio.api.free.datatypes.ClcStackListener;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.framework.history.History;
import com.clcbio.api.free.datatypes.framework.history.HistoryEntry;

public class LAP extends RootDrawingNode {
	private int [] pairings; 
	private float [] reliabilities; 
	private int broadestPair;
	private Arc [] arcs; 
	private Baseline baseline;
	private boolean firstModification = true;
	private Sequence seq;
	private ColorGradientModel gradmodel;
	private LAPFeatureView lv;
	private LAPEditor editor;
	private Arc mouseOverArc;
	
	public LAP(final Sequence seq, ColorGradientModel gradmodel, String title, LAPEditor editor){
		this.seq = seq;
		this.editor = editor;
		this.gradmodel = gradmodel;
		
		// initialize
		init();
		
		// set structure
		setStructure(RnaStructures.getStructures(seq).getStructure(0));
		
		baseline = new Baseline(seq, this);
		addChild(baseline);
		
		setColor();
		setSize();
	}
	
	
	/*
	 * Sets the RNA structure visualized by the diagram. 
	 */
	private void setStructure(RnaStructure structure){
		pairings = structure.getPairing();
		reliabilities = new float[structure.getLength()];
		
		scaleX = 700.0/pairings.length;
		scaleY = scaleX;
		
		int nr=0;
		for(int i = 0;i<pairings.length; i++){
			if(pairings[i]>i){
				nr = nr+1;
				if(pairings[i]-i > broadestPair){
					broadestPair = (pairings[i]-i);
				}
			}
		}
		
		//Generate arcs
		int cnt = 0;
			arcs = new Arc[nr];
			for(int i = 0; i<pairings.length; i++){
				if(pairings[i]>i){
					arcs[cnt] = new Arc(i,pairings[i], reliabilities[i], this);
					arcs[cnt].broadestPair = broadestPair;
					addChild(arcs[cnt]);
					arcs[cnt].pairNumber = cnt;
					cnt = cnt+1;
				}
		}
		
		//Our Rna structure
    	List<RnaStructureAnnotation> annotations = structure.getStructureAnnotations();
		RnaStructureAnnotation probAnnotation = annotations.get(0);
		
		//Set reliability values
    	for(int i = 0; i<structure.getLength(); i++){
			//get reliability of structure at that position
			reliabilities[i] = (float)probAnnotation.getValue(i);
		}
    	
		lv = new LAPFeatureView(seq,this);
		setRelevantTypes();
	}
	
	private void init(){
		// Set scaleX and scaleY for zooming. 
		setMaxScaleX(12);
		setMaxScaleY(12);
		setMinScaleX(0.05);
		setMinScaleY(0.05);
		setMinScaleRatio(1.0);
		setMaxScaleRatio(1.0);
	}
	
    /*
     * Show the rasmol colors on a sequence.
     * @show = show yes/no
     * @ground: 1 = foreground, 2 = background. 
     */
	public void showRasmolColors(boolean show, int ground){
		baseline.showRasmolColors(show, ground);
	}
	
	public void setRelevantTypes(){
		for(LAPFeatureType l : lv.getTypes()){
			removeChild(l);
			for(LAPFeatureInterval li : l.getIntervals()){
				removeChild(li);
			}
		}

		lv.buildRelevantTypes();
		for(LAPFeatureType l : lv.getRelevantTypes()){
			addChild(l);
			for(LAPFeatureInterval li : l.getIntervals()){
				addChild(li);
			}
		}
	}
		
	public void setColor(){
		int cnt2=0;
		for(int j=0; j<pairings.length; j++){
			if(pairings[j] > j){
				arcs[cnt2].setColor(gradmodel.getColor(reliabilities[j]));
				cnt2++;
			}
		}
	}
	
	/*
	 * Returns the y position of the visible screen in contrast to the entire canvas. 
	 */
	public int getYViewBounds(){
		ClcScrollPane pane = getCanvas().getScrollPane();
		List<ViewBounds> pV = pane.getVerticalViewBounds();
		return (int) pV.get(0).getPosition();
	}
	
	/*
	 * Returns the x position of the visible screen in contrast to the entire canvas. 
	 */
	public int getXViewBounds(){
		ClcScrollPane pane = getCanvas().getScrollPane();
		List<ViewBounds> pH = pane.getHorizontalViewBounds();
		return (int)pH.get(0).getPosition();
	}
	
	/*
	 * Returns the width of the screen.  
	 */
	public int getViewPaneWidth(){
		ClcScrollPane pane = getCanvas().getScrollPane();
		return pane.getWidth();
	}
	
	public int getViewPaneHeight(){
		ClcScrollPane pane = getCanvas().getScrollPane();
		return pane.getHeight();
	}
	
	@Override
	protected void setSize() {
		ClcCanvas cv = getCanvas();
		if(cv != null){
			ClcScrollPane pane = cv.getScrollPane();
			if(pane != null){
				List<ViewBounds> pp = pane.getHorizontalViewBounds();
				pp.get(0);
				
				pane.getVerticalViewBounds();
				pane.getVisibleRect();
			}	
		}
		
		if(pairings != null){
			setSize(0, pairings.length*getScaleX()+50, 0, 600+(broadestPair/4)*getScaleY());
		}
		else{
			setSize(0,1200*getScaleX(),0,600);
		}
	}
	
	/*
	 * Checks if an arc can show mouseOver, this is to ensure only 1 arc is shown at a time. 
	 * It not only checks, but also performs an operation on arc that makes it possible for LAP to control which arc is on mouseover. 
	 */
	public boolean canArcShowMouseOver(Arc arc){
		if(mouseOverArc == null){
			this.mouseOverArc = arc;
			mouseOverArc.showAnnotation(true);
			mouseOverArc.drawRect(true);
			return true;
		}
		
		double newArcValue = arc.getContainValue()-1;
		double currentArcValue = mouseOverArc.getContainValue()-1;
		
		if(Math.abs(newArcValue) < Math.abs(currentArcValue)) {
			mouseOverArc.showAnnotation(false);
			mouseOverArc = arc;
			mouseOverArc.showAnnotation(true);
			mouseOverArc.drawRect(true);
			return true;
		}
		if(arc == mouseOverArc) {
			mouseOverArc.showAnnotation(true);
			mouseOverArc.drawRect(true);
			return true;
		}
		arc.showAnnotation(false);
		return false;
	}
	
	public void refresh2(){
		setColor();
		setRelevantTypes();
	}
	
	/*
	 * Checks if it is a valid change of positions for a pair. If it is, it also makes the changes in pairings. 
	 */
	public boolean canChangeArc(int old_p1, int new_p1, int old_p2, int new_p2, int pairNumber){
		boolean first_same = old_p1 == new_p1;
		boolean second_same = old_p2 == new_p2; 
		boolean returner = true;
		
		for(int i=0; i<pairings.length; i++){
			if(pairings[i] > i){
				if(first_same && second_same){
					return true;
				}
				if(first_same && new_p1 == i){
					continue;
				}
				if(second_same && new_p2 == pairings[i]){
					continue;
				}
				if((new_p1 > i && new_p2 < pairings[i]) || new_p1 < i && pairings[i] < new_p2){
					continue;
				}
				if(new_p1 > pairings[i]) continue;
				if(new_p2 < i) continue;
				returner = false;
			}
		}
		//Make changes
		if(returner == true){
			if(firstModification){
				//changeArc(old_p1, old_p1, old_p2, old_p2, pairNumber, true, true);
				//firstModification = false;
			}
			changeArc(old_p1, new_p1, old_p2, new_p2, pairNumber, true, true);
		}
		
		return returner;
		
	}
	
	private void changeArc(int old_p1, int new_p1, int old_p2, int new_p2, int pairNumber, boolean changePairings, boolean canUndo){
		
		int[] newPairings = pairings;
		newPairings[old_p1] = -1;
		newPairings[old_p2] = -1;
		newPairings[new_p1] = new_p2;
		newPairings[new_p2] = new_p1;
		
		// structure annotations
		List<RnaStructureAnnotation> annotations = RnaStructures.getStructures(seq).getStructure(0).getStructureAnnotations();
		RnaStructureAnnotation probAnnotation = annotations.get(0);
		
		// old structures
		RnaStructures manager = RnaStructures.getStructures(seq);
		manager.getStructures();

		new ArrayList<RnaStructure>();
		List<RnaStructureElement> elements = RnaStructureTools.createStructureElements(newPairings);
		
		// create rnaStructure with new pairings
		RnaStructure structureout = new RnaStructure("PPfold prediction", pairings, elements, new Date(), 1);
		
		// add the old annotations - these will stay the same. 
		structureout.addStructureAnnotation(probAnnotation);
		
		seq.startUndoAndEventBlock("Changing arcs");	

			RnaStructures.setStructures(seq, new RnaStructures(structureout));
			HistoryEntry histEntry = new HistoryEntry("Changed pair positions", editor.getManager());
			histEntry.addParameterEntry("new first", ""+new_p1);
			histEntry.addParameterEntry("new second", ""+new_p2);
			histEntry.addParameterEntry("pair number", ""+pairNumber);
			
			histEntry.addReferredObject(seq);
			seq.addHistory(histEntry);
			seq.endUndoAndEventBlock();
	}
	
	public Baseline getBaseline() {
		return baseline;
	}

	public LAPEditor getEditor() {
		return editor;
	}

	public void setEditor(LAPEditor editor) {
		this.editor = editor;
	}
	
	public Sequence getSequence(){
		return seq;
	}

	public LAPFeatureView getLv() {
		return lv;
	}

	public void setLv(LAPFeatureView lv) {
		this.lv = lv;
	}

	  private double[] toDoubleArray(float[] input){
	    	double[] output = new double[input.length];
	    	for(int i = 0; i<input.length; i++){
	    		output[i] = (double)input[i];
	    	}
	    	return output; 
	    }
	  
	/*
	 * This returns the Y position of the Base X-axis used for drawing. 
	 */
	public int getBaseXAxis(){
		return (int)(100+(broadestPair/4)*getScaleY());
	}
		
	public int GetLDHeight(){
		return 500;
	}
		
}
