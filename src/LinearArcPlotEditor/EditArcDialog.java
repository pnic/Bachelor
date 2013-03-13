package LinearArcPlotEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class EditArcDialog extends JDialog{
	private Arc arc;
	private final String errorMessage = "An error occured when trying to validate. The error could be caused by: \n";
	
	public EditArcDialog(Arc arc){
		this.arc = arc;
		init();
	}
	
	public void init(){
		setSize(300, 300);
		setTitle("Edit Arc");
		setBackground(Color.white);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        JPanel editArcFields = new JPanel(new FlowLayout());

        // First
        JLabel first = new JLabel("First");
        final JTextField firstNumber = new JTextField(Integer.toString(arc.p1));
        firstNumber.setPreferredSize(new Dimension(60,20));

        
        // Second
        JLabel second = new JLabel("second");
        final JTextField secondNumber = new JTextField(Integer.toString(arc.p2));
        secondNumber.setPreferredSize(new Dimension(60,20));

        editArcFields.add(first);
        editArcFields.add(firstNumber);
        editArcFields.add(second);
        editArcFields.add(secondNumber);
        add(editArcFields);
        
        JPanel histPanel = new JPanel();
        histPanel.setPreferredSize(new Dimension(299,13));
        
        histPanel.setBackground(Color.lightGray);
        JLabel history_label = new JLabel("History");
        history_label.setOpaque(true);
        history_label.setBackground(Color.LIGHT_GRAY);
        history_label.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane history = new JScrollPane();
        history.setPreferredSize(new Dimension(290,150));
        histPanel.add(history_label);
       	this.add(histPanel);
       	this.add(history);
        
        //this.add(history);
        JPanel btns = new JPanel(new FlowLayout());
        
        JButton cancelBtn = new JButton("cancel");
        cancelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EditArcDialog.this.dispose();
			}
        	
        });
        
        JButton confirmBtn = new JButton("confirm");
        confirmBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean canParseFirst = tryParse(firstNumber.getText());
				boolean canParseSecond = tryParse(secondNumber.getText());
				
				if(!canParseFirst || ! canParseSecond){
					JOptionPane.showMessageDialog(null, errorMessage + "Please dont use letters in number boxes", "Input Error", JOptionPane.ERROR_MESSAGE);
				}
				else{
					int first = Integer.parseInt(firstNumber.getText());
					int second = Integer.parseInt(secondNumber.getText());
					
					if(first > second){
						JOptionPane.showMessageDialog(null, errorMessage + "Second must be bigger than first number", "Input Error", JOptionPane.ERROR_MESSAGE);
					}
					else{
						if(arc.canChangeArc(first, second)){
							arc.p1 = first;
							arc.p2 = second;

							EditArcDialog.this.dispose();
						}
						else{
							JOptionPane.showMessageDialog(null, errorMessage + "No pseudoknots allowed", "Input Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
        });
        
        btns.add(cancelBtn);
        btns.add(confirmBtn);
        
        add(btns);
        
        
        

        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
        Point newLocation = new Point(middle.x - (300/2), 
                                      middle.y - (200/2));
        setLocation(newLocation.x, newLocation.y);
        
	}
	
	private boolean tryParse(String number){
		try{
			Integer.parseInt(number);
			return true;
		}
		catch(NumberFormatException ex){
			return false;
		}
	}
}
