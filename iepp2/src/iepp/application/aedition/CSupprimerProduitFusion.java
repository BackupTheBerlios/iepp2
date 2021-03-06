package iepp.application.aedition;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.IeppCell;
import iepp.ui.iedition.dessin.rendu.ProduitCell;
import iepp.ui.iedition.dessin.rendu.ProduitCellEntree;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;
import iepp.ui.iedition.dessin.rendu.ProduitCellSortie;
import iepp.ui.iedition.dessin.rendu.liens.LienEdge;

import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

public class CSupprimerProduitFusion extends CommandeAnnulable
{
	/**
	 * Id du composant � supprimer du graphe
	 */
	private IdObjetModele composant;
	
	/**
	 * Cellule du composant � supprimer du graphe
	 */
	private ProduitCellFusion produitF;
	
	
	/**
	 * Diagramme duquel on veut supprimer un composant
	 */
	private VueDPGraphe diagramme;
	
	
	/**
	 * Constructeur de la commande, r�cup�re le composant � supprimer 
	 * et le diagramme courant de l'application
	 * @param compo id du composant � supprimer
	 */
	public CSupprimerProduitFusion (VueDPGraphe diag,ProduitCellFusion prodF)
	{
		// initialiser le composant � supprimer
		this.produitF = prodF;
		this.composant = prodF.getId() ;
		this.diagramme = diag;
		
	}
	
	/**
	 * La commande renvoie si elle s'est bien pass�e ou non
	 * Parcours la liste des produits du composant, v�rifie s'il n'y a pas
	 * de produits fusion "� d�fusionner", supprime les figures des produits et du composant
	 * @return true si l'export s'est bien pass� false sinon
	 */
	public boolean executer()
	{
		/*
		// r�cup�re la liste des liens du composant � supprimer
		Vector listeLiens = ((ComposantProcessus)this.composant.getRef()).getLien();
		
		// r�cup�rer la liste des produits en entr�e du composant
		Vector listeEntree = composant.getProduitEntree();
		for (int i = 0; i < listeEntree.size(); i++)
		{
			// r�cup�rer le produit courant
			IdObjetModele produitCourant = (IdObjetModele)listeEntree.elementAt(i);
			// supprimer les �ventuels produits fusion et supprimer le produit
			this.supprimerFusion(produitCourant, listeLiens);		
		}
		
		// r�cup�rer la liste des produits en sortie du composant
		Vector listeSortie = composant.getProduitSortie();
		for (int i = 0; i < listeSortie.size(); i++)
		{
			// r�cup�rer le produit courant
			IdObjetModele produitCourant = (IdObjetModele)listeSortie.elementAt(i);
			// supprimer les �ventuels produits fusion et supprimer le produit
			this.supprimerFusion(produitCourant, listeLiens);		
		}

		// suppression du composant
		this.diagramme.supprimerFigure(this.diagramme.contient(this.composant));
		 
		*/
		// modif Aldo Nit 15/01/06
		// on remet le produit en entr�e
		
		for(int i=0;i<this.produitF.getProduitCellEntrees().size();i++){
			this.diagramme.AfficherCelluleMasquee((IeppCell)this.produitF.getProduitCellEntrees().elementAt(i));
		}
		
		this.diagramme.AfficherCelluleMasquee(this.produitF.getProduitCellSortie());
		 /*
		 ProduitCellSortie pcs;
		 
		 // on remet le produit en sortie
		 ProduitCellSortie ps=this.produitF.getProduitCellSortie();
		 
		 // On cree un edge pour la connection
		 LienEdge edge2 = new LienEdge();
		 
		 // On declare la source et l'extremite
		 cc = this.diagramme.chercherComposant(Application.getApplication().getReferentiel().chercherId(ps.getCompParent().getCompProc()));
		 pcs = (ProduitCellSortie) this.diagramme.chercherProduit(Application.getApplication().getReferentiel().chercherId( cc.getCompProc() ), ps.getNomCompCell());
		 pcs.setPortComp(new DefaultPort());
		 pcs.setCellLiee(false);
		 edge.setSource(cc);
		 edge.setDestination(pcs);

		 System.out.println("Composant:"+cc.getNomCompCell());
		 System.out.println("Produit:"+pcs.getNomCompCell());

		 // on cree la map
		 Map AllAttribute2 = GraphConstants.createMap();

		 // On ajoute l'edge
		 AllAttribute2.put(edge2, edge2.getEdgeAttribute());
		 AllAttribute2.put(pcs, pcs.getAttributs());

		 // On recupere les ports
	     DefaultPort portS2 = cc.getPortComp();
	     DefaultPort portD2 = pcs.getPortComp();
		 
		 pcs.ajoutLien(edge2);
		 cc.ajoutLien(edge2);
		 
		 ConnectionSet cs2 = new ConnectionSet(edge2, portS2, portD2);
		 
		 // On l'ajoute au modele
		 Vector vecObj2 = new Vector();
		 vecObj2.add(pcs);
		 vecObj2.add(edge2);

		 this.diagramme.getModel().insert(vecObj2.toArray(), AllAttribute2, null, null, null);
		 this.diagramme.getModel().insert(null, null, cs2, null, null);

		 //this.diagramme.ajouterCell(ps);
		 this.diagramme.ajouterLien(edge2);
		
		 this.diagramme.supprimerCellule(produitF);
		
		 diagramme.repaint();
		 */
		 this.diagramme.supprimerCellule(produitF);
		 return true;
 
	}
	
	/**
	 * Recherche dans la liste des liens du composant le produitCourant
	 * pour v�rifier s'il n'y a pas un produit fusion � supprimer
	 * d�fusionne le produit fusion s'il existe, supprime le lien interface et le produit
	 * simple recr�� lorsqu'on supprimer un lien fusion
	 * @param produitCourant id du produit pour lequel on v�rifie s'il fait partie d'un produit fusion
	 * @param listeLiens liens des liens dans le mod�le du composant � supprimer
	 */
	public void supprimerFusion(IdObjetModele produitCourant, Vector listeLiens)
	{
		// v�rifier que les produits ne soient pas dans des produits fusions
		/*for (int j = 0; j < listeLiens.size(); j++)
		{
			// r�cup�rer le lien courant
			LienProduits lp = (LienProduits)listeLiens.elementAt(j);
			if (lp.getProduitEntree().equals(produitCourant)
					|| lp.getProduitSortie().equals(produitCourant))
			{
				CSupprimerLienFusion c = new CSupprimerLienFusion(this.diagramme, lp.getLienFusion());
				c.executer();
			}
		}
		
		// supprimer le lien
		FProduit fp = (FProduit)this.diagramme.contient(produitCourant);
		this.diagramme.supprimerFigure(fp.getLienInterface());
		// supprimer le produit
		this.diagramme.supprimerFigure(this.diagramme.contient(produitCourant));*/
	}
}



