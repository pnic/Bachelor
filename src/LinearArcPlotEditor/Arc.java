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
	int p1;
	int p2;
	int newp1;
	int newp2;
	private LAP root;
	private Color color;
	private boolean showAnnotation;
	private int oldViewX=0;
	private int oldViewY=0;
	private boolean drawRect;
	private int mouse_x;
	private int mouse_y;
	private boolean mouseListenerSat;
	
	public Arc(int p1, int p2, double seqLength, double reliability, LAP root){
		this.p1=p1;
		this.p2=p2;
		this.root = root;
		mouseListenerSat = false;
	}
	
	private void update(){
		newp1 = (int)(p1*getScaleX());
		newp2 = (int) (p2*getScaleX());
		
		int y_position = getArcYPosition();
		int height = getArcHeight();
		int width = newp2-newp1;

		arc = new Arc2D.Double(newp1,y_position,width,height,0,180,Arc2D.OPEN);
	}
	
	
	private int getArcHeight(){
		return (newp2-newp1)/2;
	}
	
	private int getArcYPosition(){
		return root.getBaseXAxis()-(getArcHeight()/2);
	}
	
	public DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
			update();
			// If we are close, make arcs thicker.
			if(isArcInScreen()){
				if(!mouseListenerSat){
					this.addMouseInputListener(this);
					mouseListenerSat = true;
				}
				if(getScaleX() > 8) g2.setStroke(new BasicStroke(2));
				if(getScaleX() > 11) g2.setStroke(new BasicStroke(3));
		
				g2.setColor(color);
				g2.draw(arc);
				oldViewX = root.getXViewBounds();
				oldViewY = root.getYViewBounds();
				
				if(showAnnotation){
					g2.drawRect(mouse_x, mouse_y, 200, 100);
				}
				
			}
			else{
				if(mouseListenerSat){
					this.removeMouseInputListener(this);
					mouseListenerSat = false;
				}
				
			}
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
	
	/*
	 * Returns whether or not the x and y parameters touches the arc. 
	 */
	private boolean touchesArc(int x_pos, int y){
		int a = (newp2-newp1)/2;
		int b = getArcHeight()/2;
		int mouse_x = x_pos-(a+newp1);
		int mouse_y = (b+getArcYPosition()-y);
		double contains = (((Math.pow(mouse_x, 2))/Math.pow(a, 2))+((Math.pow(mouse_y, 2))/Math.pow(b, 2)));
		if(contains > 0.93 && contains < 1.07 && mouse_y > 0){
			return true;
		}
		return false;
	}
	
	private boolean isArcInScreen(){
		int viewPX = root.getXViewBounds();
		int viewPY = root.getYViewBounds();
		int viewWidth = root.getViewPaneWidth();
		int viewHeight = root.getViewPaneHeight();
		
		for(int i=viewPX; i<(viewPX+viewWidth); i++){
			if(touchesArc(i,viewPY) || (newp1 > viewPX && (newp1 < (viewPX+(viewPX+viewWidth))))) return true;
		}
		for(int j=viewPY; j<(viewPY+viewHeight); j++){
			if(touchesArc(viewPX, j)) return true;
		}
		
		return false;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if(isArcInScreen()){
			int x_pos = arg0.getX()+root.getXViewBounds();
			int y_pos = arg0.getY()+root.getYViewBounds();
			if(touchesArc(x_pos, y_pos)){
				showAnnotation = true;
				mouse_x = x_pos;
				mouse_y = y_pos;
				drawRect = true;
				System.out.println("repaints true");
				repaint();
			}
			else{
				if(oldViewX == root.getXViewBounds() && oldViewY == root.getYViewBounds() && drawRect == false){
					
				}
				else{
					System.out.println("repaints false");
					showAnnotation = false;
					//repaint();
					oldViewX = root.getXViewBounds();
					oldViewY = root.getYViewBounds();
					drawRect = false;
				}
			}
		}
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
	
}
