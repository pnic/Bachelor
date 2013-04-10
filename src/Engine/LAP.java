package Engine;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import ViewCanvas.Arc;
import ViewCanvas.Baseline;
import ViewCanvas.ColorGradientRectangle;
import ViewCanvas.LAPFeatureInterval;
import ViewCanvas.LAPFeatureType;
import ViewCanvas.TitleText;

import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructure;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructureElement;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructureTools;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructures;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.annotation.RnaStructureAnnotation;
import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.clc.graphics.DrawingContext;
import com.clcbio.api.clc.graphics.components.ColorGradientManager;
import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.ClcCanvas;
import com.clcbio.api.clc.graphics.framework.ClcScrollPane;
import com.clcbio.api.clc.graphics.framework.RootDrawingNode;
import com.clcbio.api.clc.graphics.framework.ViewBounds;
import com.clcbio.api.free.datatypes.ClcObject;
import com.clcbio.api.free.datatypes.ClcPair;
import com.clcbio.api.free.datatypes.ClcStackListener;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.framework.history.History;
import com.clcbio.api.free.datatypes.framework.history.HistoryEntry;
import com.clcbio.api.free.datatypes.framework.history.ParameterEntry;
import com.clcbio.api.free.datatypes.framework.listener.ObjectEvent;
import com.clcbio.api.free.datatypes.framework.listener.ObjectListener;
import com.clcbio.api.free.gui.components.ObjectMoveable;
import com.clcbio.api.free.gui.dialog.ClcMessages;
import com.clcbio.api.free.gui.focus.ClcFocusPanel;
import com.clcbio.api.free.gui.focus.ClcFocusScrollPane;

public class LAP extends RootDrawingNode {
	private int [] pairings; 
	private float [] reliabilities; 
	private int seqLength;
	//Used to specify height of size. 
	private int broadestPair;
	private TitleText titleText;
	private Arc [] arcs; 
	private Baseline baseline;
	private boolean firstModification = true;

	private Sequence seq;
	private ColorGradientRectangle colorGradientRectangle;
	private LAPFeatureView lv;
	
	private LAPEditor editor;
	

	private Arc mouseOverArc;
	private boolean satSize = false;;
	
	public LAP(final Sequence seq, ColorGradientModel gradmodel, String title, LAPEditor editor){
		this.seq = seq;
		seqLength = seq.getLength();
		this.editor = editor;

		
		seq.getHistory().addListener(new ClcStackListener(){
			@Override
			public void update(List arg0) {
				System.out.println("Update");
				History hist = seq.getHistory();
				List<HistoryEntry> entries = hist.getEntries();
				for(int i=0; i<entries.size(); i++){
					System.out.println(entries.get(i).getSimpleParameterString());
					System.out.println("-----------------------");
				}
				//setNewStructure(RnaStructures.getStructures(seq).getStructure(0));	
				printPairings();
			}
			
		});
		// initialize
		init();
		
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
		if(arcs==null){
			arcs = new Arc[nr];
			for(int i = 0; i<pairings.length; i++){
				if(pairings[i]>i){
					arcs[cnt] = new Arc(i,pairings[i],seqLength, reliabilities[i], this);
					arcs[cnt].broadestPair = broadestPair;
					addChild(arcs[cnt]);
					arcs[cnt].pairNumber = cnt;
					cnt = cnt+1;
				}
			}
		}

		lv = new LAPFeatureView(seq,this);
		setRelevantTypes();

	
		baseline = new Baseline(seq, this);
		addChild(baseline);
		
		
		//titleText = new TitleText(TextForTitle);
		//addChild(titleText);
		
		
		//addChild(colorGradientRectangle); in the infobox now
		setColors(gradmodel);
		setSize();
		
		

		

		seq.startNoUndoBlock();
		HistoryEntry histEntry = new HistoryEntry("Changed pair positions", editor.getManager());
		histEntry.addParameterEntry("Loaded into linear arcplot");
		seq.addHistory(histEntry);
		histEntry.addReferredObject(seq);
		seq.endNoUndoBlock();
		seq.startNoUndoBlock();
		histEntry = new HistoryEntry("Changed pair positions", editor.getManager());
		histEntry.addParameterEntry("creating first stabil");
		seq.addHistory(histEntry);
		histEntry.addReferredObject(seq);
		seq.endNoUndoBlock();
	}
	
	private void init(){
		// Set scaleX and scaleY for zooming. 
		setMaxScaleX(12);
		setMaxScaleY(12);
		setMinScaleX(0.05);
		setMinScaleY(0.05);
		setMinScaleRatio(1.0);
		setMaxScaleRatio(1.0);
		
		//setup pairing and reliabilities.
		pairings = RnaStructures.getStructures(seq).getStructure(0).getPairing();
		RnaStructures.getStructures(seq).getStructure(0);
		
		reliabilities = new float[seq.getLength()];
		//Our Rna structure
    	List<RnaStructureAnnotation> annotations = RnaStructures.getStructures(seq).getStructure(0).getStructureAnnotations();
		RnaStructureAnnotation probAnnotation = annotations.get(0);
		
		//Set reliability values
    	for(int i = 0; i<seq.getLength(); i++){
			//get reliability of structure at that position
			reliabilities[i] = (float)probAnnotation.getValue(i);
		}
    	colorGradientRectangle = new ColorGradientRectangle(probAnnotation.getName(),probAnnotation.getFixedMin(),probAnnotation.getFixedMax(), editor.getInfo());
    	
    	this.seqLength = seq.getLength();
	}
	
	private void setNewStructure(RnaStructure structure){
		for(int i=0; i<arcs.length; i++){
			this.removeChild(arcs[i]);
		}
		
		arcs = null;
		
		pairings = structure.getPairing();
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
		if(arcs==null){
			arcs = new Arc[nr];
			for(int i = 0; i<pairings.length; i++){
				if(pairings[i]>i){
					arcs[cnt] = new Arc(i,pairings[i],seqLength, reliabilities[i], this);
					arcs[cnt].broadestPair = broadestPair;
					addChild(arcs[cnt]);
					arcs[cnt].pairNumber = cnt;
					cnt = cnt+1;
				}
			}
		}
		
		repaint();
	}
	
	private void printPairings(){
		int[] newPairs = RnaStructures.getStructures(seq).getStructure(0).getPairing();
		for(int i=0; i<newPairs.length; i++){
			System.out.print(" " + i);
		}
		System.out.println("");
		for(int n=0; n<newPairs.length; n++){
			System.out.print(" " + newPairs[n]);
		}
		System.out.println("");
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
			System.out.println(l.getName());
			for(LAPFeatureInterval li : l.getIntervals()){
				addChild(li);
			}
		}
	}
	
	public void setTitle(String title){
		titleText.setTitle(title);
	}
	
	public void setColors(ColorGradientModel gradmodel){  
		int cnt = 0; 
		for(int i = 0; i<pairings.length; i++){
			if(pairings[i]>i){
				arcs[cnt].setColor(gradmodel.getColor(reliabilities[i]));
				cnt = cnt+1;
			}
		}
		colorGradientRectangle.setColors(gradmodel);
		editor.getInfo().setCgr(colorGradientRectangle);
	}

	
	public boolean canArcShow(){
		return false;
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
				System.out.println("pane width " + pane.getViewWidth());
				List<ViewBounds> pp = pane.getHorizontalViewBounds();
				ViewBounds bb = pp.get(0);
				
				List<ViewBounds> pV = pane.getVerticalViewBounds();
				System.out.println("Viewbounds position x: " + bb.getPosition() + " y: " +  pV.get(0).getPosition());
				System.out.println("Viewbounds position x: " + pane.getViewWidth()/getScaleX());
				Rectangle rg = pane.getVisibleRect();
				System.out.println("rectangle x: " + rg.x + " rg.width " + rg.width + " center x " + rg.getCenterX());
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

	public void refresh(ColorGradientModel colorGradientModel) {
		setColors(colorGradientModel);
		System.out.println("refreshing");
		setRelevantTypes();		
	}
	

	public void setBaseLineText(boolean isBold, int textSize, String fontName){
		baseline.setBold(isBold);
		baseline.setFontSize(textSize);
		baseline.setFontName(fontName);
		baseline.updateFont();
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
				System.out.println("i " + i + " pairings[i] " + pairings[i] + " oldp1 " + old_p1 + " p1 " + new_p1 + " oldp2 " + old_p2 + " p2 " + new_p2);
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
		List<RnaStructure> oldStructures = manager.getStructures();

		System.out.println("oldstructures length: " + oldStructures.size());

		// new structures
		List<RnaStructure> newStructures = new ArrayList<RnaStructure>();
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

	public ColorGradientRectangle getColorGradientRectangle() {
		return colorGradientRectangle;
	}

	public void setColorGradientRectangle(
			ColorGradientRectangle colorGradientRectangle) {
		this.colorGradientRectangle = colorGradientRectangle;
	}
	
	public Baseline getBaseline() {
		return baseline;
	}

	public void setBaseline(Baseline baseline) {
		this.baseline = baseline;
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
