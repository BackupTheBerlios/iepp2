package iepp.ui.ireferentiel.popup;

import iepp.Application;
import iepp.ui.FenetreCreerScenario;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/*
 * classe permettant d'afficher le popup "ajouter un scénario" 
 * quand on clique sur le repertoire scenario
 */

public class PopupScenario extends JPopupMenu implements ActionListener{

	private JMenuItem ajouterScen ;
	
	public PopupScenario ()
	{
		// (uniquement s'il existe un projet en cours)
		if (Application.getApplication().getProjet() != null)
		{
			this.ajouterScen = new JMenuItem (Application.getApplication().getTraduction("Ajouter_Scenario")) ;
			this.add (this.ajouterScen) ;
			this.ajouterScen.addActionListener (this) ;               
		}
	}


	/** Clics sur les éléments du menu.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object source = e.getSource() ;
		// Ajout dans le répertoire scenario
		if (source == this.ajouterScen)
		{
			FenetreCreerScenario fsc=new FenetreCreerScenario(Application.getApplication().getFenetrePrincipale());
		}
	}
}
