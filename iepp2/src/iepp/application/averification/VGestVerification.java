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

import iepp.Application;
import iepp.Projet;
import iepp.domaine.IdObjetModele;

import java.util.Vector;

/**
 * Classe permettant d'effectuer les diff\uFFFDrentes v\uFFFDrifications \uFFFD appliquer sur le
 * processus.
 */
public class VGestVerification
{

	/**
	 * V\uFFFDrifie que le composant n'a pas d\uFFFDj\uFFFD \uFFFDt\uFFFD charg\uFFFD depuis le r\uFFFDf\uFFFDrentiel
	 * dans la d\uFFFDfinition de processus.
	 * @param idComposant identifiant du composant dans le r\uFFFDf\uFFFDrentiel
	 * @return le r\uFFFDsultat de la v\uFFFDrification
	 */
	// Utiliser le r\uFFFDf\uFFFDrentiel pour savoir si un \uFFFDl\uFFFDment est charge
	//(chercherReference(id) renvoie null si le composant n'est pas charg\uFFFD)
/*
	public VResultatVerif composantNonCharge (int idComposant)
	{
		// Les composants auront des identifiants dans le r\uFFFDf\uFFFDrentiel, ce qui permettra
		// de ne pas les charger plusieurs fois
		return null ;
	}
*/


	/**
	 * Renvoie le nombre de composants portant un nom donn\uFFFD
	 * @param nom nom du composant \uFFFD chercher dans la d\uFFFDfinition de processus
	 * @return le nombre de composants portant ce nom
	 */
	protected int nbComposants (String nom)
	{
		// R\uFFFDcup\uFFFDrer les composants et initialiser le r\uFFFDsultat
		Projet projet = Application.getApplication().getProjet() ;
		int nbComposants = 0 ;
		if (projet != null)
		{
			Vector listeComp = projet.getDefProc().getListeComp() ;
			// Regarder si le nom est d\uFFFDj\uFFFD pris
			for (int i = 0 ; i < listeComp.size() ; i++)
			{
				// Compter les composants du nom donn\uFFFD
				IdObjetModele id = (IdObjetModele)listeComp.get(i);
				String nomComposant = id.toString();
				if (nomComposant.equals(nom))
					nbComposants++ ;
			}
		}
		return nbComposants ;
	}

	/**
	 * V\uFFFDrifie que le nom indiqu\uFFFD n'est pas d\uFFFDj\uFFFD celui d'un composant charg\uFFFD dans la
	 * d\uFFFDfinition de processus.
	 * @param nom nom du composant
	 * @return le r\uFFFDsultat de la v\uFFFDrification
	 */
	public VResultatVerif nomDejaPris (String nom)
	{
		// R\uFFFDcup\uFFFDrer les composants et initialiser le r\uFFFDsultat
		VResultatVerif res = new VResultatVerif ("TITREVERIF_NOM_LIBRE") ;
		if (this.nbComposants (nom) > 0)
		{
			res.ajouterErr (nom+" : "+
				Application.getApplication().getTraduction ("MSGERR_NOM_EXISTANT")) ;
		}
		// Renvoy\uFFFD le r\uFFFDsultat cr\uFFFD\uFFFD
		return res ;
	}


	/**
	 * V\uFFFDrifie que chaque composant a un nom unique.
	 * @return le r\uFFFDsultat de la v\uFFFDrification
	 */
	public VResultatVerif nomsComposantsUniques ()
	{
		VResultatVerif resVerif ;	// R\uFFFDsultat \uFFFD renvoyer
		VResultatVerif res ;		// R\uFFFDsultat de la v\uFFFDrification pour 1 composant
		Vector listeComp ;	// Liste des composants de la d\uFFFDfinition de processus

		// Parcourir les composants
		resVerif = new VResultatVerif (
			Application.getApplication().getTraduction ("TITREVERIF_NOMS_UNIQUES")) ;
		Projet projet = Application.getApplication().getProjet() ;
		if (projet != null)
		{
			listeComp = projet.getDefProc().getListeComp() ;
			for (int i = 0 ; i < listeComp.size() ; i++)
			{
				// V\uFFFDrifier que son nom est unique
				IdObjetModele id = (IdObjetModele)listeComp.get(i);
				String nom = id.toString();
				if (this.nbComposants (nom) > 1)
				{
					resVerif.ajouterErr (
						Application.getApplication().getTraduction
							("MSGERR_NOM_EXISTANT") + " : "+nom) ;
				}
			}
		}
		// Renvoyer le r\uFFFDsultat global
		return resVerif ;
	}

	public VResultatVerif connexionPossible (IdObjetModele source, IdObjetModele destination)
	{
		// Cette m\uFFFDthode  sera impl\uFFFDment\uFFFDe si la solution de tout v\uFFFDriifer dans cette
		// classe est retenue
		VResultatVerif resVerif ;	// R\uFFFDsultat \uFFFD renvoyer

		// v\uFFFDrifier que l'objet source et l'objet destination sont des produits
		resVerif = new VResultatVerif("Verification connexion");
		if ( !source.estProduit() || !destination.estProduit() )
		{
			resVerif.ajouterErr ("Connexion avec un composant impossible");
		}
		// on a deux produits il ne faut pas que ce soit le m\uFFFDme produit
		else
		{
			if ( source == destination )
			{
				resVerif.ajouterErr ("Connexion avec un m\uFFFDme produit impossible");
			}
			// deux produits diff\uFFFDrents, il faut que leurs composants d'origine ne soient pas les m\uFFFDmes
			else
			{
				if (source.getRef() == destination.getRef())
				{
					resVerif.ajouterErr ("Connexion entre produits d'un m\uFFFDme composant");
				}
				// il faut que les produits soient de type diff\uFFFDrents
				else
				{
					if ((source.estProduitEntree() && destination.estProduitEntree())
						|| (source.estProduitSortie() && destination.estProduitSortie()))
					{
						resVerif.ajouterErr ("Connexion entre produits de m\uFFFDme type");
					}
					else
					{
						//TODO continuer les v\uFFFDrifs
						// v\uFFFDrifier que le lien n'existe pas d\uFFFDj\uFFFD
						// ...
					}
				}
			}
		}
		return resVerif ;
	}

//	public VResultatVerif connexionPossible(FProduitFusion fusion, FElement produit)
//	{
//		VResultatVerif resVerif ;	// R\uFFFDsultat \uFFFD renvoyer
//
//		// v\uFFFDrifier que l'objet source et l'objet destination sont des produits
//		resVerif = new VResultatVerif("Verification connexion");
//		if (!produit.getModele().getId().estProduit() )
//                {System.out.println("veriiiiiiiiiiif");
//			resVerif.ajouterErr ("Connexion avec un composant impossible");
//		}
//		// on a deux produits il ne faut pas que ce soit le m\uFFFDme produit
//		else
//                {
//			if (fusion.isLinkedComponent(produit.getModele().getId().getRef()))
//			{
//				resVerif.ajouterErr ("Connexion entre produits d'un m\uFFFDme composant");
//			}
//			else
//                        {
//				if (produit.getModele().getId().estProduitSortie())
//                                {
//                                  	// modif 2XMI chaouk
//                                        // voir le document 2XMI_Referentiel_exigence_v1.0
//                                        // r\uFFFDalisation de l'ev4
//                                        // annulation ev4 : la ligne suivante est decommentee
//					resVerif.ajouterErr ("Connexion avec un produit en sortie ");
//				}
//			}
//                }
//		return resVerif ;
//	}


}
