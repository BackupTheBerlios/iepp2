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
import iepp.application.CEnregistrerInterface;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.domaine.ComposantProcessus;
import iepp.domaine.IdObjetModele;
import iepp.ui.iedition.VueDPGraphe;
import iepp.ui.iedition.dessin.rendu.IeppCell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class FenetreProprieteComposant extends FenetrePropriete
{
	private JPanel panelCentre = new JPanel();
	private JPanel panelBas = new JPanel();
	
	private JLabel nomFusion = new JLabel(Application.getApplication().getTraduction("Proprietes"));
	private JTextField comboNom = new JTextField();
	private JButton okButton = new JButton();
	private long id ;
	
	public FenetreProprieteComposant(long idComp)
	{
		super();
		this.id = idComp ;
		
		setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		
		okButton.setText(Application.getApplication().getTraduction("Valider"));
		okButton.setDefaultCapable(true);
		
		okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{ okAction(); }
				}
		);
		
		
		comboNom.setEditable(false);
		comboNom.setMinimumSize(new Dimension(300, 25));
		comboNom.setMaximumSize(new Dimension(500, 25));
		comboNom.setPreferredSize(new Dimension(300, 25));
		
		ElementReferentiel obj = Application.getApplication().getReferentiel().chercherElement(this.id,ElementReferentiel.COMPOSANT) ;

		File fcomp = new File(obj.getChemin());

		comboNom.setText(fcomp.getName());
				
		comboNom.addKeyListener(new KeyListener()
		    		{
		 				public void keyTyped(KeyEvent arg0) {
						}
						public void keyPressed(KeyEvent arg0) {
						}
						public void keyReleased(KeyEvent e) {
							if (e.getKeyCode()==KeyEvent.VK_ENTER){
								okAction();
							}
						}
		    		});
		 
		panelCentre.add(nomFusion);
		panelCentre.add(comboNom);
		
		panelBas.add(okButton);
	
		this.pack();
		this.setSize(400, 100);
		this.setPreferredSize(new Dimension(400, 100));
		//Rectangle bounds = parent.getBounds();
		//this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		this.setResizable(false);
		this.getContentPane().add(panelCentre, BorderLayout.CENTER);
		this.getContentPane().add(panelBas, BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
	
	/**
	 * Action effectuée par le bouton de validation.
	 * Affecte le nom choisi au produit
	 */
	public void okAction()
	{ 		
		this.dispose();
	}
}

