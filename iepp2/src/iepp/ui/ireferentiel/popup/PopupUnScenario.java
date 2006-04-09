package iepp.ui.ireferentiel.popup;

import iepp.Application;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.ui.FenetreProprieteScenario;
import iepp.ui.FenetreRenommerScenario;
import iepp.ui.iedition.dessin.GraphModelView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/*
 *  affiche le popup "renommer le scenario" au niveau d'un sc?nario de la branche sc?nario
 */

public class PopupUnScenario extends JPopupMenu implements ActionListener {

	private JMenuItem renommer ;
	private JMenuItem supprimer ;
	private JMenuItem propriete	;
	private String nomScenario;
	
	public PopupUnScenario (String nom)
	{
		// (uniquement s'il existe un projet en cours)
		if (Application.getApplication().getProjet() != null)
		{
			this.nomScenario=nom;
			this.renommer = new JMenuItem (Application.getApplication().getTraduction("Renommer_Scenario")) ;
			this.add (this.renommer) ;

			this.renommer.addActionListener (this) ;               
			this.supprimer = new JMenuItem (Application.getApplication().getTraduction("Supprimer_Scenario")) ;
			this.add (this.supprimer) ;
			this.supprimer.addActionListener (this) ;  
			
			this.addSeparator();
			
			this.propriete = new JMenuItem (Application.getApplication().getTraduction("Propriete_Scenario")) ;
			this.add (this.propriete) ;
			this.propriete.addActionListener (this) ;  
		}
	}


	/** Clics sur les ?l?ments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object source = e.getSource() ;
		// si on renomme ouverte d'une fenetre
		if (source == this.renommer)
		{
			
			FenetreRenommerScenario fsc=new FenetreRenommerScenario(Application.getApplication().getFenetrePrincipale(),this.nomScenario);
			fsc.show();
		}
		//sinon on supprime le scnario du processus 
		else if (source == this.supprimer)
		{
			// on retire le modele du vector DiagrammeModele
			Vector modele=Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes();
			for (int i=1;i<modele.size();i++)
			{
				if (((GraphModelView)modele.elementAt(i)).getNomDiagModel().equals(this.nomScenario))
				{
					modele.removeElementAt(i);
				}
			}
			// on retire le scenario de l'arbre du referentiel 
			Referentiel ref=Application.getApplication().getReferentiel();
			for(int i=0;i<ref.getNoeudScenarios().getChildCount();i++)
			{
				ElementReferentiel courant=((ElementReferentiel)ref.getNoeudScenarios().getChildAt(i));
				if (courant.getNomElement().equals(this.nomScenario))
				{
					courant.removeFromParent();
					ref.majObserveurs(Referentiel.CHANGED);
				}
			}
			Application.getApplication().getProjet().setModified(true);
		}
		else if (source== this.propriete)
		{
			for (int i=0;i<Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().size();i++)
			{
				GraphModelView courant=(GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(i);
				if (courant.getNomDiagModel().equals(this.nomScenario))
				{
					FenetreProprieteScenario fsc=new FenetreProprieteScenario(Application.getApplication().getFenetrePrincipale(),courant);
					fsc.show();
				}				
			}
			//Application.getApplication().getProjet().setModified(true);
			
		}
	}

}
