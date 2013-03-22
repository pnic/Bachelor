package ViewCanvas;

import java.util.List;


import com.clcbio.api.clc.graphics.framework.ClcScrollPane;
import com.clcbio.api.clc.graphics.framework.RootDrawingNode;
import com.clcbio.api.clc.graphics.framework.ViewBounds;

public class infoBox extends RootDrawingNode{
	private TitleText title;
	private ColorGradientRectangle cgr;
	private boolean titleIsVisible = true;
	
	public infoBox(){
		title = new TitleText("Titlenn");
		title.setBox(this);
		this.addChild(title);
	}
	
	@Override
	protected void setSize() {
		setSize(-110,420,150,80);
	}

	/*
	 * Returns the y position of the visible screen in contrast to the entire canvas. 
	 */
	public int getYViewBounds(){
		if(getCanvas() != null){
			ClcScrollPane pane = getCanvas().getScrollPane();
			List<ViewBounds> pV = pane.getVerticalViewBounds();
			return (int) pV.get(0).getPosition();
		}
		else return 0;
	}
	
	
	/*
	 * Returns the x position of the visible screen in contrast to the entire canvas. 
	 */
	public int getXViewBounds(){
		if(getCanvas() != null){	
			ClcScrollPane pane = getCanvas().getScrollPane();
			List<ViewBounds> pH = pane.getHorizontalViewBounds();
			return (int)pH.get(0).getPosition();		
		}
		return 0;
	}

	public ColorGradientRectangle getCgr() {
		return cgr;
	}

	public void setCgr(ColorGradientRectangle cgr) {
		this.cgr = cgr;
	}
	
	public void setVisible(boolean visible){
		title.visible = visible;
		cgr.visible = visible;
	}
	
	public TitleText getTitleText(){
		return title;
	}
}
