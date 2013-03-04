package LinearArcPlotEditor;

import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;

import javax.swing.event.MouseInputListener;

public class ArcMouseListener implements MouseInputListener{
	
	private Arc2D arc;
	private int x1;
	private int x2;
	private int circleHeight;
	private int y;
	public ArcMouseListener(){
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		//System.out.println("Clicked");
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		//System.out.println("Enter");
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		//System.out.println("Exited");
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		//System.out.println("dragged");
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
		if(arc != null){
			//System.out.println(arc.contains(arg0.getX(), arg0.getY(), 5, 5));
		}
		
		//System.out.println("x: " + arg0.getX() + " y " + arg0.getY());
	}
	
	public void setArc(Arc2D arc){
		this.arc = arc;
	}
}
