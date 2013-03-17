package ViewCanvas;

import java.util.List;


import com.clcbio.api.clc.graphics.framework.ClcScrollPane;
import com.clcbio.api.clc.graphics.framework.RootDrawingNode;
import com.clcbio.api.clc.graphics.framework.ViewBounds;

public class infoBox extends RootDrawingNode{
	private TitleText title;
	private ColorGradientRectangle cgr;
	
	public infoBox(){
		title = new TitleText("Titlen");
		title.setBox(this);
		this.addChild(title);
	}
	
	@Override
	protected void setSize() {
		setSize(-110,420,150,150);
	}

	/*
	 * Returns the y position of the visible screen in contrast to the entire canvas. 
	 */
	public int getYViewBounds(){
		ClcScrollPane pane = getCanvas().getScrollPane();
		List<ViewBounds> pV = pane.getVerticalViewBounds();
		return (int) pV.get(0).getPosition();
	}
	
	
	/*
	 * Returns the x position of the visible screen in contrast to the entire canvas. 
	 */
	public int getXViewBounds(){
		ClcScrollPane pane = getCanvas().getScrollPane();
		List<ViewBounds> pH = pane.getHorizontalViewBounds();
		return (int)pH.get(0).getPosition();
	}

	public ColorGradientRectangle getCgr() {
		return cgr;
	}

	public void setCgr(ColorGradientRectangle cgr) {
		this.cgr = cgr;
	}
}
