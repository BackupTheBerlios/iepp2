package iepp.ui.iedition;

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

import iepp.Application;
import iepp.application.CAjouterComposantDP;
import iepp.application.aedition.CAjouterComposantGraphe;
import iepp.application.aedition.CAjouterComposantGrapheDyn;
import iepp.application.aedition.CDecalerComposantGrapheDyn;
import iepp.application.aedition.CMonterOuDecendreLienEdgeDyn;
import iepp.application.aedition.aoutil.OCreerElement;
import iepp.application.aedition.aoutil.OLier2ElementDyn;
import iepp.application.aedition.aoutil.OLier2Elements;
import iepp.application.aedition.aoutil.OSelection;
import iepp.application.aedition.aoutil.OSelectionDyn;
import iepp.application.aedition.aoutil.Outil;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.ComposantCellElementDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.TextCell;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;
import iepp.ui.iedition.dessin.vues.ComposantElementDynView;
import iepp.ui.iedition.dessin.vues.ComposantView;
import iepp.ui.iedition.dessin.vues.DocumentCellDynView;
import iepp.ui.iedition.dessin.vues.LienEdgeView;
import iepp.ui.iedition.dessin.vues.ProduitView;
import iepp.ui.iedition.dessin.vues.TextView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jgraph.JGraph;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

/**
 * Classe permettant d'afficher un diagramme d'assemblage de composant
 */
public class VueDPGraphe extends JGraph implements Observer, MouseListener,
		MouseMotionListener, Serializable, KeyListener, DropTargetListener {

	/**
	 * Outil courant.
	 */
	private Outil diagramTool;

	/**
	 * Mod?le du diagramme JGraph processus.
	 */
	private GraphModelView Gmodele;

	/**
	 * Mod?les des diagrammes JGraph processus et scenario.
	 */
	private Vector modelesDiagrammes;
	
	/**
	 * Cellule s?lectionn?s.
	 */
	private Vector selectionCells;

	/**
	 * Dimension de la zone ? afficher
	 * sert ? indiquer la taille setPreferedSize() du diagramme
	*/
	private Dimension zone_affichage;
	
	/**
	 * Outil pour la cr?ation des liens entre produits
	 */
	private OLier2Elements edgeTool = new OLier2Elements();
	
	/**
	 * Outil pour la cr?ation des liens entre produits
	 */
	private OLier2ElementDyn edgeToolDyn = new OLier2ElementDyn();
	/**
	 * Construire le diagramme ? partir de la d?finition de processus et 
	 * d'un controleur
	 * @param defProc, donn?es ? observer
	 */
	public VueDPGraphe(DefinitionProcessus defProc) {
		// la vue observe le mod?le
		defProc.addObserver(this);
		this.Gmodele=new GraphModelView(defProc.getNomDefProc(),true);
		
		this.setModel(Gmodele);

		this.setOpaque(true);
		this.setLayout(null);

		// initialiser les listes d'?l?ments
		this.selectionCells = new Vector();
                this.modelesDiagrammes=new Vector();
		this.modelesDiagrammes.add(Gmodele);

		// par d?fault, on utilise l'outil de s?lection
		// Modif Julie 03.02.06 
		// On est en statique
		if (((GraphModelView)this.getModel()).getType())
		{
			this.diagramTool = new OSelection(this);
		} else 
		// On est en dynamique
		{
			this.diagramTool = new OSelectionDyn(this);
		}

		// ajouter les controleurs
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		
		// Information pour la fenetre
		this.zone_affichage = this.getSize();
		this.setAutoscrolls(true);
		
		//prise en compte de la fin d'édition des cellules
		this.setInvokesStopCellEditing(true);
		
		// on met la couleur par d?faut au diagramme
		this.setBackground(new Color(Integer.parseInt(Application
						.getApplication().getConfigPropriete(
								"couleur_fond_diagrmme"))));

		//on peut aussi d?poser des objets dans l'arbre drop target
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this, true);

		this.setFocusable(true);
	}
	
	/**
	 * M?thode appel?e quand l'objet du domaine observ? est modifi?
	 * et qu'il appelle la m?thode notifyObservers()
	 */
	public void update(Observable o, Object arg) {

		this.repaint();
	}

	// recherche un modele a partir de son nom
	public GraphModelView ChercherModel(String nom){
		for (int i =0;i<this.modelesDiagrammes.size();i++)
		{
			GraphModelView courant=(GraphModelView)this.modelesDiagrammes.elementAt(i);
			if (courant.getNomDiagModel().equals(nom))
			{
				return (courant);
			}
		}
		return null;
	}
	//-------------------------------------------------------------------------
	//  Affichage
	//-------------------------------------------------------------------------
	
	/**
	 * Repeind le diagramme.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	//---------------------------------------------------------------------
	//                       Gestion des figures
	//---------------------------------------------------------------------

	/**
	 * Recherche la figure sur laquelle on a click?
	 * @param x, abscisse du click
	 * @param y, ordonn?e du click
	 * @return la figure sur laquelle on a cliqu?, null sinon
	 */
	public IeppCell chercherFigure(int x, int y) {
		/*Vecteur v = new Vecteur(x, y);
		Figure f;
		int n;

		// Recherche parmi les ?l?ments
		n = this.elements.size();
		for (int i = n - 1; i >= 0; i--) {
			f = (Figure) this.elements.elementAt(i);
			if (f.contient(v)) {
				return f;
			}
		}

		// Recherche parmi les liens
		n = this.liens.size();
		for (int i = n - 1; i >= 0; i--) {
			f = (Figure) this.liens.elementAt(i);
			if (f.contient(v)) {
				return f;
			}
		}*/
		return null;
	}

	public ComposantCell chercherComposant(long idcomp) {
		ComposantCell cc = null;
		Vector listComposants = this.getComposantCellCells();
		for( int i = 0 ; i < listComposants.size() ; i++) {
			ComposantCell c = (ComposantCell)listComposants.get(i);
			if ( idcomp == Application.getApplication().getReferentiel().chercherId( c.getCompProc() )) {
				cc = c;
			}
		}
		return cc;
	}

	public ComposantCell chercherComposant(String nomcomp) {
		ComposantCell cc = null;
		Vector listComposants = this.getComposantCellCells();
		for( int i = 0 ; i < listComposants.size() ; i++) {
			ComposantCell c = (ComposantCell)listComposants.get(i);
			if (nomcomp.equals(c.getNomCompCell())) {
				cc = c;
			}
		}
		return cc;
	}
	
	public ComposantCellDyn chercherComposantDyn(String nomcomp) {
		ComposantCellDyn cc = null;
		Vector listComposants = this.getComposantCellCells();
		for( int i = 0 ; i < listComposants.size() ; i++) {
			ComposantCellDyn c = (ComposantCellDyn)listComposants.get(i);
			if (nomcomp.equals(c.getNomCompCell())) {
				cc = c;
			}
		}
		return cc;
	}

	public ProduitCell chercherProduit(String nomcomp, String nomprod) {
		ProduitCell pce = chercherProduitEntree(nomcomp, nomprod);
		if (pce == null) {
			ProduitCell pcs = chercherProduitSortie(nomcomp, nomprod);
			if (pcs == null) {
				return null;
			}
			else {
				return pcs;
			}
		}
		else {
			return pce;
		}
	}

	public ProduitCell chercherProduitEntree(String nomcomp, String nomprod) {
		ProduitCellEntree pc = null;
		Vector listProduitsEntree = this.getProduitCellEntreeCells();
		for( int i = 0 ; i < listProduitsEntree.size() ; i++) {
			ProduitCellEntree c = (ProduitCellEntree)listProduitsEntree.get(i);
			if ((nomcomp.equals(c.getCompParent().getNomCompCell()))&&(nomprod.equals(c.getNomCompCell())))	{
				pc = c;
			}
		}
		return pc;
	}

	public ProduitCell chercherProduitSortie(String nomcomp, String nomprod) {
		ProduitCellSortie pc = null;
		Vector listProduitsSortie = this.getProduitCellSortieCells();
		for( int i = 0 ; i < listProduitsSortie.size() ; i++) {
			ProduitCellSortie c = (ProduitCellSortie)listProduitsSortie.get(i);
			if ((nomcomp.equals(c.getCompParent().getNomCompCell()))&&(nomprod.equals(c.getNomCompCell())))	{
				pc = c;
			}
		}
		return pc;
	}
	
	public ProduitCellFusion chercherProduitFusion(String nomprod) {
		ProduitCellFusion pcf = null;
		Vector listProduitFusion = this.getProduitCellFusionCells();
		for( int i = 0 ; i < listProduitFusion.size() ; i++) {
			ProduitCellFusion c = (ProduitCellFusion)listProduitFusion.get(i);
			if (nomprod.equals(c.getNomCompCell()))	{
				pcf = c;
			}
		}
		return pcf;
	}
	
	public TextCell chercherTextCell(int x, int y) {
		TextCell c = null;
		Vector listTextCell = this.getNoteCellCells();
		for( int i = 0 ; i < listTextCell.size() ; i++) {
			TextCell cel = (TextCell) listTextCell.get(i);
			if((cel.getAbscisse()==x) && (cel.getOrdonnee()==y)) {
				c = cel;
			}
		}
		return c;
	}
	
	/**
	 * Retourne tous les liens du diagramme.
	 */
	public Enumeration liens() {
		return ((GraphModelView)this.getModel()).getLiens().elements();
	}

	public Vector getLiens() {
		return ((GraphModelView)this.getModel()).getLiens();
	}

	public void setLiens(Vector l) {
		((GraphModelView)this.getModel()).setLiens(l);
	}
	
	public void ajouterLien(LienEdge c) {
		((GraphModelView)this.getModel()).getLiens().addElement(c);
	}

	/**
	 * D?finition des acceseurs elementCells 
	 */
	
	public Vector getElementsCell() {
		return ((GraphModelView)this.getModel()).getElementCells();
	}
	
	public Enumeration elementsCell() {
		return ((GraphModelView)this.getModel()).getElementCells().elements();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * D?finition des acceseurs selectionCells 
	 */
	public void setVectorSelectionCells(Vector l) {
		this.selectionCells.add(l);
	}
	
	public Vector getVectorSelectionCells() {
		return this.selectionCells;
	}
	
	public Enumeration selectionCellsVector() {
		return this.selectionCells.elements();
	}
	
	public Vector getComposantCellCells(){
		return ((GraphModelView)this.getModel()).getComposantCellCells();		
	}
	
	public Vector getProduitCellEntreeCells(){
		return ((GraphModelView)this.getModel()).getProduitCellEntreeCells();		
	}
	
	public Vector getProduitCellSortieCells(){
		return ((GraphModelView)this.getModel()).getProduitCellSortieCells();		
	}
	
	public Vector getProduitCellFusionCells(){
		return ((GraphModelView)this.getModel()).getProduitCellFusionCells();
	}
	
	public Vector getNoteCellCells(){
		return ((GraphModelView)this.getModel()).getNoteCellCells();		
	}
		
	/**
	 * Ajoute une cellule au diagramme (?l?ment ou lien).
	 * @param f, figure ? ajouter au diagramme
	 */
	public void ajouterCell(IeppCell c) {
		((GraphModelView)this.getModel()).getElementCells().add(c);
		if (c instanceof ComposantCellDyn){
			((GraphModelView)this.getModel()).getComposantCellCells().addElement(c);
		}
		
		if(c instanceof ComposantCell){
			((GraphModelView)this.getModel()).getComposantCellCells().addElement(c);
		}else if (c instanceof ProduitCellEntree){
			((GraphModelView)this.getModel()).getProduitCellEntreeCells().addElement(c);
		}else if (c instanceof ProduitCellSortie){
			((GraphModelView)this.getModel()).getProduitCellSortieCells().addElement(c);
		}else if (c instanceof ProduitCellFusion){
			((GraphModelView)this.getModel()).getProduitCellFusionCells().addElement(c);
		}else if (c instanceof TextCell){
			((GraphModelView)this.getModel()).getNoteCellCells().addElement(c);
		}
	}
	
	/**
	 * Supprime un ?l?ment du diagramme.
	 * @param f, l'?l?ment ? supprimer du diagramme
	 */
	public void supprimerCellule(IeppCell cell) {
		Vector vecObj = new Vector();
		
		// enlever l'?l?ment de toutes les listes disponibles
		this.clearSelection();
		
		((GraphModelView)this.getModel()).getElementCells().removeElement(cell);
		((GraphModelView)this.getModel()).getProduitCellSortieCells().removeElement(cell);
		((GraphModelView)this.getModel()).getProduitCellEntreeCells().removeElement(cell);
		((GraphModelView)this.getModel()).getComposantCellCells().removeElement(cell);
		((GraphModelView)this.getModel()).getProduitCellFusionCells().removeElement(cell);
		((GraphModelView)this.getModel()).getNoteCellCells().removeElement(cell);
		
		vecObj = new Vector();
		
		for (int i = 0; i < ((IeppCell) cell).getListeLien().size(); i++){
			((GraphModelView)this.getModel()).getLiens().removeElement(((IeppCell) cell).getListeLien().get(i));
			vecObj.add(((IeppCell) cell).getListeLien().get(i));
		}

		//((IeppCell) cell).removeAllChildren();
		vecObj.add(((IeppCell) cell).getPortComp());
		
		vecObj.add(cell);
		
		this.getModel().remove(vecObj.toArray());
		this.repaint();
		
	}

	public void MasquerCellule(IeppCell cell) {
		Vector vecObj = new Vector();
		
		// On supprime tous les liens pointants vers la cellule
		for (int i = 0; i < ((IeppCell) cell).getListeLien().size(); i++){
			((GraphModelView)this.getModel()).getLiens().removeElement(((IeppCell) cell).getListeLien().get(i));
			vecObj.add(((IeppCell) cell).getListeLien().get(i));
		}

		//((IeppCell) cell).remove(cell.getPortComp());
		
		// On supprime le port et la cellule
		vecObj.add(cell.getPortComp());
		vecObj.add(cell);
		
		this.getModel().remove(vecObj.toArray());
		this.repaint();
	}
	
	public void AfficherCelluleMasquee(IeppCell cell) {
		if( cell instanceof ProduitCellEntree) {
			AfficherCelluleEntreeMasquee((ProduitCellEntree) cell);
		}
		else if( cell instanceof ProduitCellSortie) {
			AfficherCelluleSortieMasquee((ProduitCellSortie) cell);
		}
		else
		{
			System.out.println("Impossible d'afficher ce type de cellule");
		}
	}
	
	public void AfficherCelluleEntreeMasquee(ProduitCellEntree cell) {
		ComposantCell cc;
		 
		 ProduitCellEntree pce;
		 
		 // On cree un edge pour la connection
		 LienEdge edge = new LienEdge();
		 
		 // On declare la source et l'extremite
		 //cc = this.chercherComposant(Application.getApplication().getReferentiel().chercherId(cell.getCompParent().getCompProc()));
		 //pce = (ProduitCellEntree)this.chercherProduit(cell.getCompParent().getNomCompCell(), cell.getNomCompCell());
	     
		 cc = cell.getCompParent();
		 pce = cell;
	     
		 pce.setPortComp(new DefaultPort());	
		 pce.setCellLiee(false);
		 edge.setSourceEdge(pce);
		 edge.setDestination(cc);

		 // on cree la map
		 Map AllAttribute = GraphConstants.createMap();

		 // On ajoute l'edge
		 AllAttribute.put(edge, edge.getEdgeAttribute());
		 AllAttribute.put(pce, pce.getAttributs());

		 // On recupere les ports
	     DefaultPort portS = cc.getPortComp();
	     DefaultPort portD = pce.getPortComp();
	     
		 cc.ajoutLien(edge);
		 pce.ajoutLien(edge);
		 
		 ConnectionSet cs = new ConnectionSet(edge, portD, portS);
		 
		 // On l'ajoute au modele
		 Vector vecObj = new Vector();
		 vecObj.add(pce);
		 vecObj.add(edge);

		 this.getModel().insert(vecObj.toArray(), AllAttribute, null, null, null);
		 this.getModel().insert(null, null, cs, null, null);

		 this.ajouterLien(edge);

		 repaint();
	}
	public void AfficherCelluleSortieMasquee(ProduitCellSortie cell) {
		 ProduitCellSortie pcs;
		 
		 // On cree un edge pour la connection
		 LienEdge edge = new LienEdge();
		 
		 // On declare la source et l'extremite
		 //ComposantCell cc = this.chercherComposant(Application.getApplication().getReferentiel().chercherId(cell.getCompParent().getCompProc()));
		 //pcs = (ProduitCellSortie) this.chercherProduit(cell.getCompParent().getNomCompCell(), cell.getNomCompCell());
		 
		 ComposantCell cc = cell.getCompParent();
		 pcs = cell;
		 
		 pcs.setPortComp(new DefaultPort());
		 pcs.setCellLiee(false);
		 edge.setSourceEdge(cc);
		 edge.setDestination(pcs);

		 // on cree la map
		 Map AllAttribute2 = GraphConstants.createMap();

		 // On ajoute l'edge
		 AllAttribute2.put(edge, edge.getEdgeAttribute());
		 AllAttribute2.put(pcs, pcs.getAttributs());

		 // On recupere les ports
	     DefaultPort portS2 = cc.getPortComp();
	     DefaultPort portD2 = pcs.getPortComp();
		 
		 pcs.ajoutLien(edge);
		 cc.ajoutLien(edge);
		 
		 ConnectionSet cs2 = new ConnectionSet(edge, portS2, portD2);
		 
		 // On l'ajoute au modele
		 Vector vecObj2 = new Vector();
		 vecObj2.add(pcs);
		 vecObj2.add(edge);

		 this.getModel().insert(vecObj2.toArray(), AllAttribute2, null, null, null);
		 this.getModel().insert(null, null, cs2, null, null);

		 //this.diagramme.ajouterCell(ps);
		 this.ajouterLien(edge);
				
		 repaint();
	}
	
	public void supprimerLien(LienEdge l) {
		
		((GraphModelView)this.getModel()).getLiens().removeElement(l);
		
		this.repaint();
	}

	//---------------------------------------------------------------------
	//                       Gestion de la s?lection
	//---------------------------------------------------------------------



	/**
	 * S?lectionne une cell.
	 */
	public void selectionneCell(IeppCell cell) {
		if (!this.selectionCells.contains(cell)) {
			this.selectionCells.addElement(cell);
		}
	}
	
	
	/**
	 * D?s?lectionne toutes les figures de la s?lection.
	 */
	public void clearSelection() {
		this.selectionCells.removeAllElements();
		this.setSelectionCells(null);
		this.setSelectionCell(null);
		super.clearSelection();
	}

	/**
	 * Efface compl?tement le diagramme
	 */
	public void effacerDiagramme() {
		((GraphModelView)this.getModel()).getLiens().removeAllElements();
		((GraphModelView)this.getModel()).getElementCells().removeAllElements();
		this.selectionCells.removeAllElements();
		this.removeAll();
		this.repaint();
	}

	/**
	 * S?lectionne tous les ?l?ments du diagramme.
	 */
	public void selectionnerTout() {
		
		Enumeration e = ((GraphModelView)this.getModel()).getElementCells().elements();
		while (e.hasMoreElements()) {
			IeppCell cell = (IeppCell) e.nextElement();
			if (!this.selectionCells.contains(cell)) {
				this.selectionCells.addElement(cell);
			}
			this.setSelectionCells(selectionCells.toArray());
		}
		
		// mettre ? jour l'affichage
		this.repaint();
	}

	
	public Dimension getZoneAffichage() {
		this.zone_affichage.height = this.getHeight();
		this.zone_affichage.width = this.getWidth();
		return this.zone_affichage;
	}


	//-------------------------------------------------------------------------
	//                           Gestion des outils
	//-------------------------------------------------------------------------

	/**
	 * Renvoie l'outil courant.
	 */
	public Outil getOutil() {
		return this.diagramTool;
	}

	/**
	 * Fixe l'outil courant.
	 */
	public void setOutil(Outil o) {
		this.diagramTool = o;
	}

	/**
	 * Fixe l'outil courant en tant que OSelection.
	 */
	public void setOutilSelection() {
		
		// Modif Julie 03.02.06 
	
		if (((GraphModelView)this.getModel()).getType()){

		edgeTool.uninstall(this);
			this.setOutil(new OSelection(this));
		}
		else{

			edgeToolDyn.uninstall(this);
			this.setOutil(new OSelectionDyn(this));
		}
		this.update(this.getGraphics());
	}

	/**
	 * Fixe l'outil courant en tant que OLier2Element.
	 */
	public void setOutilLier() {
		
		setOutilSelection();
		
		if (((GraphModelView)this.getModel()).getType()){
		edgeTool.install(this);
		}
		else{
			edgeToolDyn.install(this);
		}
		this.update(this.getGraphics());
	}

	/**
	 * Fixe l'outil courant en tant que OCreerElement
	 */
	public void setOutilCreerElement( int type) {
		
		this.setOutil(new OCreerElement(this, type));
		if (((GraphModelView)this.getModel()).getType()){
		edgeTool.uninstall(this);
		}
		else{
			edgeToolDyn.uninstall(this);
		}
	}

	/**
	 * Appel l'outil de d?calage de composant dyn
	 */
	public void setDecalerElement(boolean choix){
		Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
		CDecalerComposantGrapheDyn outilDecalage = new CDecalerComposantGrapheDyn(this,choix); 
		if(	outilDecalage.executer() ){
			Application.getApplication().getProjet().setModified(true);
		}
	}
	
	/**
	 * Appel l'outil de decendre ou monter un lien dyn
	 */
	public void setMonterOuDecendreLienDyn(boolean choix){
		Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
		CMonterOuDecendreLienEdgeDyn outilDecalage = new CMonterOuDecendreLienEdgeDyn(this,choix); 
		if(	outilDecalage.executer() ){
			Application.getApplication().getProjet().setModified(true);
		}
	}
	
	//---------------------------------------------------------------------
	//    Gestion des actions sur le diagramme
	//---------------------------------------------------------------------

	public void mouseClicked(MouseEvent e)
	{
		this.diagramTool.mouseClicked(e);
	}

	public void mousePressed( MouseEvent e )
	{
		this.diagramTool.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		
		this.diagramTool.mouseReleased(e);
	}

	public void mouseEntered(MouseEvent e) {
		
		this.diagramTool.mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		
		this.diagramTool.mouseExited(e);
	}

	public void mouseDragged(MouseEvent e) {
		
		this.diagramTool.mouseDragged(e);
	}
	
	public void mouseMoved(MouseEvent event) {
		
		this.diagramTool.mouseMoved(event);		
	}


	//---------------------------------------------------------------------
	//    gestion du clavier sur le diagramme
	//---------------------------------------------------------------------

	public void keyPressed(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {
		this.diagramTool.keyReleased(e);
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	//---------------------------------------------------------------------
	//    gestion du drop sur le diagramme
	//---------------------------------------------------------------------

	public void dragEnter(DropTargetDragEvent arg0) {
	}

	public void dragOver(DropTargetDragEvent arg0) {
	}

	public void dropActionChanged(DropTargetDragEvent arg0) {
	}

	public void dragExit(DropTargetEvent arg0) {
	}

	
	
	
	/**
	 * R?cup?re l'?l?ment qui a ?t? gliss? d?pos? sur le diagramme
	 * cet ?l?ment est obligatoirement un idcomposant
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent dtde) {

		// r?cup?rer l'objet d?plac?
		Transferable transferable = dtde.getTransferable();
		DataFlavor[] flavors = transferable.getTransferDataFlavors();

		// r?cup?rer l'endroit o? l'utilisateur ? d?placer l'objet
		Point p = dtde.getLocation();

		try {						
			// R?cup?rer l'objet transferr? par glissement
			Object obj = transferable.getTransferData(flavors[0]);
			IdObjetModele id = null; // composant ? afficher
			// Si c'est un Long, il s'agit en fait de l'id du composant dans le r?f?rentiel
			// => Charger le composant
			if (obj instanceof Long) {
				// R?cup?rer l'id
				long idComp = ((Long) obj).longValue();
						// Modif Julie 17/02/06
						// On peut ajouter un composant dans la diagramme dynamique
						// que s'il apparait dans la diagramme statique
						Referentiel ref = Application.getApplication().getReferentiel() ;
						if ((!(((GraphModelView) this.getModel()).getType())) 
						   && (ref.chercherReference (idComp) == null)) {
							JOptionPane.showMessageDialog( Application.getApplication().getFenetrePrincipale(),
							Application.getApplication().getTraduction("ERR_Composant_Dyn_Pas_Statique_DP"),
							Application.getApplication().getTraduction("ERR"), JOptionPane.ERROR_MESSAGE) ;
							return;
						} else {

							// Ajouter le composant ? la DP
							CAjouterComposantDP commande = new CAjouterComposantDP(idComp);
				
							if (commande.executer()) {
								Application.getApplication().getProjet().setModified(true);
							}
							// R?cup?rer sa r?f?rence et remplir l'Id
							ref = Application.getApplication().getReferentiel();
							ComposantProcessus comp = (ComposantProcessus) ref.chercherReference(idComp);
							if (comp != null) {
								id = comp.getIdComposant();
							} else {
								dtde.dropComplete(false);
								return;
							}
						}
			// Sinon c'est un vrai id, ? r?cup?rer
			}else {
				id = (IdObjetModele) obj;
			}
			dtde.dropComplete(true);

			if(((GraphModelView) getModel()).getType())
			{
			        CAjouterComposantGraphe c = new CAjouterComposantGraphe(id, p);
			        if (c.executer()) {
			        	Application.getApplication().getProjet().setModified(true);
	        		}

			}
			else
			{
				int nbre = ((GraphModelView) getModel()).getComposantCellCells().size();
				CAjouterComposantGrapheDyn c = new CAjouterComposantGrapheDyn(id, new Point(50+nbre*200,25));
				if (c.executer()) {
					Application.getApplication().getProjet().setModified(true);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		dtde.dropComplete(false);
		
	}

	/**
	 * @param IdObjetModele de l'objet
	 * @return IeppCell si l'objet est present dans le graphe, null sinon
	 */
	public IeppCell contient(IdObjetModele id) {

		// Cellule
		IeppCell courant;
		// POur toutes les cellules du diagramme courant (stat, Dyn) on cherche si le composant est present
		for (int i = 0; i < ((GraphModelView)this.getModel()).getElementCells().size(); i++) {
			courant = (IeppCell) ((GraphModelView)this.getModel()).getElementCells().elementAt(i);
			if (courant.getId() != null) {
				if (courant.getId().equals(id)) {
					return courant;
				}
			}
		}
		return null;
	}
	
	
	protected void overlay(JGraph gpgraph, Graphics g, boolean clear) {
	
	
	}


	/** 
	 * @see JGraph#createVertexView(java.lang.Object, org.jgraph.graph.CellMapper)
	 * Gestion des vues des composants
	 */
	protected VertexView createVertexView(Object v, CellMapper cm) {

		// Return the appropriate view
		if (v instanceof ComposantCell) {
			return new ComposantView(v, this, cm);
		} else if (v instanceof ProduitCell) {
			return new ProduitView(v, this, cm);
		} else if (v instanceof ProduitCellFusion) {
			return new ProduitView(v, this, cm);
		} else if (v instanceof TextCell) {
			return new TextView(v, this, cm);
		} else if (v instanceof ComposantCellElementDyn) {
			return new ComposantElementDynView(v, this, cm);
		} else if (v instanceof DocumentCellDyn) {
			return new DocumentCellDynView(v, this, cm);
		} else {
			return new VertexView(v, this, cm);
		}

	}

	/* (non-Javadoc)
	 * @see org.jgraph.JGraph#createEdgeView(java.lang.Object, org.jgraph.graph.CellMapper)
	 */
	protected EdgeView createEdgeView(Object v, CellMapper cm) {
		if (v instanceof LienEdge) {
			return new LienEdgeView(v, this, cm);
		}else {
			return new EdgeView(v, this, cm);
		}
	}

	public Vector getModelesDiagrammes() {
		return modelesDiagrammes;
	}

	public void setModelesDiagrammes(Vector modelesDiagrammes) {
		this.modelesDiagrammes = modelesDiagrammes;
	}
	
}
