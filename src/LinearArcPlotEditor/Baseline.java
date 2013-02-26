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

public class Baseline extends ChildDrawingNode {
	
	Line2D baseLine = new Line2D.Double(0,0,300,0);
	Stroke stroke = new BasicStroke(2); 
	private int length;

	private Sequence seq;
	private byte[] nrs;
	private Font font;
	private boolean isBold;
	private int fontSize;

	private LAP root;
	/*
	 * A baseline represents the x-axis of the linearArcDiagram.
	 */
	public Baseline(Sequence seq, LAP root){
		this.length = seq.getLength();
		nrs = new byte[length];
		for(int i=0; i<length; i++){
			nrs[i] = seq.getSymbolIndexAt(i);
		}
		
		fontSize = 13;
		font = new Font("SansSerif", Font.BOLD, fontSize);
		this.root = root;
	}
	
	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		// If scaleX() is under 12, draw a line.
		if(getScaleX() < 11){
			baseLine.setLine(0,root.getBaseXAxis(), (int)(length*getScaleX()), root.getBaseXAxis());
			g2.setStroke(stroke);
			g2.draw(baseLine);
			g2.fill(baseLine);
		}
		// If scaleX() is over 12, draw sequence instead.
		else{
			g2.setFont(font);
			for(int i=0; i<length; i++){
					String s = getNucleotide(nrs[i]);
					int stringWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), s);
					g2.drawString(s, (int)(i*getScaleX())-stringWidth/2, root.getBaseXAxis()+12);
			}
		}
		
		//Draw numbers and number lines
		int interval = getIntervalNumber();
		for(int i=0; i<length; i++){
			if(i%interval == 0){
				int stringWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), Integer.toString(i));
				int stringHeight = g2.getFontMetrics().getHeight();
				int stringx_pos = (int)(i*getScaleX()-stringWidth/2);
				int lineX_pos = (int)(i*getScaleX());
				
				if(getScaleX() < 11){
					g2.drawString(Integer.toString(i), stringx_pos, root.getBaseXAxis()+stringHeight+14);
					g2.drawLine(lineX_pos, root.getBaseXAxis()+5, lineX_pos, root.getBaseXAxis()+stringHeight);
				}
				else{
					g2.drawString(Integer.toString(i), stringx_pos, root.getBaseXAxis()+stringHeight+25);
					g2.drawLine(lineX_pos, root.getBaseXAxis()+stringHeight+2, lineX_pos, root.getBaseXAxis()+stringHeight+10);
				}
			}
		}
		return DrawingResult.NORMAL;
	}
	
	/*
	 * Returns the interval for when an index number should be shown. 
	 */
	private int getIntervalNumber(){
		double width = length*getScaleX();
		System.out.println(width + " getScaleX() " +getScaleX());
		if(getScaleX() < 0.2 && length > 1000) return 500;
		if(getScaleX() < 0.3) return 300;
		if(getScaleX() < 0.8) return 150;
		if(0.8 < getScaleX() && getScaleX() < 1.5) return 100;
		if(1.5 < getScaleX() && getScaleX() < 3.0) return 50;
		if(3.0 < getScaleX() && getScaleX() < 11) return 25;
		else return 10;
	}
	
	/*
	 * Returns the nucleotide represented as a string. 
	 */
	private String getNucleotide(byte letter){
		switch(letter){
			case 0: return "A";
			case 1: return "C";
			case 2: return "G";
			case 3: return "U";
			case 33: return "T";
		}
		
		return "0";
	}
	
	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public boolean isBold() {
		return isBold;
	}

	public void setBold(boolean isBold) {
		this.isBold = isBold;
		updateFont();
	}
	
	public void updateFont(){
		System.out.println(isBold);
		if(isBold){
			font = new Font(font.getName(), Font.BOLD, fontSize);
		}
		else{
			font = new Font(font.getName(), Font.PLAIN, fontSize);
		}
		repaint();
	}
}
