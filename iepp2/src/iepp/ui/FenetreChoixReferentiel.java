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
import iepp.application.CCreerReferentiel;
import iepp.application.COuvrirReferentiel;
import iepp.application.areferentiel.Referentiel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * 
 */
public class FenetreChoixReferentiel extends JDialog 
{
	/**
	 * Indiquent si l'on charge ou crée un référentiel 
	 */
	public boolean choisirReferentielActif = true;
	public boolean creerReferentielActif = false;
	
	private JPanel pfenetre = new JPanel();
	private JPanel pfenetreCentre = new JPanel();
	private GridLayout gfenetre = new GridLayout();

	private JRadioButton charger = new JRadioButton();
	private JRadioButton creer = new JRadioButton();
	
	
	private JButton suivButton = new JButton();
	private JButton cancelButton = new JButton();
	private ButtonGroup group = new ButtonGroup();
	
	private FenetrePrincipale fenetrePrincipale = null; 
	
	private Referentiel refChoisi= null;
	 
	/**
	 * Crée la fenetre de choix du référentiel
	 * @param parent
	 */
	public FenetreChoixReferentiel(FenetrePrincipale parent)
	{
		super(parent,Application.getApplication().getTraduction("Choix_referentiel"), true);
		this.fenetrePrincipale = parent;
		try
			{
			  jbInit();
			}
			catch(Exception e)
			{
			  e.printStackTrace();
			}

		// affichage de la fenêtre
		this.setResizable(false);
		this.pack();
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		
		this.setVisible(true);
	}
	
	/**
	 * Initialisation des éléments de la fenêtre
	 * @throws Exception
	 */
	
	private void jbInit() throws Exception
	{	
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pfenetre.setLayout(gfenetre);
		gfenetre.setRows(2);

		// création du bouton suivant
		suivButton.setMnemonic('T');
		suivButton.setText(Application.getApplication().getTraduction("Suivant"));
		suivButton.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			ok(e);
		  }
		});

		//creation bouton annuler
		cancelButton.setMnemonic('N');
		cancelButton.setText(Application.getApplication().getTraduction("Annuler"));
		cancelButton.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			cancel(e);
		  }
		});
		
		// positionnement du bouton suivant et annuler
		pfenetreCentre.add(suivButton, null);
		pfenetreCentre.add(cancelButton, null);
		this.getContentPane().add(pfenetre, BorderLayout.CENTER);
		
		// création des boutons radio charger et créer
		charger.setText(Application.getApplication().getTraduction("Charger_proj"));
		// charger est selectionné par défaut
		charger.setSelected(true);
		creer.setText(Application.getApplication().getTraduction("FCreer_referentiel"));

		group.add(charger);
		group.add(creer);	
		
		// positionnement des boutons radio
		pfenetre.add(charger, null);
		pfenetre.add(creer, null);
		this.getContentPane().add(pfenetreCentre, BorderLayout.SOUTH);

	}
	
	/**
	 * action sur le bouton suivant
	 * @param e
	 */
	private void ok(ActionEvent e)
	{
		String nomRef = null;
		
		// cas du chargement d'un référentiel
		if(this.charger.isSelected())
		{
			choisirReferentielActif = true;
			creerReferentielActif = false;
			
			// proposer un JFileChooser
			COuvrirReferentiel c = new COuvrirReferentiel ();
			if (c.executer())
			{
				Application.getApplication().getFenetrePrincipale().setTitle(
						Application.getApplication().getConfigPropriete("titre")
						+ " " + Application.getApplication().getReferentiel().getNomReferentiel());
				
				this.dispose();
				// fenêtre suivante
				new FenetreChoixProcessus(fenetrePrincipale);
			}
		}
		else if(this.creer.isSelected())
		{
			// cas de la création
			choisirReferentielActif = false;
			creerReferentielActif = true;
			
			// fenêtre demandant le nom du référentiel à créer
			CCreerReferentiel c = new CCreerReferentiel ();
			if (c.executer())
			{
				Application.getApplication().getFenetrePrincipale().setTitle(
						Application.getApplication().getConfigPropriete("titre")
						+ " " + Application.getApplication().getReferentiel().getNomReferentiel());
				
				this.dispose();
			}
		}
	}
	
	/**
	 * Action sur le bouton annuler 
	 * @param e
	 */
	
	private void cancel(ActionEvent e){
		this.dispose();
	}
}
