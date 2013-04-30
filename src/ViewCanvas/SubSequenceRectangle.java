package ViewCanvas;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import Engine.LAP;

import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.DrawingLayer;
import com.clcbio.api.clc.graphics.framework.DrawingResult;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.alignment.AlignmentSequenceIndexer;
import com.clcbio.api.free.datatypes.bioinformatics.sequence.index.BasicIndexer;
import com.clcbio.api.free.editors.framework.MouseMode;

public class SubSequenceRectangle extends ChildDrawingNode implements MouseInputListener {

	private boolean dragging;
	private Point startDragPoint;
	private Point curDragPoint;
	private boolean hasMouseListener;
	private LAP lap;
	private AlignmentSequenceIndexer indexer;

	public SubSequenceRectangle(LAP lap){
		this.lap = lap;
		
	}
	
	protected DrawingResult internalDraw(Graphics2D g2, boolean drawoutline, DrawingLayer drawinglayer, double minx, double maxx, double miny, double maxy){
		if(!hasMouseListener){
			this.addMouseInputListener(this);
			hasMouseListener = true;
		}
		
		if(dragging && lap.getEditor().getMouseMode() == MouseMode.SELECT_MODE){
			int startX;
			int startY;
			startX = startDragPoint.getX() > curDragPoint.getX() ? (int)(lap.getXViewBounds()+curDragPoint.getX()) : (int)(lap.getXViewBounds()+startDragPoint.getX());
			startY = startDragPoint.getY() > curDragPoint.getY() ? (int)(lap.getYViewBounds()+curDragPoint.getY()) : (int)(lap.getYViewBounds()+startDragPoint.getY());
			g2.drawRect(startX,startY,Math.abs((int)(curDragPoint.getX()-startDragPoint.getX())),Math.abs((int)(curDragPoint.getY()-startDragPoint.getY())));
		}
		return DrawingResult.NORMAL;
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		dragging = true;
		startDragPoint = e.getPoint();
		curDragPoint = startDragPoint;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragging = false;
		
		double xStart;
		double newXStart;
		
		double xEnd;
		double newXEnd;
		
		if(startDragPoint.getX() < e.getX()){
		xStart = lap.getXViewBounds() + startDragPoint.getX();
		newXStart = xStart/lap.getScaleX();
		
		xEnd = lap.getXViewBounds() + e.getX();
		newXEnd = xEnd/lap.getScaleX();
		} else {
			xStart = lap.getXViewBounds() + e.getX();
			newXStart = xStart/lap.getScaleX();
			
			xEnd = lap.getXViewBounds() + startDragPoint.getX();
			newXEnd = xEnd/lap.getScaleX();
			
		}
		int XNucleotideStart = (int)Math.round(newXStart);
		int XNucleotideEnd = (int)Math.round(newXEnd);
		
		indexer = new AlignmentSequenceIndexer(lap.getAlign(), lap.getCurrentSequenceNumber());
		
		int seqPositionStart;
		
		if(XNucleotideStart < lap.getAlign().getLength()){
			seqPositionStart = XNucleotideStart >= 0 ? indexer.getSequencePosition(XNucleotideStart) : 0;
		} else {
			 this.repaint();
			 return;
		}
		
		int seqPositionEnd;
		
		if(XNucleotideEnd > 0){
			seqPositionEnd = XNucleotideEnd <= lap.getAlign().getLength() ? indexer.getSequencePosition(XNucleotideEnd) : lap.getAlign().getLength();
		}else {
			 this.repaint();
			 return;
		}
				
		lap.showSub(seqPositionStart, seqPositionEnd);
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//if(Math.abs(e.getPoint().getX()-curDragPoint.getX()) > 15 || Math.abs(e.getPoint().getY()-curDragPoint.getY()) > 15){
		curDragPoint = e.getPoint();
		this.repaint();
		//}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
