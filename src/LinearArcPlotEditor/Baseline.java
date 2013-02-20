package LinearArcPlotEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class Baseline extends ChildDrawingNode {
	
	Line2D baseLine = new Line2D.Double(0,0,300,0);
	Stroke stroke = new BasicStroke(5); 
	
	private void updateLength(int l){
		//baseLine.setLine(0, 0, l, 0);
	
	}
	
	public Baseline(int length){
		baseLine.setLine(-600,-316,-600+length,-316);
	}
	
	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		//updateLength(3);
	
		g2.setStroke(stroke);
		g2.draw(baseLine);
		g2.fill(baseLine);
		return DrawingResult.NORMAL;
	}
}
