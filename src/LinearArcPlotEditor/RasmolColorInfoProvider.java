package LinearArcPlotEditor;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;


import com.clcbio.api.clc.gui.framework.ParameterPanel;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.SequenceTypeEditor;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.info.AbstractInfoProvider;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.info.InfoProvider;
import com.clcbio.api.free.workbench.WorkbenchManager;

public class RasmolColorInfoProvider extends AbstractInfoProvider{
	
	private final String name;
	private JCheckBox ForegroundColor;
	private JCheckBox BackgroundColor;
	
	public RasmolColorInfoProvider(WorkbenchManager manager, String name) {
		super(manager);
		this.name = name;
		ForegroundColor = new JCheckBox("Foreground color");
		BackgroundColor = new JCheckBox("Background color");
		infoPanel = new ParameterPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.add(ForegroundColor);
		infoPanel.add(BackgroundColor);
	}

	@Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getId() {
        return "MyProvider" + name;
    }
    
    @Override
    public InfoProvider createClone(SequenceTypeEditor editor) {
        return new RasmolColorInfoProvider(getManager(), name);
    }
    
    @Override
    protected void setToFactoryInternal() {
        
    }

}
