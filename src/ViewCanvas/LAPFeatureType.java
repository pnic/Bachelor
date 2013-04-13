package ViewCanvas;


import Engine.LAP;

import com.clcbio.api.base.io.ClcFileFilter;
import com.clcbio.api.base.io.ExportPlugin;
import com.clcbio.api.base.persistence.PersistenceException;
import com.clcbio.api.base.process.Activity;
import com.clcbio.api.base.util.iterator.MovableIntegerIterator;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.sidepanel.FeatureColorMap;
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
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
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

import javax.swing.event.MouseInputListener;

public class LAPFeatureType extends ChildDrawingNode implements MouseInputListener{

	private String name;
	private List<LAPFeatureInterval> intervals;
	
	private long prevClick = 0;
	private long prevHover = 0;
	
	private int x;
	private int typeOffset;
	
	private int width;
	private int height;
	
	private int lastX = -1000;
	
	private boolean changed = false;
	private boolean expanded;
	private boolean isSelected;
	
	private LAP root;

	private boolean relevant;
	
	private Font myFont = new Font("Serif", Font.BOLD, 12);
	
	private boolean hover = false;
	
	private Color color;
	
	private boolean asLines;
	private boolean asArrows;
	
	private Rectangle2D content;
	
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

	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy)
	{
		if(relevant){
			//if(changed || lastX != root.getXViewBounds()){
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.BLACK);
				this.addMouseInputListener(this);
				content = new Rectangle2D.Double(x, root.getBaseXAxis()+typeOffset, width*getScaleX(), height);
				g2.setColor(Color.WHITE);
				g2.fill(content);
				
				/*if(hover){
					g2.setColor(Color.BLACK);
					g2.draw(content);
					
				}*/
		//g2.draw(content);
				g2.setColor(Color.LIGHT_GRAY);
				g2.drawString(this.name, root.getXViewBounds()+(root.getViewPaneWidth()/2), root.getBaseXAxis()+typeOffset-5);
				g2.setColor(Color.BLACK);
			//g2.fillRect(root.getXViewBounds(), root.getBaseXAxis()+typeOffset-10, 20, 10);
			//g2.setColor(Color.WHITE);
				g2.setFont(myFont);
				//g2.drawString("+", root.getXViewBounds()+5, root.getBaseXAxis()+typeOffset);
				
					
				
				if(expanded){
					int inView = 1;
					g2.drawString("Features:", root.getXViewBounds(), root.getBaseXAxis()+typeOffset+height+15);
					for(LAPFeatureInterval li : intervals){
						if(!(li.getEndPos()*getScaleX() < root.getXViewBounds() || li.getStartPos()*getScaleX() > root.getXViewBounds()+root.getViewPaneWidth())){
							g2.setColor(li.getCol());
							g2.drawString(li.getName()+", ", root.getXViewBounds()+(inView*70), root.getBaseXAxis()+typeOffset+height+15);
							g2.drawString(li.getStartPos() + " - " + li.getEndPos(), root.getXViewBounds()+(inView*70), root.getBaseXAxis()+typeOffset+height+35);
							inView+=1;
						}
					}
				}
				changed = false;
				lastX = root.getXViewBounds();
			//}
		}
		return DrawingResult.NORMAL;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		if(arg0.getWhen() - prevClick < 500 ) return;
		if(content.getMinY() < arg0.getY()+root.getYViewBounds() && content.getMaxY() > arg0.getY()+root.getYViewBounds()){
		this.expanded = !expanded;
		this.changed = true;
		root.setRelevantTypes();
		prevClick = arg0.getWhen();
		repaint();
		}
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
		// TODO Auto-generated method stub
	/*	if(lastX != root.getXViewBounds()){ for repainting when zooming 
			root.setRelevantTypes();
			repaint();
		}*/
		if(content.getMinY() < arg0.getY()+root.getYViewBounds() && content.getMaxY() > arg0.getY()+root.getYViewBounds()){
		this.hover = true;
		prevHover = arg0.getWhen();
		repaint();
		return;
		} if(hover){
			hover = false;
			repaint();
		}
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
	
}
