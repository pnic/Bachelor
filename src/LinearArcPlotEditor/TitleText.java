package LinearArcPlotEditor;

import java.awt.Graphics2D;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class TitleText extends ChildDrawingNode{
	
	private String title;
	
	public TitleText(String title){
		this.title = title;
	}
	
	@Override
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		if(title != null){
			g2.drawString(title, 20, 20);
		}
		return DrawingResult.NORMAL;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
}
