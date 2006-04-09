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
import iepp.ui.iedition.FenetreEdition;
import iepp.ui.iedition.dessin.rendu.ProduitCellFusion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class FenetreRenommerProduitFusion extends JDialog
{
	private JPanel panelCentre = new JPanel();
	private JPanel panelBas = new JPanel();
	
	private JLabel nomFusion = new JLabel(Application.getApplication().getTraduction("FusionName"));
	private JComboBox comboNom = new JComboBox();
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private ProduitCellFusion fusion;
	
	public FenetreRenommerProduitFusion(JFrame parent,ProduitCellFusion f)
	{
		super(parent,Application.getApplication().getTraduction("FusionRename"),true);
		
		FenetreEdition fenetre = Application.getApplication().getProjet().getFenetreEdition() ;
				
		fusion = f;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		okButton.setText(Application.getApplication().getTraduction("Valider"));
		okButton.setDefaultCapable(true);
		
		okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{ okAction(); }
				}
		);
		
		
		comboNom.setEditable(true);
		comboNom.setMinimumSize(new Dimension(300, 25));
		comboNom.setMaximumSize(new Dimension(500, 25));
		comboNom.setPreferredSize(new Dimension(300, 25));
		
		
		comboNom.addItem(fusion.getNomCompCell());
				
		panelCentre.add(nomFusion);
		panelCentre.add(comboNom);
		
		panelBas.add(okButton);
	
		this.pack();
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		this.setResizable(false);
		this.getContentPane().add(panelCentre, BorderLayout.CENTER);
		this.getContentPane().add(panelBas, BorderLayout.SOUTH);
	}
	
	/**
	 * Action effectuée par le bouton de validation.
	 * Affecte le nom choisi au produit de fusion puis ferme la fenetre.
	 */
	public void okAction()
	{ 
		/*String nom1 = fusion.getProduits(fusion.getNombreProduits()-1).getModele().getId().toString();
		String nom2 = fusion.getNomFusion();
		String nom3 = comboNom.getSelectedItem().toString();
		
		// Composer le nom final
		if (nom1.equals(nom3))
		{
			nom3 = nom3 +"("+nom2+")";
		}
		else
		{
			if (nom2.equals(nom3))
			{
				nom3 = nom3 +"("+nom1+")";
			}
			else
			{
				nom3 = nom3 +"("+nom1+","+nom2+")"; 
			}
		}
		fusion.setNomFusion(nom3);*/
		
		fusion.setNomCompCell(comboNom.getSelectedItem().toString());
		this.dispose();
	}
}

