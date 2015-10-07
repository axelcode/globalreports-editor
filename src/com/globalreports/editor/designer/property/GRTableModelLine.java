/*
 * ==========================================================================
 * class name  : com.globalreports.editor.designer.property.GRTableModelLine
 * Begin       : 
 * Last Update : 
 *
 * Author      : Alessandro Baldini - alex.baldini72@gmail.com
 * License     : GNU-GPL v2 (http://www.gnu.org/licenses/)
 * ==========================================================================
 * 
 * GlobalReports Editor
 * Copyright (C) 2015 Alessandro Baldini
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Linking GlobalReports Editor(C) statically or dynamically with other 
 * modules is making a combined work based on GlobalReports Editor(C). 
 * Thus, the terms and conditions of the GNU General Public License cover 
 * the whole combination.
 *
 * In addition, as a special exception, the copyright holders 
 * of GlobalReports Editor(C) give you permission to combine 
 * GlobalReports Editor(C) program with free software programs or libraries 
 * that are released under the GNU LGPL and with code included 
 * in the standard release of GlobalReports Engine(C) under the CC license 
 * (or modified versions of such code, with unchanged license) and
 * GlobalReports Compiler(C) under the CC license. 
 * You may copy and distribute such a system following the terms of the GNU GPL 
 * for GlobalReports Editor(C) and the licenses of the other code concerned, 
 * provided that you include the source code of that other code 
 * when and as the GNU GPL requires distribution of source code.
 *
 * Note that people who make modified versions of GlobalReports Editor(C) 
 * are not obligated to grant this special exception for their modified versions; 
 * it is their choice whether to do so. The GNU General Public License 
 * gives permission to release a modified version without this exception; 
 * this exception also makes it possible to release a modified version 
 * which carries forward this exception.
 * 
 */
package com.globalreports.editor.designer.property;

import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.globalreports.editor.designer.GRPage;
import com.globalreports.editor.designer.swing.table.GRTable;
import com.globalreports.editor.designer.swing.table.GRTableCell;
import com.globalreports.editor.designer.swing.table.GRTableSeparator;
import com.globalreports.editor.designer.swing.table.element.GRColorElement;
import com.globalreports.editor.designer.swing.table.event.GRTableEvent;
import com.globalreports.editor.designer.swing.table.event.GRTableListener;
import com.globalreports.editor.graphics.GRLine;
import com.globalreports.editor.tools.GRLibrary;

@SuppressWarnings("serial")
public class GRTableModelLine extends GRTableModel implements GRTableListener {
	
	private Object[][] element = {{"Oggetto","Linea"},
								  {new GRTableSeparator("Posizione"), null},
								  {"Posizione relativa", new JCheckBox()},
								  {"X1", new JTextField()},
								  {"Y1", new JTextField()},
								  {"X2", new JTextField()},
								  {"Y2", new JTextField()},
								  {new GRTableSeparator("Aspetto"), null},
								  {"Dimensione tratto", new JTextField()},
								  {"Colore tratto", new GRColorElement(Color.BLACK)}};
	
	private GRLine objLine;	// Riferimento all'oggetto per poterne modificare le proprietà
	
	public GRTableModelLine(GRTableProperty panelProperty, String[] title) {
		super(title);
		this.panelProperty = panelProperty;
		
		createBody(element);
		addGRTableListener(this);
	}
	
	public void setGRObject(GRLine ref) {
		this.objLine = ref;
	}
	public void setX1(int value) {
		this.setValueAt(2,1,""+GRLibrary.fromPixelsToMillimeters(value));
	}
	public void setY1(int value) {
		this.setValueAt(3,1,""+GRLibrary.fromPixelsToMillimeters(value));
	}
	public void setX2(int value) {
		this.setValueAt(4,1,""+GRLibrary.fromPixelsToMillimeters(value));
	}
	public void setY2(int value) {
		this.setValueAt(5,1,""+GRLibrary.fromPixelsToMillimeters(value));
	}
	public void setWidthStroke(double value) {
		this.setValueAt(6, 1, ""+value);
	}
	public void setColorStroke(int red, int green, int blue) {
		this.setValueAt(7,1,new Color(red, green, blue));
	}

	@Override
	public void valueChanged(GRTableEvent e) {
		GRTableCell cell = (GRTableCell)e.getSource();
		
		switch(e.getRow()) {
			case 2:	// X1
				objLine.setX(GRLibrary.fromMillimetersToPixels(Double.parseDouble(e.getValue())));
				break;
				
			case 3:	// Y1
				objLine.setY(GRLibrary.fromMillimetersToPixels(Double.parseDouble(e.getValue())));
				break;
				
			case 4:	// X2
				objLine.setXEnd(GRLibrary.fromMillimetersToPixels(Double.parseDouble(e.getValue())));
				break;
				
			case 5:	// Y2
				objLine.setYEnd(GRLibrary.fromMillimetersToPixels(Double.parseDouble(e.getValue())));
				break;
			
			case 6:	// widthstroke
				objLine.setWidthStroke(Double.parseDouble(e.getValue()));
				break;
			
			case 7:	// colorstroke
				objLine.setColorStroke(((GRColorElement)cell.getObjectValue()).getColor());
				break;
				
		}
	
		panelProperty.getPage().repaint();
		
	}
	
}
