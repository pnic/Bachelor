package LinearArcPlotEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class Arc extends ChildDrawingNode implements MouseInputListener{

	private Arc2D arc;
	public int broadestPair;
	private ArcMouseListener mouseListener;
	int p1;
	int p2;
	int newp1;
	int newp2;
	private LAP root;
	
	private Color color;
	
	public Arc(int p1, int p2, double seqLength, double reliability, LAP root){
		this.p1=p1;
		this.p2=p2;
		mouseListener = new ArcMouseListener();
		this.root = root;
		this.addMouseInputListener(this);
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
		return root.getBaseXAxis()-(getArcHeight()/2);
	}
	
	public DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, 
										DrawingLayer drawinglayer, 
										double minx, double maxx, 
										double miny, double maxy){
		update();
		// If we are close, make arcs thicker.
		if(getScaleX() > 8) g2.setStroke(new BasicStroke(2));
		if(getScaleX() > 11) g2.setStroke(new BasicStroke(3));
		
		g2.setColor(color);
		g2.draw(arc);
		g2.drawRect(getCenterX(), getCenterY(), 2, 2);
		return DrawingResult.NORMAL;
	}
	
	public void setColor(Color input){
		color = input;
	}
	
	private int getCenterX(){
		return newp1+((newp2-newp1)/2);
	}
	
	private int getCenterY(){
		return getArcYPosition()+(getArcHeight()/2);
	}
	
	private boolean touchesArc(int x, int y){
		int first = (int)(Math.pow(x-getCenterX(), 2))/(int)Math.pow((getCenterY()-getArcYPosition()), 2);
		int second = (int)(Math.pow(y, 2))/(int)Math.pow((newp2-getCenterX()), 2);
		System.out.println("x: "+ x + " y: " +y + " first: " + first + " second " + second + " GCX " + getCenterX() + " GAY " + getArcYPosition() + " GCY " + getCenterY() + " np2 " + newp2);
		
		if(first+second ==1) return true;
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		//System.out.println("x pos: " + (arg0.getX()-10) + " y: " + (arg0.getY()) + " scale: " + scaleX);
		if(touchesArc(arg0.getX()-10, arg0.getY())) System.out.println("Yes");
	}
	
}
