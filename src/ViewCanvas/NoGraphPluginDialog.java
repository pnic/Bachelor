package ViewCanvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class NoGraphPluginDialog extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	
	public NoGraphPluginDialog(String error){
		this.errorMessage = error;
		init();
	}
	
	private void init(){
		setSize(400, 150);
		setTitle("Plugin searching");
		setBackground(Color.white);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));        
        
        JTextArea error = new JTextArea(this.errorMessage);
        error.setSize((int)this.getSize().getWidth(),(int)this.getSize().getHeight()-50);
        error.setEditable(false);
        error.setLineWrap(true);
        error.setBackground(new Color(0xF0F0F0));
        add(error);
        
        JButton confirmButton = new JButton("Ok");
        confirmButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				NoGraphPluginDialog.this.dispose();
			}        	
        });
        confirmButton.setLocation(this.getX()+((int)this.getSize().getWidth()/2), this.getY()+100);
        
        add(confirmButton);
        
        
        
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
        Point newLocation = new Point(middle.x - (300/2), 
                                      middle.y - (200/2));
        setLocation(newLocation.x, newLocation.y);
        
        
	}
	
}
