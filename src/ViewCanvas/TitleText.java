package ViewCanvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;


import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class TitleText extends ChildDrawingNode{
	
	private String title;
	private infoBox box;
	public boolean visible;
	
	public TitleText(String title){
		this.title = title;
		this.visible = true;
	}
	
	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		if(title != null){
			if(box != null){
				if(visible){
					//g2.drawString(title, 20, 20);
					g2.setColor(Color.white);
					g2.setStroke(new BasicStroke(2));
					g2.fillRect(box.getXViewBounds()-1, box.getYViewBounds()-1, 200, 79);
					g2.setColor(Color.black);
					g2.setFont(new Font("sansserif", Font.BOLD, 12));
					g2.drawRect(box.getXViewBounds()-1, box.getYViewBounds()-1, 201, 80);
					g2.drawString(title, box.getXViewBounds()+5, box.getYViewBounds() + 20);;
				}
			}
		}
		
		return DrawingResult.NORMAL;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public infoBox getBox() {
		return box;
	}

	public void setBox(infoBox box) {
		this.box = box;
	}

}
