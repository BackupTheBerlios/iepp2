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
import iepp.Projet;
import iepp.domaine.DefinitionProcessus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


public class PanneauDPDescription extends PanneauOption
{
	private JTextArea sCommentaireDP;
	private JTextField sContenuDesc;
        //modif 2XMI
        private JTextArea sPiedPageDP;
        //fin modif



	private DefinitionProcessus defProc;
	private JButton browseButton;

	public static final String DP_DESCRIPTION_PANEL_KEY = "Propriete_DP_Desc";

	public PanneauDPDescription(String name)
	{
	    Projet p = Application.getApplication().getProjet();
	    if (p != null)
	    {
	        this.defProc = Application.getApplication().getProjet().getDefProc();
	    }
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

		// fichier contenu
		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label = new JLabel(Application.getApplication().getTraduction("ContenuDP"));
		gridbag.setConstraints(label, c);
		mPanel.add(label);
		c.weightx = 3 ;
		c.gridwidth = GridBagConstraints.RELATIVE;
		sContenuDesc = new JTextField(25);
		mPanel.add(sContenuDesc);
		gridbag.setConstraints(sContenuDesc, c);
		c.weightx = 0 ;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		this.browseButton = new JButton(Application.getApplication().getTraduction("Parcourir"));
		browseButton.addActionListener(man);
		gridbag.setConstraints(browseButton, c);
		mPanel.add(browseButton);

//		 linefeed
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

//		 fichier contenu
		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label2 = new JLabel(Application.getApplication().getTraduction("Commentaire_DP"));
		gridbag.setConstraints(label2, c);
		mPanel.add(label2);

		c.weightx = 4 ;
		c.weighty = 6;
		c.gridwidth = GridBagConstraints.REMAINDER;
		sCommentaireDP = new JTextArea(70,100);
		JScrollPane sp = new JScrollPane(sCommentaireDP);
		mPanel.add(sp);
		gridbag.setConstraints(sp, c);

                //linefeed
                c.weighty = 0;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                makeLabel(" ", gridbag, c);


                //modif 2XMI
                c.weighty = 0;
                c.weightx = 0;
                c.fill = GridBagConstraints.BOTH;
                c.gridwidth = GridBagConstraints.REMAINDER;
                JLabel label3 = new JLabel(Application.getApplication().getTraduction("Pied_Page_DP"));
                gridbag.setConstraints(label3, c);
                mPanel.add(label3);

                c.weightx = 4;
                c.weighty = 6;
                c.gridwidth = GridBagConstraints.REMAINDER;
                sPiedPageDP = new JTextArea(5, 100);
                JScrollPane spBasPage = new JScrollPane(sPiedPageDP);
                mPanel.add(spBasPage);
                gridbag.setConstraints(spBasPage, c);
                //fin modif

		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 2.0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		this.add(new JLabel("    "),BorderLayout.WEST);
		this.add(mPanel,BorderLayout.CENTER);

		// initialiser les champs
		if (this.defProc != null)
		{
		    sCommentaireDP.setText(this.defProc.getCommentaires());
		    sContenuDesc.setText(this.defProc.getFichierContenu());
                    sPiedPageDP.setText(this.defProc.getPiedPage());
		}
	}


	public PanneauOption openPanel(String key)
	{
		this.setName(Application.getApplication().getTraduction(key)) ;
		return this ;
	}


	public void save ()
	{
	    this.defProc.setCommentaires(this.sCommentaireDP.getText());
	    this.defProc.setFicContenu(this.sContenuDesc.getText());
            this.defProc.setPiedPage(this.sPiedPageDP.getText());
	}

	private class ManagerButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//récupérer l'objet source de l'évènement reçu
			Object source = e.getSource();
			// selon cet objet, réagir en conséquence
			if (source == PanneauDPDescription.this.browseButton)
			{
				JFileChooser fileChooser;
				if (PanneauDPDescription.this.sContenuDesc.getText() != "")
					fileChooser = new JFileChooser(PanneauDPDescription.this.sContenuDesc.getText());
				else
					fileChooser = new JFileChooser(Application.getApplication().getConfigPropriete("rep_composant"));

				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int res = fileChooser.showDialog(PanneauDPDescription.this, Application.getApplication().getTraduction("OK"));
				if(res == JFileChooser.APPROVE_OPTION)
					PanneauDPDescription.this.sContenuDesc.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}
}
