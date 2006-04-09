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
package iepp.application.areferentiel;

import javax.swing.*;

import iepp.*;
import iepp.application.*;
import iepp.domaine.*;

/**
 * Retire un paquetage de présentation du référentiel.
 */
public class CRetirerPaqPres extends CommandeNonAnnulable
{
    private long idPres; // Identifiant de la présentation

    /**
     * Construit la commande.
     * @param idComp identifiant du paquetage de présentation dans le référentiel
     */
    public CRetirerPaqPres(long idPres)
    {
        this.idPres = idPres;
    }

    /**
     * Retire la présentation du référentiel.
     * @see iepp.application.Commande#executer()
     * @return true si la commande s'est exécutée correctement
     */
    public boolean executer()
    {
        String txtMsg; // Message à afficher pour demander confirmation
        int typeMsg; // Type de la boîte de dialogue (info, avertissement)

        // Demander confirmation à l'utilisateur
        int choice = JOptionPane.showConfirmDialog(Application.getApplication().getFenetrePrincipale(), Application.getApplication().getTraduction("BD_SUPP_PRES_REF"), Application.getApplication().getTraduction("Confirmation"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (choice == JOptionPane.OK_OPTION)
        {
            executerSilence();
        }
        // Si on arrive ici, la commande a été annulée
        return false;
    }

    /**
     * Retire la présentation du référentiel sans afficher de message à l'utilisateur
     * @see iepp.application.Commande#executer()
     * @return true si la commande s'est exécutée correctement
     */
    public boolean executerSilence()
    {
        // Demander au référentiel de retirer ce composant
            Referentiel ref = Application.getApplication().getReferentiel();
            //on recupere la reference de l'objet que l'on souhaite supprimer
            //ici, c'est un PaquetagePresentation et non un IdObjetModele
            /*modif 2XMI pour la correction du bug D6 - apres suppression d'un paquetage dans le referentiel,
            suppression dans la listeAGenere.
            le rechargement des paquetages de presentation en memoire (au lancement de IEPP) n'ayant pas
            ete pris en compte dans le choix de conception de IEPP v1.1.7,
            la correction du bug D6 n'a pu se faire dans le meme esprit que la suppression d'un composant.
            Le chargement en memoire consiste a garder l'association id - objet en memoire
            afin de pouvoir a partir de l'id (id present dans le noeud d'un arbre) avoir l'objet.
            Pour la suppression d'un paquetage dans la listeAGenerer, nous avons besoin du PaquetagePresentation,
            ou plus précisément son nom.
            Si on souhaite supprimer un paquetage pendant la meme session que l'ajout, le composant est en memoire.
            Sinon, dans une autre session (redemarrage d'IEPP) le composant n'était plus en mémoire.
            Ce chargement n'ayant pas ete pris en compte dans la conception d'IEPP 1.1.7, nous allons directement le chercher
            dans le referentiel, et nous supprimons le paquetage de la listeAGenerer en utilisant son nom
            et non l'objet charge en memoire (reference commune)*/
            PaquetagePresentation pp = (PaquetagePresentation) ref.chercherReference(this.idPres);
            if (pp == null){
              //on va chercher le paquetage dans la liste du referentiel
              pp=ref.chargerPresentation(this.idPres);
            }
            if (pp != null)
            {
                //si cette reference existe, le paquetage a ete ajoute a la liste a generer
                CSupprimerPresentationDP c = new CSupprimerPresentationDP(pp);
                c.executer();
            }

            Application.getApplication().getProjet().setModified(true);
            return (ref.supprimerElement(this.idPres, ElementReferentiel.PRESENTATION));
    }
}
