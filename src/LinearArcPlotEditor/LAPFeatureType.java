package LinearArcPlotEditor;


import com.clcbio.api.base.io.ClcFileFilter;
import com.clcbio.api.base.io.ExportPlugin;
import com.clcbio.api.base.persistence.PersistenceException;
import com.clcbio.api.base.process.Activity;
import com.clcbio.api.base.util.iterator.MovableIntegerIterator;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructures;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.annotation.RnaStructureAnnotation;
import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LAPFeatureType extends ChildDrawingNode{

	private String name;
	private List<LAPFeatureInterval> intervals;
	
	private int x;
	private int typeOffset;
	
	private int width;
	private int height;
	
	private LAP root;

	private boolean relevant;
	
	private Rectangle2D content;
	
	public LAPFeatureType(String name, LAP root){
		this.name = name;
		intervals = new ArrayList<LAPFeatureInterval>();
	
		this.root = root;
	}
	
	public LAPFeatureType(String name, int x, int offset, int w, int h, LAP root) {
		this.name = name;
		intervals = new ArrayList<LAPFeatureInterval>();
		
		this.root = root;
		
		this.x = x;
		this.typeOffset = offset;
		this.width = w;
		this.height = h;
	}
	
	public String getName(){
		return name;
	}
	
	public void addInterval(LAPFeatureInterval f){
		intervals.add(f);
	}
	
	public List<LAPFeatureInterval> getIntervals(){
		return intervals;
	}

	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy)
	{
		if(relevant){
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.BLACK);
		
		content = new Rectangle2D.Double(x, root.getBaseXAxis()+typeOffset, width*getScaleX(), height);
			g2.setColor(Color.LIGHT_GRAY);
			g2.fill(content);
		//g2.draw(content);
			g2.drawString(this.name, (x+((width-x)/2))*(int)getScaleX(), root.getBaseXAxis()+typeOffset-5);
		}
		return DrawingResult.NORMAL;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getTypeOffset() {
		return typeOffset;
	}

	public void setTypeOffset(int typeOffset) {
		this.typeOffset = typeOffset;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isRelevant() {
		return relevant;
	}

	public void setRelevant(boolean relevant) {
		this.relevant = relevant;
	}
	
}
