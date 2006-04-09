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
package iepp.ui.ireferentiel.popup;

import iepp.Application;
import iepp.application.CAjouterComposantDP;
import iepp.application.aedition.CAjouterComposantGraphe;
import iepp.application.areferentiel.CRetirerComposant;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;

import javax.swing.* ;

import java.awt.event.* ;

import iepp.ui.FenetreProprieteComposant;
import iepp.ui.FenetreVersionComposant;


/**
 * Menu contextuel associé aux composants dans l'arbre du référentiel.
 */
public class PopupComposant extends JPopupMenu implements ActionListener
{
	private long idComp ;	// Identifiant du composant concerné
	// Options du menu
	private JMenuItem ajouterComposantDP ;
        //private JMenuItem actualiserComposantDP;// modif 2xmi Albert
	private JMenuItem ajouterComposantDiagramme ;
	private JMenuItem proprieteComposant ;


	/**
	 * Constructeur.
	 * @param idComp identifiant dans le référentiel du composant associé au menu.
	 */
	public PopupComposant (long idComp)
	{
		// Enregistrer les paramètres
		this.idComp = idComp ;
		// Ajouter les éléments optionnels au menu
		// (uniquement s'il existe un projet en cours)
		if (Application.getApplication().getProjet() != null)
		{
			this.ajouterComposantDP = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Composant_DP2")) ;
			// DEBUT modif 2xmi Albert
			//this.actualiserComposantDP = new JMenuItem (Application.getApplication().getTraduction("Actualiser_Composant_DP2")) ;
			// FIN modif 2xmi Albert
			this.ajouterComposantDiagramme = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Composant")) ;
			this.add (this.ajouterComposantDP) ;
                        //this.add(this.actualiserComposantDP) ; // modif 2xmi Albert
			this.add (this.ajouterComposantDiagramme) ;
			this.ajouterComposantDP.addActionListener (this) ;
                       // this.actualiserComposantDP.addActionListener (this); // modif 2xmi Albert
			this.ajouterComposantDiagramme.addActionListener (this) ;
		}
		
		// Créer les éléments
		this.proprieteComposant = new JMenuItem (Application.getApplication().getTraduction("Proprietes_Composant")) ;
		this.add (this.proprieteComposant) ;
		// Ecouter les événements
		this.proprieteComposant.addActionListener (this) ;
	}


	/** Clics sur les éléments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object source = e.getSource() ;
		// Ajout à la définition de processus
		if (source == this.ajouterComposantDP)
		{
			if ((new CAjouterComposantDP(this.idComp)).executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}
		}
                // DEBUT modif 2xmi Albert
                // Actualiser le composant à la définition de processus
                //else if (source == this.actualiserComposantDP)
                //{
                //        FenetreVersionComposant fVersion = new FenetreVersionComposant(Application.getApplication().getFenetrePrincipale(),idComp);
				//
                //}
                // FIN modif 2xmi Albert
		// Ajout au diagramme
		else if (source == this.ajouterComposantDiagramme)
		{
			Referentiel ref = Application.getApplication().getReferentiel() ;
			// Regarder si le composant est déjà chargé en mémoire
			Object obj = ref.chercherReference (this.idComp) ;
			// S'il n'est pas en mémoire, le charger
			if (obj == null)
			{
				if (!(new CAjouterComposantDP(this.idComp)).executer())
					return ;
				obj = ref.chercherReference (this.idComp) ;
			}
			// Récupérer le composant et l'ajouter au diagramme
			ComposantProcessus comp = (ComposantProcessus) obj ;
			if ((new CAjouterComposantGraphe(comp.getIdComposant())).executer())
			{
				Application.getApplication().getProjet().setModified(true);
			}
		}
		// Supprimer le composant du référentiel
		else if (source == this.proprieteComposant)
		{
			FenetreProprieteComposant fprop = new FenetreProprieteComposant(this.idComp);
		}
	}

}
