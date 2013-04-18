package LinearArcPlotEditor;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import com.clcbio.api.base.util.State;
import com.clcbio.api.free.editors.framework.sidepanel.SidePanelModel;
import com.clcbio.api.free.workbench.WorkbenchManager;

public class TextModel extends SidePanelModel{
	
	private Font cachedFont = null;
	private boolean isBold; 
	private int selectedFont;
	private int textSize;
	
	private static String[] fontNames;
	
	private static final String KEY_IS_BOLD = "isBold";
    private static final String KEY_FONT_NAME = "fontName";
    private static final String KEY_TEXT_SIZE = "textSize";
//Here we define some keys for later use in usersettings of the sidepanel model.

    private static final String[] textSizeNames = new String[]{"Tiny", "Small", "Medium", "Large", "Huge"};
    
	static {
        String[] allFontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        java.util.List goodFontList = new ArrayList();

        for (int i = 0; i < allFontNames.length; i++) {
            Font font = new Font(allFontNames[i], 0, 12);
            if (font.canDisplayUpTo("azAZ09") == -1) {
                goodFontList.add(allFontNames[i]);
            }
        }
        fontNames = (String[]) goodFontList.toArray(new String[0]);
    }
	
	
	public TextModel(WorkbenchManager manager) {
		super("Text format");
		isBold = true;
		selectedFont = 0;
		textSize = 12;
	}

	public void loadModel(State model) {
        String s = model.get(KEY_FONT_NAME, String.class);
        if (s != null) {
            setFontName(s);
        }
        isBold = model.get(KEY_IS_BOLD, Boolean.class, isBold);
        textSize = model.get(KEY_TEXT_SIZE, Integer.class, textSize);
    }

    public State saveModel() {
        State model = new State();
        model.put(KEY_FONT_NAME, getFontName());
        model.put(KEY_IS_BOLD, isBold);
        model.put(KEY_TEXT_SIZE, textSize);
        return model;
    }
    
    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean isBold) {
        if (isBold == this.isBold) {
            return;
        }
        this.isBold = isBold;
        cachedFont = null;
        fireModelChanged();
    }

    public String[] getFontNames() {
        return TextModel.fontNames;
    }

    public int getFontNumber() {
        return selectedFont;
    }

    public void setFontNumber(int index) {
    	System.out.println("index number chosen " + index);
        if (selectedFont == index) {
            return;
        }
        selectedFont = index;
        cachedFont = null;
        fireModelChanged();
    }

    public String getFontName() {
        return fontNames[selectedFont];
    }

    public void setFontName(String name) {
        for (int i = 0; i < fontNames.length; i++) {
            if (fontNames[i].equals(name)) {
                setFontNumber(i);
                break;
            }
        }
    }
    
    public Font getFont() {
        if (cachedFont == null) {
            int style = 0;
            if (isBold) {
                style |= Font.BOLD;
            }
            cachedFont = new Font(getFontName(), style, 12);
        }
        return cachedFont;
    }

    public String[] getTextSizeNames() {
        return textSizeNames;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        if (this.textSize == textSize) {
            return;
        }
        this.textSize = textSize;
        fireModelChanged();
    }
    
    public String getId() {
        return "TextLayoutModel";
    }
    
    public void setToFactory() {
        isBold = true;
        textSize = 2;
        selectedFont = 0;
        setFontName("SansSerif");
    }
}
