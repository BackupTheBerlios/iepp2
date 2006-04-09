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

package iepp.application.aedition.aoutil;

import iepp.Application;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeNote;
import iepp.ui.iedition.popup.PopupComposantProcessus;
import iepp.ui.iedition.popup.PopupDiagramme;
import iepp.ui.iedition.popup.PopupFusion;
import iepp.ui.iedition.popup.PopupNote;
import iepp.ui.iedition.popup.PopupProduit;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import util.Vecteur;

/**
 * La sélection est l'outil principal à toutes les applications de dessin. C'est
 * l'outil le plus complexe, car il gère à la fois la sélection multiple et la
 * translation.
 */
public class OSelection extends Outil {

	/**
	 * Dernière figure cliquée. NULL si on a cliqué sur le diagramme.
	 */
	private IeppCell figureCliquee;

	/**
	 * L'outil déplace les figures sélectionnées.
	 */
	public static final int TRANSLATE_STATE = 1;

	/**
	 * L'outil sélectionne toutes les figures dans le rectangle.
	 */
	public static final int SELRECT_STATE = 2;

	/**
	 * L'outil redimensionne la figure.
	 */
	public static final int MOVE_HANDLE = 3;

	/**
	 * L'outil clique sur controle.
	 */
	public static final int CTRL_HANDLE = 4;

	/**
	 * Création d'un outil de sélection d'élement dans le diagramme
	 * 
	 * @param diag,
	 *            diagramme sur lequel on va effectuer la sélection
	 */
	public OSelection(VueDPGraphe diag) {
		super(diag);
	}

	// ------------------------------------------------------------------------
	// Gestion des évennements
	// ------------------------------------------------------------------------

	/**
	 * Un click sur le diagramme ou un lien déselectionne tout. Un click (sur un
	 * élément non-sélectionné) déselectionne tous les éléments et sélectionne
	 * celui clické. Un click avec la touche CTRL enfoncée, ajoute/enlève
	 * l'élément clické de la sélection.
	 */
	public void mousePressed(MouseEvent event) {
		// mettre à jour les coordonnées du dernier click
		super.mousePressed(event);
		//System.out.println(diagramme.getFirstCellForLocation(event.getX(), event.getY()).getClass().getName());
		// on vérifie si on a cliqué sur une figure
		if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof IeppCell) {

			if (event.isControlDown()) {
				this.state = CTRL_HANDLE;
			}
			this.state = TRANSLATE_STATE;

		} else {

			this.state = IDLE_STATE;

		}

		diagramme.repaint();
		this.update();

	}

	/**
	 * Translation d'éléments, ou affichage du rectangle de sélection.
	 */
	public void mouseDragged(MouseEvent event) {
		// super.mouseDragged(event);

		if (event.isControlDown() && state == CTRL_HANDLE ) {

			if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof IeppCell) {
				// ne rien faire pour eviter la copie de cellule
				diagramme.setDropEnabled(false);
				
			}

		} else {
			diagramme.setDropEnabled(true);
		}

		//this.update();
	}

	/**
	 * Si on relâche la souris : - après une sélection : tous les éléments dans
	 * le rectangle sont sélectionnés. - après une translation : ajout de la
	 * translation dans la pile undo. - ...
	 */
	public void mouseReleased(MouseEvent event) 
	{
		super.mouseReleased(event);

			// Hubert : popup menu sur le graph (hors cellules et lien)
			if (!((diagramme
					.getFirstCellForLocation(event.getX(), event.getY()) instanceof IeppCell) || (diagramme
					.getFirstCellForLocation(event.getX(), event.getY()) instanceof LienEdge))) {
				if (event.isPopupTrigger()) {
					showPopupMenuDiagramme();
				}
			}
			// modif aldo nit 15/01/06
			else {
				if (event.isPopupTrigger()) {
			
						if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof ComposantCell){
							ComposantCell ic = (ComposantCell) diagramme.getFirstCellForLocation(event.getX(), event.getY());
							
							PopupComposantProcessus p = new PopupComposantProcessus(diagramme,ic);
							
							p.show(diagramme, event.getX(), event.getY());
						}
						else if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof TextCell) {
								showPopupMenuNote((TextCell)diagramme.getFirstCellForLocation(event.getX(), event.getY()));
							
						}
						else if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof ProduitCellFusion){
							ProduitCellFusion pf=(ProduitCellFusion) diagramme.getFirstCellForLocation(event.getX(), event.getY());
							PopupFusion f=new PopupFusion(diagramme,pf,event.getX(),event.getY());
							f.show(diagramme,event.getX(),event.getY());
						}
						else if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof ProduitCell){
							ProduitCell pf=(ProduitCell) diagramme.getFirstCellForLocation(event.getX(), event.getY());
							PopupProduit f=new PopupProduit(diagramme,pf);
							f.show(diagramme,event.getX(),event.getY());
						}
				}
				
				// On bouge un objet
				Vecteur translation = new Vecteur();
				translation.setSubstraction(this.current, this.start);
				if (translation.x != 0 || translation.y != 0) {
					Application.getApplication().getProjet().setModified(true);
					
					//Modification composant a bougé
				}
			}

				
				
			
		this.update();
	}


	/**
	 * La touche SUPPR détruit les éléments sélectionnés du diagramme.
	 */
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_DELETE) {
			if (this.diagramme.getSelectionCount() > 0) {
				// ajouterEditionDiagramme(new SupprimerSelection(diagramme));
				 
				if(this.diagramme.getSelectionCell() instanceof LienEdgeNote){
					LienEdgeNote lien = (LienEdgeNote)this.diagramme.getSelectionCell();
					TextCell note;
					if(lien.getDestination() instanceof TextCell) {
						note = (TextCell)lien.getDestination();
					}
					else {
						note = (TextCell)lien.getSourceEdge();
					}
					note.supprimerLien(lien);
					this.diagramme.supprimerLien(lien);
					this.diagramme.getModel().remove(new Object[]{lien});
					this.diagramme.setSelectionCell(null);
					Application.getApplication().getProjet().setModified(true);
				 }
				 
			}
		}
		this.terminer();
	}

	// ------------------------------------------------------------------------
	// Menus contextuels
	// ------------------------------------------------------------------------

	/**
	 * Affiche le menu popup (contextuel) pour un élément.
	 */
	protected void showPopupMenuFigure(VueDPGraphe d, ComposantCell ic) {
		PopupComposantProcessus p = new PopupComposantProcessus(d,ic);
		p.show(diagramme, getStart().x, getStart().y);
	}

	/**
	 * Affiche le menu popup (contextuel) pour un diagramme.
	 */
	protected void showPopupMenuDiagramme() {
		PopupDiagramme p = new PopupDiagramme(diagramme, getStart().x,
				getStart().y);
		p.show(diagramme, getStart().x, getStart().y);
	}
	
	/**
	 * Affiche le menu popup (contextuel) pour un diagramme.
	 */
	protected void showPopupMenuNote(TextCell note) {
		PopupNote p = new PopupNote(diagramme, note);
		p.show(diagramme, getStart().x, getStart().y);
	}
	
}
