package ViewCanvas;

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
	String name;
	double fixedMin;
	double fixedMax;
	
	int colorResolution = 100; 
	int length = 200; 
	int width = 20; 
	private double x_pos; 
	private double y_pos; 
	private double circ_r; 
	Rectangle2D[] rectangles = new Rectangle2D[colorResolution];
	Color[] colors = new Color[colorResolution];
	Stroke stroke = new BasicStroke(1f); 
	private infoBox box;
	public boolean visible = true;
		
	public ColorGradientRectangle(String name, double fixedMin, double fixedMax, infoBox box, ColorGradientModel colorModel){		
		this.name=name;
		this.fixedMin = fixedMin;
		this.fixedMax = fixedMax;
		this.box = box;

		
		//Create rectangle array
		for(int i = 0; i<colorResolution; i++){
			rectangles[i] = new Rectangle2D.Double((float)i*(float)length/(float)colorResolution+x_pos,y_pos,(float)length/(float)colorResolution,width);			
		}
		this.setColors(colorModel);
		
	}
	
	@Override
	protected DrawingResult internalDraw(Graphics2D ga, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy) {
		if(visible){
			y_pos = box.getYViewBounds()+50; 
			x_pos = box.getXViewBounds(); 	
			System.out.println("visible");
			ga.setStroke(stroke);
			for(int i = 0; i<colorResolution; i++){
				rectangles[i].setFrame((float)i*(float)length/(float)colorResolution+x_pos,y_pos,(float)length/(float)colorResolution,width);
				ga.setColor(colors[i]);
				ga.fill(rectangles[i]);
			}
			ga.setColor(Color.BLACK);
			ga.drawString(name, (int)x_pos, (int)y_pos-20);
			ga.drawString(Double.toString(fixedMin), (int)x_pos, (int)y_pos-2);
			ga.drawString(Double.toString((fixedMax+fixedMin)/2), (int)x_pos+length/2-15, (int)y_pos-2);
			ga.drawString(Double.toString(fixedMax), (length+(int)x_pos)-20, (int)y_pos-2);
		}		
		else{
			System.out.println("not visible");
		}
		return DrawingResult.NORMAL;
	}
  
	public void setColors(ColorGradientModel model){
		for(int i = 0; i<colorResolution; i++){
			colors[i] = model.getColor((float)i/(float)colorResolution);
		}
		this.repaint();
	}
	  
}
