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
			System.out.println(arc.contains(arg0.getX(), arg0.getY(), 5, 5));
		}
		
		//System.out.println("x: " + arg0.getX() + " y " + arg0.getY());
	}
	
	public void setXs(int x1, int x2){
		this.x1 = x1;
		this.x2 = x2;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setHeight(int height){
		this.circleHeight = height;
	}
	
	private boolean isOnCircle(int x_pos, int y_pos){
		int radius = (x2-x1)/2;
		int circle_xorigin = x1+radius;
		int circle_yorigin = y+radius;
		System.out.println("x1 " + x1 + " x2 " + x2 + " x_origin " + circle_xorigin+ " y origin " + circle_yorigin);
		int returner = (int) (Math.pow(x_pos-circle_xorigin, 2)+Math.pow(y_pos-circle_yorigin, 2) - Math.pow(radius, 2));
		//System.out.println(returner);
		if(returner == 0){
			return true;
		}
		return false;
	}
	
	public void setArc(Arc2D arc){
		this.arc = arc;
	}
}
