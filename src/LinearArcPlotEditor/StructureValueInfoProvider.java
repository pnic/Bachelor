package LinearArcPlotEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.plaf.ColorChooserUI;

import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.clc.editors.graphics.components.GradientChooser;
import com.clcbio.api.clc.editors.graphics.components.GradientModelListener;
import com.clcbio.api.clc.graphics.components.ColorGradientManager;
import com.clcbio.api.clc.gui.framework.ParameterPanel;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.SequenceTypeEditor;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.info.AbstractInfoProvider;
import com.clcbio.api.clc.plugins.editors.graphics.sequence.info.InfoProvider;
import com.clcbio.api.free.workbench.WorkbenchManager;

public class StructureValueInfoProvider extends AbstractInfoProvider{
	
	private final String name;
	private ColorGradientModel colorModel;
	private GradientChooser colorChooser;
	
	public StructureValueInfoProvider(WorkbenchManager manager, String name, ColorGradientModel colorModel) {
		super(manager);
		this.name = name;
		this.colorModel = colorModel;
		fillPanel();
	}
	
	public void fillPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		String[] nameChoises = {"PPFold Reliabilities"};
		JComboBox choises = new JComboBox(nameChoises);
		final JCheckBox ForegroundColor = new JCheckBox("Foreground color");
		ForegroundColor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		colorChooser = new GradientChooser(colorModel);
		colorChooser.setVisible(false);
		ForegroundColor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				colorChooser.setVisible(ForegroundColor.isSelected());
			}
		});
		colorModel.addListener(new GradientModelListener() {
			@Override
			public void stringsChanged() {
				// TODO Auto-generated method stub
			}
			@Override
			public void limitChanged() {
				// TODO Auto-generated method stub
			}
			@Override
			public void gradientChanged() {
				fireColorChange();
			}
			@Override
			public void changeStarting() {
				// TODO Auto-generated method stub	
			}
			@Override
			public void changeEnding() {
				// TODO Auto-generated method stub		
			}
		});
		
		infoPanel = new ParameterPanel();
	
		panel.add(choises, BorderLayout.NORTH);
		panel.add(ForegroundColor, BorderLayout.CENTER);
		panel.add(colorChooser, BorderLayout.SOUTH);
		infoPanel.add(panel);
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
        return new StructureValueInfoProvider(getManager(), name, colorModel);
    }
    
    @Override
    protected void setToFactoryInternal() {
        
    }
}
