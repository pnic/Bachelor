package ViewCanvas;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import Engine.LAP;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class LAPFeature extends ChildDrawingNode{

	
	String name;
	private int startPos;
	private int endPos;
	private int offset;
	private List<FeatureInterval> intervals;
	
	private LAP root;
	
	
	public LAPFeature(String name, int startPos, int endPos, int offset, LAP root){
		this.name = name;
		this.startPos = startPos;
		this.endPos = endPos;
		this.offset = offset;
		intervals = new ArrayList<FeatureInterval>();		
		this.root = root;
	}
	
	public void addFeatureInterval(FeatureInterval l){
		intervals.add(l);
	}
	
	public void removeFeatureInterval(FeatureInterval l){
		intervals.remove(l);
	}
	
	public int getYAxis(){
		return offset+root.getBaseXAxis();
	}
	
	public List<FeatureInterval> getIntervals(){
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
