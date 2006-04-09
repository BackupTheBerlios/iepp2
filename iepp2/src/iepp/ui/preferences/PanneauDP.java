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
import iepp.application.CSauvegarderDP;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.domaine.DefinitionProcessus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


public class PanneauDP extends PanneauOption
{
	private JTextField sNomDP;
	private JTextField sAuteurDP;
	private JTextField sEmailDP;

	private JButton browseButton3;
	private JTextField mDefaultPaq;
	private JButton browseButton4;

	private DefinitionProcessus defProc = null;

	public static final String DP_PANEL_KEY = "Propriete_DP";

	public PanneauDP(String name)
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

//		 linefeed
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label = new JLabel(Application.getApplication().getTraduction("Nom_DP"));
		gridbag.setConstraints(label, c);
		mPanel.add(label);
		c.weightx = 3 ;
		c.gridwidth = GridBagConstraints.RELATIVE;
		sNomDP = new JTextField(25);
		
		//sNomDP.setEditable(false);
		
		mPanel.add(sNomDP);
		gridbag.setConstraints(sNomDP, c);
		c.weightx = 0 ;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		//linefeed
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		// auteur
		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label3 = new JLabel(Application.getApplication().getTraduction("Auteur_DP"));
		gridbag.setConstraints(label3, c);
		mPanel.add(label3);
		c.weightx = 3 ;
		c.gridwidth = GridBagConstraints.RELATIVE;
		sAuteurDP = new JTextField(25);
		mPanel.add(sAuteurDP);
		gridbag.setConstraints(sAuteurDP, c);
		c.weightx = 0 ;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		//linefeed
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		// email
		c.weighty = 0;
		c.weightx = 0 ;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		JLabel label4 = new JLabel(Application.getApplication().getTraduction("E_mail_DP"));
		gridbag.setConstraints(label4, c);
		mPanel.add(label4);
		c.weightx = 3 ;
		c.gridwidth = GridBagConstraints.RELATIVE;
		sEmailDP = new JTextField(25);
		mPanel.add(sEmailDP);
		gridbag.setConstraints(sEmailDP, c);
		c.weightx = 0 ;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		// linefeed
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 2.0;
		 c.gridwidth = GridBagConstraints.REMAINDER; //end row
		makeLabel(" ", gridbag, c);

		this.add(new JLabel("    "),BorderLayout.WEST);
		this.add(mPanel,BorderLayout.CENTER);

		// initialiser les champs
		if (this.defProc != null)
		{
		    this.sNomDP.setText(this.defProc.getNomDefProc());
		    this.sAuteurDP.setText(this.defProc.getAuteur());
		    this.sEmailDP.setText(this.defProc.getEmailAuteur());
		}
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
		    this.defProc.setAuteur(this.sAuteurDP.getText());
		    this.defProc.setEmailAuteur(this.sEmailDP.getText());
		    
		    Referentiel ref = Application.getApplication().getReferentiel();
		    
		    String lastName = ref.getRacine().getNomElement();
		    
		    if(!lastName.equals(this.sNomDP.getText())){
		    	
			    String path1 =  ref.getRacine().getChemin()+File.separator+ref.getRacine().getNomElement();
			    if (!path1.endsWith(".iepp") && !path1.endsWith(".IEPP"))
				{
			    	path1 += ".iepp";
				}
			    File f1 = new File(path1);
			    
			    String path2 =  ref.getRacine().getChemin()+File.separator+this.sNomDP.getText();
			    if (!path2.endsWith(".iepp") && !path2.endsWith(".IEPP"))
				{
			    	path2 += ".iepp";
				}
			    File f2 = new File(path2);
			    
			    if(f2.exists())
	            {
		   			 int choice = JOptionPane.showConfirmDialog( Application.getApplication().getFenetrePrincipale(),
		   														 Application.getApplication().getTraduction("msgConfirmEcrasement") ,
		   														 Application.getApplication().getTraduction("msgSauvegarde"),
		   														 JOptionPane.YES_NO_OPTION,
		   														 JOptionPane.QUESTION_MESSAGE);
		
		   			 if(choice!=JOptionPane.YES_OPTION)
		   			 {
		   				 return ;
		   			 }
		   			 File old = f1;
		   			 f1.renameTo(f2);
		   			 old.delete();
	            }else{
	            	f1.renameTo(f2);
	            }
			    
			    			    
			    
			    this.defProc.setNomDefProc(this.sNomDP.getText());
			    ref.getRacine().setNomElement(this.sNomDP.getText());
			    
			    ref.majObserveurs(Referentiel.ELEMENT_INSERTED);
			    
			    ElementReferentiel feuille = null;
			   
			    // On va chercher la d?finition processus correspondante dans l'arbre
			    for(int i=0;i<ref.getNoeudDp().getChildCount();i++){
	            	if(ref.getNoeudDp().getChildAt(i).toString().equals(lastName)){
	            		feuille = ((ElementReferentiel)(ref.getNoeudDp().getChildAt(i)));
	            		feuille.setNomElement(this.sNomDP.getText());
	            		feuille.setChemin(path2);
	            	}
	            }
			    
			    // On sauve la d?finition processus ? l'endroit o? elle est stock?e dans le r?f?rentiel
			    CSauvegarderDP saveDp = new CSauvegarderDP(feuille.getChemin());
			    saveDp.executer();
			    
		    }
		}
	}

	/**
	 * Vérifie les informations saisies : les champs nom, auteur et mail ne doivent pas être vides,
	 * le nom du processus ne doit pas contenir les caractères /\":*<>|?
	 * @return
	 */
	public boolean verifierDonnees(){
		boolean atTrouve = false;
		boolean pointTrouve = false;
		if(! this.sNomDP.getText().equals(""))
		{
			for(int j = 0; j < this.sNomDP.getText().length(); j++)
			{
				char c = this.sNomDP.getText().charAt(j);
				if(c=='/'||c=='\\'||c=='"'||c==':'||c=='*'||c=='<'||c=='>'||c=='|'||c=='?'||c=='+')
				{
					JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("ERR_Nom_Proc_Incorrect"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
					return false;
				}
			}
		}
		if(this.sAuteurDP.getText().equals("")){
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_nomauteur"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(this.sEmailDP.getText().equals("")){
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_emailauteur"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
			return false;
		}
		for(int i = 0; i < this.sEmailDP.getText().length(); i++){
			if(this.sEmailDP.getText().charAt(i)== '@')
				atTrouve = true;
			if(this.sEmailDP.getText().charAt(i)=='.')
				pointTrouve = true;
		}
		if(atTrouve == false || pointTrouve == false){
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_emailinvalide"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
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
			/*
			if (source == PanneauDP.this.browseButton4)
			{
				JFileChooser fileChooser = new JFileChooser(PanneauDP.this.mDefaultComp.getText());
				fileChooser.setDialogTitle(Application.getApplication().getTraduction("titre_choix_rep"));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int res = fileChooser.showDialog(PanneauDP.this, Application.getApplication().getTraduction("OK"));
				if(res == JFileChooser.APPROVE_OPTION)
				    PanneauDP.this.mDefaultPaq.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
			*/
		}
	}
}
