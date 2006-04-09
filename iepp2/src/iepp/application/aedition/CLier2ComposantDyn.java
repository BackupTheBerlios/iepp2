package iepp.application.aedition;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.GraphModelView;
import iepp.ui.iedition.dessin.rendu.ComposantCellDyn;
import iepp.ui.iedition.dessin.rendu.DocumentCellDyn;
import iepp.ui.iedition.dessin.rendu.liens.LienEdgeDyn;

import java.util.Map;
import java.util.Vector;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

/*
 *  permet de lier 2 composant dynamique
 */

public class CLier2ComposantDyn extends CommandeAnnulable
{

	// reference au diagramme dynamique
	private VueDPGraphe diagramme;

	// composant source et destination
	private ComposantCellDyn composantSrc,composantDest;
	
	// chemin de l'image du lien
	private String image;
	
	// texte associé au lien
	private String valeur;

	// document source et destination
	private DocumentCellDyn cellsource, celldestination;
	

	
	
		

	/**
	* Constructeur de la commande ? partir du diagramme sur lequel on va effectuer la liaison
	* les deux ?l?ments que l'on veut fusionner et l'ensemble des points d'ancrage utilis?s pour
	* faire la liaison (ne servent qu'? la liaison, ils n'apparaissent pas lorsque le produit fusion est cr??)
	* @param d diagramme sur lequel on effectue la liaison
	* @param source figure sur laquelle on a cliqu? en premier
	* @param destination figure sur laquelle on a cliqu? en second
	* @param pointsAncrageIntermediaires liste des points d'ancrage cr??e lors de la liaison entre les deux figures
	*/

	public CLier2ComposantDyn(VueDPGraphe d, ComposantCellDyn compoSrc , ComposantCellDyn compoDest,DocumentCellDyn Cellsource, DocumentCellDyn Celldestination, Vector pointsAncrageIntermediaires, String texte,String i)

	{
        this.diagramme = d;
        this.valeur=texte;
        this.image=i;
        this.composantSrc=compoSrc;
        this.composantDest=compoDest;
        this.celldestination = Celldestination;
        this.cellsource = Cellsource;
	}



	/**
	* Retourne le nom de l'?dition.
	*/
	public String getNomEdition()
	{
		return "Lier element";
	}


	/**
	 * La commande renvoie si elle s'est bien pass?e ou non
	 * Si la fusion est possible, cr?? les figures du produit fusion s'il n'existe pas
	 * cr?? les liens fusions entre les produits fusion et les composants concern?s
	 * Cr?er les liens au niveau du mod?le, chaque composant connait les liens entre
	 * ses produits et les produits des autres composants
	 * @return true si la liaison s'est bien pass?e false sinon
	 */
	public boolean executer()
	{
		/////////////////////////////////////////////
		// Ajout pour la prise en compte de JGraph //
		/////////////////////////////////////////////
		

		this.diagramme.clearSelection();
		this.diagramme.setSelectionCells(null);

		LienEdgeDyn edge1 = new LienEdgeDyn(this.valeur,this.image,celldestination,cellsource);
		
		this.composantDest.ajoutLien(edge1);
		this.composantSrc.ajoutLien(edge1);
		
		celldestination.ajouterLien(edge1);
		cellsource.ajouterLien(edge1);
		
		Map AllAttribute = GraphConstants.createMap();
		
		AllAttribute.put(edge1, edge1.getEdgeAttribute());
		
		DefaultPort portS = cellsource.getPortComp();
		DefaultPort portD = celldestination.getPortComp();

		ConnectionSet cs1 = new ConnectionSet(edge1, portD,portS);
		
		Vector vecObj = new Vector();
		
		vecObj.add(edge1);

		celldestination.IconifierDocument(this.image,celldestination.getNiveau());
		this.diagramme.getModel().insert(vecObj.toArray(), AllAttribute,null, null, null);
		this.diagramme.getModel().insert(null, null, cs1, null, null);
		
		this.diagramme.ajouterLien(edge1);
		this.diagramme.repaint();
	
		// reprendre l'outil de s?l?ction
		//Application.getApplication().getProjet().getFenetreEdition().setOutilSelection();
		//incrementer la ligne de vie de touts les composants
		for (int i=0;i<((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModel()).getComposantCellCells().size();i++){
			((ComposantCellDyn)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getComposantCellCells().elementAt(i)).incrementerLigneDeVie();
		}
		
		
		return true;

	}

}



