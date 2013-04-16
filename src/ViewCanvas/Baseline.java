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
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.Alignment;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.AlignmentSequenceIndexer;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alphabet.AlphabetTools;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.index.BasicIndexer;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.region.RegionTools;

public class Baseline extends ChildDrawingNode {
	
	Line2D baseLine = new Line2D.Double(0,0,300,0);
	Stroke stroke = new BasicStroke(2); 
	private int length;
	private int gapHeight;
	private int stringHeight;
	private Font font;
	private Font numbersFont;
	private boolean isBold;
	private int fontSize;
	private String fontName;
	private LAP root;
	private boolean drawNumbers; 
	private boolean rasmolFront;
	private boolean rasmolBack;
	private int startingIndexNumber;
	private Alignment alignment;
	private String[][] nucleotideSequences;
	private int[] sequenceLengths;
	
	/*
	 * A baseline represents the x-axis of the linearArcDiagram.
	 */
	public Baseline(Alignment alignment, LAP root){
		this.alignment = alignment; 
		init();
		Sequence seq = alignment.getSequence(0);
		this.length = alignment.getLength();
		
		drawNumbers = true;
		fontSize = 14;
		stringHeight = 15;
		font = new Font("SansSerif", Font.BOLD, fontSize);
		numbersFont = new Font("SansSerif", Font.PLAIN, fontSize);
		this.root = root;
	}
	
	private void init(){
		nucleotideSequences = new String[alignment.getSequenceCount()][alignment.getLength()];
		sequenceLengths = new int[alignment.getSequenceCount()];
		for(int i=0; i<alignment.getSequenceCount(); i++){
			sequenceLengths[i] = alignment.getSequence(i).getLength();
		}
		String s;
		
		for(int j=0; j<alignment.getSequenceCount(); j++){
			BasicIndexer indexer = new AlignmentSequenceIndexer(alignment, j);
			Sequence seq = alignment.getSequence(j);
				for(int i=0; i<alignment.getLength(); i++){
				if(indexer.getSequencePosition(i) < 0){
					s = "-";
				}
				else{
					s = AlphabetTools.getRnaAlphabet().getSymbol(seq.getSymbolIndexAt(indexer.getSequencePosition(i))).getShortName();	
				}
				nucleotideSequences[j][i] = s;
			}
		}
	}
	
	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		// If scaleX() is under 12, draw a line.
		int viewPX = root.getXViewBounds();
		int viewPWidth = root.getViewPaneWidth();
		
		System.out.println("BASELINE PRINT GET SCALEX " + getScaleX());
		System.out.println("Root getsacleX " + root.getScaleX());
		
		if(font != null) {
			g2.setFont(font);
		}
		
		if(root.getScaleX() < 11){
			baseLine.setLine(0,root.getBaseXAxis(), (int)(length*root.getScaleX()), root.getBaseXAxis());
			
			g2.setStroke(stroke);
			g2.draw(baseLine);
			g2.fill(baseLine);
		}
		// If scaleX() is over 12, draw sequence instead.
		else{
			stringHeight = g2.getFontMetrics().getHeight();
			int stringWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), "U");
			for(int j=0; j<alignment.getSequenceCount(); j++){
				for(int i=0; i<alignment.getLength(); i++){	
					// Only if number is in the screen. 
					if(viewPX < (i*root.getScaleX()) && i*root.getScaleX() < (viewPX+viewPWidth)){
						//String s = AlphabetTools.getRnaAlphabet().getSymbol(nrs[i]).getShortName();
						String s = nucleotideSequences[j][i];
						g2.setColor(getRasmolColor(s));
						
						if(rasmolBack && s != "-") g2.fillRect((int)(i*root.getScaleX())-(stringWidth/2), root.getBaseXAxis()+4+(j*stringHeight), stringWidth, stringHeight-2);
						if(!rasmolFront) g2.setColor(new Color(0,0,0));
						g2.drawString(s, (int)(i*root.getScaleX())-stringWidth/2, root.getBaseXAxis()+stringHeight+(j*stringHeight));
					}
				}	
			}
		}
		
		g2.setColor(new Color(0,0,0));
		//Draw numbers and number lines
		if(drawNumbers){
			int interval = getIntervalNumber();
			if(numbersFont != null) {
				g2.setFont(numbersFont);
			}
			
			
			// Y positions to calcualte where number and line should be. 
			int firstGap = 6;
			// Even though its 0 - keep it, it will make further changes easier. 
			int secondGap = 0;
			int intervalHeight = 10;
			
			stringHeight = g2.getFontMetrics().getHeight();
			gapHeight = firstGap + secondGap + intervalHeight;
			int stringWidth;
			
			for(int i=0; i<length; i++){
				if(i%interval == 0){
					stringWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), Integer.toString(i-startingIndexNumber));
					int stringx_pos = (int)(i*root.getScaleX()-stringWidth/2);
					int lineX_pos = (int)(i*root.getScaleX());
					
					if(root.getScaleX() < 11){
						g2.drawString(Integer.toString(i-startingIndexNumber), stringx_pos, root.getBaseXAxis()+stringHeight+firstGap + secondGap + intervalHeight);
						g2.drawLine(lineX_pos, root.getBaseXAxis()+firstGap, lineX_pos, root.getBaseXAxis()+firstGap + intervalHeight);
					}
					// If scale is above 11, we need more space since nucleotides are shown instead of just a line. 
					else{
						g2.drawString(Integer.toString(i-startingIndexNumber), stringx_pos, root.getBaseXAxis()+((alignment.getSequenceCount()+1)*stringHeight)+firstGap + secondGap + intervalHeight);
						g2.drawLine(lineX_pos, root.getBaseXAxis()+(stringHeight*alignment.getSequenceCount())+firstGap, lineX_pos, root.getBaseXAxis()+(stringHeight*alignment.getSequenceCount())+firstGap+intervalHeight);
					}
				}
			}
			for(int i=0; i<sequenceLengths.length; i++){
				int seqLength = sequenceLengths[i];
				g2.drawString(Integer.toString(seqLength), (int)((length)*root.getScaleX() + 10),root.getBaseXAxis()+(i*stringHeight));
			}
		}
		return DrawingResult.NORMAL;
	}
	
	public Color getRasmolColor(String nucleotide){
		if(nucleotide == "A"){
			return new Color(210, 45, 45);
		}
		if(nucleotide == "C"){
			return new Color(45, 45, 210);
		}
		if(nucleotide == "T" || nucleotide == "U"){
			return new Color(45, 210, 45);
		}
		if(nucleotide == "G"){
			return new Color(211, 211, 50);
		}
		return new Color(0,0,0);
	}
	
	/*
	 * Returns the interval for when an index number should be shown. 
	 */
	private int getIntervalNumber(){
		if(root.getScaleX() < 0.2 && length > 1000) return 500;
		if(root.getScaleX() < 0.3) return 300;
		if(root.getScaleX() < 0.8) return 150;
		if(0.8 < root.getScaleX() && root.getScaleX() < 1.5) return 100;
		if(1.5 < root.getScaleX() && root.getScaleX() < 3.0) return 50;
		if(3.0 < root.getScaleX() && root.getScaleX() < 11) return 25;
		else return 10;
	}
	
	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		System.out.println("Font size: " + fontSize);
		this.fontSize = fontSize;
	}

	public boolean isBold() {
		return isBold;
	}

	public void setBold(boolean isBold) {
		this.isBold = isBold;
	}
	
	public void updateFontWithFont(Font font){
		this.font = font;
		System.out.println("updateFontWithFont");
		repaint();		
	}
	
	public void updateFont(){
		if(isBold){
			font = new Font(fontName, Font.BOLD, fontSize);
		}
		else{
			font = new Font(fontName, Font.PLAIN, fontSize);
		}
		numbersFont = new Font(fontName, Font.PLAIN, fontSize);
		System.out.println("updateFont arc");
		repaint();
	}
	
	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	
	public void drawNumbers(boolean drawNum){
		System.out.println("base: " + drawNum);
		this.drawNumbers = drawNum;
		System.out.println("drawNumbers");
		this.repaint();
	}
	
	public void showRasmolColors(boolean show, int ground){
		if(ground == 0){
			rasmolFront = show;
		}
		if(ground == 1){
			rasmolBack = show;
		}
		System.out.println("showRasmolColors");
		this.repaint();
	}
	
	public void setIndexNumber(int index){
		this.startingIndexNumber = index;
		System.out.println("setIndexNumber");
		repaint();
	}
	
	public int getHeight(){
		if(root.getScaleX() < 11){
			return  (2+sequenceLengths.length*stringHeight) + gapHeight;
		}
		else{
			return alignment.getSequenceCount()*stringHeight + gapHeight;	
		}
	}
}
