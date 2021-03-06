/*
 * ==========================================================================
 * class name  : com.globalreports.editor.designer.swing.toolbar
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
package com.globalreports.editor.designer.swing.toolbar;

import java.awt.event.*;

import javax.swing.*;

import com.globalreports.editor.GRSetting;
import com.globalreports.editor.configuration.languages.GRLanguageMessage;
import com.globalreports.editor.designer.GREditor;
import com.globalreports.editor.tools.GRLibrary;

@SuppressWarnings("serial")
public class GRToolBar extends JToolBar implements ActionListener {
	public static final int TYPEBUTTON_SELECTED			= 1;
	public static final int TYPEBUTTON_TEXT				= 2;
	public static final int TYPEBUTTON_LINE				= 3;
	public static final int TYPEBUTTON_RECTANGLE		= 4;
	public static final int TYPEBUTTON_IMAGE			= 5;
	public static final int TYPEBUTTON_LIST				= 6;
	public static final int TYPEBUTTON_TABLELIST		= 7;
	public static final int TYPEBUTTON_CHART			= 8;
	public static final int TYPEBUTTON_CIRCLE			= 9;
	public static final int TYPEBUTTON_GROUP			= 10;
	public static final int TYPEBUTTON_TEXTCONDITION	= 11;
	
	private GREditor greditor;
	private JButton bNewDoc;
	private JButton bOpenDoc;
	private JButton bAddPage;
	private JButton bSaveDoc;
	private JButton bPDF;
	
	
	public GRToolBar(GREditor greditor) {
		this.greditor = greditor;
		
		ImageIcon ico_newdoc = new ImageIcon(GRSetting.PATHIMAGE+"ico_newfile.png");
		ImageIcon ico_opendoc = new ImageIcon(GRSetting.PATHIMAGE+"ico_opendoc.png");
		ImageIcon ico_addpage = new ImageIcon(GRSetting.PATHIMAGE+"ico_addpage.png");
		ImageIcon ico_savedoc = new ImageIcon(GRSetting.PATHIMAGE+"ico_savedoc.png");
		ImageIcon ico_pdf = new ImageIcon(GRSetting.PATHIMAGE+"ico_pdf.png");
		
		bNewDoc = new JButton(ico_newdoc);
		bOpenDoc = new JButton(ico_opendoc);
		bAddPage = new JButton(ico_addpage);
		bSaveDoc = new JButton(ico_savedoc);
		bPDF = new JButton(ico_pdf);
		
		bNewDoc.addActionListener(this);
		bNewDoc.setToolTipText(GRLanguageMessage.messages.getString("tlbnewdocument"));
		add(bNewDoc);
		bOpenDoc.addActionListener(this);
		bOpenDoc.setToolTipText(GRLanguageMessage.messages.getString("tlbopendocument"));
		add(bOpenDoc);
		bAddPage.addActionListener(this);
		bAddPage.setToolTipText(GRLanguageMessage.messages.getString("tlbaddpage"));
		add(bAddPage);
		
		bSaveDoc.addActionListener(this);
		bSaveDoc.setToolTipText(GRLanguageMessage.messages.getString("tlbsavedocument"));
		add(bSaveDoc);
		bPDF.addActionListener(this);
		bPDF.setToolTipText(GRLanguageMessage.messages.getString("tlbpreview"));
		add(bPDF);
		
		addSeparator();
		
		this.setFloatable(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == bNewDoc) {
			greditor.newDocument((int)GRLibrary.fromPixelsToMillimeters(GRSetting.WIDTHPAGE),(int)GRLibrary.fromPixelsToMillimeters(GRSetting.HEIGHTPAGE));
		} else if(e.getSource() == bOpenDoc) {
			greditor.openDocument();
		} else if(e.getSource() == bAddPage) {
			greditor.addPageDocument();
		} else if(e.getSource() == bSaveDoc) {
			greditor.saveProject(true,false);
		} else if(e.getSource() == bPDF) {
			greditor.printPDF(false, "");
		} 
	}
	public void activeButton(boolean b1, boolean b2, boolean b3, boolean b4, boolean b5) {
		bNewDoc.setEnabled(b1);
		bOpenDoc.setEnabled(b2);
		bAddPage.setEnabled(b3);
		bSaveDoc.setEnabled(b4);
		bPDF.setEnabled(b5);
	}
	
}