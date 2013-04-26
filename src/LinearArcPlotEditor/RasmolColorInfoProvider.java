package LinearArcPlotEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

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
		fillPanel();
	}
	
	public void fillPanel(){
		ForegroundColor = new JCheckBox("Foreground color");
		ForegroundColor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fireColorChange();
			}
		});
	
		BackgroundColor = new JCheckBox("Background color");
		BackgroundColor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fireColorChange();
			}
		});
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
    
    public boolean getForeground(){
    	return ForegroundColor.isSelected();
    }
    
    public boolean getBackground(){
    	return BackgroundColor.isSelected();
    }

}
