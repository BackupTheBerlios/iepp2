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

package iepp.ui;

import iepp.Application;
import iepp.application.CAjouterComposantDP;
import iepp.application.areferentiel.CRemplacerComposantRef;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.ComposantProcessus;
import iepp.infrastructure.jsx.ChargeurComposant;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 */

public class FenetreVersionComposant extends JDialog
{
    JPanel pfenetreSud = new JPanel();
    JPanel pfenetreCentre = new JPanel();
    GridLayout gfenetreCentre = new GridLayout();

    JLabel lnomVer = new JLabel();
    JFormattedTextField nomVer = new JFormattedTextField();

    JButton valider = new JButton();
    JButton annuler = new JButton();

    FenetrePrincipale fparent = null;
    long idComp;

    /**
     * Crée un bd permettant de créer un référentiel
     */

    public FenetreVersionComposant(FenetrePrincipale parent, long idComp)
    {
        super(parent, Application.getApplication().getTraduction("FCreer_version"), true);
        this.fparent = parent;
        this.idComp = idComp;

        jbInit();

        // affichage de la bd
        this.setResizable(false);
        this.pack();
        Rectangle bounds = parent.getBounds();
        this.setLocation(bounds.x + (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
        this.setVisible(true);
    }

    /**
     * Initialise les éléments de la bd
     */

    private void jbInit()
    {
        pfenetreCentre.setLayout(gfenetreCentre);
        gfenetreCentre.setRows(2);

        // champs nom
        lnomVer.setDisplayedMnemonic('V');
        lnomVer.setText(Application.getApplication().getTraduction("Nom_vers"));
        lnomVer.setLabelFor(lnomVer);
        pfenetreCentre.add(lnomVer, null);
        pfenetreCentre.add(nomVer, null);
        this.getContentPane().add(pfenetreCentre, BorderLayout.CENTER);

        // creation du bouton valider
        valider.setMnemonic('I');
        valider.setText(Application.getApplication().getTraduction("Valider"));
        valider.setFocusable(true);
        valider.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ok(e);
            }
        });

        // création du bouton annuler
        annuler.setMnemonic('N');
        annuler.setText(Application.getApplication().getTraduction("Annuler"));
        annuler.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cancel(e);
            }
        });

        // positionnement des deux boutons
        pfenetreSud.add(valider, null);
        pfenetreSud.add(annuler, null);
        this.getContentPane().add(pfenetreSud, BorderLayout.SOUTH);

        // pre-remplisssage du champ version de notre FenetreVersionComposant
        Referentiel ref = Application.getApplication().getReferentiel();
        ElementReferentiel er = ref.chercherElement(idComp, ElementReferentiel.COMPOSANT);
        this.nomVer.setText(er.getVersion());

    }

    /**
     * Action sur le bouton annuler
     * @param e
     */

    private void cancel(ActionEvent e)
    {
        this.dispose();
    }

    /**
     * Action sur le bouton valider
     * @param e
     */

    private void ok(ActionEvent e)
    {
        // vérification des données saisies
        if (this.verifierDonnees())
        {
            // vérifier s'il y a un processus en cours, dans ce cas le fermer
            // si le fichier n'a pas été sauvegardé, c'est pas grave, car on a demandé
            // déjà confirmation avant de sa suppression

            // modif 2XMI Albert
            Referentiel ref = Application.getApplication().getReferentiel();
            ElementReferentiel er = ref.chercherElement(idComp, ElementReferentiel.COMPOSANT);
            String sauvegardeVersion = new String(this.nomVer.getText());
            //modif 2XMI Amandine : date de placement mise à jour
            String datePlacement = ElementReferentiel.FORMATEUR.format(new Date());
            //ancienne version String sauvegardeDateDePlacement = new String(er.getDatePlacement());

            ComposantProcessus comp=(ComposantProcessus)ref.chercherReference(this.idComp);
            // remplacer le composant dans le referentiel
            if (!new CRemplacerComposantRef().executer(idComp,sauvegardeVersion, datePlacement))
            {
            }
            else{
              //ajout du composant a la definition de processus
              ElementReferentiel dernierElt=ref.getDernierElementAjoute();
              if(dernierElt!=null)
              {
                CAjouterComposantDP cacdp=new CAjouterComposantDP(dernierElt.getIdElement());
                cacdp.executer();
                Application.getApplication().getProjet().setModified(true);
              }
              //charger le nouveau composant
              ChargeurComposant cc=new ChargeurComposant(dernierElt.getChemin());
              cc.chargerComposant();
              comp=cc.getComposantCharge();
              Application.getApplication().getProjet().setModified(true);
            }

            //recharger dans le diagramme et refaire les fusions impossibles
            this.dispose();
        }
    }

    /**
     * Vérifie les caractères saisis : le nom du référentiel ne doit pas être vide
     * le nom du référentiel ne doit pas contenir les caractères /\":*<>|?
     * @return
     */

    private boolean verifierDonnees()
    {
        if (this.nomVer.getText().length() > ElementReferentiel.LONGUEUR_VERSION)
        {
            JOptionPane.showMessageDialog(this, Application.getApplication().getTraduction("ERR_Nom_Vers_Incorrect"), Application.getApplication().getTraduction("M_creer_proc_titre"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        else
        {
            {
                char c = this.nomVer.getText().charAt(0);
                if (!estChiffre(c) && c != ' ')
                {
                    JOptionPane.showMessageDialog(this, Application.getApplication().getTraduction("ERR_Nom_Vers_Incorrect"), Application.getApplication().getTraduction("M_creer_proc_titre"), JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                for (int i = 1; i <= (this.nomVer.getText().length()) - 1; i++)
                {
                    if (!estChiffre(this.nomVer.getText().charAt(i)) && (this.nomVer.getText().charAt(i) != '.'))
                    {
                        JOptionPane.showMessageDialog(this, Application.getApplication().getTraduction("ERR_Nom_Vers_Incorrect"), Application.getApplication().getTraduction("M_creer_proc_titre"), JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }

            }
        }
        return true;
    }

    private boolean estChiffre(char c)
    {
        return (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9');
    }
}
