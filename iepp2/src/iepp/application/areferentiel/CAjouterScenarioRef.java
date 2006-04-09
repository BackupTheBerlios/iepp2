package iepp.application.areferentiel;

import iepp.Application;
import iepp.application.CommandeNonAnnulable;

import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.ipsquad.utils.SimpleFileFilter;

public class CAjouterScenarioRef extends CommandeNonAnnulable {

	public boolean executer() {
	    return (executer(ElementReferentiel.DEFAUT_VERSION, ElementReferentiel.FORMATEUR.format( (new Date()))));
	}

	  /**
	   * Ajoute un composant choisi par l'utilisateur au référentiel suivant une version donne et une date fixee.
	   * @see iepp.application.Commande#executer(String _version, String _datePlacement)
	   * @return true si la commande s'est exécutée correctement
	   */
	  public boolean executer(String _version, String _datePlacement) {
	    String cheminComp; // Chemin du composant à charger

	    // Demander à l'utilisateur de choisir un composant
	    JFileChooser chooser = new JFileChooser(Application.getApplication().getConfigPropriete("rep_composant"));
	    chooser.setAcceptAllFileFilterUsed(false);
	    chooser.setFileFilter(new SimpleFileFilter
	                          (new String[] {"pre"}, Application.getApplication().getTraduction("Composants_Publiables")));
	    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

	    // récupère le nom de fichier sélectionné par l'utilisateur
	    if (chooser.showOpenDialog(Application.getApplication().getFenetrePrincipale()) != JFileChooser.APPROVE_OPTION) {
	      return false;
	    }

	    // Recuperer le référentiel courant et lui demander de charger un composant
	    Referentiel ref = Application.getApplication().getReferentiel();
	    long id = ref.ajouterElement(chooser.getSelectedFile().getAbsolutePath(), ElementReferentiel.COMPOSANT, _version, _datePlacement);
	    if (id == -2) {
	      JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),
	                                    Application.getApplication().getTraduction("ERR_Non_Composant"),
	                                    Application.getApplication().getTraduction("ERR"),
	                                    JOptionPane.ERROR_MESSAGE);
	    }
	    if (id == -3) {
	      JOptionPane.showMessageDialog(Application.getApplication().getFenetrePrincipale(),
	                                    Application.getApplication().getTraduction("ERR_Composant_Deja"),
	                                    Application.getApplication().getTraduction("ERR"),
	                                    JOptionPane.ERROR_MESSAGE);
	    }
	    // Retourner vrai si ça s'est bien passé
	    return (id >= 0);
	  }	
}
