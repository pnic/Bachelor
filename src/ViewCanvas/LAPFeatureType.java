package ViewCanvas;


import Engine.LAP;

import com.clcbio.api.clc.plugins.editors.graphics.sequence.sidepanel.FeatureColorMap;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructure;
import com.clcbio.api.clc.datatypes.bioinformatics.structure.rnasecondary.RnaStructures;
import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;
import com.clcbio.api.free.datatypes.ClcObject;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.region.Region;
import com.clcbio.api.free.editors.framework.Editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.event.MouseInputListener;

public class LAPFeatureType extends ChildDrawingNode implements MouseInputListener{

	private String name;
	private List<LAPFeatureInterval> intervals;
	
	private long prevClick = 0;
	private long prevHover = 0;
	
	private JLabel label;
	
	private int x;
	private int typeOffset;
	
	private int width;
	private int height;
	
	private int lastX = -1000;
	
	private boolean changed = false;
	private boolean expanded;
	private boolean isSelected;
	
	private int expandedIntervals = 0;
	
	private LAP root;

	private boolean relevant;
	
	private Font myFont = new Font("Serif", Font.BOLD, 12);
	
	private boolean hover = false;
	
	private Color color;
	
	private boolean asLines;
	private boolean asArrows;
	
	
	private Rectangle2D stringContainer;
	private Rectangle2D drawnStringContainer;
	private Rectangle2D content;
	private boolean hasMouseListener;
	private boolean typeHover;
	
	public LAPFeatureType(String name, LAP root){
		this.name = name;
		intervals = new ArrayList<LAPFeatureInterval>();
	
		
		
		this.color = FeatureColorMap.getColor(name);
		
		this.asArrows = true;
		this.asLines = false;
		
		this.isSelected = true;
		this.root = root;
	}
	
	public LAPFeatureType(String name, int x, int offset, int w, int h, LAP root) {
		this.name = name;
		intervals = new ArrayList<LAPFeatureInterval>();
		this.asArrows = true;
		this.asLines = false;
		
		label = new JLabel(name);
		
		this.color = FeatureColorMap.getColor(name);
		
		this.root = root;
		
		this.isSelected = true;
		this.x = x;
		this.typeOffset = offset;
		this.width = w;
		this.height = h;
		expanded = false;
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

	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy)
	{
		if(relevant && root.getLv().isShowAnnotations() && isSelected){
			//if(changed || lastX != root.getXViewBounds()){
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.BLACK);
				if(!hasMouseListener){
					this.addMouseInputListener(this);
					hasMouseListener = true;
				}
				
				content = new Rectangle2D.Double(x, root.getBaseXAxis()+typeOffset, width*getScaleX(), height);
				g2.setColor(Color.WHITE);
				g2.fill(content);
				g2.setColor(Color.BLACK);
			//	g2.draw(new Line2D.Double(new Point2D.Double(x, root.getLv().getFeaturesLowerY()), new Point2D.Double(x + 200, root.getLv().getFeaturesUpperY())));
				
				/*if(hover){
					g2.setColor(Color.BLACK);
					g2.draw(content);
					
				}*/
		//g2.draw(content);
				
				g2.setColor(Color.LIGHT_GRAY);
				FontMetrics fm = g2.getFontMetrics();
				
				stringContainer = fm.getStringBounds(name, g2);
				drawnStringContainer = new Rectangle2D.Double(root.getXViewBounds()+root.getViewPaneWidth()/2,(root.getBaseXAxis()+typeOffset-5)-fm.getAscent(), (int)stringContainer.getWidth(), (int)stringContainer.getHeight());
				//g2.draw(drawnStringContainer);
				if(typeHover){
					g2.setColor(Color.BLUE);
				}
				g2.drawString(this.name, root.getXViewBounds()+(root.getViewPaneWidth()/2), root.getBaseXAxis()+typeOffset-5);
				
				//stringContainer = new Rectangle2D.Double(root.getXViewBounds()+root.getViewPaneWidth()/2,(root.getBaseXAxis()+typeOffset-5)-fm.getAscent(),(int)fm.)
				
				g2.setColor(Color.BLACK);
			//g2.fillRect(root.getXViewBounds(), root.getBaseXAxis()+typeOffset-10, 20, 10);
			//g2.setColor(Color.WHITE);
				g2.setFont(myFont);
				//g2.drawString("+", root.getXViewBounds()+5, root.getBaseXAxis()+typeOffset);
				
				//if(expanded){
					int inView = 1;
					//g2.drawString("Features:", root.getXViewBounds(), root.getBaseXAxis()+typeOffset+height+15);
					for(LAPFeatureInterval li : intervals){
						if(!(li.getEndPos()*getScaleX() < root.getXViewBounds() || li.getStartPos()*getScaleX() > root.getXViewBounds()+root.getViewPaneWidth()) && li.isExpanded()){
							//g2.setColor(li.getCol());
							
							g2.setColor(color);
							g2.drawString(li.getName()+", ", root.getXViewBounds()+(inView*70), root.getBaseXAxis()+typeOffset+height+15);
							g2.drawString(li.getStartPos() + " - " + li.getEndPos(), root.getXViewBounds()+(inView*70), root.getBaseXAxis()+typeOffset+height+35);
							inView+=1;
						}
					}
				//}
				changed = false;
				lastX = root.getXViewBounds();
			//}
		}
		return DrawingResult.NORMAL;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		if(arg0.getWhen() - prevClick < 500 ) return;
		if(drawnStringContainer.contains(new Point(arg0.getX()+root.getXViewBounds(),arg0.getY()+root.getYViewBounds()))){
			if(this.expanded){
				for(LAPFeatureInterval li : intervals){
					li.setExpanded(false);
					this.expanded = false;
					this.expandedIntervals = 0;
					root.setRelevantTypes();
					repaint();
				}
			} else {
				this.expanded = true;
				for(LAPFeatureInterval li : intervals){
					li.setExpanded(true);
					this.expanded = true;
					this.expandedIntervals = intervals.size();
					root.setRelevantTypes();
					repaint();
				}
			}
			
		}
		/*if(content.getMinY() < arg0.getY()+root.getYViewBounds() && content.getMaxY() > arg0.getY()+root.getYViewBounds()){
		this.expanded = !expanded;
		this.changed = true;
		root.setRelevantTypes();
		prevClick = arg0.getWhen();
		System.out.println("mouse clicked");
		repaint();
		}*/
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
		
		//root.setRelevantTypes();
		
		
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
	
		if(drawnStringContainer.contains(new Point(arg0.getX()+root.getXViewBounds(),arg0.getY()+root.getYViewBounds()))){
			this.typeHover = true;
			repaint();
			System.out.println("highlight string");
			return;
		}
		if(typeHover){
			typeHover = false;
			System.out.println("Don't highligt");
			repaint();
		}
		System.out.println(stringContainer.getX() + " is value stringContainer x " + stringContainer.getY() + " is value of stringContainer y");
		System.out.println(arg0.getX() + " arg0 x " + arg0.getY() + " Is arg0 y");
		
	/*	if(content.getMinY() < arg0.getY()+root.getYViewBounds() && content.getMaxY() > arg0.getY()+root.getYViewBounds()){
		this.hover = true;
		prevHover = arg0.getWhen();
		repaint();
		return;
		} if(hover){
			hover = false;

			repaint();
		}*/
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public int getLastX() {
		return lastX;
	}

	public void setLastX(int lastX) {
		this.lastX = lastX;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setAsLines(boolean asLines) {
		this.asLines = asLines;
	}

	public boolean asLines() {
		return asLines;
	}

	public void setAsArrows(boolean asArrows) {
		this.asArrows = asArrows;
	}

	public boolean asArrows() {
		return asArrows;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isHover() {
		return hover;
	}

	public void setHover(boolean hover) {
		this.hover = hover;
	}

	public void setExpandedIntervals(int expandedIntervals) {
		this.expandedIntervals = expandedIntervals;
	}

	public void incrementIntervals(){
		this.expandedIntervals++;
	}
	
	public void decrementIntervals(){
		this.expandedIntervals--;
	}
	
	public int expandedIntervals() {
		return expandedIntervals;
	}
	
}
