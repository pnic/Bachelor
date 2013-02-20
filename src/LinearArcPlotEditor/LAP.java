package LinearArcPlotEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import LinearArcPlotEditor.Arc;

import LinearArcPlotEditor.ColorGradientRectangle;
import com.clcbio.api.clc.editors.graphics.components.ColorGradientModel;
import com.clcbio.api.clc.graphics.DrawingContext;
import com.clcbio.api.clc.graphics.components.ColorGradientManager;
import com.clcbio.api.clc.graphics.framework.ChildDrawingNode;
import com.clcbio.api.clc.graphics.framework.RootDrawingNode;
import com.clcbio.api.free.gui.components.ObjectMoveable;
import com.clcbio.api.free.gui.dialog.ClcMessages;
import com.clcbio.api.free.gui.focus.ClcFocusPanel;
import com.clcbio.api.free.gui.focus.ClcFocusScrollPane;

public class LAP extends RootDrawingNode {

	
	private int [] pairings; 
	private float [] reliabilities; 
	private Color [] colors;
	private int seqLength;
	
	private ColorGradientRectangle colorGradientRectangle; 
	
	
	private Arc [] arcs; 
	
	private Baseline baseline;
	
	private String title;

	public LAP(int[] pairings, float[] reliabilities, int seqLength, ColorGradientModel gradmodel, String title){
		this.pairings = pairings;
		this.reliabilities = reliabilities;
		this.seqLength = seqLength;
	
		setMaxScaleX(30);
		setMaxScaleY(30);
		setMinScaleX(0.1);
		setMinScaleY(0.1);
		setMinScaleRatio(1.0);
		setMaxScaleRatio(1.0);
		
		baseline = new Baseline(seqLength);
		baseline.setOffset(20, 20);
		addChild(baseline);
		
		int nr=0;
		for(int i = 0;i<pairings.length; i++){
			if(pairings[i]>i){
				nr = nr+1;
			}
		}
		
		int cnt = 0;
		if(arcs==null){
			arcs = new Arc[nr];
			for(int i = 0; i<pairings.length; i++){
				if(pairings[i]>i){
					System.out.println(i + " i");
					System.out.println(pairings[i] + " p[i]");
					arcs[cnt] = new Arc(i,pairings[i],seqLength, reliabilities[i]);
					//arcs[cnt].setOffset(drawingOffsetX, drawingOffsetY);
					addChild(arcs[cnt]);
					cnt = cnt+1;
				}
			}
		}
		
		setColors(gradmodel);
	}
	
	
	public void setColors(ColorGradientModel gradmodel){  
		int cnt = 0; 
		for(int i = 0; i<pairings.length; i++){
			if(pairings[i]>i){
				arcs[cnt].setColor(gradmodel.getColor(reliabilities[i]));
				cnt = cnt+1;
			}
		}
		//colorGradientRectangle.setColors(gradmodel);
	}
	
	@Override
	protected void setSize() {
		// TODO Auto-generated method stub
		setSize(-600,600,-600,600);
	}
	
	

}
