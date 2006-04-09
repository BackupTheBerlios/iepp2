/*
 * IEPP: Isi Engineering Process Publisher
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */
 
package iepp.ui.iedition.popup;


import iepp.Application;
import iepp.application.aedition.CExporterJPEG;
import iepp.application.aedition.CExporterPNG;
import iepp.application.aedition.CImprimerDiagramme;
import iepp.ui.iedition.VueDPGraphe;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import util.IconManager;

/**
 * Crée par Julie TAYAC le 25/01/06: Presque identique à PopupDiagramme.java
 * Classe permettant d'afficher un popupmenu contextuel lorsque l'utilisateur
 * clique avec le bouton droit sur le diagramme dynamique
 */
public class PopupDiagrammeDyn extends JPopupMenu implements ActionListener 
{

	/**
	 * Items du menu à afficher
	 */
	private JMenuItem 	exportJPEG,
						exportPNG,
						imprimer;
						//modifierCouleur,
						//selectionnerTout;
	
    /**
    * Diagramme sur lequel on a cliqué.
    */
	private VueDPGraphe diagramme;

   /**
	* coordonnées du click droit
	*/
    private int clickX, clickY;
		 
	
	/**
	 * Création du menu contextuel
	 */
	public PopupDiagrammeDyn(VueDPGraphe diagramme,int clickX, int clickY )
	{
		this.diagramme = diagramme;
		this.clickX = clickX;
		this.clickY = clickY;
		
		// création des items
		this.exportPNG = new JMenuItem(Application.getApplication().getTraduction("Exporter_PNG"), IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "png.png"));
		this.exportJPEG = new JMenuItem(Application.getApplication().getTraduction("Exporter_JPEG"), IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "jpeg.png"));
		//this.modifierCouleur = new JMenuItem(Application.getApplication().getTraduction("Modifier_Couleur"));
		this.imprimer = new JMenuItem(Application.getApplication().getTraduction("Imprimer"));
		//this.selectionnerTout = new JMenuItem(Application.getApplication().getTraduction("Selectionner_Tout"));
		
		// ajouter les items au menu
		this.add(this.exportPNG);
		this.add(this.exportJPEG);
		this.addSeparator();
		//this.add(this.modifierCouleur);
		this.add(this.imprimer);
		//this.add(this.selectionnerTout);
		// pouvoir réagr aux clicks des utilisateurs
		this.exportPNG.addActionListener(this);
		this.exportJPEG.addActionListener(this);
		//this.modifierCouleur.addActionListener(this);
		this.imprimer.addActionListener(this);
		//this.selectionnerTout.addActionListener(this);
	}
	
	/**
	 * Gestionnaire de clicks sur les items
	 */
	public void actionPerformed(ActionEvent event)
	{
		 if (event.getSource() == this.exportJPEG)
		 {
			CExporterJPEG c = new CExporterJPEG(this.diagramme);
			c.executer();
		 }
	     else if (event.getSource() == this.exportPNG)
	     {
			CExporterPNG c = new CExporterPNG(this.diagramme);
			c.executer();
	     }
		 /*
	     else if (event.getSource() == this.modifierCouleur)
		 {
			Color couleur = JColorChooser.showDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("Modifier_Couleur"), diagramme.getModele().getFillColor());
			if (couleur != null)
			{
				diagramme.getModele().setFillColor(couleur);
				diagramme.repaint();
			}
		 }
		 */
		 /*else if (event.getSource() == this.selectionnerTout)
		 {
		    this.diagramme.selectionnerTout();
		 }*/
	     else if (event.getSource() == this.imprimer)
		 {
			CImprimerDiagramme c = new CImprimerDiagramme(this.diagramme);
			c.executer();
		 }
	 }
}
