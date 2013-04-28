package ViewCanvas;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import Engine.LAP;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class RectangleOverNucleotide extends ChildDrawingNode{

	private int first_indexNumber;
	private int second_indexNumber;
	private LAP root;
	private int first_rect_x;
	private int first_rect_y;
	private int second_rect_x;
	private int second_rect_y;
	private boolean drawRect;
	private RoundRectangle2D first_round;
	private RoundRectangle2D second_round;
	private int stringWidth;
	private int stringHeight;
	
	public RectangleOverNucleotide(LAP root){
		this.root = root;
		drawRect = false;
		first_round = new RoundRectangle2D.Double();
		second_round = new RoundRectangle2D.Double();
	}
	
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		System.out.println("Draw rect");
		if(root.getScaleX() >= 11){
			if(drawRect){
				update();
				
				Color cg = new Color(130, 130, 255);
				g2.setColor(cg);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
				g2.setStroke(new BasicStroke(4));
				g2.draw(first_round);
				g2.draw(second_round);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				g2.setColor(Color.red);
				g2.setStroke(new BasicStroke(1));
				g2.draw(first_round);
				g2.draw(second_round);
				
			}
		}
		return DrawingResult.NORMAL;
	}
	
	public void update(){
		first_rect_x = (int)(first_indexNumber * root.getScaleX()-(stringWidth/2));
		first_rect_y = root.getBaseXAxis()+root.getLv().getFeaturesLowerY();
		second_rect_x = (int)(second_indexNumber * root.getScaleX()-(stringWidth/2));
		second_rect_y = root.getBaseXAxis()+root.getLv().getFeaturesLowerY();
		stringHeight = root.getBaseline().getStringHeight();
		stringWidth = root.getBaseline().getStringWidth()+3;
		int height;
		boolean align = root.getBaseline().isShowAlignments();
		if(align){
			height = root.getAlign().getSequenceCount()*stringHeight;	
		}
		else{
			height = stringHeight;
		}
		
		height += 5;
		first_round = new RoundRectangle2D.Double(first_rect_x, first_rect_y, stringWidth, height, 4, 4);
		second_round = new RoundRectangle2D.Double(second_rect_x, second_rect_y, stringWidth, height, 4, 4);
	}
	
	public void setIndexNumbers(int first_number, int second_number){
		this.first_indexNumber = first_number;
		this.second_indexNumber = second_number;
		drawRect = true;
		repaint();
	}
	
	public void setDrawRect(boolean draw){
		drawRect = draw;
	}
}
