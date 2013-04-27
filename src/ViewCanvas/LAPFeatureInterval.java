package ViewCanvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.event.MouseInputListener;

import Engine.LAP;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class LAPFeatureInterval extends ChildDrawingNode implements Comparable, MouseInputListener{

	Line2D startLine;
	Line2D endLine;
	private int startPos;
	private int endPos;
	private int offset;
	private Polygon arrowPolygon;
	//private int[] xpoints;
	//private int[] ypoints;
	private long prevClick = 0;
	//Colors for differentiation of intervals
	private float red;
	private float green;
	private float blue;
	private Color col;


	private String name;

	private LAPFeatureType type;
	private LAP root;
	
	private boolean lines;
	private boolean arrows;
	private boolean hover;
	private long prevHover;
	private boolean hasMouseListener;
	private boolean expanded;
	private boolean changed;

	public LAPFeatureInterval(String name, int startPos, int endPos, int offset, LAP root, LAPFeatureType type){
	
		this.lines = false;
		this.arrows = true;
		this.name = name;
		this.startPos = startPos;
		this.endPos = endPos;
		this.offset = offset;
		this.startLine = new Line2D.Double(startPos*getScaleX(), offset+30, startPos*getScaleX(), offset+15);
		this.endLine = new Line2D.Double(endPos*getScaleX(),offset+30,endPos*getScaleX(), offset);
		this.root = root;		
		this.type = type;
		
		calcColors(type.getWidth());
	}

	private void calcColors(float normalizeBound) {
		this.red = (this.getEndPos()/normalizeBound)*255;
		this.green = (this.getStartPos()/normalizeBound)*255;
		this.blue = 0;
		col = new Color((int)red, (int)green,(int) blue);
	}

	private void update(){

	}

	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy)
	{
		
		if(root.getLv().isShowAnnotations() && type.isSelected()){
			
			if(!hasMouseListener){
				this.addMouseInputListener(this);
				hasMouseListener = true;
			}
			
			if(type.isRelevant()){
				//	if(type.isChanged() || type.getLastX() != root.getXViewBounds()){
				g2.setStroke(new BasicStroke(1));
				g2.setColor(col);

				int lineStart = (int)(startPos*getScaleX());
				int lineEnd = (int)(endPos*getScaleX());
				if(type.asLines()){					

					g2.draw(new Line2D.Double(lineStart, root.getBaseXAxis()+offset+type.getHeight()/2, lineEnd, root.getBaseXAxis()+offset+type.getHeight()/2));
					startLine = new Line2D.Double(lineStart, root.getBaseXAxis()+offset+type.getHeight(), lineStart, root.getBaseXAxis()+offset+type.getHeight()/2);
					endLine = new Line2D.Double(lineEnd,root.getBaseXAxis()+offset+(type.getHeight()/2),lineEnd,root.getBaseXAxis()+offset);
					g2.draw(startLine);
					g2.draw(endLine);
				} else if(type.asArrows()) {
					int points = 7;
					
					int x1 = lineStart; //Lowerleft
					
					//start of arrow
					int x2 = (lineEnd-lineStart < 20) ? lineStart : lineEnd-20;
					
					int x3 = x2;
					
					int x4 = lineEnd; //Tip of arrow
					
					//Back to start of arrow
					int x5 = x3;
					int x6 = x5;
					
					int x7 = x1; //upperleft
					
					int y1 = root.getBaseXAxis()+offset+type.getHeight()-type.getHeight()/4; //Lower left
					
					int y2 = y1; //Middle start of bottom of arrow
					
					int y3 = root.getBaseXAxis()+offset+type.getHeight(); //Bottom of start of arrow
					
					int y4 = root.getBaseXAxis()+offset+type.getHeight()/2; //Tip of arrow
					
					int y5 = root.getBaseXAxis()+offset; //Back to top of start of arrow
					
					int y6 = root.getBaseXAxis()+offset+type.getHeight()/4; //Back to middle start of top of arrow
					
					int y7 = y6; //upper left
					
					int[] xpoints = {x1,x2,x3,x4,x5,x6,x7};
					int[] ypoints = {y1,y2,y3,y4,y5,y6,y7};
					if(hover){
					Color c = new Color(type.getColor().getRed(),type.getColor().getGreen(),type.getColor().getBlue(),140);
					g2.setColor(c);
					} else {
						g2.setColor(type.getColor());						
					}
					arrowPolygon = new Polygon(xpoints, ypoints, points);
					
					g2.fillPolygon(arrowPolygon);
					g2.setColor(Color.BLACK);
					g2.drawPolygon(arrowPolygon);
				}
			}
		}
		//}
		return DrawingResult.NORMAL;
	}

	public int getStartPos(){
		return startPos;
	}

	public int getEndPos(){
		return endPos;
	}

	public int getOffset(){
		return offset;
	}

	public void setOffset(int o){
		this.offset=o;
	}

	public void addToOffset(int a){
		this.offset += a;
	}

	@Override
	public int compareTo(Object o) {
		LAPFeatureInterval li = (LAPFeatureInterval)o;
		return this.getStartPos() < li.getStartPos() ? 1 : 0;

	}

	public LAPFeatureType getType() {
		return type;
	}

	public void setType(LAPFeatureType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getCol() {
		return col;
	}

	public void setCol(Color col) {
		this.col = col;
	}

	public void repaintInterval(){
		this.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		if(arg0.getWhen() - prevClick < 500 ) return;
		if(arrowPolygon.contains(new Point(arg0.getX()+root.getXViewBounds(),arg0.getY()+root.getYViewBounds()))){
		if(!this.expanded){
			this.expanded = true;
			type.incrementIntervals();
			if(type.expandedIntervals() == 1){
				type.setExpanded(true);
				root.setRelevantTypes();
			}
		} else {
			this.expanded = false;
			type.decrementIntervals();
			if(type.expandedIntervals() == 0){
				type.setExpanded(false);
				root.setRelevantTypes();
			}
		}
		this.changed = true;
		prevClick = arg0.getWhen();
		repaint();
		}		
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
		
		if(arrowPolygon.contains(new Point(arg0.getX()+root.getXViewBounds(),arg0.getY()+root.getYViewBounds()))){
			this.hover = true;
			prevHover = arg0.getWhen();
			repaint();
			return;
		}
		if(hover){
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
}
