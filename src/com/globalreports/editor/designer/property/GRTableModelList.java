/*
 * ==========================================================================
 * class name  : com.globalreports.editor.designer.property.GRTableModelList
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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.globalreports.editor.designer.GRPage;
import com.globalreports.editor.graphics.GRList;
import com.globalreports.editor.graphics.GRRectangle;
import com.globalreports.editor.tools.GRLibrary;

@SuppressWarnings("serial")
public class GRTableModelList extends GRTableModel implements TableModelListener {
	private Object[][] element = {{"Type","List"},
			  {"",""},
			  {"Relative Height Position",new Boolean(false)},
			  {"Id",""},
			  {"Top",""},
			  {"Height",""}};
			  
	private GRList objList;	// Riferimento all'oggetto per poterne modificare le propriet���

	public GRTableModelList(GRPage page) {
		this.pagina = page;
		eventChangeActive = false;
		setDataVector(element,header);
		addTableModelListener(this);
	}
	
	public void tableChanged(TableModelEvent e) {
		
		if(eventChangeActive) {
			switch(e.getFirstRow()) {
				case 3:
					objList.setNameXml(getValueAt(3,1).toString());
					break;
					
				case 4:
					objList.setY(GRLibrary.fromMillimetersToPixels(Double.parseDouble(getValueAt(4,1).toString())));
					break;	

				case 5:
					objList.setHeight(GRLibrary.fromMillimetersToPixels(Double.parseDouble(getValueAt(5,1).toString())));
					break;
			}
			
			eventChangeActive = false;
			pagina.repaint();
		}
	}
	public boolean isCellEditable(int row, int column) {
        if (column == 1) {
			if(row == 0 || row == 1 || row == 2)
				return false;
				
        	return true;
        }
        
        return false;
    }
	
	public void setGRObject(GRList ref) {
		this.objList = ref;
	}
	public void setNameXml(String value) {
		this.setValueAt(value, 3, 1);
	}
	public void setTop(int value) {
		this.setValueAt(""+GRLibrary.fromPixelsToMillimeters(value),4,1);
	}
	public void setHeight(int value) {
		this.setValueAt(""+GRLibrary.fromPixelsToMillimeters(value),5,1);
	}
	
}