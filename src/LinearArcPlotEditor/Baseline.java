package LinearArcPlotEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;

public class Baseline extends ChildDrawingNode {
	
	Line2D baseLine = new Line2D.Double(0,0,300,0);
	Stroke stroke = new BasicStroke(2); 
	private int length;
	public int broadestPair;
	public static final int baseLineY = 100;
	private Sequence seq;
	private byte[] nrs;
	
	public Baseline(Sequence seq){
		this.length = seq.getLength();
		nrs = new byte[length];
		for(int i=0; i<length; i++){
			nrs[i] = seq.getSymbolIndexAt(i);
		}
		baseLine.setLine(0,316,length,316);
	}
	
	
	
	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		// If scaleX() is under 12, draw a line.
		if(getScaleX() < 11){
			baseLine.setLine(0,100+(broadestPair/4)*getScaleY(), (int)(length*getScaleX()), 100+(broadestPair/4)*getScaleY());
			g2.setStroke(stroke);
			g2.draw(baseLine);
			g2.fill(baseLine);
		}
		// If scaleX() is over 12, draw sequence instead.
		else{
			for(int i=0; i<length; i++){
					String s = getNucleotide(nrs[i]);
					g2.drawString(s, (int)(i*getScaleX()), (int)(112+(broadestPair/4)*getScaleY()));
			}
		}
		
		//Draw numbers
		int interval = getIntervalNumber();
		for(int i=0; i<length; i++){
			if(i%interval == 0){
				if(getScaleX()>11)g2.drawString(Integer.toString(i), (int)(i*getScaleX())-4, (int)(100+(broadestPair/4)*getScaleY()+24));
				else{	
					g2.drawString(Integer.toString(i), (int)(i*getScaleX()), (int)(100+(broadestPair/4)*getScaleY()+20));
				}
			}
		}
		return DrawingResult.NORMAL;
	}
	
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
	
	public String getNucleotide(byte letter){
		switch(letter){
			case 0: return "A";
			case 1: return "C";
			case 2: return "G";
			case 3: return "U";
			case 33: return "T";
		}
		
		return "0";
	}
}
