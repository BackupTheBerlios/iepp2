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
package iepp.ui.ireferentiel;

import iepp.Application;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.IDTransferable;
import iepp.ui.VueDPArbre;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.ireferentiel.popup.PopupComposant;
import iepp.ui.ireferentiel.popup.PopupDefProc;
import iepp.ui.ireferentiel.popup.PopupPaqDP;
import iepp.ui.ireferentiel.popup.PopupReferentiel;
import iepp.ui.ireferentiel.popup.PopupScenario;
import iepp.ui.ireferentiel.popup.PopupUnScenario;

import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.ipsquad.utils.IconManager;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphModel;


/**
 * Arbre graphique présentant les éléments contenu dans le référentiel.
 * On peut faire des glisser/deposer de composants de processus de l'arbre vers le diagramme d'assemblage.
 */
public class VueReferentielArbre extends JTree
								 implements MouseListener, Observer,
								 	DragGestureListener, DragSourceListener
{

	/**
	 * Liste des icons à utiliser dans l'arbre pour différencier les éléments
	 */
	// pour unifier la définition des icônes
	private static final String iconeReferentiel = "edittrash.png" ;

	/**
	 * Objet permettant de récupérer les évents de drag
	 */
	private DragSource mDragSource;


	/**
	 * Construit une vue du référentiel à partir de son modèle.
	 * @param ref référentiel (modèle) observé par cette vue
	 */
	public VueReferentielArbre (Referentiel ref)
	{
		super (ref) ;
		// S'enregistrer comme observateur du modèle
		ref.addObserver (this) ;
		// Définir le rendu des éléments de l'arbre
		this.setCellRenderer (new RenduArbre()) ;
		// Ecouter les événéments souris
		this.addMouseListener (this) ;
		// indique que l'arbre est un générateur d'event drag source
		// on peut dragger les éléments de l'arbre
		this.mDragSource = DragSource.getDefaultDragSource();
		this.mDragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}


	/**
	 * Construit une vue sur aucun référentiel.
	 */
	public VueReferentielArbre ()
	{
		super(new DefaultTreeModel(null));
	}


	/** Lance la mise-à-jour de l'arbre graphique. Cette méthode est appelée par le modèle
	 * observé par la vue quand il est modifié.
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		// Lancer la mise à jour de l'image
		this.updateUI() ;
	}


	/**
	 * Classe permettant d'afficher les icones associées aux différents types de données
	 * (paquetages, composants, définitions de processes, ...).
	 */
	protected class RenduArbre extends DefaultTreeCellRenderer
	{

		public Component getTreeCellRendererComponent(
														JTree tree,
														Object value,
														boolean sel,
														boolean expanded,
														boolean leaf,
														int row,
														boolean hasFocus)
		{
			// le renderer par défaut est un Label, on peut dont lui associer une icone
			super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus);
			// associer l'icone selon le type de données à afficher
			Icon icone = VueReferentielArbre.this.getIconeAssociee((ElementReferentiel)value) ;
			if (icone != null)
				this.setIcon (icone) ;
			// aucun commentaire à afficher
			this.setToolTipText(null);
			// retourner le JLabel avec l'icone et le texte associé
			return this;
		}
	}


	/**
	 * Renvoie l'icone associée à l'objet value passé en paramètre
	 * @param eltRef l'élément du référentiel à afficher
	 * @return Icone associée à cet objet
	 */
	protected Icon getIconeAssociee(ElementReferentiel eltRef)
	{
		int typeElt = eltRef.getType() ;
		if (typeElt == ElementReferentiel.DP)
		{
			return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeDefProc));
		}
		else if (typeElt == ElementReferentiel.COMPOSANT || typeElt == ElementReferentiel.COMPOSANT_VIDE)
		{
			return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeComposant));
		}
		else if (typeElt == ElementReferentiel.SCENARIO)
		{
			return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueDPArbre.iconeScenario));
		}
		else if (typeElt == ElementReferentiel.PAQ_COMP || typeElt == ElementReferentiel.PAQ_DP)
		{
			return null ;
		}
		else if (typeElt == ElementReferentiel.REFERENTIEL)
		{
			return (IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + VueReferentielArbre.iconeReferentiel));
		}
		return null;
	}

	/**
	 * Renvoie le popup à afficher selon le noeud cliqué dans l'arbre value
	 * @param eltRef l'élément du référentiel pour lequel on veut un menu
	 * @return le popup associé au type de l'élément sélectionné dans l'arbre, null
	 * si aucun menu ne lui est associé
	 */
	public JPopupMenu getMenuAssocie (ElementReferentiel eltRef)
	{
		int typeElt = eltRef.getType() ;
		// Définition de processus
		if (typeElt == ElementReferentiel.DP)
		{
			return new PopupDefProc(eltRef.getIdElement()) ;
		}
		// Composant de processus (plein ou vide)
		else if (typeElt == ElementReferentiel.COMPOSANT || typeElt == ElementReferentiel.COMPOSANT_VIDE)
		{
			return new PopupComposant (eltRef.getIdElement()) ;

		}
		
		else if (typeElt == ElementReferentiel.PAQ_SCEN)
		{
			return new PopupScenario() ;

		}
		else if (typeElt == ElementReferentiel.SCENARIO)
		{
			
			return new PopupUnScenario(eltRef.getNomElement()) ;

		}
		/*
		// Paquetages de présentation
		// (composants sans modèle)
		else if (typeElt == ElementReferentiel.PRESENTATION)
		{
			return new PopupPresentation (eltRef.getIdElement()) ;
		}
		*/
		/*
		// Paquetage du référentiel contenant les composants
		else if (typeElt == ElementReferentiel.PAQ_COMP)
		{
			return new PopupPaqComposants() ;
		}
		*/
		// Paquetage du référentiel contenant les définitions de processus
		else if (typeElt == ElementReferentiel.PAQ_DP)
		{
			return new PopupPaqDP() ;
		}
		/*
		// Paquetage du référentiel contenant les paquetages de présentation
		// (composants sans modèle)
		else if (typeElt == ElementReferentiel.PAQ_PRESENTATION)
		{
			return new PopupPaqPresentations() ;
		}
		*/
		// Paquetage représentant le référentiel
		else if (typeElt == ElementReferentiel.REFERENTIEL)
		{
			return null;//new PopupReferentiel() ;
		}
		return null;
	}


	/**
	 * L'utilisateur a cliqué sur l'arbre
	 */
	public void mousePressed(MouseEvent e)
	{
		if (Application.getApplication().getProjet()!=null)
		{
			// on récupère la ligne sur laquelle on a cliqué
			int selRow = getRowForLocation(e.getX(), e.getY());
			// récupérer le chemin de l'arbre associé, le path
			TreePath selPath = getPathForLocation(e.getX(), e.getY());
			// the modifiers test is needed in order to make it work on OSes that don't correctly set the isPopupTrigger flag (swing sux0r)
			if(selRow != -1 && (e.isPopupTrigger() || (e.getModifiers() & MouseEvent.BUTTON3_MASK)!=0) )
			{
				// si on clique droit, on affiche le popupmenu associé à l'élément sélectionné
				JPopupMenu popup = this.getMenuAssocie ((ElementReferentiel)selPath.getLastPathComponent());
				// si aucun menu existe, ne rien faire
				if(popup != null)
				{
					e.consume();
					setSelectionPath(selPath);
					// afficher le menu contextuel
					popup.show(this, e.getX(), e.getY());
				}
			}
			// on a cliqué sur l'arbre mais sur aucun élément, cliqué deux fois
			else if(selRow != -1 && e.getClickCount()==2)
			{
				// ne rien faire
				ElementReferentiel element=(ElementReferentiel)selPath.getLastPathComponent();
				
				GraphModelView mod=Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().ChercherModel(element.getNomElement());
				if (mod!=null)
				{
					Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().clearSelection();
					Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel(mod);
					Application.getApplication().getProjet().getFenetreEdition().setDynamique();
				}
				else{
					e.consume();
				}
			}
			else if ((selRow==0)){//||(item==2)||(item==3)||(item==4)||(item==5)||(item==6)||(item==0)){
					Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().clearSelection();
					Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(0));
					Application.getApplication().getProjet().getFenetreEdition().setStatique();
			}
			
			Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setOutilSelection();
	
		}
	}
	
	public void mouseClicked (MouseEvent e) {}
	public void mouseReleased (MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseExited (MouseEvent e) {}


	//--------------------------------------------------------------//
	// Gestion du drag & drop de l'arbre vers le diagramme			//
	//--------------------------------------------------------------//

	/**
	 * Détection d'un début de glisser.
	 */
	public void dragGestureRecognized(DragGestureEvent dge)
	{
		if(dge.getTriggerEvent().isConsumed()) return;

		ElementReferentiel elt = (ElementReferentiel) getLastSelectedPathComponent() ;

		if (elt != null)
		{
			// on ne commence le drag que lorsque l'on sélectionne un composant
			if(elt.getType() == ElementReferentiel.COMPOSANT || elt.getType() == ElementReferentiel.COMPOSANT_VIDE)
			{
				dge.getTriggerEvent().consume();
				Referentiel ref = Application.getApplication().getReferentiel() ;
				ComposantProcessus comp = (ComposantProcessus)ref.chercherReference (elt.getIdElement()) ;
				// Si le composant n'est pas en mémoire passer son id dans le référentiel
				if (comp == null)
				{
					Long idComp = new Long (elt.getIdElement()) ;
					mDragSource.startDrag(dge, DragSource.DefaultMoveDrop, new IDTransferable(idComp), this) ;
				}
				// Sinon passer son idObjetModele (géré normalement par le diagramme)
				else
				{
					IdObjetModele idMod = comp.getIdComposant() ;
					mDragSource.startDrag(dge, DragSource.DefaultMoveDrop, new IDTransferable(idMod), this);
				}
			}
		}
	}

	public void dragEnter(DragSourceDragEvent dsde) { cancelEditing(); }
	public void dragOver(DragSourceDragEvent dsde) { cancelEditing(); }
	public void dropActionChanged(DragSourceDragEvent dsde) { cancelEditing(); }
	public void dragExit(DragSourceEvent dse) { cancelEditing(); }
	public void dragDropEnd(DragSourceDropEvent dsde) { cancelEditing(); }

}
