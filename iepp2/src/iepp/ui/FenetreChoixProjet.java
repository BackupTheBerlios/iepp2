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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import iepp.Application;
import iepp.application.CCreerReferentiel;
import iepp.application.CFermerProjet;
import iepp.application.CNouveauProjet;
import iepp.application.COuvrirDP;
import iepp.application.COuvrirReferentiel;


public class FenetreChoixProjet extends JDialog implements KeyListener {
	
	/**
	 * indiquent si le choix est de créer ou de charger un projet 
	 */ 
	public boolean 	chargerProjetActif = true;
	public boolean creerProjetActif = false;
	
	private JPanel pfenetre = new JPanel();
	private JPanel pfenetreSud = new JPanel();
	private GridLayout gfenetre = new GridLayout();
	private JRadioButton charger = new JRadioButton();
	private JRadioButton creer = new JRadioButton();


	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	
	private ButtonGroup group = new ButtonGroup();

	private FenetrePrincipale fenetrePrincipale = null;
	
	/**
	 * Crée la fenetre de choix d'un processus
	 * @param parent
	 * @param fcr
	 */
	
	public FenetreChoixProjet(FenetrePrincipale parent)
	{
		super(parent,Application.getApplication().getTraduction("Choix_projet"), true);
		this.fenetrePrincipale = parent;	
		try
			{
			  jbInit();
			}
			catch(Exception e)
			{
			  e.printStackTrace();
			}
		// affichage de la fenetre
			
		this.setResizable(false);
		this.setSize(350,150);
		this.pack();
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setVisible(true);		
	}
	
	/**
	 * Initialise les éléments de la fenêtre
	 */
	private void jbInit() throws Exception
	{
		//this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pfenetre.setLayout(gfenetre);
		gfenetre.setRows(2);
	
		// creation bouton suivant
		okButton.setMnemonic('T');
		okButton.setText(Application.getApplication().getTraduction("Suivant"));
		okButton.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
				ok(e);
		  }
		});
		
		// creation bouton annuler
		cancelButton.setMnemonic('Q');
		cancelButton.setText(Application.getApplication().getTraduction("Quitter"));
		cancelButton.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			cancel(e);
		  }
		});
	
		// positionnement des boutons annuler et quitter
		pfenetreSud.add(okButton, null);
		pfenetreSud.add(cancelButton, null);
		this.getContentPane().add(pfenetre, BorderLayout.CENTER);
	
		// création des boutons radio : charger et créer
		charger.setText(Application.getApplication().getTraduction("Charger_proj"));
		creer.setText(Application.getApplication().getTraduction("Creer_proj"));
		
		// charger selectionné par défaut
		charger.setSelected(true);
	
		group.add(charger);
		group.add(creer);	
	
		// postitonnement des boutons radio
		pfenetre.add(charger, null);
		pfenetre.add(creer, null);
		this.getContentPane().add(pfenetreSud, BorderLayout.SOUTH);
		
		// Activation de la navigation au clavier
		charger.addKeyListener(this);
		creer.addKeyListener(this);		
		
		
		//souris normal
		Application.getApplication().getFenetrePrincipale().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
	
	
	/**
	 * Action sur le bouton suivant
	 * @param e
	 */
	private void ok(ActionEvent e)
	{
		// cas : chargement d'un référentiel : affiche un JFileChooser
		if(this.charger.isSelected())
		{		
			this.chargerProjetActif = true;
			this.creerProjetActif = false;
			
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
		else if (this.creer.isSelected())
		{
			// Creation d'un nouveau projet
			CCreerReferentiel c = new CCreerReferentiel();
			if(c.executer()){
				
				Application.getApplication().getFenetrePrincipale().setTitle(
						Application.getApplication().getConfigPropriete("titre")
						+ " " + Application.getApplication().getReferentiel().getNomReferentiel());
				
				this.dispose();
				new FenetreChoixProcessus(fenetrePrincipale);
			}
		}
		if (Application.getApplication().getProjet() != null){
			this.dispose();
		}
	}
	
	/**
	 * Action sur le bouton annuler 
	 * @param e
	 */
	
	private void cancel(ActionEvent e){
		System.exit(0);
	}
	
	/**
	 * Action sur la fenetre par les touches du clavier
	 */		
	public void keyTyped(KeyEvent arg0) {
		
	}

	public void keyPressed(KeyEvent arg0) {
		
	}

	public void keyReleased(KeyEvent event) {
		
		int caract = event.getKeyCode();
		if (event.getSource() == charger) {
			if (caract == KeyEvent.VK_ENTER) {
				okButton.doClick();
			}
			else if (caract == KeyEvent.VK_ESCAPE) {
				cancelButton.doClick();
			}
			else if (caract == KeyEvent.VK_DOWN) {
				creer.setSelected(true);
				creer.grabFocus();
			}
		}
			
		else if (event.getSource() == creer) {
			if (caract == KeyEvent.VK_ENTER) {
				okButton.doClick();
			}
			else if (caract == KeyEvent.VK_ESCAPE) {
				cancelButton.doClick();
			}
			else if (caract == KeyEvent.VK_UP) {
				charger.setSelected(true);
				charger.grabFocus();
			}
		}
	}
}