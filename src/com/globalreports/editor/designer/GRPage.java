/*
 * ==========================================================================
 * class name  : com.globalreports.editor.designer.GRPage
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
package com.globalreports.editor.designer;

import java.awt.*;
import java.awt.event.*;
import java.awt.Rectangle;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.*;

import com.globalreports.editor.configuration.languages.GRLanguageMessage;
import com.globalreports.editor.designer.dialog.GRDialogCreateChart;
import com.globalreports.editor.designer.dialog.GRDialogCreateTableList;
import com.globalreports.editor.designer.dialog.GRDialogEditText;
import com.globalreports.editor.designer.dialog.GRDialogPropertyCell;
import com.globalreports.editor.designer.dialog.GRDialogPropertyTableList;
import com.globalreports.editor.designer.dialog.GRDialogTextCondition;
import com.globalreports.editor.designer.property.*;
import com.globalreports.editor.designer.swing.toolbar.GRToolBar;
import com.globalreports.editor.designer.swing.toolbar.GRToolBarDesigner;
import com.globalreports.editor.designer.swing.toolbar.GRToolBarStrumenti;
import com.globalreports.editor.GRSetting;
import com.globalreports.editor.graphics.*;
import com.globalreports.editor.graphics.chart.GRChartVoice;
import com.globalreports.editor.graphics.tablelist.GRTableListCell;
import com.globalreports.editor.graphics.tablelist.GRTableListText;
import com.globalreports.editor.graphics.text.GRTextFormatted;
import com.globalreports.editor.tools.GRLibrary;

@SuppressWarnings("serial")
public class GRPage extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
	
	private final static int ACTION_NULL				= 0;
	private final static int ACTION_SELECT				= 1;
	private final static int ACTION_RESIZE_TSX			= 3;
	private final static int ACTION_RESIZE_TDX			= 4;
	private final static int ACTION_RESIZE_BSX			= 5;
	private final static int ACTION_RESIZE_BDX			= 6;
	private final static int ACTION_MULTISELECT			= 7;
	
	private final static int ACTION_DRAWLINE			= 11;
	private final static int ACTION_DRAWRECTANGLE		= 12;
	private final static int ACTION_DRAWTEXT			= 13;
	private final static int ACTION_DRAWIMAGE			= 14;
	private final static int ACTION_DRAWLIST			= 15;
	private final static int ACTION_DRAWTABLELIST		= 16;
	private final static int ACTION_DRAWCHART			= 17;
	private final static int ACTION_DRAWCIRCLE			= 18;
	private final static int ACTION_DRAWGROUP			= 19;
	private final static int ACTION_DRAWTEXTCONDITION	= 20;
	// Default 1mm --> 3 pixel
	
	// Griglia
	private boolean activeGrid;	// Definisce se gli oggetti devono essere agganciati alla griglia
	private boolean viewGrid;	// Definisce se visualizzare la griglia
	private int GAPGRIDX = 15;
	private int GAPGRIDY = 15;
	
	// Pagina
	private int width;
	private int height;
	private float zoom;
	
	private int xStart;
	private int yStart;
	private int xEnd;
	private int yEnd;
	
	private int heightHeader;	// Definisce l'altezza dello spazio riservato all'HEAD
	private int heightFooter;	// Definisce l'altezza dello spazio riservato al FOOT
	
	// Azioni
	private int flagAzione;		
	
	private long idObj;
	private GRDocument grdoc;
	private Vector<GRObject> grobj;
	private GRObject refObj;		// Riferimento all'oggetto corrente selezionato
	private GRObject refObjCopy;	// Riferimento all'oggetto da copiare
	private GRUndo grUndo;			// Oggetto che viene riempito con l'action da annullare
	private Vector<GRObject> grobjSelected;
	private Vector<GRObject> grobjCopy;
	
	// Riferimenti agli oggetti utilizzati durante l'apertura di un documento preesistente
	GRTableListCell refCell;
	
	// Tabella delle proprietà degli oggetti
	GRTableProperty panelProperty;
	GRToolBarDesigner grtoolbar;
	GRToolBarStrumenti grtoolbarStrumenti;
	
	// Cursori
	Cursor defaultCursor;
	Cursor TSXBDXCursor;
	Cursor TDXBSXCursor;
	
	// Tastiera
	private boolean CTRL_PRESS	= false;
	
	// Rendering degli oggetti grafici
	private AlphaComposite composite;	// Canale Alpha per la trasparenza degli oggetti
	
	// FLAG
	private boolean flagPartito = false;
	
	// POSIZIONE RELATIVA DEGLI OGGETTI
	public int hPosition;
	
	// POPUP MENU
	private JPopupMenu menuCell;
	private JMenuItem menuCellUnisciCella;
	private JMenuItem menuCellPropertyCell;
	private JMenuItem menuCellPropertyTableList;
	
	private JPopupMenu menuRectangle;
	private JMenuItem menuRectangleInsertText;
	
	private JPopupMenu menuObjSelected;
	private JMenuItem menuObjSelectedRaggruppa;
	
	// Riferimenti per gli oggetti che hanno finestre di proprietà Dialog
	private GRTableListCell grcellSelected;
	
	public GRPage(GRDocument doc,GRTableProperty property, GRToolBarDesigner grtoolbar, GRToolBarStrumenti grtoolbarStrumenti) {
		this(doc,property,grtoolbar,grtoolbarStrumenti,GRSetting.WIDTHPAGE,GRSetting.HEIGHTPAGE);
	}
	
	public GRPage(GRDocument doc,GRTableProperty property, GRToolBarDesigner grtoolbar, GRToolBarStrumenti grtoolbarStrumenti, int width, int height) {
		grdoc = doc;
		panelProperty = property;
		this.grtoolbar = grtoolbar;
		this.grtoolbarStrumenti = grtoolbarStrumenti;
		this.width = width;
		this.height = height;
		
		composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f);
		
		viewGrid = true;
		activeGrid = true;	
		zoom = 1;
		
		heightHeader = 0;
		heightFooter = 0;
		
		setSize(width,height);
		setBackground(Color.white);
		setBorder(new MatteBorder(1,1,1,1,Color.black));
		setLocation(20,20);
		setLayout(null);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		
		grobj = new Vector<GRObject>();
		refObj = null;
		refObjCopy = null;
		grobjSelected = null;
		grobjCopy = null;
		
		// Istanzio i cursori che andrò ad utilizzare
		defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		TSXBDXCursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
		TDXBSXCursor = new Cursor(Cursor.NE_RESIZE_CURSOR);
		
		panelProperty.setPage(this);
		panelProperty.setModel(GRTableProperty.TYPEMODEL_PAGE);
		GRTableModelPage modelPage = (GRTableModelPage)panelProperty.getTable();
		modelPage.setHeader(heightHeader);
		modelPage.setFooter(heightFooter);
		
		hPosition = 0;
		
		idObj = 1;
		
		grdoc.manageMenu(GREditor.MENUVOICE_CANCELLA, false);
		
		restoreToolBar();
		
		initMenu();		
	}

	private void initMenu() {
		/* POPUP CELLA DELLA TABLELIST */
		menuCell = new JPopupMenu();
		
		menuCellUnisciCella = new JMenuItem(GRLanguageMessage.messages.getString("menupopupcellunion"));
		menuCellUnisciCella.addActionListener(this);
		menuCell.add(menuCellUnisciCella);
		
		menuCell.addSeparator();
		
		menuCellPropertyCell = new JMenuItem(GRLanguageMessage.messages.getString("menupopupcellproperty"));
		menuCellPropertyCell.addActionListener(this);
		menuCell.add(menuCellPropertyCell);
		
		menuCellPropertyTableList = new JMenuItem(GRLanguageMessage.messages.getString("menupopuptableproperty"));
		menuCellPropertyTableList.addActionListener(this);
		menuCell.add(menuCellPropertyTableList);
		
		/* POPUP RECTANGLE */
		menuRectangle = new JPopupMenu();
		
		menuRectangleInsertText = new JMenuItem(GRLanguageMessage.messages.getString("menupopuprectangleinserttext"));
		menuRectangleInsertText.addActionListener(this);
		menuRectangle.add(menuRectangleInsertText);
		
		/* POPUP OGGETTI SELEZIONATI */
		menuObjSelected = new JPopupMenu();
		
		menuObjSelectedRaggruppa = new JMenuItem(GRLanguageMessage.messages.getString("menupopupobjselectedgroups"));
		menuObjSelectedRaggruppa.addActionListener(this);
		menuObjSelected.add(menuObjSelectedRaggruppa);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		Color oldColor = g.getColor();
		
		// Griglia
		if(viewGrid) {
			g.setColor(Color.LIGHT_GRAY);
			for(int x = GAPGRIDX;x < (width * (int)zoom);x = x + GAPGRIDX) {
				for(int y = GAPGRIDY;y < (height * (int)zoom);y = y + GAPGRIDY) {
					g.drawLine(x-1,y,x+1,y);
					g.drawLine(x,y-1,x,y+1);
				}
			}
			g.setColor(Color.BLACK);
		}
		g.setColor(oldColor);
		
		// HEAD
		if(heightHeader > 0) {
			g.setColor(Color.BLUE);
			g.drawLine(0,heightHeader,this.getWidth(),heightHeader);
			g.setColor(Color.BLACK);
		}
		g.setColor(oldColor);
		
		// FOOT
		if(heightFooter > 0) {
			g.setColor(Color.BLUE);
			g.drawLine(0,(this.getHeight() - heightFooter),this.getWidth(),(this.getHeight() - heightFooter));
		}
		g.setColor(oldColor);
		
		hPosition = 0;
		for(int i = 0;i < grobj.size();i++) {
			if((flagAzione != GRPage.ACTION_RESIZE_TSX &&
				flagAzione != GRPage.ACTION_RESIZE_TDX &&
				flagAzione != GRPage.ACTION_RESIZE_BSX &&
				flagAzione != GRPage.ACTION_RESIZE_BDX) || refObj != grobj.get(i)) { 
				int type = grobj.get(i).getType();
				
				switch(type) {
					case GRObject.TYPEOBJ_TEXT:
						GRText refText = (GRText)grobj.get(i);
						refText.draw(g);
						
						break;
					
					case GRObject.TYPEOBJ_LINE:
						GRLine refLine = (GRLine)grobj.get(i);
						refLine.draw(g);
						
						break;
						 
					case GRObject.TYPEOBJ_RECTANGLE:
						GRRectangle refRect = (GRRectangle)grobj.get(i);
						refRect.draw(g);
						
						break;
					
					case GRObject.TYPEOBJ_CIRCLE:
						GRCircle refCircle = (GRCircle)grobj.get(i);
						refCircle.draw(g);
						
						break;
					
					case GRObject.TYPEOBJ_IMAGE:
						GRImage refImage = (GRImage)grobj.get(i);
						refImage.draw(g);
						
						break;
						
					case GRObject.TYPEOBJ_LIST:
						GRList refList = (GRList)grobj.get(i);
						refList.draw(g);
						
						break;
						
					case GRObject.TYPEOBJ_TABLELIST:
						GRTableList refTableList = (GRTableList)grobj.get(i);
						refTableList.draw(g);
						
						break;
						
					case GRObject.TYPEOBJ_CHART:
						GRChart refChart = (GRChart)grobj.get(i);
						refChart.draw(g);
						
						break;
					
					case GRObject.TYPEOBJ_GROUP:
						GRGroup refGroup = (GRGroup)grobj.get(i);
						refGroup.draw(g);
						
						break;
				}
				
			}			
		}
		
		
		/*
		if(flagAzione == GRPage.ACTION_SELECT && refObj == null) {
			drawSelection(g);
		}
		*/
		if(flagAzione == GRPage.ACTION_DRAWLINE) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawLine(g);
		} else if(flagAzione == GRPage.ACTION_DRAWRECTANGLE) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawRectangle(g);
		} else if(flagAzione == GRPage.ACTION_DRAWCIRCLE) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawCircle(g);
		} else if(flagAzione == GRPage.ACTION_DRAWTEXT) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawText(g);
		} else if(flagAzione == GRPage.ACTION_DRAWIMAGE) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawRectangle(g);
		} else if(flagAzione == GRPage.ACTION_DRAWLIST) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawList(g);
		} else if(flagAzione == GRPage.ACTION_DRAWTABLELIST) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawRectangle(g);
		} else if(flagAzione == GRPage.ACTION_DRAWCHART) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawRectangle(g);
		} else if(flagAzione == GRPage.ACTION_DRAWGROUP) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawGroup(g);
		} else if(flagAzione == GRPage.ACTION_DRAWTEXTCONDITION) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawText(g);
		} else if(flagAzione == ACTION_MULTISELECT) {
			if(!flagPartito) {
				flagPartito = true;
				return;
			}
			drawSelection(g);
		} else if(flagAzione == GRPage.ACTION_RESIZE_TSX || 
				  flagAzione == GRPage.ACTION_RESIZE_BDX ||
				  flagAzione == GRPage.ACTION_RESIZE_TDX ||
				  flagAzione == GRPage.ACTION_RESIZE_BSX) {
			resizeRectangle(g);
		}
				
	}
	private void resizeRectangle(Graphics g) {
		int x1,y1,x2,y2;
		int width,height;
		
		if(xStart > xEnd) {
			x1 = xEnd;
			x2 = xStart;
		} else {
			x1 = xStart;
			x2 = xEnd;
		}
		width = x2 - x1;
			
		if(yStart > yEnd) {
			y1 = yEnd;
			y2 = yStart;
		} else {
			y1 = yStart;
			y2 = yEnd;
		}
		height = y2 - y1;
		
		g.drawRect(x1,y1,width,height);
	}
	private void drawSelection(Graphics g) {
		int x1,y1,x2,y2;
		int width,height;
		Graphics2D g2d = (Graphics2D)g;
		
		BasicStroke strokeSelection = new BasicStroke(0.5f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,4,new float[]{5,5,5},2);
		Stroke oldStroke = g2d.getStroke();
		
		if(xStart > xEnd) {
			x1 = xEnd;
			x2 = xStart;
		} else {
			x1 = xStart;
			x2 = xEnd;
		}
		width = x2 - x1;
			
		if(yStart > yEnd) {
			y1 = yEnd;
			y2 = yStart;
		} else {
			y1 = yStart;
			y2 = yEnd;
		}
		height = y2 - y1;
		
		g2d.setColor(Color.GRAY);
		g2d.setStroke(strokeSelection);
		g2d.drawRect(x1,y1,width,height);
		
		g2d.setColor(Color.BLACK);
		g2d.setStroke(oldStroke);
	}
	private void drawLine(Graphics g) {
		g.drawLine(xStart, yStart, xEnd, yEnd);
	}
	private void drawRectangle(Graphics g) {
		
		int x1,y1,x2,y2;
		int width,height;
		
		if(xStart > xEnd) {
			x1 = xEnd;
			x2 = xStart;
		} else {
			x1 = xStart;
			x2 = xEnd;
		}
		width = x2 - x1;
			
		if(yStart > yEnd) {
			y1 = yEnd;
			y2 = yStart;
		} else {
			y1 = yStart;
			y2 = yEnd;
		}
		height = y2 - y1;
			
		g.drawRect(x1,y1,width,height);
	}
	private void drawCircle(Graphics g) {
		int x1,y1,x2,y2;
		int x,y;
		int width,height;
		int raggio;
		
		if(xStart > xEnd) {
			x1 = xEnd;
			x2 = xStart;
		} else {
			x1 = xStart;
			x2 = xEnd;
		}
		width = x2 - x1;
			
		if(yStart > yEnd) {
			y1 = yEnd;
			y2 = yStart;
		} else {
			y1 = yStart;
			y2 = yEnd;
		}
		height = y2 - y1;
		
		if(width > height) {
			raggio = width;
		} else {
			raggio = height;
		}
		x = xStart - raggio;
		y = yStart - raggio;
		
		g.drawOval(x, y, raggio * 2, raggio * 2);
	}
	private void drawText(Graphics g) {
		int x1,x2;
		int width;
		
		if(xStart > xEnd) {
			x1 = xEnd;
			x2 = xStart;
		} else {
			x1 = xStart;
			x2 = xEnd;
		}
		width = x2 - x1;
		
		Color oldC = g.getColor();
		g.setColor(Color.GRAY);
		g.drawRect(x1,yStart,width,30);
		g.setColor(oldC);
	}
	private void drawList(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		int y1,y2;
		int height;
		
		if(yStart > yEnd) {
			y1 = yEnd;
			y2 = yStart;
		} else {
			y1 = yStart;
			y2 = yEnd;
		}
		height = y2 - y1;
		
		Color oldC = g.getColor();
		g.setColor(Color.BLUE);
		Composite compositeOld = g2d.getComposite();
		
		g.drawRect(0,y1,630,height);
		
		g2d.setComposite(composite);
		g2d.setPaint(Color.BLUE);
		g2d.fill(new Rectangle(0,y1,630,height));
		
		g2d.setComposite(compositeOld);
		g.setColor(oldC);
	}
	private void drawGroup(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		int y1,y2;
		int height;
		
		if(yStart > yEnd) {
			y1 = yEnd;
			y2 = yStart;
		} else {
			y1 = yStart;
			y2 = yEnd;
		}
		height = y2 - y1;
		
		Color oldC = g.getColor();
		Color newC = new Color(140,9,9);
		g.setColor(newC);
		Composite compositeOld = g2d.getComposite();
		
		g.drawRect(0,y1,630,height);
		
		g2d.setComposite(composite);
		g2d.setPaint(newC);
		g2d.fill(new Rectangle(0,y1,630,height));
		
		g2d.setComposite(compositeOld);
		g.setColor(oldC);
	}
	private void clearAction() {
		clearSelected();
		
		flagAzione = GRPage.ACTION_NULL;
		
	}
	public void clearSelected() {
		for(int i = 0;i < grobj.size();i++)
			grobj.get(i).setSelected(false);
			
		refObj = null;
		grobjSelected = null;
		grdoc.manageMenu(GREditor.MENUVOICE_CANCELLA, false);
		grtoolbarStrumenti.setBackwardEnabled(false);
		grtoolbarStrumenti.setForwardEnabled(false);
		
		this.setPageProperty();
		
	}
	private void setPageProperty() {
		panelProperty.setPage(this);
		
		panelProperty.setModel(GRTableProperty.TYPEMODEL_PAGE);
		GRTableModelPage modelPage = (GRTableModelPage)panelProperty.getTable();
		modelPage.setHeader(heightHeader);
		modelPage.setFooter(heightFooter);
	}
	public void setAction(int value) {
		clearAction();
		
		switch(value) {
			case GRToolBar.TYPEBUTTON_SELECTED:
				flagAzione = GRPage.ACTION_SELECT;
				break;
			
			case GRToolBar.TYPEBUTTON_TEXT:
				flagAzione = GRPage.ACTION_DRAWTEXT;
				break;
			
			case GRToolBar.TYPEBUTTON_LINE:
				flagAzione = GRPage.ACTION_DRAWLINE;
				break;
				
			case GRToolBar.TYPEBUTTON_RECTANGLE:
				flagAzione = GRPage.ACTION_DRAWRECTANGLE;
				break;
			
			case GRToolBar.TYPEBUTTON_CIRCLE:
				flagAzione = GRPage.ACTION_DRAWCIRCLE;
				break;
				
			case GRToolBar.TYPEBUTTON_IMAGE:
				flagAzione = GRPage.ACTION_DRAWIMAGE;
				break;
				
			case GRToolBar.TYPEBUTTON_LIST:
				flagAzione = GRPage.ACTION_DRAWLIST;
				break;
				
			case GRToolBar.TYPEBUTTON_TABLELIST:
				flagAzione = GRPage.ACTION_DRAWTABLELIST;
				break;
				
			case GRToolBar.TYPEBUTTON_CHART:
				flagAzione = GRPage.ACTION_DRAWCHART;
				break;
				
			case GRToolBar.TYPEBUTTON_GROUP:
				flagAzione = GRPage.ACTION_DRAWGROUP;
				break;
			
			case GRToolBar.TYPEBUTTON_TEXTCONDITION:
				flagAzione = GRPage.ACTION_DRAWTEXTCONDITION;
				break;
		}
		
	}
	public void setActionText(int value) {
		if(refObj != null && refObj instanceof GRText) {
			GRText refText = (GRText)refObj;
			
			switch(value) {
				case GRToolBarStrumenti.TYPEBUTTON_TEXTALIGN_LEFT:
					refText.setFontAlignment(GRText.ALIGNTEXT_LEFT);
					
					break;
					
				case GRToolBarStrumenti.TYPEBUTTON_TEXTALIGN_CENTER:
					refText.setFontAlignment(GRText.ALIGNTEXT_CENTER);
					
					break;
					
				case GRToolBarStrumenti.TYPEBUTTON_TEXTALIGN_RIGHT:
					refText.setFontAlignment(GRText.ALIGNTEXT_RIGHT);
					
					break;
					
				case GRToolBarStrumenti.TYPEBUTTON_TEXTALIGN_JUSTIFY:
					refText.setFontAlignment(GRText.ALIGNTEXT_JUSTIFY);
					
					break;
			}
			
			refText.refresh();
			this.repaint();
		}
		
	}
	public double getZoom() {
		return zoom;
	}
	public void settings(int type, boolean value) {
		
		switch(type) {
			case GRToolBarStrumenti.TYPEBUTTON_GRID:
				viewGrid = value;
				repaint();
				break;
				
			case GRToolBarStrumenti.TYPEBUTTON_ANCHORGRID:
				activeGrid = value;
				break;
		}
	}
	public void refreshHeader(int value) {
		grdoc.setHeaderPage(value);
		
		this.releaseHeader();
	}
	public void refreshFooter(int value) {
		grdoc.setFooterPage(value);
		
		//this.releaseFooter();
	}
	public void modifyHeader(int value) {
		if(value < 0)
			value = 0;
		heightHeader = value;
		
		((GRTableModelPage)panelProperty.getTable()).setHeader(value);
		
	}
	public void modifyFooter(int value) {
		if(value < 0)
			value = 0;
		heightFooter = value;
		
		((GRTableModelPage)panelProperty.getTable()).setFooter(value);
	}
	public int getHeaderSize() {
		return heightHeader;
	}
	public double getHeaderSizeToMM() {
		return heightHeader / 3;
	}
	public int getFooterSize() {
		return heightFooter;
	}
	public double getFooterSizeToMM() {
		return heightFooter / 3;
	}
	private GRGroup isGroup(int x, int y) {
		// Verifica se le coordinate passate sono interne ad un oggetto GRGROUP
		// Se le trova ritorna l'oggetto altrimenti null
		
		for(int i = (grobj.size()-1);i >= 0;i--) {
			if(grobj.get(i).getType() == GRObject.TYPEOBJ_GROUP) {
				if(grobj.get(i).isIntersect(x, y))
					return (GRGroup)grobj.get(i);
			}
		}
		
		return null;
	}
	private void selectObj(GRObject grobj) {
		if(grobjSelected == null) 
			grobjSelected = new Vector<GRObject>();
		
		grobjSelected.add(grobj);
		
		grobj.setSelected(true);
		if(grobjSelected.size() == 1) {
			panelProperty.setModel(grobj.getTypeModel());
			grobj.setProperty(panelProperty.getTable());
		} else {
			this.setPageProperty();
		}
	}
	/* OLD VERSION
	private void selectObj(GRObject grObj, int x, int y) {
		
		refObj = grObj;
		refObj.setSelected(true);	   
		refObj.setCoordsSelected(x,y);
		
		if(refObj instanceof GRLine) {
			GRLine grline = (GRLine)refObj;
			
			panelProperty.setModel(GRTableProperty.TYPEMODEL_LINE);
			grline.setProperty(panelProperty.getTable());
						
			grline.setLocation(grline.getX(),grline.getY());
		} else if(refObj instanceof GRRectangle) {
			GRRectangle grrect = (GRRectangle)refObj;
			
			panelProperty.setModel(GRTableProperty.TYPEMODEL_RECTANGLE);
			grrect.setProperty(panelProperty.getTable());
			
			grrect.setLocation(grrect.getX(),grrect.getY());
		} else if(refObj instanceof GRCircle) {
			GRCircle grcircle = (GRCircle)refObj;
			
			panelProperty.setModel(GRTableProperty.TYPEMODEL_CIRCLE);
			grcircle.setProperty(panelProperty.getTable());
			
			grcircle.setLocation(grcircle.getX(),  grcircle.getY());
		} else if(refObj instanceof GRText) {
			GRText grtext = (GRText)refObj;
			
			panelProperty.setModel(GRTableProperty.TYPEMODEL_TEXT);
			grtext.setProperty(panelProperty.getTable());
			
			grtext.setLocation(grtext.getX(),grtext.getY());
		} else if(refObj instanceof GRImage) {
			GRImage grimage = (GRImage)refObj;
			
			panelProperty.setModel(GRTableProperty.TYPEMODEL_IMAGE);
			grimage.setProperty(panelProperty.getTable());
								
			grimage.setLocation(grimage.getX(),grimage.getY());
		} else if(refObj instanceof GRList) {
			GRList grlist = (GRList)refObj;
			
			panelProperty.setModel(GRTableProperty.TYPEMODEL_LIST);
			grlist.setProperty(panelProperty.getTable());
			
			grlist.setLocation(grlist.getX(),grlist.getY());			
		} else if(refObj instanceof GRTableList) {
			GRTableList grtablelist = (GRTableList)refObj;
			
			panelProperty.setModel(GRTableProperty.TYPEMODEL_TABLELIST);
			grtablelist.setProperty(panelProperty.getTable());
			
			grtablelist.setLocation(grtablelist.getX(), grtablelist.getY());
		} else if(refObj instanceof GRChart) {
			GRChart grchart = (GRChart)refObj;
			
			panelProperty.setModel(GRTableProperty.TYPEMODEL_CHART);
			grchart.setProperty(panelProperty.getTable());
			
			grchart.setLocation(grchart.getX(),grchart.getY());
		} else if(refObj instanceof GRGroup) {
			GRGroup grgroup = (GRGroup)refObj;
			
			panelProperty.setModel(GRTableProperty.TYPEMODEL_GROUP);
			grgroup.setProperty(panelProperty.getTable());
			
			grgroup.setLocation(grgroup.getX(),grgroup.getY());	
		}
		
		if(grobj.size() > 1) {
			grtoolbarStrumenti.setBackwardEnabled(true);
			grtoolbarStrumenti.setForwardEnabled(true);
		}
		
	}
	*/
	public void mouseClicked(MouseEvent me) {
		if(me.getClickCount() == 2) {
			if(grobjSelected != null && grobjSelected.size() == 1) {
				if(grobjSelected.get(0) instanceof GRText) {
					GRText refText = (GRText)grobjSelected.get(0);
					GREditText et = new GREditText(this, new Rectangle(xStart, yStart, xEnd-xStart, yEnd-yStart));
					
					et.setData(refText.getId(), refText.getTextFormatted());
					et.showDialog(GREditText.MODIFYTEXT);
				} else if(grobjSelected.get(0) instanceof GRTableList) {
					GRTableList refTable = (GRTableList)grobjSelected.get(0);
					
					if(refTable.isCellIntersect(me.getX(),me.getY())) {
						refCell = refTable.getCellSelected();
						Rectangle r = refCell.getCellArea();
						
						GREditText et = new GREditText(this, r, GREditText.CONTEXT_TABLELIST);
						
						if(refCell.getText() == null)
							et.setFont(grtoolbarStrumenti.getFontName(),grtoolbarStrumenti.getFontSize(),grtoolbarStrumenti.getFontStyle());
						else
							et.setData(refTable.getId(),refCell.getText().getTextFormatted());
						et.showDialog(GREditText.NEWTEXT);
						
					}
				}
			}
		}
		
	}
	public void mouseEntered(MouseEvent me) {
	}
	public void mouseExited(MouseEvent me) {
	}
	public void mousePressed(MouseEvent me) {
		xStart = me.getX();
		yStart = me.getY();
		
		if(activeGrid) {
			if(xStart % GAPGRIDX > (GAPGRIDX / 2)) {
				xStart = (xStart - (xStart % GAPGRIDX)) + GAPGRIDX;
			} else {
				xStart = xStart - (xStart % GAPGRIDX);
			}
			if(yStart % GAPGRIDY > (GAPGRIDY / 2)) {
				yStart = (yStart - (yStart % GAPGRIDY)) + GAPGRIDY;
			} else {
				yStart = yStart - (yStart % GAPGRIDY);
			}
		}
		
		if(flagAzione == ACTION_SELECT) {
			boolean flagSelected = false;
			
			if(!CTRL_PRESS)
				clearSelected();
			
			// Cicla per tutti gli oggetti in memoria.
			// Il primo oggetto che viene intersecato, lo seleziona. Parte dall'ultimo inserito
			for(int i = (grobj.size()-1);i >= 0;i--) {
				if(!grobj.get(i).getSelected()) {
					if(grobj.get(i).isIntersect(me.getX(),me.getY())) {
						
						this.selectObj(grobj.get(i));
						
						grdoc.manageMenu(GREditor.MENUVOICE_CANCELLA, true);
						grdoc.setMenuVoiceEnabled(GREditor.MENUTYPE_MODIFICA, 3, true);
						grdoc.setMenuVoiceEnabled(GREditor.MENUTYPE_MODIFICA, 4, true);
						flagSelected = true;
						break;
					}
				}
			}
			
			if(!flagSelected) {
				grdoc.setMenuVoiceEnabled(GREditor.MENUTYPE_MODIFICA, 3, false);
				
				xEnd = xStart;
				yEnd = yStart;
				
				if(grobjSelected == null)
					flagAzione = ACTION_MULTISELECT;
			}
			
			repaint();
			
		} 
	}
	public void mouseReleased(MouseEvent me) {
		xEnd = me.getX();
		yEnd = me.getY();
		
		if(me.isPopupTrigger()) {
			if(grobjSelected != null) {
				if(grobjSelected.size() == 1) {
					if(grobjSelected.get(0) instanceof GRTableList) {
						GRTableList refTable = (GRTableList)refObj;
						grcellSelected = refTable.getCellSelected();
						
						if(grcellSelected != null) {
							
							menuCell.show(me.getComponent(),me.getX(),me.getY());
						}
					} else if(grobjSelected.get(0) instanceof GRRectangle) {
						menuRectangle.show(me.getComponent(),me.getX(),me.getY());
					}
				} else if(grobjSelected.size() > 1) {
					menuObjSelected.show(me.getComponent(),me.getX(),me.getY());
				}
			}
		}
		
		if(activeGrid) {
			if(xEnd % GAPGRIDX > (GAPGRIDX / 2)) {
				xEnd = (xEnd - (xEnd % GAPGRIDX)) + GAPGRIDX;
			} else {
				xEnd = xEnd - (xEnd % GAPGRIDX);
			}
			if(yEnd % GAPGRIDY > (GAPGRIDY / 2)) {
				yEnd = (yEnd - (yEnd % GAPGRIDY)) + GAPGRIDY;
			} else {
				yEnd = yEnd - (yEnd % GAPGRIDY);
			}
		}
				
		if(flagAzione == GRPage.ACTION_SELECT) {
			
			if(refObj == null) {
				// Verifica se sono stati selezionati degli oggetti
				/*
				for(int i = (grobj.size()-1);i >= 0;i--) {
					grobj.get(i).setSelected(true);	   
					grobj.get(i).setCoordsSelected(me.getX(),me.getY());
				}
				*/
				/*
				for(int i = (grobj.size()-1);i >= 0;i--) {
					if(grobj.get(i).isIntersect(me.getX(),me.getY())) {
						this.selectObj(grobj.get(i), me.getX(), me.getY());
						idObjSelected = i;
						grdoc.manageMenu(GREditor.MENUVOICE_CANCELLA, true);
						
						
						break;
					}
				}
				*/
				//System.out.println(xStart+" - "+xEnd+" - "+Math.abs(xEnd-xStart));
				/*
				xStart = xEnd;
				yStart = yEnd;
				
				flagAzione = GRPage.ACTION_MULTISELECT;
				
				repaint();
				*/
			} else {
				//refObj.setX(refObj.getX() / (int)zoom);
				//refObj.setY(refObj.getY() / (int)zoom);
				
				//refObj.setX(refObj.getX());
				//refObj.setY(refObj.getY());
				
				refObj.setOriginalX(refObj.getX());
				refObj.setOriginalY(refObj.getY());
				
				if(refObj instanceof GRLine) {
					GRLine refLine = (GRLine)refObj;
					
					refLine.setOriginalXEnd(refLine.getXEnd());
					refLine.setOriginalYEnd(refLine.getYEnd());
				}
			}
		} else if(flagAzione == ACTION_MULTISELECT) {
			// Procede a selezionare tutti gli oggetti che rientrano nell'area
				
			Rectangle area = new Rectangle(xStart,yStart,xEnd-xStart,yEnd-yStart);
			for(int i = (grobj.size()-1);i >= 0;i--) {
							
				if(grobj.get(i).isAreaIntersect(area)) {
								
					this.selectObj(grobj.get(i));
								
					if(grobjSelected == null)
						grobjSelected = new Vector<GRObject>();
								
					grobjSelected.add(grobj.get(i));
					
				}
			}
			
			flagAzione = ACTION_SELECT;
			
			repaint();
		} else if(flagAzione == GRPage.ACTION_DRAWLINE) {
			if(Math.abs(xEnd-xStart) < GRSetting.MIN_DIMENSION_OBJ &&
					Math.abs(yEnd - yStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			
			//GRLine refLine = new GRLine(this,idObj,xStart,yStart,xEnd,yEnd,grtoolbarStrumenti.getColorStroke());
			GRLine refLine = new GRLine(this,idObj,(int)(xStart / zoom),(int)(yStart / zoom),(int)(xEnd / zoom),(int)(yEnd / zoom),grtoolbarStrumenti.getColorStroke());
			refLine.setZoom(zoom);		
			
			grobj.add(refLine);
			idObj++;
			
			restoreToolBar();
			repaint();
		} else if(flagAzione == GRPage.ACTION_DRAWRECTANGLE) {
			if(Math.abs(xEnd-xStart) < GRSetting.MIN_DIMENSION_OBJ ||
					Math.abs(yEnd - yStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			
			GRRectangle refRect = new GRRectangle(this,idObj,(int)(xStart / zoom),(int)(yStart / zoom),(int)(xEnd / zoom),(int)(yEnd / zoom),grtoolbarStrumenti.getColorStroke(),grtoolbarStrumenti.getColorFill());
			refRect.setZoom(zoom);
			
			GRGroup grgroup = isGroup(xStart, yStart);
			if(grgroup != null)
				grgroup.addObject(refRect);
			else {
				grobj.add(refRect);
				idObj++;
			}
			
			restoreToolBar();
			repaint();
		} else if(flagAzione == GRPage.ACTION_DRAWCIRCLE) {
			if(Math.abs(xEnd-xStart) < GRSetting.MIN_DIMENSION_OBJ &&
					Math.abs(yEnd - yStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			
			GRCircle refCircle = new GRCircle(this,idObj,(int)(xStart / zoom),(int)(yStart / zoom),(int)(xEnd / zoom),(int)(yEnd / zoom),grtoolbarStrumenti.getColorStroke(),grtoolbarStrumenti.getColorFill());
			refCircle.setZoom(zoom);
			
			grobj.add(refCircle);
			idObj++;
			
			restoreToolBar();
			repaint();
		} else if(flagAzione == GRPage.ACTION_DRAWTEXT) {
			if(Math.abs(xEnd-xStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			GREditText et = new GREditText(this, new Rectangle(xStart, yStart, xEnd-xStart, yEnd-yStart));
			
			et.setFont(grtoolbarStrumenti.getFontName(),grtoolbarStrumenti.getFontSize(),grtoolbarStrumenti.getFontStyle());
			et.showDialog(GREditText.NEWTEXT);
			
		} else if(flagAzione == GRPage.ACTION_DRAWIMAGE) {
			if(Math.abs(xEnd-xStart) < GRSetting.MIN_DIMENSION_OBJ ||
					Math.abs(yEnd - yStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("Images files","jpg","png"));
			fc.setCurrentDirectory(new File("."));
			int r = fc.showOpenDialog(this);
			
			if(r == JFileChooser.APPROVE_OPTION) {
				GRImage refImage = new GRImage(idObj,this,fc.getSelectedFile().toString(),xStart,yStart,xEnd,yEnd);
				refImage.setIdImage(grdoc.getImgIdResources(refImage));
				
				grobj.add(refImage);
				idObj++;
			}
			restoreToolBar();
			repaint();
		} else if(flagAzione == GRPage.ACTION_DRAWLIST) {
			if(Math.abs(yEnd - yStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			
			GRList refList = new GRList(this,idObj,yStart,yEnd);
			//refList.setZoom(zoom);
			
			grobj.add(refList);
			idObj++;
			
			restoreToolBar();
			repaint();
			
			panelProperty.addListFather(refList.getNameXml());
			
		} else if(flagAzione == GRPage.ACTION_DRAWTABLELIST) {
			if(Math.abs(xEnd-xStart) < GRSetting.MIN_DIMENSION_OBJ ||
					Math.abs(yEnd - yStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			
			new GRDialogCreateTableList(null,this,xStart,yStart,xEnd,yEnd);
		} else if(flagAzione == GRPage.ACTION_DRAWCHART) {
			if(Math.abs(xEnd-xStart) < GRSetting.MIN_DIMENSION_OBJ ||
					Math.abs(yEnd - yStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			
			new GRDialogCreateChart(this,xStart,yStart,xEnd,yEnd);
		} else if(flagAzione == GRPage.ACTION_DRAWGROUP) {
			if(Math.abs(yEnd - yStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			
			GRGroup refGroup = new GRGroup(this,idObj,yStart,yEnd);
			
			grobj.add(refGroup);
			idObj++;
			
			restoreToolBar();
			repaint();	
		} else if(flagAzione == GRPage.ACTION_DRAWTEXTCONDITION) {
			if(Math.abs(xEnd-xStart) < GRSetting.MIN_DIMENSION_OBJ)
				return;
			GRDialogTextCondition tc = new GRDialogTextCondition(this, new Rectangle(xStart, yStart, xEnd-xStart, yEnd-yStart));
			
			//et.setFont(grtoolbarStrumenti.getFontName(),grtoolbarStrumenti.getFontSize(),grtoolbarStrumenti.getFontStyle());
			tc.showDialog(GRDialogTextCondition.NEWTEXT);
		} else if(flagAzione == GRPage.ACTION_SELECT && refObj != null) {
			this.selectObj(refObj);	
		} else if(flagAzione == GRPage.ACTION_RESIZE_TSX ||
				  flagAzione == GRPage.ACTION_RESIZE_TDX ||
				  flagAzione == GRPage.ACTION_RESIZE_BSX ||
				  flagAzione == GRPage.ACTION_RESIZE_BDX) {
			
			if(flagAzione == GRPage.ACTION_RESIZE_TSX) {
				xStart = me.getX();
				yStart = me.getY();
			
				xEnd = refObj.getX()+refObj.getWidth();
				yEnd = refObj.getY()+refObj.getHeight();
			} else if(flagAzione == GRPage.ACTION_RESIZE_BDX) {
				xStart = refObj.getX();
				yStart = refObj.getY();
				
				xEnd = me.getX();
				yEnd = me.getY();
			} else if(flagAzione == GRPage.ACTION_RESIZE_TDX) {
				xStart = refObj.getX();
				yStart = me.getY();
				
				xEnd = me.getX();
				yEnd = refObj.getY()+refObj.getHeight();
			} else if(flagAzione == GRPage.ACTION_RESIZE_BSX) {
				xStart = me.getX();
				yStart = refObj.getY();
				
				xEnd = refObj.getX()+refObj.getWidth();
				yEnd = me.getY();
			}
			
			switch(refObj.getType()) {
				case GRObject.TYPEOBJ_RECTANGLE:
					GRRectangle ref = (GRRectangle)refObj;
					ref.resize(xStart,yStart,xEnd,yEnd);
					
					break;
			}
			
			this.selectObj(refObj);	
		}
		requestFocusInWindow();
	}
	
	private void moveObjects(int x, int y) {
		if(grobjSelected == null)
			return;
		
		for(int i = 0;i < grobjSelected.size();i++) {
			if(y == -1)
				grobjSelected.get(i).translateX(x);
			else if(x == -1)
				grobjSelected.get(i).translateY(y);
			else
				grobjSelected.get(i).moveTo(x, y);
		}
	}
	public void mouseMoved(MouseEvent me) {
		if(refObj != null) {
			int anchor = refObj.isAnchor(me.getX(),me.getY());
			
			if(anchor == GRObject.ANCHOR_TSX) {
				setCursor(TSXBDXCursor);
				flagAzione = GRPage.ACTION_RESIZE_TSX;
			} else if(anchor == GRObject.ANCHOR_BDX) {
				setCursor(TSXBDXCursor);
				flagAzione = GRPage.ACTION_RESIZE_BDX;
			} else if(anchor == GRObject.ANCHOR_TDX) {
				setCursor(TDXBSXCursor);
				flagAzione = GRPage.ACTION_RESIZE_TDX;
			} else if(anchor == GRObject.ANCHOR_BSX) {
				setCursor(TDXBSXCursor);
				flagAzione = GRPage.ACTION_RESIZE_BSX;
			} else {
				setCursor(defaultCursor);
				flagAzione = GRPage.ACTION_SELECT;
			}
			
			if(refObj instanceof GRTableList) {
				GRTableList refTable = (GRTableList)refObj;
				
				refTable.isCellIntersect(me.getX(),me.getY());
				repaint();	
			}
				
		}
			
	}
	public void mouseDragged(MouseEvent me) {
			
		if(grobjSelected != null) {
			if(flagAzione == GRPage.ACTION_RESIZE_TSX) {
				xStart = me.getX();
				yStart = me.getY();
		
				xEnd = refObj.getX()+refObj.getWidth();
				yEnd = refObj.getY()+refObj.getHeight();
			} else if(flagAzione == GRPage.ACTION_RESIZE_BDX) {
				xStart = refObj.getX();
				yStart = refObj.getY();
				
				xEnd = me.getX();
				yEnd = me.getY();
			} else if(flagAzione == GRPage.ACTION_RESIZE_TDX) {
				xStart = refObj.getX();
				yStart = me.getY();
				
				xEnd = me.getX();
				yEnd = refObj.getY()+refObj.getHeight();
			} else if(flagAzione == GRPage.ACTION_RESIZE_BSX) {
				xStart = me.getX();
				yStart = refObj.getY();
				
				xEnd = refObj.getX()+refObj.getWidth();
				yEnd = me.getY();
			} else {
				// Movimento di oggetti
				xEnd = me.getX();
				yEnd = me.getY();
			
				if(activeGrid) {
					// ASSE DELLE X
					if((xEnd - xStart) >= GAPGRIDX) {
						if((xEnd - xStart) % GAPGRIDX > GAPGRIDX / 2)
							this.moveObjects((xEnd - xStart) + (GAPGRIDX - ((xEnd - xStart) % GAPGRIDX)), -1);
						else
							this.moveObjects((xEnd - xStart) - ((xEnd - xStart) % GAPGRIDX), -1);
						
						xStart = xEnd;
					} else if((xStart - xEnd) >= GAPGRIDX) {
						if((xStart - xEnd) % GAPGRIDX > GAPGRIDX / 2)
							this.moveObjects(((xStart - xEnd) + (GAPGRIDX - ((xStart - xEnd) % GAPGRIDX))) * -1, -1);
						else
							this.moveObjects(((xStart - xEnd) - ((xStart - xEnd) % GAPGRIDX)) * -1, -1);
						
						xStart = xEnd;
					}
					
					// ASSE DELLE Y
					if((yEnd - yStart) >= GAPGRIDY) {
						if((yEnd - yStart) % GAPGRIDY > GAPGRIDY / 2)
							this.moveObjects(-1, (yEnd - yStart) + (GAPGRIDY - ((yEnd - yStart) % GAPGRIDY)));
						else
							this.moveObjects(-1, (yEnd - yStart) - ((yEnd - yStart) % GAPGRIDY));
						
						yStart = yEnd;
					} else if((yStart - yEnd) >= GAPGRIDY) {
						if((yStart - yEnd) % GAPGRIDY > GAPGRIDY / 2)
							this.moveObjects(-1, ((yStart - yEnd) + (GAPGRIDY - ((yStart - yEnd) % GAPGRIDY))) * -1);
						else
							this.moveObjects(-1, ((yStart - yEnd) - ((yStart - yEnd) % GAPGRIDY)) * -1);
						
						yStart = yEnd;
					}
					
				} else {
					this.moveObjects(xEnd,yEnd);
				}
				
			
			}
			repaint();
		} else {
			// Disegno di oggetti
			xEnd = me.getX();
			yEnd = me.getY();
				
			if(activeGrid) {
				if(xEnd % GAPGRIDX > (GAPGRIDX / 2)) {
					xEnd = (xEnd - (xEnd % GAPGRIDX)) + GAPGRIDX;
				} else {
					xEnd = xEnd - (xEnd % GAPGRIDX);
				}
				if(yEnd % GAPGRIDY > (GAPGRIDY / 2)) {
					yEnd = (yEnd - (yEnd % GAPGRIDY)) + GAPGRIDY;
				} else {
					yEnd = yEnd - (yEnd % GAPGRIDY);
				}
			}
			
			repaint();
			
		}
	}
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == 17) {
			CTRL_PRESS = true;
		} else if(e.getKeyCode() == 86 && CTRL_PRESS) { // "CTRL-V"
			pasteObject();
		}
		
		if(flagAzione == GRPage.ACTION_SELECT && grobjSelected != null) {
			
			if(e.getKeyCode() == 127) {
				this.clearObject();
			} else if(e.getKeyCode() >= 37 && e.getKeyCode() <= 40) {
				int coord = 1;
				
				switch(e.getKeyCode()) {
					case 37:
						if(activeGrid)
							coord = -1 * GAPGRIDX;
						else
							coord = -1;
						
						refObj.translateX(coord);
						
						break;
					
					case 38:
						if(activeGrid)
							coord = -1 * GAPGRIDY;
						else
							coord = -1;
						
						refObj.translateY(coord);
						
						break;
						
					case 39:
						if(activeGrid)
							coord = GAPGRIDX;
						refObj.translateX(coord);
						
						break;
						
					case 40:
						if(activeGrid)
							coord = GAPGRIDY;
						refObj.translateY(coord);
						
						break;
				}
				
				repaint();
			} else if(e.getKeyCode() == 67 && CTRL_PRESS) { // "CTRL-C"
				copyObject();
			} 
		}
		
	}
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == 17) {
			CTRL_PRESS = false;
		}
	}
	
	/**
	 * Riorganizza l'elemento appena associato alla lista ponendolo, nello storybook,
	 * subito dopo la lista.
	 * Questo diventa necessario per permettere all'utente di poter selezionare
	 * gli oggetti appartenenti alla lista che altrimenti, se disegnati prima, verrebbero
	 * posti sotto la lista stessa.
	 */
	public void refreshObjectList(GRList grlist, GRObject obj) {
		int indexList = grobj.indexOf(grlist);
		int indexObj = grobj.indexOf(obj);
		
		// Se l'indice dell'oggetto è inferiore all'indice della lista, lo posiziona subito dopo
		if(indexObj < indexList) {
			grobj.remove(obj);	
			grobj.insertElementAt(obj,(indexList+1));
		}
		
	}
	/**
	 * Restituisce la lista avente id passato come parametro
	 * 
	 * @param name Il nome della lista da restituire
	 * @return La lista ricercata. Se non è presente all'interno della pagina, ritorna <i>null</i>
	 */
	public GRList getList(String name) {
		for(int i = 0;i < grobj.size();i++) {
			if(grobj.get(i) instanceof GRList) {
				GRList refList = (GRList)grobj.get(i);
				
				if(refList.getNameXml().equals(name))
					return refList;
			}
		}
		
		return null;
	}
	/**
	 * Ritorna un Vector di oggetti associati alla lista il cui id è passato come parametro
	 * 
	 * @param name L'id della lista da ricercare
	 * @return Un Vector di oggetti associati alla lista. Se non è presente alcun oggetto ritorna <i>null</i>
	 */
	public Vector<GRObject> getListChild(String name) {
		Vector<GRObject> child = null;
		
		for(int i = 0;i < grobj.size();i++) {
			if(grobj.get(i).hasListFather()) {
				if(grobj.get(i).getListFather().getNameXml().equals(name)) {
					if(child == null)
						child = new Vector<GRObject>();
					
					child.add(grobj.get(i));
				}
			}
		}
		
		return child;
	}
	public void setZoom(float value) {
		zoom = value;
		
		Dimension d = new Dimension();
		
		d.setSize(width * value,height * value);
		this.setSize(d);
		
		/* Cicla per tutti gli oggetti ed applica lo zoom */
		for(int i = 0;i < grobj.size();i++) {
			grobj.get(i).setZoom(value);
		}
		
	}
	public void releaseHeader() {
		/* Cicla per tutti gli oggetti per aggiornare il riferimento */
		for(int i = 0;i < grobj.size();i++) {
			grobj.get(i).refreshReferenceSection();
		}
			
	}
	public void copyObject() {
		if(grobjSelected == null)
			return;
		
		grobjCopy = new Vector<GRObject>();
		for(int i = 0;i < grobjSelected.size();i++)
			grobjCopy.add(grobjSelected.get(i));
		
		//grdoc.grobjCopy = grobjCopy;
	}
	public void pasteObject() {
		//System.out.println(grdoc.grobjCopy.size());
		if(grobjCopy == null)
			return;
		
		// Esegue la copia di ogni oggetto presente nel Vector
		for(int i = 0;i < grobjCopy.size();i++) {
			GRObject refCopy = grobjCopy.get(i).clone(idObj);
			
			if(refCopy != null) {
				grobj.add(refCopy);
				idObj++;
				
				
			}
		}
		repaint();
		
	}
	public void clearObject() {
		if(grobjSelected == null)
			return;
		
		// Cancella ogni oggetto selezionato
		for(int i = grobjSelected.size()-1;i >= 0;i--) {
			GRObject refObj = grobjSelected.get(i);
			
			// Prima di cancellare l'oggetto riempie l'undo
			//grUndo = new GRUndo(grobj.get(idObjSelected),idObjSelected,GRUndo.ACTION_CLEAROBJ);
			
			if(refObj instanceof GRImage) {
				GRImage refImage = (GRImage)refObj;
				
				grdoc.removeImgResources(refImage.getIdImage());
			} else if(refObj instanceof GRList) {
				String nameList = ((GRList)refObj).getNameXml();
				
				// Cancella tutti gli oggetti associati. Parte dall'ultimo 
				for(int index = grobj.size()-1;index >= 0;index--) {
					if(grobj.get(index).getListFather() != null) {
						if(grobj.get(index).getListFather().getNameXml().equals(nameList)) {
							
							grobj.remove(index);
						}
					}
				}
				
				// Cancella il riferimento alla lista
				panelProperty.removeListFather(nameList);
				
			}
			
			grobj.remove(refObj);
		}
		
		flagAzione = GRPage.ACTION_SELECT;
		
		clearSelected();

		repaint();
		
	}
	
	public void backwardObject() {
		if(grobjSelected == null)
			return;
		/* DA RISCRIVERE
		grUndo = new GRUndo(grobj.get(idObjSelected),idObjSelected,GRUndo.ACTION_BACKWARDOBJ);
		
		GRObject grtemp = refObj;
		int idTemp = idObjSelected;
		grobj.remove(idObjSelected);
		grobj.add(0,refObj);
		
		grtoolbarStrumenti.setBackwardEnabled(false);
		grdoc.manageMenu(GREditor.MENUVOICE_ANNULLA, true, grUndo.getActionUndoToString());
		
		repaint();
		*/
	}
	public void forwardObject() {
		if(grobjSelected == null)
			return;
		/* DA RISCRIVERE
		grUndo = new GRUndo(grobj.get(idObjSelected),idObjSelected,GRUndo.ACTION_FORWARDOBJ);
		
		grobj.remove(idObjSelected);
		grobj.add(grobj.size(),grUndo.getObject());
		
		grtoolbarStrumenti.setBackwardEnabled(false);
		grdoc.manageMenu(GREditor.MENUVOICE_ANNULLA, true, grUndo.getActionUndoToString());
		
		repaint();
		*/
	}
	public void undo() {
		if(grUndo == null)
			return;
		
		switch(grUndo.getActionUndo()) {
			case GRUndo.UNDO_CLEAROBJ:
				grobj.add(grUndo.getLastId(),grUndo.getObject());
				grdoc.manageMenu(GREditor.MENUVOICE_ANNULLA, false, "Annulla");
				
				break;
				
			case GRUndo.UNDO_BACKWARDOBJ:
				grobj.remove(0);
				grobj.add(grUndo.getLastId(),grUndo.getObject());
				grdoc.manageMenu(GREditor.MENUVOICE_ANNULLA, false, "Annulla");
				
				break;
				
			case GRUndo.UNDO_FORWARDOBJ:
				grobj.remove(grobj.size()-1);
				grobj.add(grUndo.getLastId(),grUndo.getObject());
				grdoc.manageMenu(GREditor.MENUVOICE_ANNULLA, false, "Annulla");
				
				break;
		}
		
		grUndo = null;
	
		repaint();
	}
	public void annullaInsertText() {
		restoreToolBar();
		repaint();
	}
	public void annullaInsertTableList() {
		restoreToolBar();
		repaint();
	}
	public void annullaInsertChart() {
		restoreToolBar();
		repaint();
	}
	public void insertText(javax.swing.text.Document dc, String value, Rectangle r) {
		/*
		grobj.add(new GRText(this,this.getGraphics(),idObj,dc,grtoolbarStrumenti.getFontAlignment(),value,r));
		idObj++;
		
		restoreToolBar();
		repaint();
		*/
		GRText refText = new GRText(this,this.getGraphics(),idObj,dc,grtoolbarStrumenti.getFontAlignment(),value,r);
		
		GRGroup grgroup = isGroup(r.x, r.y);
		if(grgroup != null)
			grgroup.addObject(refText);
		else {
			grobj.add(refText);
			idObj++;
		}
		
		restoreToolBar();
		repaint();
		
	}
	public void modifyText(long id, javax.swing.text.Document dc, String value) {
		for(int i = 0;i < grobj.size();i++) {
			if(grobj.get(i).getId() == id && grobj.get(i).getType() == GRObject.TYPEOBJ_TEXT) {
				GRText ref = (GRText)grobj.get(i);
				
				ref.modifyText(dc,  value);
				
				repaint();
			}
		}
	}
	public void insertTextCell(javax.swing.text.Document dc, String value, Rectangle r) {
		if(refObj == null)
			return;
		
		refCell.setText(new GRTableListText((GRTableList)refObj,this.getGraphics(),1,dc,grtoolbarStrumenti.getFontAlignment(),value,r));
		refCell = null;
		
		repaint();
		
	}
	public void selectObject(GRObject grobj) {
		clearSelected();
		
		if(grobj == null) {
			grdoc.setMenuVoiceEnabled(GREditor.MENUTYPE_MODIFICA, 3, false);
			
		} else {
			this.selectObj(grobj);
			
			grdoc.manageMenu(GREditor.MENUVOICE_CANCELLA, true);
			grdoc.setMenuVoiceEnabled(GREditor.MENUTYPE_MODIFICA, 3, true);
			grdoc.setMenuVoiceEnabled(GREditor.MENUTYPE_MODIFICA, 4, true);
		}
		
		repaint();
		
	}
	public void insertTableList(int x1, int y1, int x2, int y2, int numColumns, boolean header, boolean footer) {
		grobj.add(new GRTableList(this,idObj,x1,y1,x2,y2,numColumns));
		idObj++;
		
		restoreToolBar();
		repaint();
	}
	public void insertChart(int x1, int y1, int x2, int y2, int typeChart, int view, Vector<GRChartVoice> grdata) {
		
		GRChart refChart = new GRChart(this,idObj,(int)(x1 / zoom),(int)(y1 / zoom),(int)(x2 / zoom),(int)(y2 / zoom));
		refChart.setTypeChart(typeChart);
		refChart.setZoom(zoom);
		refChart.setView(view);
		
		// Se sono presenti dati statici li inserisce
		if(grdata != null) {
			refChart.addDataStatic(grdata);
		}
		
 		grobj.add(refChart);
		idObj++;
		
		restoreToolBar();
		repaint();
	}
	private void restoreToolBar() {
		grtoolbar.restore();
		flagAzione = GRPage.ACTION_SELECT;
		
	}
	
	public int getTotaleObject() {
		return grobj.size();
	}
	public GRObject getObject(int i) {
		
		return grobj.get(i);
	}
	/**
	 * Restituisce il Vector contenente tutti gli oggetti attualmente presenti nella pagina
	 * 
	 * @return Il Vector con tutti gli oggetti censiti.
	 */
	public Vector<GRObject> getObjectInThePage() {
		return grobj;
	}
	public Point getGapGrid() {
		return new Point(GAPGRIDX,GAPGRIDY);
	}
	public int getGapGridX() {
		return GAPGRIDX;
	}
	public int getGapGridY() {
		return GAPGRIDY;
	}
	public void setGapGrid(int x, int y) {
		GAPGRIDX = x;
		GAPGRIDY = y;
		
		repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == menuCellUnisciCella) {
			grcellSelected.getFatherSection().mergeCell(grcellSelected);
			
		} else if(e.getSource() == menuCellPropertyCell) {
			new GRDialogPropertyCell(grcellSelected);
			
		} else if(e.getSource() == menuCellPropertyTableList) {
			new GRDialogPropertyTableList(grcellSelected.getFatherSection().getTableList());
		} else if(e.getSource() == menuRectangleInsertText) {
			int gapH = 0;
			int gapV = ((grobjSelected.get(0).getHeight() / grtoolbarStrumenti.getFontSize()) + ((grobjSelected.get(0).getHeight() / 3) / grtoolbarStrumenti.getFontSize())) * 3;
			int w = grobjSelected.get(0).getWidth();
			// Se l'area è maggiore delle caratteristiche minime
			// aggiusta di conseguenza l'area ove comparirà il testo
			if(w >= 60)	{// Almeno 2 cm
				gapH = 15;	// 5mm
				w = w - 30;
			}
			Rectangle r = new Rectangle((grobjSelected.get(0).getX() + gapH), (grobjSelected.get(0).getY() + gapV), w, grobjSelected.get(0).getHeight());
			
			GREditText et = new GREditText(this, r);
			
			et.setFont(grtoolbarStrumenti.getFontName(),grtoolbarStrumenti.getFontSize(),grtoolbarStrumenti.getFontStyle());
			et.showDialog(GREditText.NEWTEXT);
		} else if(e.getSource() == menuObjSelectedRaggruppa) {
			raggruppaOggetti();
		}
	}
	public void raggruppaOggetti() {
		if(grobjSelected == null)
			return;
		
		int yStart = 0;
		int yEnd = 0;
		int indexObjMin = 0;
		
		Vector<GRObject> grobjSorted = new Vector<GRObject>();
		// Crea un Vector ordinato. Questo sarà quello che andrà collegato al gruppo
		for(int i = 0;i < grobj.size();i++) {
			if(grobjSelected.contains(grobj.get(i)))
				grobjSorted.add(grobj.get(i));
		}
		for(int i = 0;i < grobjSelected.size();i++) {
			
			GRObject refObj = grobjSelected.get(i);
			int indexTemp = grobj.indexOf(refObj);
			
			if(i == 0) {
				yStart = refObj.getY();
				yEnd = refObj.getY() + refObj.getHeight();
				
				indexObjMin = indexTemp;
				// Se il primo oggetto è hposition=relative lo trasforma in absolute
				
			} else {
				if(yStart > refObj.getY())
					yStart = refObj.getY();
				
				// Aggiorna l'altezza
				if(refObj.getHPosition()) {
					// Relative
				} else {
					// Absolute
					if(yEnd < refObj.getY() + refObj.getHeight())
						yEnd = refObj.getY() + refObj.getHeight();
				}
			
				if(indexObjMin > indexTemp)
					indexObjMin = indexTemp;
			}
				
		}
		
		GRGroup refGroup = new GRGroup(this,idObj,yStart,yEnd);
		
		// Aggiunge il gruppo prima del primo oggetto selezionato in ordine nello storybook
		grobj.add(indexObjMin,refGroup);
		idObj++;
		
		// Adesso aggiunge gli oggetti selezionati al gruppo, nell'ordine in cui
		// si presentano nello storybook
		for(int i = 0;i < grobjSorted.size();i++) {
			refGroup.addObject(grobjSorted.get(i));	// Aggiunge l'oggetto al gruppo
			grobj.remove(grobjSorted.get(i));	// e lo elimina dallo storybook
		}
		
		restoreToolBar();
		repaint();	
	}
	/* Lettura dati da xml - GRS */
	public void loadHead(Element el) {
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			if(element.getName().equals("text")) {
				if(((Element)element.getParent()).getName().equals("grheader")) {
					GRText refText = readText(element);
					refText.setSection(GRObject.SECTION_HEADER);
					
					grobj.add(refText);
				} 
			} else if(element.getName().equals("shape")) {
				if(((Element)element.getParent()).getName().equals("grheader")) {
					if(element.getChild("type").getValue().equals("rectangle")) {
						GRRectangle refRect = readRectangle(element);
						refRect.setSection(GRObject.SECTION_HEADER);
						
						grobj.add(refRect);
						
					} else if(element.getChild("type").getValue().equals("line")) {
						GRLine refLine = readLine(element);
						
						refLine.setSection(GRObject.SECTION_HEADER);
						grobj.add(refLine);
						
					} else if(element.getChild("type").getValue().equals("circle")) {
						GRCircle refCircle = readCircle(element);
						
						refCircle.setSection(GRObject.SECTION_HEADER);
						grobj.addElement(refCircle);
					}
					
					
				}
			} else if(element.getName().equals("image")) {
				if(((Element)element.getParent()).getName().equals("grheader")) {
					GRImage refImage = readImage(element);
					refImage.setSection(GRObject.SECTION_HEADER);
					
					grobj.add(refImage);
				}
			}
		}
	}

	public void loadBody(Element el) {
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			
			if(element.getName().equals("shape")) {
				if(element.getChild("type").getValue().equals("rectangle")) {
					GRRectangle refRect = readRectangle(element);
					
					refRect.setSection(GRObject.SECTION_BODY);
					grobj.add(refRect);
				} else if(element.getChild("type").getValue().equals("line")) {
					GRLine refLine = readLine(element);
					
					refLine.setSection(GRObject.SECTION_BODY);
					grobj.add(refLine);
				} else if(element.getChild("type").getValue().equals("circle")) {
					GRCircle refCircle = readCircle(element);
					
					refCircle.setSection(GRObject.SECTION_BODY);
					grobj.add(refCircle);
				} 
			} else if(element.getName().equals("text")) {
				if(((Element)element.getParent()).getName().equals("grbody")) {
					GRText refText = readText(element);
					
					refText.setSection(GRObject.SECTION_BODY);
					grobj.add(refText);
					
				} 
			} else if(element.getName().equals("image")) {
				if(((Element)element.getParent()).getName().equals("grbody")) {
					GRImage refImage = readImage(element);
					
					refImage.setSection(GRObject.SECTION_BODY);
					grobj.add(refImage);
				}
			} else if(element.getName().equals("list")) {
				if(((Element)element.getParent()).getName().equals("grbody")) {
					GRList refList = readList(element);
					
					refList.setSection(GRObject.SECTION_BODY);
					
				}
			} else if(element.getName().equals("group")) {
				if(((Element)element.getParent()).getName().equals("grbody")) {
					GRGroup refGroup = readGroup(element);
					
					refGroup.setSection(GRObject.SECTION_BODY);
					grobj.add(refGroup);
				}
			}
		}
	}
	private GRText readText(Element el) {
		GRText refText = new GRText(this,this.getGraphics(),idObj);
		idObj++;
		
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			
			if(element.getName().equals("left")) {
				if(((Element)element.getParent()).getName().equals("text")) {
					refText.setX(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("top")) {
				if(((Element)element.getParent()).getName().equals("text")) {
					refText.setY(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("value")) {
				if(((Element)element.getParent()).getName().equals("text")) {
					refText.setValueFromGRS(element.getValue(),grdoc.getFontResources());
				}
			} else if(element.getName().equals("width")) {
				if(((Element)element.getParent()).getName().equals("text")) {
					refText.setWidth(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("alignment")) {
				if(((Element)element.getParent()).getName().equals("text")) {
					refText.setFontAlignment(element.getValue());
				}
			} else if(element.getName().equals("linespacing")) {
				if(((Element)element.getParent()).getName().equals("text")) {
					refText.setLineSpacing(Float.parseFloat(element.getValue()));
				}
			} else if(element.getName().equals("hposition")) {
				if(((Element)element.getParent()).getName().equals("text")) {
					if(element.getValue().equals("absolute"))
						refText.setHPosition(false);
					else if(element.getValue().equals("relative"))
						refText.setHPosition(true);
				}
			} 
		}
	
		return refText;
	}
	private GRRectangle readRectangle(Element el) {
		GRRectangle refRect = new GRRectangle(this, idObj);
		idObj++;
		
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			
			if(element.getName().equals("widthstroke")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refRect.setWidthStroke(Double.parseDouble(element.getValue()));
				}
			} else if(element.getName().equals("colorstroke")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					String[] cStroke = element.getValue().split(" ");
					refRect.setColorStroke(Integer.parseInt(cStroke[0]),Integer.parseInt(cStroke[1]),Integer.parseInt(cStroke[2]));
				}
			} else if(element.getName().equals("colorfill")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refRect.setColorFill(element.getValue());
				}
			} else if(element.getName().equals("left")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refRect.setX(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("top")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					if(refRect.getSection() == GRObject.SECTION_HEADER)
						refRect.setY(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
					else if(refRect.getSection() == GRObject.SECTION_BODY)
						refRect.setY(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())) + heightHeader);
				}
			} else if(element.getName().equals("width")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refRect.setWidth(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("height")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refRect.setHeight(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			}
		}
		
		return refRect;
	}
	private GRCircle readCircle(Element el) {
		GRCircle refCircle = new GRCircle(this,idObj);
		idObj++;
		
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		int x = 0;
		int y = 0;
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			System.out.println(element.getName());
			if(element.getName().equals("widthstroke")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refCircle.setWidthStroke(Double.parseDouble(element.getValue()));
				}
			} else if(element.getName().equals("x")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					x = GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue()));
				}
			} else if(element.getName().equals("y")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					y = GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue()));
				}
			} else if(element.getName().equals("radius")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refCircle.setRadius(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("colorstroke")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					String[] cStroke = element.getValue().split(" ");
					refCircle.setColorStroke(Integer.parseInt(cStroke[0]),Integer.parseInt(cStroke[1]),Integer.parseInt(cStroke[2]));
				}
			} else if(element.getName().equals("colorfill")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refCircle.setColorFill(element.getValue());
				}
			}
		}
		
		// Imposta le coordinate del punto
		refCircle.setXRadius(x);
		refCircle.setYRadius(y);
		
		return refCircle;
	}
	private GRLine readLine(Element el) {
		GRLine refLine = new GRLine(this, idObj);
		idObj++;
		
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			
			if(element.getName().equals("widthstroke")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refLine.setWidthStroke(Double.parseDouble(element.getValue()));
				}
			} else if(element.getName().equals("colorstroke")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					String[] cStroke = element.getValue().split(" ");
					refLine.setColorStroke(Integer.parseInt(cStroke[0]),Integer.parseInt(cStroke[1]),Integer.parseInt(cStroke[2]));
				}
			} else if(element.getName().equals("x1")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refLine.setX(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("y1")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refLine.setY(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("x2")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refLine.setXEnd(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("y2")) {
				if(((Element)element.getParent()).getName().equals("shape")) {
					refLine.setYEnd(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			}
		}
		
		return refLine;
	}
	private GRImage readImage(Element el) {
		GRImage refImage = new GRImage(this,idObj,grdoc.getImgResources());
		idObj++;
		
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			
			if(element.getName().equals("refid")) {
				if(((Element)element.getParent()).getName().equals("image")) {
					refImage.setIdImageFromGRS(element.getValue());
				}
			} else if(element.getName().equals("left")) {
				if(((Element)element.getParent()).getName().equals("image")) {
					refImage.setX(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("top")) {
				if(((Element)element.getParent()).getName().equals("image")) {
					refImage.setY(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("width")) {
				if(((Element)element.getParent()).getName().equals("image")) {
					refImage.setWidth(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("height")) {
				if(((Element)element.getParent()).getName().equals("image")) {
					refImage.setHeight(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("hposition")) {
				if(((Element)element.getParent()).getName().equals("image")) {
					refImage.setHPosition(element.getValue());
				}
			} 
		}
		
		
		return refImage;
	}
	private GRGroup readGroup(Element el) {
		GRGroup refGroup = new GRGroup(this,idObj);
		idObj++;
		
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			
			if(element.getName().equals("top")) {
				if(((Element)element.getParent()).getName().equals("group")) {
					refGroup.setY(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("height")) {
				if(((Element)element.getParent()).getName().equals("group")) {
					// DA DEPRECARE
					refGroup.setHeight(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("hposition")) {
				if(((Element)element.getParent()).getName().equals("group")) {
					refGroup.setHPosition(element.getValue());
				}
			} else if(element.getName().equals("content")) {
				if(((Element)element.getParent()).getName().equals("group")) {
					readContentGroup(element, refGroup);
				}
			} 
		}
		
		return refGroup;
	}
	private void readContentGroup(Element el, GRGroup refGroup) {
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			
			if(element.getName().equals("shape")) {
				if(element.getChild("type").getValue().equals("rectangle")) {
					GRRectangle refRect = readRectangle(element);
					
					refGroup.addObjectFromGRS(refRect);
				} else if(element.getChild("type").getValue().equals("line")) {
					GRLine refLine = readLine(element);
					
					refGroup.addObject(refLine);
				} else if(element.getChild("type").getValue().equals("circle")) {
					GRCircle refCircle = readCircle(element);
					
					refGroup.addObject(refCircle);
				} 
			} else if(element.getName().equals("text")) {
				GRText refText = readText(element);
				
				refGroup.addObjectFromGRS(refText);
				 
			} else if(element.getName().equals("image")) {
				GRImage refImage = readImage(element);
				refGroup.addObject(refImage);
				
			} else if(element.getName().equals("list")) {
				GRList refList = readList(element);
					
			} else if(element.getName().equals("group")) {
				// Per adesso freezo la ricorsione	
			}
		}
		
		
	}
	
	private GRList readList(Element el) {
		GRList refList = new GRList(this, idObj);
		idObj++;
		
		grobj.add(refList);
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			
			if(element.getName().equals("id")) {
				if(((Element)element.getParent()).getName().equals("list")) {
					refList.setNameXml(element.getValue());
					
					panelProperty.addListFather(refList.getNameXml());
				}
			} else if(element.getName().equals("top")) {
				if(((Element)element.getParent()).getName().equals("list")) {
					refList.setY(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}
			} else if(element.getName().equals("height")) {
				if(((Element)element.getParent()).getName().equals("list")) {
					refList.setHeight(GRLibrary.fromMillimetersToPixels(Double.parseDouble(element.getValue())));
				}	
			} if(element.getName().equals("row")) {
				if(((Element)element.getParent()).getName().equals("list")) {
					readRowList(element, refList);
				}	
			}
		}
		
		return refList;
	}
	private void readRowList(Element el, GRList refList) {
		List children = el.getChildren();
		Iterator iterator = children.iterator();
		
		while(iterator.hasNext()) {
			Element element = (Element)iterator.next();
			
			if(element.getName().equals("shape")) {
				if(element.getChild("type").getValue().equals("rectangle")) {
					GRRectangle refRect = readRectangle(element);
					
					refRect.setSection(refList.getSection());
					grobj.add(refRect);
							
					refRect.setListFather(refList);
				} else if(element.getChild("type").getValue().equals("line")) {
					GRLine refLine = readLine(element);
					
					refLine.setSection(refList.getSection());
					grobj.add(refLine);
					
					refLine.setListFather(refList);
				}
			} else if(element.getName().equals("text")) {
				GRText refText = readText(element);
					
				refText.setSection(refList.getSection());
				grobj.add(refText);
				
				refText.setListFather(refList);
			}
		}
	}
	
}