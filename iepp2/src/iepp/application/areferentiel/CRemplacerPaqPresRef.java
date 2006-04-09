package iepp.application.areferentiel;

import iepp.application.CommandeNonAnnulable;
import iepp.Application;
import util.SimpleFileFilter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import iepp.infrastructure.jsx.ChargeurComposant;

public class CRemplacerPaqPresRef extends CommandeNonAnnulable
{
    public boolean executer()
    {
        return false;
    }

    public boolean executer(long idCompASupprimer)
    {
        String cheminComp; // Chemin du paquetage à charger

        // Demander à l'utilisateur de choisir un paquetage
        JFileChooser chooser=new JFileChooser(Application.getApplication().getConfigPropriete("rep_paquetage"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new SimpleFileFilter(new String[]
                                                   {"pre"}, Application.getApplication().getTraduction("Presentations")));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // récupère le nom de fichier sélectionné par l'utilisateur
        if(chooser.showOpenDialog(Application.getApplication().getFenetrePrincipale())!=JFileChooser.APPROVE_OPTION)
        {
            return false;
        }

        if((new CRetirerPaqPres(idCompASupprimer)).executerSilence())
        {
            Application.getApplication().getProjet().setModified(true);
            return(new CAjouterPaqPresRef()).executer(chooser.getSelectedFile());
        }

        return false;
    }
}
