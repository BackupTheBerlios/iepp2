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
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeDyn;
import iepp.ui.iedition.popup.PopupComposantProcessusDyn;
import iepp.ui.iedition.popup.PopupDiagrammeDyn;
import iepp.ui.iedition.popup.PopupLienEdgeDyn;
import iepp.ui.iedition.popup.PopupNoteDyn;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import util.Vecteur;

/**
 * La s?lection est l'outil principal ? toutes les applications de dessin. C'est
 * l'outil le plus complexe, car il g?re ? la fois la s?lection multiple et la
 * translation.
 */
public class OSelectionDyn extends Outil {

	/**
	 * Derni?re figure cliqu?e. NULL si on a cliqu? sur le diagramme.
	 */
	private IeppCell figureCliquee;

	/**
	 * L'outil d?place les figures s?lectionn?es.
	 */
	public static final int TRANSLATE_STATE = 1;

	/**
	 * L'outil s?lectionne toutes les figures dans le rectangle.
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
	 * Cr?ation d'un outil de s?lection d'?lement dans le diagramme
	 * 
	 * @param diag,
	 *            diagramme sur lequel on va effectuer la s?lection
	 */
	public OSelectionDyn(VueDPGraphe diag) {
		super(diag);
	}

	// ------------------------------------------------------------------------
	// Gestion des ?vennements
	// ------------------------------------------------------------------------

	/**
	 * Un click sur le diagramme ou un lien d?selectionne tout. Un click (sur un
	 * ?l?ment non-s?lectionn?) d?selectionne tous les ?l?ments et s?lectionne
	 * celui click?. Un click avec la touche CTRL enfonc?e, ajoute/enl?ve
	 * l'?l?ment click? de la s?lection.
	 */
	public void mousePressed(MouseEvent event) {
		// mettre ? jour les coordonn?es du dernier click
		super.mousePressed(event);
		//System.out.println(diagramme.getFirstCellForLocation(event.getX(), event.getY()).getClass().getName());
		// on v?rifie si on a cliqu? sur une figure
		if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof IeppCell) {

			if (event.isControlDown()) {
				this.state = CTRL_HANDLE;
			}

		} else {

			this.state = IDLE_STATE;

		}

		diagramme.repaint();
		this.update();

	}

	/**
	 * Translation d'?l?ments, ou affichage du rectangle de s?lection.
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
	 * Si on rel?che la souris : - apr?s une s?lection : tous les ?l?ments dans
	 * le rectangle sont s?lectionn?s. - apr?s une translation : ajout de la
	 * translation dans la pile undo. - ...
	 */
	public void mouseReleased(MouseEvent event) 
	{
		super.mouseReleased(event);

			
			// popup menu sur le graphe dynamique (hors cellules et lien)
			if (!((diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof IeppCell) 
					|| (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof LienEdge))
					) {
				
				
				if (event.isPopupTrigger()) {
					showPopupMenuDiagrammeDyn();
				} 
			}else{
				if (event.isPopupTrigger()) {
			
						// popup si on clic droit sur un composant dynamique
						if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof ComposantCellDyn){
							ComposantCellDyn ic = (ComposantCellDyn) diagramme.getFirstCellForLocation(event.getX(), event.getY());
							showPopupMenuComposantDyn(ic);
						}
						else if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof TextCell) {
							showPopupMenuNoteDyn((TextCell)diagramme.getFirstCellForLocation(event.getX(), event.getY()));
						}
						// popup si on clic droit sur un lienEdgeDyn
						else if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof LienEdgeDyn) {
							showPopupMenuLienEdgeDyn((LienEdgeDyn)diagramme.getFirstCellForLocation(event.getX(), event.getY()));
						}

						/*else if (diagramme.getFirstCellForLocation(event.getX(), event.getY()) instanceof ProduitCellFusion){
							ProduitCellFusion pf=(ProduitCellFusion) diagramme.getFirstCellForLocation(event.getX(), event.getY());
							PopupFusion f=new PopupFusion(diagramme,pf,event.getX(),event.getY());
							f.show(diagramme,event.getX(),event.getY());
						}*/
				}
				
				// On bouge un objet
				Vecteur translation = new Vecteur();
				translation.setSubstraction(this.current, this.start);
				if (translation.x != 0 || translation.y != 0) {
					Application.getApplication().getProjet().setModified(true);
				}
			}

				
				
			
		this.update();
	}


	/**
	 * La touche SUPPR d?truit les ?l?ments s?lectionn?s du diagramme.
	 */
	public void keyReleased(KeyEvent event) {
		
		if (event.getKeyCode() == KeyEvent.VK_DELETE) {
			if (this.diagramme.getSelectionCount() > 0) {
				// ajouterEditionDiagramme(new SupprimerSelection(diagramme));
				 
				if(this.diagramme.getSelectionCell() instanceof LienEdgeDyn){
					LienEdgeDyn lien = (LienEdgeDyn)this.diagramme.getSelectionCell();
					
					this.diagramme.clearSelection();
					
					DocumentCellDyn docSource = ((DocumentCellDyn)(lien.getSourceEdge()));
					 
					 GraphModelView graph = (GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel();
					 
					 for(int i=0;i<graph.getComposantCellCells().size();i++){
						 ((ComposantCellDyn)graph.getComposantCellCells().elementAt(i)).supprimerNiveauLigneDeVie(docSource.getNiveau());
					 }
					 
					 //System.out.println("Doc actionPerformed "+docSource.getNomCompCell()+" niveau : "+docSource.getNiveau());
					 Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().supprimerLien(lien);
					 Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel().remove(new Object[]{lien});
				 	//this.diagramme.repaint();
					 
					 
				 }
			}
				 
		}else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
			if (this.diagramme.getSelectionCount() > 0) {
				this.diagramme.setMonterOuDecendreLienDyn(true);
			}
		}else if (event.getKeyCode() == KeyEvent.VK_UP) {
			if (this.diagramme.getSelectionCount() > 0) {
				this.diagramme.setMonterOuDecendreLienDyn(false);
			}
		}else if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (this.diagramme.getSelectionCount() > 0) {
				this.diagramme.setDecalerElement(true);
			}
		}else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
			if (this.diagramme.getSelectionCount() > 0) {
				this.diagramme.setDecalerElement(false);
			}
		}
			
		this.terminer();
	}

	 

	// ------------------------------------------------------------------------
	// Menus contextuels
	// ------------------------------------------------------------------------

	/**
	 * Affiche le menu popup (contextuel) pour un ?l?ment.
	 */
	protected void showPopupMenuComposantDyn(ComposantCellDyn ic) {
		PopupComposantProcessusDyn p = new PopupComposantProcessusDyn(ic);
		p.show(diagramme, getStart().x, getStart().y);
	}

	/**
	 * Affiche le menu popup (contextuel) pour un diagramme.
	 */
	protected void showPopupMenuDiagrammeDyn() {
		PopupDiagrammeDyn p = new PopupDiagrammeDyn(diagramme, getStart().x,
				getStart().y);
		p.show(diagramme, getStart().x, getStart().y);
	}
	
	/**
	 * Affiche le menu popup (contextuel) pour un diagramme.
	 */
	protected void showPopupMenuNoteDyn(TextCell note) {
		PopupNoteDyn p = new PopupNoteDyn(diagramme, note);
		p.show(diagramme, getStart().x, getStart().y);
	}
	
	/**
	 * Affiche le menu popup (contextuel) pour un diagramme.
	 */
	protected void showPopupMenuLienEdgeDyn(LienEdgeDyn lien) {
		PopupLienEdgeDyn p = new PopupLienEdgeDyn(diagramme, lien);
		p.show(diagramme, getStart().x, getStart().y);
	}

	
}
