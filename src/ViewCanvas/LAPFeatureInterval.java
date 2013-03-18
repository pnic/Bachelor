package ViewCanvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import Engine.LAP;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;
import com.clcbio.api.clc.graphics.framework.RootDrawingNode;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;

public class LAPFeatureInterval extends ChildDrawingNode implements Comparable{

	Line2D startLine;
	Line2D endLine;
	private int startPos;
	private int endPos;
	private int offset;
	
	//Colors for differentiation of intervals
	private float red;
	private float green;
	private float blue;
	private Color col;
	
	
	private String name;
		
	private LAPFeatureType type;
	private LAP root;
	
	public LAPFeatureInterval(String name, int startPos, int endPos, int offset, LAP root, LAPFeatureType type){
		this.name = name;
		this.startPos = startPos;
		this.endPos = endPos;
		this.offset = offset;
		System.out.println("offset: " + offset + " \n");
		this.startLine = new Line2D.Double(startPos*getScaleX(), offset+30, startPos*getScaleX(), offset+15);
		this.endLine = new Line2D.Double(endPos*getScaleX(),offset+30,endPos*getScaleX(), offset);
		this.root = root;		
		this.type = type;
		
		calcColors((float)type.getWidth());
	}
	
	private void calcColors(float normalizeBound) {
		this.red = ((float)this.getEndPos()/normalizeBound)*255;
		this.green = ((float)this.getStartPos()/normalizeBound)*255;
		this.blue = 0;
		System.out.println("red: " + (int)red + " green: " + (int)green);
		col = new Color((int)red, (int)green,(int) blue);
	}

	private void update(){
		
	}
	
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy)
	{
		if(type.isRelevant()){
		//	if(type.isChanged() || type.getLastX() != root.getXViewBounds()){
				g2.setStroke(new BasicStroke(1));
				g2.setColor(col);
		
				int lineStart = (int)(startPos*getScaleX());
				int lineEnd = (int)(endPos*getScaleX());
		
				g2.draw(new Line2D.Double(lineStart, root.getBaseXAxis()+offset+type.getHeight()/2, lineEnd, root.getBaseXAxis()+offset+type.getHeight()/2));
				startLine = new Line2D.Double(lineStart, root.getBaseXAxis()+offset+type.getHeight(), lineStart, root.getBaseXAxis()+offset+type.getHeight()/2);
				endLine = new Line2D.Double(lineEnd,root.getBaseXAxis()+offset+(type.getHeight()/2),lineEnd,root.getBaseXAxis()+offset);
				g2.draw(startLine);
				g2.draw(endLine);
				
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
	
}
