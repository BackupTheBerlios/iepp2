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

import iepp.application.averification.VResultatVerif;

/**
 *
 */
public abstract class CommandeVerifiable extends CommandeAnnulable
{
	/**
	 * Méthode executer redéfinie. La méthode appelle les méthodes lancer() et verifier().
	 * La méthode est finale pour ne pas être redéfinie par mégarde.
	 * @see iepp.application.Commande#executer()
	 * @return true si la commande a réussi à s'exécuter
	 */
	public final boolean executer ()
	{
		boolean resultatCommande = this.lancer() ;
		this.verifier() ;
		// Afficher le résultat de la commande avant de quitter
		// Pour cela, il faudrait fournir aux CommandeVerifiable l'objet chargé de l'affichage
		// en direct (ou au moins un objet capable de stocker les données) dans le constructeur
		// La méthode executer devrait être modifiée pour utiliser cet objet 
		return resultatCommande ;
	}
	
	/**
	 * Exécute le traitement réel de la commande.
	 * @return true si la commande a réussi à s'exécuter
	 */
	public abstract boolean lancer() ;
	
	/**
	 * Renvoie un rapport sur la situation des objets concernés par modification entraînée
	 * par la commande. Cette méthode doit appeler les méthodes de vérification corespondant
	 * aux objets modifiés et remettre un rapport.
	 * @return le rapport sur l'exécution de la commande (erreurs ou avertissements par
	 * rapport au processus modélisé).
	 */
	public abstract VResultatVerif verifier() ;
	
	/**
	 * Renvoie un résultat de vérification avant que la commande ne soit exécutée, juste
	 * après sa création. Ceci permet de savoir si la commande va marcher sans l'exécuter.
	 * @return un rapport sur la possibilité d'exécution de la commande
	 */
	public abstract VResultatVerif preverifier() ;
}
