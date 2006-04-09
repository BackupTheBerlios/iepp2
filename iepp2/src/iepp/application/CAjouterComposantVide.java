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

package iepp.application;

import java.awt.Point;
import java.io.File;

import iepp.* ;
import iepp.application.aedition.CAjouterComposantGraphe;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.* ;

/**
 * Commande non annulable permettant d'ajouter un composant dans la définition de processus courante
 */
public class CAjouterComposantVide extends CommandeNonAnnulable
{

	boolean place;
	int posX;
	int posY;
	
	public CAjouterComposantVide(){
		this.place = false;
	}
	
	public CAjouterComposantVide(int x, int y){
		this.place = true;
		this.posX = x;
		this.posY = y;
	}
	
	/**
	 * Ajoute un composant dans la définition de processus
	 * @see iepp.application.Commande#executer()
	 * @return true si le composant a été ajouté
	 */
	public boolean executer()
	{
		// on crée notre composant de processus vide
		ComposantProcessus compProc = new ComposantProcessus (Application.getApplication().getProjet().getDefProc()) ;
		compProc.setVide(true);
		compProc.setNomComposant(Projet.getNouveauNom());
		
		// on regarde si le fichier du composant n'existe déja pas 
		Referentiel ref = Application.getApplication().getReferentiel();
		String chemin = ref.getCheminReferentiel()+Application.filesep;
		File f = new File(chemin+compProc+".apes");
		while(f.exists()){
			compProc.setNomComposant(Projet.getNouveauNom());
			f = new File(chemin+compProc+".apes");
		}
		
		//compProc.setNomFichier(chemin+compProc+".apes");
		//compProc.getIdComposant().setChemin(chemin+compProc+".apes");
		//compProc.getIdComposant().setNomElement(compProc.toString());
		
		// ajouter le composant créé à la définition de processus du projet courant
		
		//Application.getApplication().getProjet().getDefProc().getListeComp();
		
		Application.getApplication().getProjet().getDefProc().ajouterComposant(compProc) ;
		// Ajouter le composant au référentiel (création de la structure logique et du répertoire,
		// sans rien enregistrer dedans)
		long id = ref.ajouterElement (compProc.getNomComposant(), ElementReferentiel.COMPOSANT_VIDE) ;
		if (id < 0)
			return false ;
		// Ajouter au référentiel l'association id-référence
		ref.ajouterReferenceMemoire (compProc, id) ;
		// Sauver réellement le composant
		ref.sauverComposantVide (compProc) ;
		
		if(place == true){
			
			CAjouterComposantGraphe c = new CAjouterComposantGraphe(compProc.getIdComposant(), new Point(this.posX,this.posY));
	        if (c.executer()) {
	        	Application.getApplication().getProjet().setModified(true);
    		}
			
		}
		
		
               return true ;
	}
}
