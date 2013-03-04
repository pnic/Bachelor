package LinearArcPlotEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;
import com.clcbio.api.clc.graphics.framework.RootDrawingNode;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.feature.Feature;

public class LAPFeature extends ChildDrawingNode{

	
	String name;
	private int startPos;
	private int endPos;
	private int offset;
	private List<LAPFeatureInterval> intervals;
	
	private LAP root;
	
	
	public LAPFeature(String name, int startPos, int endPos, int offset, LAP root){
		this.name = name;
		this.startPos = startPos;
		this.endPos = endPos;
		this.offset = offset;
		intervals = new ArrayList<LAPFeatureInterval>();		
		this.root = root;
	}
	
	public void addFeatureInterval(LAPFeatureInterval l){
		intervals.add(l);
	}
	
	public void removeFeatureInterval(LAPFeatureInterval l){
		intervals.remove(l);
	}
	
	public int getYAxis(){
		return offset+root.getBaseXAxis();
	}
	
	public List<LAPFeatureInterval> getIntervals(){
		return intervals;
	}
	
	private void update(){
		
	}
	
	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy)
	{
		g2.drawString(this.name, -108, root.getBaseXAxis()+offset);
		return DrawingResult.NORMAL;
		
	}
	
	
}
