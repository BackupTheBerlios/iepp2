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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import util.SimpleFileFilter;


public class PanneauReferentiel extends PanneauOption 
{
	private JTextField mDefaultPath;
	private JButton browseButton;
	private JTextField mDefaultRef;
	private JButton browseButton2;
	private JTextField mDefaultComp;
	private JButton browseButton3;
	private JTextField mDefaultPaq;
	private JButton browseButton4;
	
	public static final String REPOSITORY_PANEL_KEY = "RepositoryTitle";
	
	private SimpleFileFilter filter = new SimpleFileFilter("ref", "Referentiel") ;
	
	public PanneauReferentiel(String name)
	{

		this.mTitleLabel = new JLabel (name) ;
		this.setLayout(new BorderLayout());
		mPanel = new JPanel() ;
		GridBagLayout gridbag = new GridBagLayout();
		mPanel.setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		ManagerButton man = new ManagerButton();

		// Title
		c.weightx = 1.0;
		c.weighty = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row			//	title
		this.mTitleLabel = new JLabel (name);
		TitledBorder titleBor = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK));
		titleBor.setTitleJustification(TitledBorder.CENTER);
		mTitleLabel.setBorder(titleBor);
		gridbag.setConstraints(mTitleLabel, c);
		mPanel.add(mTitleLabel);

		// linefeed
		c.weighty = 0;      		
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
		
		// rep référentiel
		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label = new JLabel(Application.getApplication().getTraduction("CheminReferentiel"));
		gridbag.setConstraints(label, c);
		mPanel.add(label);
		c.weightx = 3 ;
		c.gridwidth = GridBagConstraints.RELATIVE;
		mDefaultPath = new JTextField(25);
		mDefaultPath.setText(Application.getApplication().getConfigPropriete("chemin_referentiel"));
		mPanel.add(mDefaultPath);
		gridbag.setConstraints(mDefaultPath, c);
		c.weightx = 0 ;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		this.browseButton = new JButton(Application.getApplication().getTraduction("Parcourir"));
		browseButton.addActionListener(man);
		gridbag.setConstraints(browseButton, c);
		mPanel.add(browseButton);		
		
		//linefeed
		c.weighty = 0;      		
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
		
		
		// referentiel par défaut
		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label2 = new JLabel(Application.getApplication().getTraduction("ReferentielDefaut"));
		gridbag.setConstraints(label2, c);
		mPanel.add(label2);
		c.weightx = 3 ;
		c.gridwidth = GridBagConstraints.RELATIVE;
		mDefaultRef = new JTextField(25);
		mDefaultRef.setText(Application.getApplication().getConfigPropriete("referentiel_demarrage"));
		mPanel.add(mDefaultRef);
		gridbag.setConstraints(mDefaultRef, c);
		c.weightx = 0 ;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		this.browseButton2 = new JButton(Application.getApplication().getTraduction("Parcourir"));
		browseButton2.addActionListener(man);
		gridbag.setConstraints(browseButton2, c);
		mPanel.add(browseButton2);		
		
		//linefeed
		c.weighty = 0;      		
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
		
		
		// rep des composant à importer
		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label3 = new JLabel(Application.getApplication().getTraduction("RepComposantDefaut"));
		gridbag.setConstraints(label3, c);
		mPanel.add(label3);
		c.weightx = 3 ;
		c.gridwidth = GridBagConstraints.RELATIVE;
		mDefaultComp = new JTextField(25);
		mDefaultComp.setText(Application.getApplication().getConfigPropriete("rep_composant"));
		mPanel.add(mDefaultComp);
		gridbag.setConstraints(mDefaultComp, c);
		c.weightx = 0 ;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		this.browseButton3 = new JButton(Application.getApplication().getTraduction("Parcourir"));
		browseButton3.addActionListener(man);
		gridbag.setConstraints(browseButton3, c);
		mPanel.add(browseButton3);		
		
		//linefeed
		c.weighty = 0;      		
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
		
		//	rep des paquetage de présentation à importer
		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label4 = new JLabel(Application.getApplication().getTraduction("RepPaquetageDefaut"));
		gridbag.setConstraints(label4, c);
		mPanel.add(label4);
		c.weightx = 3 ;
		c.gridwidth = GridBagConstraints.RELATIVE;
		mDefaultPaq = new JTextField(25);
		mDefaultPaq.setText(Application.getApplication().getConfigPropriete("rep_paquetage"));
		mPanel.add(mDefaultPaq);
		gridbag.setConstraints(mDefaultPaq, c);
		c.weightx = 0 ;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		this.browseButton4 = new JButton(Application.getApplication().getTraduction("Parcourir"));
		browseButton4.addActionListener(man);
		gridbag.setConstraints(browseButton4, c);
		mPanel.add(browseButton4);		
		
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
		Application.getApplication().setConfigPropriete("chemin_referentiel", mDefaultPath.getText());
		Application.getApplication().setConfigPropriete("referentiel_demarrage", mDefaultRef.getText());
		Application.getApplication().setConfigPropriete("rep_composant", mDefaultComp.getText());
		Application.getApplication().setConfigPropriete("rep_paquetage", mDefaultPaq.getText());
	}
	
	private class ManagerButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//récupérer l'objet source de l'évènement reçu
			Object source = e.getSource();
			// selon cet objet, réagir en conséquence
			if (source == PanneauReferentiel.this.browseButton)
			{
				JFileChooser fileChooser = new JFileChooser(PanneauReferentiel.this.mDefaultPath.getText());
				fileChooser.setDialogTitle(Application.getApplication().getTraduction("titre_choix_rep"));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int res = fileChooser.showDialog(PanneauReferentiel.this, Application.getApplication().getTraduction("OK"));
				if(res == JFileChooser.APPROVE_OPTION)
					PanneauReferentiel.this.mDefaultPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
			else if (source == PanneauReferentiel.this.browseButton2)
			{
				JFileChooser fileChooser;
				if (PanneauReferentiel.this.mDefaultPath.getText() != "")
					fileChooser = new JFileChooser(PanneauReferentiel.this.mDefaultPath.getText());
				else
					fileChooser = new JFileChooser(Application.getApplication().getConfigPropriete("chemin_referentiel"));
				
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setFileFilter(filter);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogTitle(Application.getApplication().getTraduction("titre_choix_ref"));
				int res = fileChooser.showDialog(PanneauReferentiel.this, Application.getApplication().getTraduction("OK"));
				if(res == JFileChooser.APPROVE_OPTION)
					PanneauReferentiel.this.mDefaultRef.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
			else if (source == PanneauReferentiel.this.browseButton3)
			{
				JFileChooser fileChooser = new JFileChooser(PanneauReferentiel.this.mDefaultComp.getText());
				fileChooser.setDialogTitle(Application.getApplication().getTraduction("titre_choix_rep"));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int res = fileChooser.showDialog(PanneauReferentiel.this, Application.getApplication().getTraduction("OK"));
				if(res == JFileChooser.APPROVE_OPTION)
					PanneauReferentiel.this.mDefaultComp.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
			else if (source == PanneauReferentiel.this.browseButton4)
			{
				JFileChooser fileChooser = new JFileChooser(PanneauReferentiel.this.mDefaultComp.getText());
				fileChooser.setDialogTitle(Application.getApplication().getTraduction("titre_choix_rep"));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int res = fileChooser.showDialog(PanneauReferentiel.this, Application.getApplication().getTraduction("OK"));
				if(res == JFileChooser.APPROVE_OPTION)
					PanneauReferentiel.this.mDefaultPaq.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}
}
