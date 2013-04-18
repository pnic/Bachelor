package ViewCanvas;
/*
 * David Korczynski
 * dgeo@itu.dk
 * May, 2013
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import javax.swing.SwingUtilities;

import Engine.LAP;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.Sequence;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.Alignment;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.AlignmentSequenceIndexer;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alphabet.AlphabetTools;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.index.BasicIndexer;

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
	private boolean showAlignments;
	
	/*
	 * A baseline represents the x-axis of the linearArcDiagram.
	 */
	public Baseline(Alignment alignment, LAP root){
		this.alignment = alignment; 
		this.root = root;
		this.length = alignment.getLength();
		
		init();
	}
	
	/*
	 * Initializes sequence arrays, with correct symbols. 
	 * Initializes the font attributes. 
	 */
	private void init(){
		nucleotideSequences = new String[alignment.getSequenceCount()][alignment.getLength()];
		sequenceLengths = new int[alignment.getSequenceCount()];
		for(int i=0; i<alignment.getSequenceCount(); i++){
			sequenceLengths[i] = alignment.getSequence(i).getLength();
		}
		String s;
		
		// Put all the sequences into string arrays, then it is faster to draw them. 
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
		
		drawNumbers = true;
		showAlignments = true;
		fontSize = 14;
		stringHeight = 15;
		isBold = true;
		fontName = "SansSerif";
		updateFont();
	}
	
	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		// If scaleX() is under 12, draw a line.
		int viewPX = root.getXViewBounds();
		int viewPWidth = root.getViewPaneWidth();
		int interval = getIntervalNumber();
		int firstGap = 6;
		int secondGap = 0;
		int intervalHeight = 10;
		gapHeight = firstGap + secondGap + intervalHeight;
		
		g2.setFont(font);
		stringHeight = g2.getFontMetrics().getHeight();
		g2.setStroke(stroke);
		
		int numbersGap = 50;
		
		if(root.getScaleX() < 11){
			baseLine.setLine(0, root.getBaseXAxis()+2, length*root.getScaleX(), root.getBaseXAxis()+2);
			g2.draw(baseLine);
			for(int i=0; i<sequenceLengths.length; i++){
				baseLine.setLine(0,root.getBaseXAxis()+(i*stringHeight)+numbersGap, (int)(length*root.getScaleX()), root.getBaseXAxis()+(i*stringHeight)+numbersGap);		
				g2.draw(baseLine);
			}
		}
		// If scaleX() is over 12, draw sequence instead.
		else{
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
		
		// Draw sequence lengths to the right.
		int seqLength, number_y_pos, number_x_pos;
		number_x_pos = (int) (length*root.getScaleX() + 10);
		if(root.getScaleX() < 11){ g2.drawString(Integer.toString(alignment.getLength()), number_x_pos, root.getBaseXAxis()+stringHeight/2); }
		
		for(int i=0; i<sequenceLengths.length; i++){
			seqLength = sequenceLengths[i];
			//Depending on zoom level, we need to adjust the y_pos of the number. 
			if(root.getScaleX() < 11){ 	number_y_pos = root.getBaseXAxis()+(i*stringHeight)+(stringHeight/3)+50; }
			else{ 						number_y_pos = root.getBaseXAxis()+(i*stringHeight)+(stringHeight);}
			
			g2.drawString(Integer.toString(seqLength), number_x_pos ,number_y_pos);
		}
		
		//Draw numbers
		if(drawNumbers){
			if(numbersFont != null) { g2.setFont(numbersFont); }

			// Making variables ready who wont change during the loop, e.g. y-coordinate of texts. 
			int stringWidth, YPosition_text, YPosition_line_start, YPosition_line_end;
			//Under 11
			if(root.getScaleX() < 11){
				YPosition_text = root.getBaseXAxis()+stringHeight+firstGap + secondGap + intervalHeight;
				YPosition_line_end = root.getBaseXAxis()+firstGap + intervalHeight;
				YPosition_line_start = root.getBaseXAxis()+firstGap;
			}//Over 11
			else{
				YPosition_text = root.getBaseXAxis()+((alignment.getSequenceCount()+1)*stringHeight)+firstGap + secondGap + intervalHeight;
				YPosition_line_start = root.getBaseXAxis()+(stringHeight*alignment.getSequenceCount())+firstGap;
				YPosition_line_end = root.getBaseXAxis()+(stringHeight*alignment.getSequenceCount())+firstGap+intervalHeight;
			}
			
			for(int i=0; i<length; i+= interval){
					stringWidth = SwingUtilities.computeStringWidth(g2.getFontMetrics(), Integer.toString(i-startingIndexNumber));
					int lineX_pos = (int)(i*root.getScaleX());
					
					// Draw number and line. 
					g2.drawString(Integer.toString(i-startingIndexNumber), lineX_pos-(stringWidth/2), YPosition_text);
					g2.drawLine(lineX_pos, YPosition_line_start, lineX_pos, YPosition_line_end);
			}
		}
		return DrawingResult.NORMAL;
	}
	
	public Color getRasmolColor(String nucleotide){
		if(nucleotide == "A") 						return new Color(210, 45, 45);
		if(nucleotide == "C") 						return new Color(45, 45, 210);
		if(nucleotide == "T" || nucleotide == "U") 	return new Color(45, 210, 45);
		if(nucleotide == "G") 						return new Color(211, 211, 50);

		return new Color(0,0,0);
	}
	
	/*
	 * Returns the interval for when an index number should be shown. 
	 */
	private int getIntervalNumber(){
		if(root.getScaleX() < 0.12 && length > 1000) 			return 1000;
		if(root.getScaleX() < 0.2 && length > 1000) 			return 500;
		if(root.getScaleX() < 0.3) 								return 300;
		if(root.getScaleX() < 0.8) 								return 150;
		if(0.8 < root.getScaleX() && root.getScaleX() < 1.5) 	return 100;
		if(1.5 < root.getScaleX() && root.getScaleX() < 3.0) 	return 50;
		if(3.0 < root.getScaleX() && root.getScaleX() < 11) 	return 25;
		else return 10;
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
	}
	
	public void updateFontWithFont(Font font){
		this.font = font;
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
	
		repaint();
	}
	
	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	
	public void drawNumbers(boolean drawNum){
		this.drawNumbers = drawNum;
		this.repaint();
	}
	
	public void showRasmolColors(boolean show, int ground){
		if(ground == 0)	rasmolFront = show;
		if(ground == 1) rasmolBack = show;

		this.repaint();
	}
	
	public void setIndexNumber(int index){
		this.startingIndexNumber = index;
		repaint();
	}
	
	public int getHeight(){
		if(root.getScaleX() < 11){
			return  (2+sequenceLengths.length*stringHeight) + gapHeight+50;
		}
		else{
			return alignment.getSequenceCount()*stringHeight + gapHeight;	
		}
	}

	public boolean isShowAlignments() {
		return showAlignments;
	}

	public void setShowAlignments(boolean showAlignments) {
		this.showAlignments = showAlignments;
	}
	
	
}
