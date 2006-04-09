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
import iepp.application.CFermerProjet;
import iepp.application.CNouveauProjet;
import iepp.application.areferentiel.CAjouterComposantRef;
import iepp.application.areferentiel.CAjouterPaqPresRef;
import iepp.ui.FenetreCreerScenario;

import javax.swing.* ;

import java.awt.event.* ;

/**
 * Menu contextuel associé au référentiel dans l'arbre du référentiel.
 */
public class PopupReferentiel extends JPopupMenu implements ActionListener
{
	// Eléments de menu
	//private JMenuItem ajouterComposantRef ;
	private JMenuItem creerDP ;
	private JMenuItem ajouterScen ;
	//private JMenuItem ajouterPresentationRef ;
	
	/**
	 * Constructeur.
	 */
	public PopupReferentiel ()
	{
		// Créer les éléments
		//this.ajouterComposantRef = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Composant_Ref")) ;
		this.creerDP = new JMenuItem (Application.getApplication().getTraduction("Creer_DP")) ;
		this.ajouterScen = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Scenario")) ;
		//this.ajouterPresentationRef = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Presentation_Ref")) ;
		// Ajouter les éléments au menu
		//this.add (this.ajouterComposantRef) ;
		this.add (this.creerDP) ;
		this.add (this.ajouterScen) ;
		//this.add (this.ajouterPresentationRef) ;
		// Ecouter les événements
		//this.ajouterComposantRef.addActionListener (this) ;
		this.creerDP.addActionListener (this) ;
		this.ajouterScen.addActionListener (this) ;               
		//this.ajouterPresentationRef.addActionListener (this) ;
	}


	/** Clics sur les éléments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object source = e.getSource() ;
		/*
		// Ajouter un composant au référentiel
		if (source == this.ajouterComposantRef)
			(new CAjouterComposantRef()).executer() ;
		*/
		// Créer une nouvelle DP
		if (source == this.creerDP)
		{
		    if(! new CNouveauProjet().executer())
		    {
		        new CFermerProjet().executer() ;
		    }
		}
		// Ajout à la définition de processus
		 else if (source == this.ajouterScen)
			{
				FenetreCreerScenario fsc=new FenetreCreerScenario(Application.getApplication().getFenetrePrincipale());
				/*if ((new CAjouterScenarioRef()).executer())
				{
					Application.getApplication().getProjet().setModified(true);
				}*/
			}
		/*
		else if (source == this.ajouterPresentationRef)
			(new CAjouterPaqPresRef()).executer() ;
		*/
	}

}
