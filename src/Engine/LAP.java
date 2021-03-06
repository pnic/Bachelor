package Engine;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ViewCanvas.Arc;
import ViewCanvas.Baseline;
import ViewCanvas.CanvasChangedListener;
import ViewCanvas.FeatureInterval;
import ViewCanvas.FeatureType;
import ViewCanvas.RectangleOverNucleotide;
import ViewCanvas.SubSequenceRectangle;

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

import com.clcbio.api.free.datatypes.ClcObject;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.Alignment;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.region.Region;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.region.RegionTools;

import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.AlignmentSequenceIndexer;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.index.BasicIndexer;

import com.clcbio.api.free.datatypes.framework.history.HistoryEntry;
import com.clcbio.api.free.editors.framework.Editor;
import com.clcbio.api.free.framework.workspace.WorkspaceManager;
import com.clcbio.api.free.gui.dialog.ClcMessages;
import com.clcbio.api.free.workbench.WorkbenchManager;

public class LAP extends RootDrawingNode {
	private int [] pairings; 
	private float [] reliabilities; 
	private Alignment align;
	private int broadestPair;
	private Arc [] arcs; 
	private Baseline baseline;
	private boolean firstModification = true;
	private Sequence current_sequence;
	private ColorGradientModel gradmodel;
	private FeatureView lv;
	private LAPEditor editor;
	private Arc mouseOverArc;
	private int currentSequenceNumber;
	private int pairArrais[][];
	private float reliabilityArrays[][];
	private RectangleOverNucleotide overRect;
	private int prevXViewBounds = -999;
	private boolean isExporting = false;
	
	private WorkbenchManager manager;
	
	public LAP(Alignment align, ColorGradientModel gradmodel, String title, LAPEditor editor, WorkbenchManager man){

		this.align = align;
		this.current_sequence = align.getSequence(0);
		currentSequenceNumber = 0;
		this.editor = editor;
		this.gradmodel = gradmodel;
		this.manager = man;
		
		// initialize
		init();
		
		// set structure
		setStructure(RnaStructures.getStructures(current_sequence).getStructure(0));
		
		overRect = new RectangleOverNucleotide(this);
		this.addChild(overRect);
	}
	
	public void drawArcsFromSequence(int sequenceNumber){
		if(sequenceNumber >= 0 && sequenceNumber < align.getSequenceCount() && sequenceNumber != currentSequenceNumber){
			current_sequence = align.getSequence(sequenceNumber);
			currentSequenceNumber = sequenceNumber;
			setStructure(RnaStructures.getStructures(current_sequence).getStructure(0));	
		}
	}
	
	/*
	 * Sets the RNA structure visualized by the diagram. 
	 */
	private void setStructure(RnaStructure structure){
		removeArcs();
        removeTypes();
        
        //Our RNA structure
    	List<RnaStructureAnnotation> annotations = structure.getStructureAnnotations();
		RnaStructureAnnotation probAnnotation = annotations.get(0);

		pairings = structure.getPairing();
		reliabilities = new float[structure.getLength()];
		
		//Set reliability values
    	for(int i = 0; i<structure.getLength(); i++){
			reliabilities[i] = (float)probAnnotation.getValue(i);
		} 
		
		if(baseline == null){
			scaleX = 700.0/pairings.length;
			scaleY = scaleX;
		}
		
		//Array for converting pair sequence numbers to alignment numbers.
		int[] seqNumbers = new int[align.getLength()];
		
		//Array for converting reliability sequence numbers to alignment numbers. 
		float[] seqReliabillities = new float[align.getLength()];
			
		if(pairArrais[currentSequenceNumber][0] != -1){
			seqNumbers = pairArrais[currentSequenceNumber];
			seqReliabillities = reliabilityArrays[currentSequenceNumber];
		}
		else{
			setPairsAndReliabillities(seqNumbers, seqReliabillities);
		}

		int cnt = 0;
		int nr = getNumberOfPairs(pairings);
		arcs = new Arc[nr];
		for(int i=0; i<seqNumbers.length; i++){
			if(seqNumbers[i] > i){
				arcs[cnt] = new Arc(i, seqNumbers[i], seqReliabillities[i], this);
				arcs[cnt].broadestPair = broadestPair;
				addChild(arcs[cnt]);
				arcs[cnt].pairNumber = cnt;
				cnt = cnt+1;
			}
		}

		lv = new FeatureView(current_sequence,this);
		setRelevantTypes();
		
		if(editor.getAnnotationTypeView() != null){
			editor.updateAnnotationView(lv.getTypes());
		}
			
		if(baseline == null){
			baseline = new Baseline(align, this);
			addChild(baseline);
			
		}
			
		setColor();
		setSize();
		repaint();
	}
	
	private int getNumberOfPairs(int[] pairSequence){
		int nr=0;
		for(int i = 0;i<pairSequence.length; i++){
			if(pairSequence[i]>i){
				nr = nr+1;
			}
		}
		return nr;
	}
	
	/*
	 * Sets the correct array positions for pairs and reliabilities in respect to the currentSequence. 
	 */
	private void setPairsAndReliabillities(int[] seqNumbers, float[] seqReliabillities){
		BasicIndexer indexer = new AlignmentSequenceIndexer(align, currentSequenceNumber);
		pairArrais[currentSequenceNumber][0] = 0;
		for(int i=0; i<pairings.length; i++){
			if(pairings[i] > i){
				int last_pos = RegionTools.convertPosition(pairings[i], false, indexer);
				int first_pos = RegionTools.convertPosition(i, false, indexer);
				seqNumbers[last_pos] = first_pos;
				seqNumbers[first_pos] = last_pos;
				seqReliabillities[last_pos] = reliabilities[pairings[i]];
				seqReliabillities[first_pos] = reliabilities[i];
			}
		}
		// Caching
		pairArrais[currentSequenceNumber] = seqNumbers;
		reliabilityArrays[currentSequenceNumber] = seqReliabillities;
	}
	
	private void removeArcs(){
		if(arcs != null){
			for(Arc arc: arcs){
				this.removeChild(arc);
			}
		}
	}
	
	private void init(){
		// Set scaleX and scaleY for zooming. 
		setMaxScaleX(12);
		setMaxScaleY(12);
		setMinScaleX(0.05);
		setMinScaleY(0.05);
		setMinScaleRatio(1.0);
		setMaxScaleRatio(1.0);
		pairArrais = new int[align.getSequenceCount()][align.getLength()];
		reliabilityArrays = new float[align.getSequenceCount()][align.getLength()];
		for(int i=0; i < align.getSequenceCount(); i++){
			pairArrais[i][0] = -1;
		}

		CanvasChangedListener canvasChanged = new CanvasChangedListener(this);
		this.addChild(canvasChanged);
		SubSequenceRectangle subSeqRect = new SubSequenceRectangle(this);
		this.addChild(subSeqRect);
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
		for(FeatureType l : lv.getTypes()){
			removeChild(l);
			for(FeatureInterval li : l.getIntervals()){
				removeChild(li);
			}
		}

		lv.buildRelevantTypes();
		for(FeatureType l : lv.getRelevantTypes()){
			addChild(l);
			for(FeatureInterval li : l.getIntervals()){
				addChild(li);
			}
		}
	}
		
	public void setColor(){
		int cnt2=0;
		for(int j=0; j<pairings.length; j++){
			if(pairings[j] > j){
				if(arcs[cnt2] == null){
					System.out.println("Arc er null");
				}

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
		
		if(align != null && baseline != null){
			setSize(0, (align.getLength()*getScaleX())+50, 0, 100+(getBaseXAxis())+baseline.getHeight()+lv.getFeaturesLowerY());
			
		}
		else{
			if(align != null){
				setSize(0, (align.getLength()*getScaleX())+50, 0, lv.getFeaturesLowerY()+100+(getBaseXAxis())+(align.getSequenceCount()+1)*14);
			}
			else{
				setSize(0,1200*getScaleX(),0,getBaseXAxis());	
			}
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
		//setRelevantTypes();
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
		List<RnaStructureAnnotation> annotations = RnaStructures.getStructures(current_sequence).getStructure(0).getStructureAnnotations();
		RnaStructureAnnotation probAnnotation = annotations.get(0);
		
		// old structures
		RnaStructures manager = RnaStructures.getStructures(current_sequence);
		manager.getStructures();

		new ArrayList<RnaStructure>();
		List<RnaStructureElement> elements = RnaStructureTools.createStructureElements(newPairings);
		
		// create rnaStructure with new pairings
		RnaStructure structureout = new RnaStructure("PPfold prediction", pairings, elements, new Date(), 1);
		
		// add the old annotations - these will stay the same. 
		structureout.addStructureAnnotation(probAnnotation);
		
		current_sequence.startUndoAndEventBlock("Changing arcs");	

			RnaStructures.setStructures(current_sequence, new RnaStructures(structureout));
			HistoryEntry histEntry = new HistoryEntry("Changed pair positions", editor.getManager());
			histEntry.addParameterEntry("new first", ""+new_p1);
			histEntry.addParameterEntry("new second", ""+new_p2);
			histEntry.addParameterEntry("pair number", ""+pairNumber);
			
			histEntry.addReferredObject(current_sequence);
			current_sequence.addHistory(histEntry);
			current_sequence.endUndoAndEventBlock();
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
		return current_sequence;
	}

	public FeatureView getLv() {
		return lv;
	}

	public void setLv(FeatureView lv) {
		this.lv = lv;
	}

	  private double[] toDoubleArray(float[] input){
	    	double[] output = new double[input.length];
	    	for(int i = 0; i<input.length; i++){
	    		output[i] = input[i];
	    	}
	    	return output; 
	    }
	  
	/*
	 * This returns the Y position of the Base X-axis used for drawing. 
	 */
	public int getBaseXAxis(){
		return (int)(250+(broadestPair/4)*getScaleY());
	}
		
	public int GetLDHeight(){
		return 500;
	}


	public WorkbenchManager getManager() {
		return manager;
	}


	public void setManager(WorkbenchManager manager) {
		this.manager = manager;
	}
	
	public void showSub(Arc arc){
		showSub(arc.p1,arc.p2);
		
	}
	
	public void showSub(int start, int end){
			Sequence subSeq = current_sequence.getSubsequence(new Region(start,end));
			
			subSeq.setName("Extract " + start + " - " + end);
			
			getManager().getWorkspaceManager().getCurrentObjectsContainer().setCurrentObjects(new ClcObject[]{subSeq});
			getManager().getWorkspaceManager().getCurrentObjectsContainer().setSelected(true);
		
	    	try{
			    Editor editor = getManager().getEditorManager().getEditorClassById("com.clcbio.plugins.rnasecondary.editor.RnaSecondaryStructureEditor").newInstance();
				getManager().getWorkspaceManager().getCurrentWorkspace().edit(new ClcObject[] { subSeq }, editor);
			} catch(Exception e) {
				ClcMessages.showError(null, "Planar graph plugin was not found", "A problem occured trying to view the 2D planar graph plugin. The plugin either does not exist in your workbench or you do not have sufficient access rights to view it.");
			}
			
			getManager().getActionManager().getAction("MFoldAction").actionPerformed(null);
			
		}

	public Alignment getAlign() {
		return align;
	}

	public void setAlign(Alignment align) {
		this.align = align;
	}	
	
	public void removeTypes(){
		if(lv!= null){
			for(FeatureType l : lv.getTypes()){
				removeChild(l);
				for(FeatureInterval li : l.getIntervals()){
					removeChild(li);
				}
			}
		}
	}	
	
	public int getCurrentSequenceNumber(){
		return currentSequenceNumber;
	}

	public void setPrevXViewBounds(int prevXViewBounds) {
		this.prevXViewBounds = prevXViewBounds;
	}

	public int getPrevXViewBounds() {
		return prevXViewBounds;
	}
	
	public void setNucleotideRectangleIndex(int first_indexNumber, int second_indexNumber){
		overRect.setIndexNumbers(first_indexNumber, second_indexNumber);
	}
	
	public void drawNucleotideRectangle(boolean draw){
		overRect.setDrawRect(draw);
	}

	public int[] getPairings() {
		return pairings;
	}

	public void setPairings(int[] pairings) {
		this.pairings = pairings;
	}

	public int[][] getPairArrais() {
		return pairArrais;
	}

	public void setPairArrais(int[][] pairArrais) {
		this.pairArrais = pairArrais;
	}

	public void setCurrentSequenceNumber(int currentSequenceNumber) {
		this.currentSequenceNumber = currentSequenceNumber;
	}
	
	public void setExporting(boolean exp){
		this.isExporting = exp;
	}
	
	public boolean getExporting(){
		return this.isExporting;
	}
}
