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
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.ui.iedition.dessin.GraphModelView;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * fenetre pour renommer un scenario
 */

public class FenetreRenommerScenario extends JDialog
{
	private JPanel panelCentre = new JPanel();
	private JPanel panelBas = new JPanel();
	
	private JLabel nomFusion = new JLabel(Application.getApplication().getTraduction("Renommer"));
	private JTextField nomScenario= new JTextField(20);
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private String ancienNom;
	
	
	public FenetreRenommerScenario(JFrame parent, String nom)
	{
		super(parent,Application.getApplication().getTraduction("Renommer"),true);
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		this.setResizable(true);
		this.getContentPane().add(panelCentre, BorderLayout.CENTER);
		this.getContentPane().add(panelBas, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		this.ancienNom=nom;
		cancelButton.setText(Application.getApplication().getTraduction("Annuler"));
		okButton.setText(Application.getApplication().getTraduction("Valider"));
		okButton.setDefaultCapable(true);
		
		okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{ okAction(); }
				}
		);
		
		cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
						{ dispose(); }
				}
		);
		
		nomScenario.setText(ancienNom);
		
		panelCentre.add(nomFusion);
		panelCentre.add(nomScenario);
		
		panelBas.add(okButton);
		panelBas.add(cancelButton);
		this.pack();
		
	}
	
	/**
	 * Action effectuée par le bouton de validation.
	 * Affecte le nom choisi au produit
	 */
	public void okAction()
	{ 
		//VueDPGraphe diagramme = Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe();
		Referentiel ref=Application.getApplication().getReferentiel();
		int count=0;
		// on verifie que le nouveau nom n'est pas deja utillisé par un autre scenario
		// ou que le nom de scenario ne soit pas DefinitionProcessus
		for (int i=0;i<ref.getNoeudScenarios().getChildCount();i++)
		{
			ElementReferentiel e=(ElementReferentiel)ref.getNoeudScenarios().getChildAt(i);
			if ((e.getNomElement().equals(nomScenario.getText()))||(nomScenario.getText().equals("DefinitionProcessus")))
			{
				count++;
			}
		}
		if (count==0){
			
			for (int i=0;i<ref.getNoeudScenarios().getChildCount();i++)
			{
				ElementReferentiel e=(ElementReferentiel)ref.getNoeudScenarios().getChildAt(i);
				
				
				if (e.getNomElement().equals(ancienNom))
				{
					e.setNomElement(nomScenario.getText());
					for (int j=0;j<Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().size();j++)
					{
						if (((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(j)).getNomDiagModel().equals(ancienNom))
						{
							((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(j)).setNomDiagModel(nomScenario.getText());
						}
					}
					ref.majObserveurs(Referentiel.CHANGED);
					Application.getApplication().getProjet().setModified(true);
					this.dispose();
					return;
				}
			}
		}
		
		this.dispose();
	}
}


