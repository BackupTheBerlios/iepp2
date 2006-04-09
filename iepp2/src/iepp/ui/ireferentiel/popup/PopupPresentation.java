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
import iepp.application.areferentiel.CRetirerPaqPres;
import iepp.application.areferentiel.CActualiserPaqPres;

import iepp.ui.preferences.FenetrePreference;

import javax.swing.*;
import java.awt.event.*;

/**
 * Menu contextuel associé aux paquetages de présentation dans l'arbre du référentiel.
 */
public class PopupPresentation extends JPopupMenu implements ActionListener
{
    private long idPres; // Identifiant du paquetage de présentation concerné
    // Eléments de menu
    private JMenuItem retirerPresentation; // Retrait du référentiel
    //private JMenuItem actualiserPresentation; // Actualiser le paquetage
    //private JMenuItem proprietesPresentation; // Obtenir les proprietes d'un paquetage

    /**
     * Constructeur.
     * @param idPres identifiant dans le référentiel du paquetage de présentation associé au menu.
     */
    public PopupPresentation(long idPres)
    {
        // Enregistrer les paramètres
        this.idPres = idPres;
        // Créer les éléments
        this.retirerPresentation = new JMenuItem(Application.getApplication().getTraduction("Retirer_Pres_Ref"));
        //modif 2XMI Amandine
        //this.actualiserPresentation = new JMenuItem(Application.getApplication().getTraduction("Actualiser_Pres_Ref"));
        //this.proprietesPresentation = new JMenuItem(Application.getApplication().getTraduction("Proprietes_Pres_Ref"));
        // Ajouter les éléments au menu
        this.add(this.retirerPresentation);
        //this.add(this.actualiserPresentation);
        //this.add(this.proprietesPresentation);
        // Ecouter les événements
        this.retirerPresentation.addActionListener(this);
        //this.actualiserPresentation.addActionListener(this);
        //this.proprietesPresentation.addActionListener(this);
    }

    /** Clics sur les éléments du menu.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        // Ajouter un composant au référentiel
        if (source == this.retirerPresentation)
        {
             (new CRetirerPaqPres(this.idPres)).executer();
        }
        /*else if (source == this.actualiserPresentation)
        {
            (new CActualiserPaqPres(this.idPres)).executer() ;
        }*/
        /*else if (source == this.proprietesPresentation)
        {
            new FenetrePreference(Application.getApplication().getFenetrePrincipale(), FenetrePreference.TYPE_PAQ, this.idPres);
        }*/
    }
}
