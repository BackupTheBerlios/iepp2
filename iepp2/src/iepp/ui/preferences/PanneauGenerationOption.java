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
import iepp.application.ageneration.GenerationManager;
import iepp.domaine.DefinitionProcessus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;



public class PanneauGenerationOption extends PanneauOption
{
	private JTextField sRepGen;
	private JComboBox mStyles;
	private String mOldStyle;
	private JButton mBackgroundColorButton;
	private JButton browseButton;
	private JRadioButton mAvant;
	private JRadioButton mApres;
        private JRadioButton pDiagFlots;		// 2XMI Albert
        private JRadioButton pDiagActivites;	// 2XMI Albert
        private JRadioButton pComposant;		// 2XMI Albert
        private JRadioButton pDiagComp;			// 2XMI Albert
        private JRadioButton pDiagFlots2;		// 2XMI Albert
	private JCheckBox bInfoBulles;
        private JCheckBox bInfoBullesActivites;	//2XMI Amandine
	private JCheckBox bstatistique;
	private JCheckBox brecap;

	private DefinitionProcessus defProc;

	public static final String GENERATION_OPTION_PANEL_KEY = "Generation_DP_Desc";

	public PanneauGenerationOption(String name)
	{
		Projet p = Application.getApplication().getProjet();
	    if (p != null)
	    {
	        this.defProc = Application.getApplication().getProjet().getDefProc();
	    }
	    Date D = new Date();
	    
	    // Chemin du dossier du site généré
	    
	    DateFormat format = new SimpleDateFormat("_ddMMyyyy_HHmmss"); 
	    String date = format.format(D);
		String cheminGen = Application.getApplication().getReferentiel().getCheminReferentiel()+File.separator+"WebSite"+File.separator+Application.getApplication().getProjet().getDefProc().getNomDefProc()+ date;
		// lien vers l'ancienne feuille de style utilisée
		this.mOldStyle = Application.getApplication().getConfigPropriete("feuille_style");
		boolean trouve = false;

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

//		 fichier contenu
		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label = new JLabel(Application.getApplication().getTraduction("Dossier_Generation"));
		JLabel labelChemin = new JLabel(cheminGen) ;
		gridbag.setConstraints(label, c);
		//mPanel.add(label);
		//mPanel.add(labelChemin);
		c.weightx = 3 ;
		c.gridwidth = 6;
		sRepGen = new JTextField(25);
		//mPanel.add(sRepGen);
		gridbag.setConstraints(sRepGen, c);
		c.weightx = 0 ;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		this.browseButton = new JButton(Application.getApplication().getTraduction("Parcourir"));
		browseButton.addActionListener(man);
		gridbag.setConstraints(browseButton, c);
		//mPanel.add(browseButton);

		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 2.0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
		this.add(new JLabel("    "),BorderLayout.WEST);
		this.add(mPanel,BorderLayout.CENTER);


		// Le repertoire de génération par défaut devient (nomRef+WebSite+nomProcessus_dateDeCreation) 		
		sRepGen.setText(cheminGen+File.separator);


		// Styles
		
		// récupérer les styles disponibles
	    String css_user = Application.getApplication().getReferentiel().getCheminReferentiel()+File.separator+"css"+File.separator;
	    String css_apes = Application.getApplication().getConfigPropriete("styles");
	    String ext_css = Application.getApplication().getConfigPropriete("extensionFeuilleStyle");
	    Application.getApplication().recupererStylesDisponibles(css_apes, css_user, ext_css);
	    
		c.gridwidth = 6 ;//next-to-last in row
		makeLabel(Application.getApplication().getTraduction("style_pages"), gridbag, c);

		Vector styles = Application.getApplication().getStyles();
		mStyles = new JComboBox(styles);
		for (int i = 0; i < mStyles.getModel().getSize() && !trouve;i++)
		{
			if (mOldStyle.equals(((String)mStyles.getModel().getElementAt(i))))
			{
				mStyles.setSelectedIndex(i);
				trouve = true;
			}
		}
		if (!trouve && mStyles.getModel().getSize()> 0) mStyles.setSelectedIndex(0);

		gridbag.setConstraints(mStyles, c);
		mPanel.add(mStyles);
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
		makeLabel(" ", gridbag, c);

		// couleur des élements dans l'arbre
		mBackgroundColorButton = new JButton("");
		c.gridwidth = 6 ;//next-to-last in row
		makeLabel(Application.getApplication().getTraduction("couleur_surlign"), gridbag, c);
		mBackgroundColorButton.addActionListener(man);
		mBackgroundColorButton.setBackground(new Color(Integer.parseInt(Application.getApplication().getConfigPropriete("couleur_arbre"))));

		gridbag.setConstraints(mBackgroundColorButton, c);
		mPanel.add(mBackgroundColorButton);
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
		makeLabel(" ", gridbag, c);


		//Style
		c.weightx = 1.0;
		c.weighty = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row			//	title
		JPanel style = new JPanel();
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleStyle = BorderFactory.createTitledBorder( loweredetched,Application.getApplication().getTraduction("PlaceContenu"));

		style.setBorder(titleStyle);
		gridbag.setConstraints(style, c);
		mPanel.add(style);
		ButtonGroup groupe_contenu_html = new ButtonGroup();
		this.mAvant = new JRadioButton(Application.getApplication().getTraduction("ContenuAvant"),
                   Application.getApplication().getConfigPropriete("place_contenu").equals(GenerationManager.AVANT_CONTENU));
		this.mApres = new JRadioButton(Application.getApplication().getTraduction("ContenuApres"),
                   Application.getApplication().getConfigPropriete("place_contenu").equals(GenerationManager.APRES_CONTENU));
		groupe_contenu_html.add(this.mAvant);
		groupe_contenu_html.add(this.mApres);

		style.setLayout(new GridLayout(2,1));
		style.add(mAvant);
		style.add(mApres);

                // 2XMI Albert DEBUT
                JPanel pageDefaut = new JPanel();
                Border loweredetched2 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
                TitledBorder titlePageDefaut = BorderFactory.createTitledBorder( loweredetched,Application.getApplication().getTraduction("PageOuverteDT"));

                pageDefaut.setBorder(titlePageDefaut);
                gridbag.setConstraints(pageDefaut, c);
                mPanel.add(pageDefaut);
                ButtonGroup groupe_page_ouverte = new ButtonGroup();

                this.pDiagFlots = new JRadioButton(Application.getApplication().getTraduction("OuvertureDiagFlots"),
                                Application.getApplication().getConfigPropriete("place_page").equals(GenerationManager.DIAGFLOTS_PAGE));
                this.pDiagActivites = new JRadioButton(Application.getApplication().getTraduction("OuvertureDiagActivites"),
                        Application.getApplication().getConfigPropriete("place_page").equals(GenerationManager.DIAGACTIVITES_PAGE));
                this.pComposant = new JRadioButton(Application.getApplication().getTraduction("OuvertureDiagComposant"),
                        Application.getApplication().getConfigPropriete("place_page").equals(GenerationManager.COMPOSANT_PAGE));
                groupe_page_ouverte.add(this.pDiagFlots);
                groupe_page_ouverte.add(this.pDiagActivites);
                groupe_page_ouverte.add(this.pComposant);

                pageDefaut.setLayout(new GridLayout(3,1));
                pageDefaut.add(pDiagFlots);
                pageDefaut.add(pDiagActivites);
                pageDefaut.add(pComposant);
                // 2XMI Albert FIN

                // 2XMI Albert DEBUT
                JPanel lienAssemblage = new JPanel();
                Border loweredetched3 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
                TitledBorder titleLienAssemblage = BorderFactory.createTitledBorder( loweredetched,Application.getApplication().getTraduction("PageOuverteDA"));

                lienAssemblage.setBorder(titleLienAssemblage);
                gridbag.setConstraints(lienAssemblage, c);
                mPanel.add(lienAssemblage);
                ButtonGroup groupe_lien_assemblage = new ButtonGroup();

                this.pDiagFlots2 = new JRadioButton(Application.getApplication().getTraduction("OuvertureDiagFlots2"),
                                Application.getApplication().getConfigPropriete("place_assemblage").equals(GenerationManager.DIAGFLOTS2_PAGE));
                this.pDiagComp = new JRadioButton(Application.getApplication().getTraduction("OuvertureDefTravail"),
                        Application.getApplication().getConfigPropriete("place_assemblage").equals(GenerationManager.DIAGCOMP_PAGE));
                groupe_lien_assemblage.add(this.pDiagFlots2);
                groupe_lien_assemblage.add(this.pDiagComp);

                lienAssemblage.setLayout(new GridLayout(2,1));
                lienAssemblage.add(pDiagFlots2);
                lienAssemblage.add(pDiagComp);
                // 2XMI Albert FIN


		//	linefeed
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		// Info bulles sur les diagrammes
		this.bInfoBulles = new JCheckBox(Application.getApplication().getTraduction("Info-bulle"));
		this.bInfoBulles.setSelected(Application.getApplication().getConfigPropriete("info_bulle").equals(GenerationManager.PRESENT));
		c.gridwidth = 6 ;//next-to-last in row
		gridbag.setConstraints(bInfoBulles, c);
		mPanel.add(bInfoBulles);

		//	linefeed
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

                //modif 2XMI Amandine
                // Info bulles sur les diagrammes
                this.bInfoBullesActivites = new JCheckBox(Application.getApplication().getTraduction("Info-bulle_activite"));
                this.bInfoBullesActivites.setSelected(Application.getApplication().getConfigPropriete("info_bulle_activite").equals(GenerationManager.PRESENT));
                c.gridwidth = 6 ;//next-to-last in row
                gridbag.setConstraints(bInfoBullesActivites, c);
                mPanel.add(bInfoBullesActivites);
                //fin modif 2XMI Amandine

                //	linefeed
                c.weighty = 0;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                makeLabel(" ", gridbag, c);

		// Statistiques sur la génération
		this.bstatistique = new JCheckBox(Application.getApplication().getTraduction("Stats"));
		this.bstatistique.setSelected(Application.getApplication().getConfigPropriete("statistiques").equals(GenerationManager.PRESENT));
		c.gridwidth = 6 ;//next-to-last in row
		gridbag.setConstraints(bstatistique, c);
		mPanel.add(bstatistique);

		//linefeed
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		// Récapitulatif des roles, produits, et activités
		this.brecap = new JCheckBox(Application.getApplication().getTraduction("Recap"));
		this.brecap.setSelected(Application.getApplication().getConfigPropriete("recapitulatif").equals(GenerationManager.PRESENT));
		c.gridwidth = 6 ;//next-to-last in row

		gridbag.setConstraints(brecap, c);
		mPanel.add(brecap);
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);
		makeLabel(" ", gridbag, c);


		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 2.0;
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
		if (this.verifierDonnees())
		{
			this.defProc.setRepertoireGeneration(this.sRepGen.getText());
			if ( mStyles.getSelectedItem().toString() != null)
			{
				Application.getApplication().setConfigPropriete("feuille_style", mStyles.getSelectedItem().toString());
			}
			else
			{
				Application.getApplication().setConfigPropriete("feuille_style","");
			}
			// récupère la couleur choisie dans la bd
			Application.getApplication().setConfigPropriete("couleur_arbre", "" + mBackgroundColorButton.getBackground().getRGB());

			if (this.mAvant.isSelected())
			    Application.getApplication().setConfigPropriete("place_contenu", GenerationManager.AVANT_CONTENU);
			else if (this.mApres.isSelected())
			    Application.getApplication().setConfigPropriete("place_contenu", GenerationManager.APRES_CONTENU);

                          if (this.pDiagFlots.isSelected())                                                                     // 2XMI Albert
                            Application.getApplication().setConfigPropriete("place_page", GenerationManager.DIAGFLOTS_PAGE);    // 2XMI Albert
                          else if (this.pDiagActivites.isSelected())                                                            // 2XMI Albert
                            Application.getApplication().setConfigPropriete("place_page", GenerationManager.DIAGACTIVITES_PAGE);// 2XMI Albert
                          else if (this.pComposant.isSelected())                                                                // 2XMI Albert
                            Application.getApplication().setConfigPropriete("place_page", GenerationManager.COMPOSANT_PAGE);    // 2XMI Albert

                          if (this.pDiagFlots2.isSelected())                                                                         // 2XMI Albert
                          Application.getApplication().setConfigPropriete("place_assemblage", GenerationManager.DIAGFLOTS2_PAGE);    // 2XMI Albert
                        else if (this.pDiagComp.isSelected())                                                                      // 2XMI Albert
                          Application.getApplication().setConfigPropriete("place_assemblage", GenerationManager.DIAGCOMP_PAGE);    // 2XMI Albert


			if (this.bInfoBulles.isSelected())
				Application.getApplication().setConfigPropriete("info_bulle", GenerationManager.PRESENT);
			else
				Application.getApplication().setConfigPropriete("info_bulle", GenerationManager.NON_PRESENT);

                        //modif 2XMI Amandine
                        if (this.bInfoBullesActivites.isSelected())
                          Application.getApplication().setConfigPropriete("info_bulle_activite", GenerationManager.PRESENT);
                        else
                          Application.getApplication().setConfigPropriete("info_bulle_activite", GenerationManager.NON_PRESENT);
                        //fin modif 2XMI Amandine

			if (this.bstatistique.isSelected())
				Application.getApplication().setConfigPropriete("statistiques", GenerationManager.PRESENT);
			else
				Application.getApplication().setConfigPropriete("statistiques", GenerationManager.NON_PRESENT);

			if (this.brecap.isSelected())
				Application.getApplication().setConfigPropriete("recapitulatif", GenerationManager.PRESENT);
			else
				Application.getApplication().setConfigPropriete("recapitulatif", GenerationManager.NON_PRESENT);

		}
	}

	/**
	 * Vérifie les informations saisies
	 * @return
	 */
	public boolean verifierDonnees()
	{
		if(this.sRepGen.getText().equals("")){
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_repGen"),
										Application.getApplication().getTraduction("M_creer_proc_titre"),
										JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	private class ManagerButton implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//récupérer l'objet source de l'évènement reçu
			Object source = e.getSource();
			// selon cet objet, réagir en conséquence
			if (source == PanneauGenerationOption.this.mBackgroundColorButton)
			{
				Color couleur_choisie = JColorChooser.showDialog(PanneauGenerationOption.this,Application.getApplication().getTraduction("choix_couleur"), new Color(Integer.parseInt(Application.getApplication().getConfigPropriete("couleur_arbre"))));
				// si l'utilisateur choisit annuler, la bd renvoie null, donc on vérifie le retour
				if (couleur_choisie != null)
				{
					PanneauGenerationOption.this.mBackgroundColorButton.setBackground(couleur_choisie);
				}
			}
			else if (source == PanneauGenerationOption.this.browseButton)
			{
				JFileChooser fileChooser = new JFileChooser(PanneauGenerationOption.this.sRepGen.getText());
				fileChooser.setDialogTitle(Application.getApplication().getTraduction("titre_choix_rep"));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int res = fileChooser.showDialog(PanneauGenerationOption.this, Application.getApplication().getTraduction("OK"));
				if(res == JFileChooser.APPROVE_OPTION)
					PanneauGenerationOption.this.sRepGen.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}
}
