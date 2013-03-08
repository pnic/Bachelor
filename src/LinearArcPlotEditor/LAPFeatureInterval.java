package LinearArcPlotEditor;

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

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;
import com.clcbio.api.clc.graphics.framework.RootDrawingNode;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;

public class LAPFeatureInterval extends ChildDrawingNode implements Comparable{

	Line2D line;
	private int startPos;
	private int endPos;
	private int offset;
	
	private LAP root;
	
	public LAPFeatureInterval(int startPos, int endPos, int offset, LAP root){
		this.startPos = startPos;
		this.endPos = endPos;
		this.offset = offset;
		System.out.println("offset: " + offset + " \n");
		this.line = new Line2D.Double(startPos, offset, endPos*getScaleX(), offset);
		this.root = root;
	}
	
	private void update(){
		
	}
	
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy)
	{
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.BLACK);
		
		int lineStart = (int)(startPos*getScaleX());
		int lineEnd = (int)(endPos*getScaleX());
		
		line = new Line2D.Double(lineStart, root.getBaseXAxis()+offset, lineEnd, root.getBaseXAxis()+offset);
		g2.draw(line);
		
		
		
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

	@Override
	public int compareTo(Object o) {
		LAPFeatureInterval li = (LAPFeatureInterval)o;
		return this.getStartPos() < li.getStartPos() ? 1 : 0;
		
	}
	
}
