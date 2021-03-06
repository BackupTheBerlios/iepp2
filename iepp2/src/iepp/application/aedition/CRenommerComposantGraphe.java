package iepp.application.aedition;

import iepp.Application;
import iepp.application.CommandeAnnulable;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;
import iepp.ui.iedition.dessin.rendu.IeppCell;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//fait par Youssef

public class CRenommerComposantGraphe extends CommandeAnnulable
{
		/**
         * Id du composant � renommer du graphe
         */
        private IdObjetModele composant;

        
        /**
         * Cell du composant � renommer du graphe
         */
        private IeppCell cell;
        
        /**
         * Diagramme duquel on veut renommer un composant
         */
        private VueDPGraphe diagramme;

        /**
         * nouveau nom du composant
         */
        String nom;

        /**
         * Constructeur de la commande, r�cup�re le composant � supprimer
         * et le diagramme courant de l'application
         * @param compo id du composant � supprimer
         * */

		  public CRenommerComposantGraphe(IdObjetModele compo,String n)
		  {
		             // initialiser le composant � renommer
		                this.composant = compo ;
		                this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		                this.nom=n;
		                this.cell = (IeppCell)this.diagramme.contient(compo);
		  }
        /**
         * La commande renvoie si elle s'est bien pass�e ou non
         * Parcours la liste des produits du composant, v�rifie s'il n'y a pas
         * de produits fusion "� d�fusionner", supprime les figures des produits et du composant
         * @return true si l'export s'est bien pass� false sinon
         */
        public boolean executer()
        {
         		
        	Map att = new HashMap();
        	
        	cell.setNomCompCell(nom);
        		
        	att.put(cell,cell.getAttributs());
        	
        	this.diagramme.getModel().insert(null,att,null,null,null);
        		
        	this.diagramme.repaint();
        	
        	return true;


}
}
