package ViewCanvas;

import java.util.List;


import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.clc.graphics.framework.ClcScrollPane;
import com.clcbio.api.clc.graphics.framework.RootDrawingNode;
import com.clcbio.api.clc.graphics.framework.ViewBounds;

public class infoBox extends RootDrawingNode{
	private TitleText titleText;
	private ColorGradientRectangle cgr;
	private ColorGradientModel colorModel;
	private int textSize;
	private boolean bold;
	private String fontName;
	
	public infoBox(String title, ColorGradientModel colorModel){
		bold = true;
		textSize = 14;
		setFontName("sansSerif");
		this.titleText = new TitleText(title,this);
		titleText.setBox(this);
		this.colorModel = colorModel;
		cgr = new ColorGradientRectangle("Color of pairs", 0, 0, this, this.colorModel);
		this.addChild(titleText);
		this.addChild(cgr);
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
		titleText.visible = visible;
		cgr.visible = visible;
	}
	
	
	
	public TitleText getTitleText(){
		return titleText;
	}
	
	public void refresh(){
		cgr.setColors(colorModel);
	}

	public ColorGradientModel getColorModel() {
		return colorModel;
	}

	public void setColorModel(ColorGradientModel colorModel) {
		this.colorModel = colorModel;
	}

	public void setTitleText(TitleText titleText) {
		this.titleText = titleText;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String getFontName() {
		return fontName;
	}
}
