package LinearArcPlotEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class Arc extends ChildDrawingNode {

	private Arc2D arc;
	private double seqLength;
	private double reliability;
	private Stroke stroke = new BasicStroke(3);
	public int broadestPair;
	private ArcMouseListener mouseListener;
	int p1;
	int p2;
	int newp1;
	int newp2;
	
	private Color color;
	
	public Arc(int p1, int p2, double seqLength, double reliability){
		this.seqLength = seqLength;
		this.reliability = reliability;
		this.p1=p1;
		this.p2=p2;
		mouseListener = new ArcMouseListener();
		this.addMouseInputListener(mouseListener);
		arc = new Arc2D.Double(p1,(100+broadestPair*getScaleY())-((p2-p1)/2),(p2-p1),(p2-p1),0,180,Arc2D.OPEN);
	}
	
	private void update(){
		newp1 = (int)(p1*getScaleX());
		newp2 = (int) (p2*getScaleX());
		
		int y_position = getArcYPosition();
		int height = getArcHeight();
		int width = newp2-newp1;
		// mouse listener
		
		//mouseListener.setXs(newp1, newp2);
		//mouseListener.setHeight(height);
		//mouseListener.setY(y_position);
		
		arc = new Arc2D.Double(newp1,y_position,width,height,0,180,Arc2D.OPEN);
		mouseListener.setArc(arc);
	}
	
	
	private int getArcHeight(){
		return (newp2-newp1)/2;
	}
	
	private int getArcYPosition(){
		return (int)(100+(broadestPair/4)*getScaleY())-(getArcHeight()/2);
	}
	
	public DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, 
										DrawingLayer drawinglayer, 
										double minx, double maxx, 
										double miny, double maxy){
		update();
		//g2.setStroke(stroke);
		g2.setColor(color);
		g2.draw(arc);
		//Useless Comment
		return DrawingResult.NORMAL;
	}
	
	public void setColor(Color input){
		color = input;
	}
	
}
