package ViewCanvas;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.event.MouseInputListener;

import Engine.LAP;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;

public class CanvasChangedListener extends ChildDrawingNode implements MouseInputListener, MouseWheelListener{

	private int prevX;
	private LAP lap;
	private boolean hasMouseListener;
	private double prevScaleX;
	
	public CanvasChangedListener(LAP lap){
		prevX = -999;
		prevScaleX = -999;
		this.lap = lap;
	}
	
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		if(!hasMouseListener){
			this.addMouseInputListener(this);
			this.addMouseWheelListener(this);
			hasMouseListener = true;
		}
		
		return DrawingResult.NORMAL;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(lap.getXViewBounds() != prevX){
			prevX = lap.getXViewBounds();
			lap.setRelevantTypes();
			lap.getLv().repaintTypes();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(lap.getXViewBounds() != prevX){
			prevX = lap.getXViewBounds();
			lap.setRelevantTypes();
			lap.getLv().repaintTypes();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(lap.getXViewBounds() != prevX){
			prevX = lap.getXViewBounds();
			lap.setRelevantTypes();
			lap.getLv().repaintTypes();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(lap.getXViewBounds() != prevX){
			prevX = lap.getXViewBounds();
			lap.setRelevantTypes();
			lap.getLv().repaintTypes();
		}
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("pre mouse wheel moved");
			lap.setRelevantTypes();
			lap.getLv().repaintTypes();
			System.out.println("wheelmoved");
		
	}
	
	

}
