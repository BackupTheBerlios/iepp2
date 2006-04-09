package iepp.application.aedition;

import iepp.Application;
import iepp.application.CEnregistrerInterface;
import iepp.application.CommandeAnnulable;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.ComposantCell;

import java.io.File;

import javax.swing.JOptionPane;

//fait par Youssef

public class CRenommerComposant extends CommandeAnnulable
{
         /**
         * Id du composant à renommer du graphe
         */
        private IdObjetModele composant;
        
        /**
         * Cell du composant à renommer du graphe
         */
        private ComposantCell composantCell;

        /**
         * nouveau nom du composant
         */
        private String nom;

        /**
         * Diagramme duquel on veut renommer un composant
         */
        private VueDPGraphe diagramme;


        /**
         * Constructeur de la commande, récupère le composant à renommer
         * et le diagramme courant de l'application
         * @param compo id du composant à renommer
         */

        public CRenommerComposant(IdObjetModele compo,String n)
        {
        	// initialiser le composant à renommer
                this.composant = compo ;
                this.diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
                this.nom=n;
         }
        
        /**
         * La commande renvoie si elle s'est bien passée ou non
         * Parcours la liste des produits du composant, vérifie s'il n'y a pas
         * de produits fusion "à défusionner", supprime les figures des produits et du composant
         * @return true si l'export s'est bien passé false sinon
         */
        public boolean executer()
        {
        	 // Chemin de point apes
            Referentiel ref = Application.getApplication().getReferentiel() ;
            String chemin = ref.getCheminReferentiel()+Application.filesep;
            
        	// Fichier temp pour verifier son existance
            File fic = new File(chemin+this.nom+".apes");
   		 	
            // vérifier si le fichier existe déjà, auquel cas on demande confirmation pour l'écrasement
            if(fic.exists())
            {
	   			 int choice = JOptionPane.showConfirmDialog( Application.getApplication().getFenetrePrincipale(),
	   														 Application.getApplication().getTraduction("msgConfirmEcrasement") ,
	   														 Application.getApplication().getTraduction("msgSauvegarde"),
	   														 JOptionPane.YES_NO_OPTION,
	   														 JOptionPane.QUESTION_MESSAGE);
	
	   			 if(choice!=JOptionPane.YES_OPTION)
	   			 {
	   				 return false;
	   			 }
            }
            
            // Verifier si le composant est dans le diagramme, et si oui, le renommer
            if (diagramme.contient(this.composant) != null)
            {
                    new CRenommerComposantGraphe(this.composant,this.nom).executer();
            }
            
            //Suppression de l'ancien .apes
            File old = new File(chemin+this.composant+".apes");
            old.delete();
            
            // Mise a jour dans les composant du projet
            for(int i=0;i<ref.getNoeudComposants().getChildCount();i++){
            	if(ref.getNoeudComposants().getChildAt(i).toString().equals(this.composant.toString())){
            		ElementReferentiel elref = ((ElementReferentiel)(ref.getNoeudComposants().getChildAt(i)));
                	elref.setNomElement(this.nom);
                	elref.setChemin(chemin+this.nom+".apes");
            	}else if(ref.getNoeudComposants().getChildAt(i).toString().equals(this.composant.toString()+Application.getApplication().getTraduction("Comp_Present"))){
            		ElementReferentiel elref = ((ElementReferentiel)(ref.getNoeudComposants().getChildAt(i)));
                	elref.setNomElement(this.nom+Application.getApplication().getTraduction("Comp_Present"));
                	elref.setChemin(chemin+this.nom+".apes");
            	}
            }
            ref.majObserveurs(ref.CHANGED);
            
            // Renommer le composant de la definition processus
            ComposantProcessus cp = (ComposantProcessus)this.composant.getRef();
            cp.setNomComposant(this.nom);
             
            Application.getApplication().getProjet().getDefProc().renommerComposant(this.composant,this.nom);
             
            // mise a jour du chemin du composant
            this.composant.setChemin(chemin+this.composant+".apes");
            
            // Sauvegarde du nouveau point APES
            CEnregistrerInterface saveCp = new CEnregistrerInterface(this.composant);
            saveCp.sauvegarderInterface(chemin+this.composant+".apes");
            
            // Mettre a jour l'arbre
            Application.getApplication().getFenetrePrincipale().getVueDPArbre().updateUI();

            return true;
        }

}
