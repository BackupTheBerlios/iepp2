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
package iepp.ui.preferences;

import iepp.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class PanneauDiagramme extends PanneauOption 
{
	private JButton mBackgroundColorButton;

	public static final String DIAGRAM_PANEL_KEY = "DiagramTitle";
	
	public PanneauDiagramme(String name)
	{
		
		this.mTitleLabel = new JLabel (name) ;
		this.setLayout(new BorderLayout());
		mPanel = new JPanel() ;
		mBackgroundColorButton = new JButton("");
		GridBagLayout gridbag = new GridBagLayout();
		mPanel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		ManagerButton man = new ManagerButton();

		// Title
		c.weightx = 1.0;
		c.weighty = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		this.mTitleLabel = new JLabel (name);
		TitledBorder titleBor = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK));
		titleBor.setTitleJustification(TitledBorder.CENTER);
		mTitleLabel.setBorder(titleBor);
		gridbag.setConstraints(mTitleLabel, c);
		mPanel.add(mTitleLabel);

		//linefeed
		makeLabel(" ", gridbag, c);
		
		// couleur du diagramme
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 3 ;//next-to-last in row
		makeLabel(Application.getApplication().getTraduction("Choix_couleur_fond"), gridbag, c);
		mBackgroundColorButton.addActionListener(man);
		mBackgroundColorButton.setBackground(new Color(Integer.parseInt(Application.getApplication().getConfigPropriete("couleur_fond_diagrmme"))));
	
		gridbag.setConstraints(mBackgroundColorButton, c);
		mPanel.add(mBackgroundColorButton);
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
		makeLabel(" ", gridbag, c);
		
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 2.0; 
		// linefeed     		
		 c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
       
		this.add(new JLabel("    "),BorderLayout.WEST);
		this.add(mPanel,BorderLayout.CENTER);
	}
	
	
	public PanneauOption openPanel(String key)
	{
		this.setName(Application.getApplication().getTraduction(key)) ;
		return this ;
	}

	
	public void save ()
	{
		// récupère la couleur choisie dans la bd
		Application.getApplication().setConfigPropriete("couleur_fond_diagrmme", "" + mBackgroundColorButton.getBackground().getRGB());
	}
	
	private class ManagerButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//récupérer l'objet source de l'évènement reçu
			Object source = e.getSource();
			// selon cet objet, réagir en conséquence
			if (source == PanneauDiagramme.this.mBackgroundColorButton)
			{
				Color couleur_choisie = JColorChooser.showDialog(PanneauDiagramme.this,Application.getApplication().getTraduction("choix_couleur"), new Color(Integer.parseInt(Application.getApplication().getConfigPropriete("couleur_fond_diagrmme"))));
				// si l'utilisateur choisit annuler, la bd renvoie null, donc on vérifie le retour
				if (couleur_choisie != null)
				{
				    PanneauDiagramme.this.mBackgroundColorButton.setBackground(couleur_choisie);
				}
			}
		}
	}
}
