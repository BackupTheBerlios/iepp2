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
package iepp.application.averification;

import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import iepp.Application;
import iepp.application.CommandeNonAnnulable;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.DefinitionProcessus;
import iepp.domaine.IdObjetModele;
import iepp.domaine.LienProduits;


/**
 * Commande lançant différentes vérifications sur le processus en cours de définition. 
 */
public class CVerification extends CommandeNonAnnulable
{

	private Vector listeRes ;	// Vecteur contenant la liste de résultats

	/**
	 * Lance la commande de vérification générale de la définition de processus.
	 * La commande remplit une liste avec les résultats obtenus ; ces résultats peuvent
	 * être récupérés grâce à la commande getResultats().
	 * Une commande doit pouvoir s'exécuter et indiquer si elle s'est bien déroulée ou non
	 * @return true (sauf une éventuelle exception, la commande s'exécute toujours bien)
	 */
	public boolean executer()
	{
		// Lancer les vérifications et noter les résultats
		this.listeRes = new Vector() ;
		VGestVerification gestVerif = new VGestVerification() ;
		// (noms de composants uniques)
		this.listeRes.add (gestVerif.nomsComposantsUniques()) ;
		this.recupererListe();
		// La commande s'exécute toujours bien
		return true ;
	}
	
	public boolean estLie(IdObjetModele id, Vector liens)
	{
		for (int k=0; k < liens.size() ; k++)
		{
			LienProduits lienCourant = (LienProduits) liens.elementAt(k);
			
			if (lienCourant.getProduitEntree().equals(id) || lienCourant.getProduitSortie().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	public void recupererListe()
	{
		Vector listeProdEntree = new Vector();
		Vector listeProdSortie = new Vector();
		Vector listeComposants = new Vector();
		
		
		DefinitionProcessus defProc = Application.getApplication().getProjet().getDefProc();
		for (int i = 0; i < defProc.getListeComp().size(); i++)
		{
			IdObjetModele id = (IdObjetModele) defProc.getListeComp().elementAt(i);
			ComposantProcessus cp = (ComposantProcessus)id.getRef();
		
			int nbInterfaceNonResolues = 0 ;
			
			Vector liens = cp.getLien();
			// récupérer la liste des produits en entrée qui ne sont pas liés
			Vector produitEntree = cp.getProduitEntree();
			
			for (int j = 0; j < produitEntree.size();j++)
			{
				IdObjetModele idproduits = (IdObjetModele)produitEntree.elementAt(j);
				if ( !this.estLie(idproduits, liens))
				{
					listeProdEntree.addElement(idproduits);
					nbInterfaceNonResolues++;
				}
			}
			
			
			// récupérer la liste des produits en sortie qui ne sont pas liés
			Vector produitSortie = cp.getProduitSortie();
			for (int j = 0; j < produitSortie.size();j++)
			{
				IdObjetModele idproduits = (IdObjetModele)produitSortie.elementAt(j);
				if ( !this.estLie(idproduits, liens))
				{
					listeProdSortie.addElement(idproduits);
					nbInterfaceNonResolues++;
				}
			}
			
			if  ( nbInterfaceNonResolues != 0 )
			{
				listeComposants.addElement(id);
			}
		}
		
		if (listeProdEntree.size() != 0 || listeProdSortie.size() != 0 || listeComposants.size() != 0 )
		{
			// afficher le panneau de vérification
			//Application.getApplication().getFenetrePrincipale().afficherPanneauVerification(listeProdEntree, listeProdSortie, listeComposants);    
		}
		else
		{
        	//Application.getApplication().getFenetrePrincipale().getPanneauVerif().clearErrorArea();
        	 JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),Application.getApplication().getTraduction("projet_valide"),
	 				Application.getApplication().getTraduction("Valider"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}


	/**
	 * Renvoie un vecteur contenant les résultats de la vérification. le vecteur
	 * contient une série d'objets VResultatVerif indiquant les messages obtenus.
	 * @return un vecteur contenant les résultats de la vérification
	 */
	public Vector getResultats()
	{	
		return (Vector) this.listeRes.clone() ;
	}

}
