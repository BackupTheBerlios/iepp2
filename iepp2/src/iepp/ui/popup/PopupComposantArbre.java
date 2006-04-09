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

package iepp.ui.popup;

import iepp.Application;
import iepp.domaine.*;
import iepp.application.CEnregistrerInterface;
import iepp.application.aedition.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import util.IconManager;
import iepp.ui.preferences.FenetrePreference;


/**
 * Classe permettant de créer un popup lorsque l'utilisateur clique droit
 * sur un composant de processus dans l'arbre de la définition
 */
public class PopupComposantArbre extends JPopupMenu implements ActionListener
{
	/**
	 * Items du menu contextuel
	 */
	private JMenuItem ajouterComposantGraphe ;
	private JMenuItem enregistrerInterfaces ;
	private JMenuItem supprimerComposant;
        private JMenuItem renommerComposant;//modif 2xmi youssef
        private JMenuItem proprietesComposant;
	/**
	 * Lien vers le composant de processus
	 */
	private IdObjetModele compProc ;

	/**
	 * Construit un menu contextuel à partir d'un composant de processus
	 * @param comp, id du composant de processus
	 */
	public PopupComposantArbre(IdObjetModele comp)
	{
		// sauvegarder le lien avec le composant
		this.compProc = comp ;


                  // création des items du menu
		this.ajouterComposantGraphe = new JMenuItem(Application.getApplication().getTraduction("Ajouter_Composant"), IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "ajouter.png"));

		this.enregistrerInterfaces = new JMenuItem(Application.getApplication().getTraduction("Enregistrer_Interface"));

		this.supprimerComposant = new JMenuItem(Application.getApplication().getTraduction("Supprimer_Composant"));

                this.renommerComposant = new JMenuItem(Application.getApplication().getTraduction("Renommer_Composant"));//modif 2xmi youssef

		// ajouter les items au menu
		this.add(this.ajouterComposantGraphe);

		this.add(this.supprimerComposant);

		if (comp.estComposantVide())
		{
			//this.add(this.enregistrerInterfaces);
		}
                
		if(comp.estComposantVide()) //(!(this.compProc.estRenomme()))
        {
			this.add(this.renommerComposant);//modif 2xmi youssef
        }
        if (!comp.estComposantVide())
		{
        	this.proprietesComposant=new JMenuItem(Application.getApplication().getTraduction("Proprietes_Composant"));
        	this.add(this.proprietesComposant);
        	this.proprietesComposant.addActionListener(this);
		}

		// pouvoir récupérer les actions sur ce menu
		this.ajouterComposantGraphe.addActionListener(this);
		this.enregistrerInterfaces.addActionListener(this);
		this.supprimerComposant.addActionListener(this);
                this.renommerComposant.addActionListener(this);//modif 2xmi youssef
              }

	/**
	 * Gestionnaire de click sur le menu
	 */
        public void actionPerformed(ActionEvent event)
        {
          if(event.getSource()==this.ajouterComposantGraphe)
          {
            CAjouterComposantGraphe c=new CAjouterComposantGraphe(this.compProc);
            if(c.executer())
            {
              Application.getApplication().getProjet().setModified(true);

            }
          } else
            if(event.getSource()==this.enregistrerInterfaces)
            {

              CEnregistrerInterface c=new CEnregistrerInterface(this.compProc);
              c.executer();
            } else
              if(event.getSource()==this.supprimerComposant)
              {
                CSupprimerComposant c=new CSupprimerComposant(this.compProc);
                if(c.executer())
                {
                  Application.getApplication().getProjet().setModified(true);
                }
              } else
                if(event.getSource()==this.renommerComposant)
                {
                  //modif 2xmi youssef remodifier par Hubert -
                  // passage de la fenetre principal en paramètre
                  // du constructeur pour le centrage de la fenetre de renommage
                  DialogRenommerComposant c=new DialogRenommerComposant(Application.getApplication().getFenetrePrincipale(), this.compProc);
                }
          else
            if (event.getSource()==this.proprietesComposant)
            {
              new FenetrePreference(Application.getApplication().getFenetrePrincipale(), FenetrePreference.TYPE_COMPOSANT, this.compProc);
            }
        }
      }
