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
	Stroke stroke = new BasicStroke(2); 
	private int length;
	public int broadestPair;
	public static final int baseLineY = 100;
	public Baseline(int length){
		this.length = length;
		baseLine.setLine(0,316,length,316);
	}
	
	
	
	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		//updateLength(3);
		baseLine.setLine(0,100+(broadestPair/4)*getScaleY(), (int)(length*getScaleX()), 100+(broadestPair/4)*getScaleY());
		g2.setStroke(stroke);
		g2.draw(baseLine);
		g2.fill(baseLine);
		
		//Draw numbers
		int interval = getIntervalNumber();
		for(int i=0; i<length; i++){
			if(i%interval == 0){
				g2.drawString(Integer.toString(i), (int)(i*getScaleX()), (int)(100+(broadestPair/4)*getScaleY()+20));
			}
		}
		return DrawingResult.NORMAL;
	}
	
	private int getIntervalNumber(){
		double width = length*getScaleX();
		System.out.println(width + " getScaleX() " +getScaleX());
		if(getScaleX() < 0.2 && length > 1000) return 500;
		if(getScaleX() < 0.3) return 300;
		if(getScaleX() < 0.8) return 150;
		if(0.8 < getScaleX() && getScaleX() < 1.5) return 100;
		if(1.5 < getScaleX() && getScaleX() < 3.0) return 50;
		if(3.0 < getScaleX() && getScaleX() < 15) return 25;
		else return 10;
	}
}
