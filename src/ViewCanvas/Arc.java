package ViewCanvas;

/*
 * David Korczynski
 * dgeo@itu.dk
 * May, 2013. 
 */
import java.awt.AlphaComposite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;

import java.util.Timer;

import javax.swing.event.MouseInputListener;

import Engine.*;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alphabet.AlphabetTools;
import com.clcbio.api.free.editors.framework.MouseMode;

public class Arc extends ChildDrawingNode implements MouseInputListener{

	private Arc2D arc;
	public int broadestPair;
	public int p1;
	public int p2;
	int newp1;
	int newp2;
	private LAP root;
	private Color color;
	private boolean showAnnotation;
	private int oldViewX=0;
	private int oldViewY=0;
	private boolean drawRect;
	private boolean mouseListenerSat;
	private int arc_width;
	private int arc_height;
	private int arc_y_position;
	private double contains;
	private double mouse_limit;
	private double reliability;
	public int pairNumber;
	public boolean firstModification = true;
	
	
	//Strokes
	private BasicStroke backArcStroke; 
	private BasicStroke normalArcStroke;
	private BasicStroke overArcStroke;
	
	
	public Arc(int p1, int p2, double reliability, LAP root){
		this.p1=p1;
		this.p2=p2;
		this.root = root;
		this.reliability = reliability;
		mouseListenerSat = false;
	}
	
	private void update(){
		arc_y_position = getArcYPosition(newp1, newp2);
		arc_height = getArcHeight(newp1, newp2);
		arc_width = newp2-newp1;

		if(arc_width > 1000)					{ mouse_limit = 0.0005; }
		if(arc_width < 1000 && arc_width > 500)	{ mouse_limit = 0.01; 	}
		if(arc_width < 500 && arc_width > 150)	{ mouse_limit = 0.015; 	}
		if(arc_width < 150 && arc_width > 100)	{ mouse_limit = 0.04; 	}	
		if(arc_width < 100)						{ mouse_limit = 0.06; 	}

		arc = new Arc2D.Double(newp1,arc_y_position,arc_width,arc_height,0,180,Arc2D.OPEN);
	}
	
	private int getArcHeight(int x1, int x2){
		return (x2-x1)/2;
	}
	
	private int getArcYPosition(int x1, int x2){
		return root.getBaseXAxis()-(getArcHeight(x1,x2)/2);
	}
	
	public void updateStrokes(){
		backArcStroke = new BasicStroke(3);
		normalArcStroke = new BasicStroke(1);
		overArcStroke = new BasicStroke(3);
		
		// If we are close, make arcs thicker.
		if(root.getScaleX() > 8) {
			normalArcStroke = new BasicStroke(2);
			backArcStroke = new BasicStroke(4);
			overArcStroke = new BasicStroke(6);
		}
		if(root.getScaleX() > 11) {
			normalArcStroke = new BasicStroke(3);
			backArcStroke = new BasicStroke(5);
			overArcStroke = new BasicStroke(7);
		}
	}
	

	public DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
			newp1 = (int)(p1*root.getScaleX());
			newp2 = (int) (p2*root.getScaleX());
			
			update();
			// Check if arc is in screen. 
			if(isArcInScreen() && arc_width > 5){
				updateStrokes();
				if(!mouseListenerSat && (arc_width > 100 || getScaleX() > 8)){
					this.addMouseInputListener(this);
					mouseListenerSat = true;
				}
				//If mouseover. 
				if(showAnnotation){
					Color cg = new Color(130, 130, 255);
					g2.setColor(cg);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.setStroke(overArcStroke);
					g2.draw(arc);
				}
				//If not mouseover, we draw a background for each arc to make it pretty. 
				else if(!showAnnotation && getScaleX() > 8){
					g2.setColor(Color.black);
					g2.setStroke(backArcStroke);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
					g2.draw(arc);
				}
				//Draw the arc
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				g2.setColor(color);
				g2.setStroke(normalArcStroke);
				g2.draw(arc);
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

	
	/*
	 * Returns whether or not the x and y parameters touches the arc. 
	 * This method is not used, but I keep it for a bit, because its easier to read - before optimization.
	 */
	private boolean touchesArc(int x_pos, int y){
		int a = (newp2-newp1)/2;
		int b = getArcHeight(newp1, newp2)/2;
		int mouse_x = x_pos-(a+newp1);
		int mouse_y = (b+getArcYPosition(newp1, newp2)-y);
		contains = (((Math.pow(mouse_x, 2))/Math.pow(a, 2))+((Math.pow(mouse_y, 2))/Math.pow(b, 2)));
		if(contains > 0.9 && contains < 1.1 && mouse_y > 0){
			return true;
		}
		return false;
	}
	
	/*
	 * Optimized solution of touchersArc().
	 */
	private boolean touchesArc2(int x_pos, int y){
		int a = (newp2-newp1)/2;
		int b = getArcHeight(newp1, newp2)/2;
		int mouse_x = (x_pos-(a+newp1))-5;
		int x_limit = mouse_x+10;
		int mouse_y = ((b+getArcYPosition(newp1, newp2)-y))-5;
		int y_limit = mouse_y+10;
		
		for(int i = mouse_x; i < x_limit; i++){
			for(int j= mouse_y; j < y_limit; j++){
				contains = (((Math.pow(i, 2))/Math.pow(a, 2))+((Math.pow(j, 2))/Math.pow(b, 2)));

				if(contains > 1-mouse_limit && contains < 1+mouse_limit && mouse_y > 0){
					return true;
				}
			}
		}
		return false;
	}
	
	
	/*
	 * Returns if the arc is inside the visible screen. 
	 */
	private boolean isArcInScreen(){
		if(getScaleX() < 2) return true;
		
		int viewPX = root.getXViewBounds();
		int viewPY = root.getYViewBounds();
		int viewWidth = root.getViewPaneWidth();
		int viewHeight = root.getViewPaneHeight();
		
		if(newp1 > viewPX && newp1 < (viewPX+viewWidth)) return true;
		if(newp2 > viewPX && newp2 < (viewPX+viewWidth)) return true;
		
		for(int i=viewPX; i<(viewPX+viewWidth); i += 2){
			if(touchesArc(i,viewPY)) return true;
		}
		for(int j=viewPY; j<(viewPY+viewHeight); j += 2){
			if(touchesArc(viewPX, j)) return true;
		}
		
		return false;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if(root.getEditor().getMouseMode() == MouseMode.SELECT_MODE){
		
		if(isArcInScreen()){
			if(arg0.getY() < (root.getBaseXAxis() + 5)){
			int x_pos = arg0.getX()+root.getXViewBounds();
			int y_pos = arg0.getY()+root.getYViewBounds();
			if(touchesArc2(x_pos, y_pos)){
					boolean rt = root.canArcShowMouseOver(this);
					if(rt){
						root.setNucleotideRectangleIndex(p1, p2);
						repaint();
						root.getEditor().setToolTip(this, arg0.getX()+10, arg0.getY()+10, 
								"pair at (" +p1 + ","+p2 + ") with PPFold reliability " + reliability);
					}
			}
			else{
				if(oldViewX == root.getXViewBounds() && oldViewY == root.getYViewBounds() && drawRect == false){
					
				}
				else{
					showAnnotation = false;
					oldViewX = root.getXViewBounds();
					oldViewY = root.getYViewBounds();
					drawRect = false;

					root.getEditor().removeToolTip(this);
					root.getEditor().setStatusInformation(this, "");
					root.drawNucleotideRectangle(false);
					repaint();
				}
			}
		}
		}
		}
	}
	
	public boolean canChangeArc(int new_point1, int new_points2){
		if(root.canChangeArc(p1, new_point1, p2, new_points2, pairNumber)) return true;
		return false;
	}
	
	public void showAnnotation(boolean show){
		this.showAnnotation = show;
	}
	
	public void drawRect(boolean rect){
		this.drawRect = rect;
	}
	
	public double getContainValue(){
		return contains;
	}

	@Override
	public boolean equals(Object obj){
		if(obj == null || obj.getClass() != this.getClass()){
			return false;
		}
		
		Arc ac = (Arc) obj;
		return (p1 == ac.p1 && p2 == ac.p2);
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int res = 1;
		res = res * prime;
		res *= p1;
		res *= p2;
		
		return res;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(root.getEditor().getMouseMode() == MouseMode.SELECT_MODE){
		if(showAnnotation){
			EditArcDialog et = new EditArcDialog(this);
			et.setVisible(true);
		}
		showAnnotation = false;
	}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
	@Override
	public void mouseDragged(MouseEvent arg0) {}

	public LAP getRoot() {
		return root;
	}

	public void setRoot(LAP root) {
		this.root = root;
	}
	
}
