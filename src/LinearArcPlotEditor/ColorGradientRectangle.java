package LinearArcPlotEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;


import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class ColorGradientRectangle extends ChildDrawingNode  {
	private static final long serialVersionUID = -5232136413742494000L;
	
	//Owned by every arc:
	int colorResolution = 100; 
	int length = 200; 
	int width = 20; 
	private double x_pos; 
	private double y_pos; 
	private double circ_r; 
	Rectangle2D[] rectangles = new Rectangle2D[colorResolution];
	Color[] colors = new Color[colorResolution];
	Stroke stroke = new BasicStroke(1f); 
	
	public ColorGradientRectangle(){		
		update();
		//Create rectangle array
		for(int i = 0; i<colorResolution; i++){
			rectangles[i] = new Rectangle2D.Double((float)i*(float)length/(float)colorResolution+x_pos,y_pos,(float)length/(float)colorResolution,width);			
			colors[i] = Color.GREEN;
		}
	}
	
	@Override
	protected DrawingResult internalDraw(Graphics2D ga, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy) {
		update();
		ga.setStroke(stroke);
		for(int i = 0; i<colorResolution; i++){
			rectangles[i].setFrame((float)i*(float)length/(float)colorResolution+x_pos,y_pos,(float)length/(float)colorResolution,width);
			ga.setColor(colors[i]);
			ga.fill(rectangles[i]);
		}

		return DrawingResult.NORMAL;
	}
  
	public void setColors(ColorGradientModel model){
		for(int i = 0; i<colorResolution; i++){
			colors[i] = model.getColor((float)i/(float)colorResolution);
		}
		update();
	}
	
	public void update(){
		  //This method is called when the user eg. changes color gradient
		//circ_r = ArcplotDraw.radiusfactor*getScaleX();
		y_pos = circ_r*2 + 30; 
		x_pos = circ_r - length/2; 
	}
	  
}
