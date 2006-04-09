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
import iepp.ui.iedition.dessin.GraphModelView;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 */
public class FenetreCreerProcessus extends JDialog implements KeyListener {
	
	JPanel pfenetreCentre = new JPanel();
	JPanel pfenetreCentrelNom = new JPanel();
	JPanel pfenetreCentrelCom = new JPanel();
	JPanel pfenetreSud = new JPanel();
	JPanel pfenetreCentreSud = new JPanel();
	JPanel pfenetreCentrelAuteur = new JPanel();
	JPanel pfenetreCentrelEmail = new JPanel();
	JPanel pfenetreSudButton = new JPanel();
	JPanel pfenetreCentreNom = new JPanel();
	JPanel pfenetreCentreCom = new JPanel();
	JPanel pfenetreCentreAuteur = new JPanel();
	JPanel pfenetreCentreEmail = new JPanel();
	
	GridLayout lfenetreCentreSud = new GridLayout();
	 
	FlowLayout fcentreNom = new FlowLayout();
	FlowLayout fcentreCom = new FlowLayout();
	FlowLayout fcentreAuteur = new FlowLayout();
	FlowLayout fcentreEmail = new FlowLayout();
	
	BorderLayout bcentre = new BorderLayout();
	BorderLayout bcentreCom = new BorderLayout();
	BorderLayout bcentreNom = new BorderLayout();
	BorderLayout bcentreAuteur = new BorderLayout();
	BorderLayout bcentreMail = new BorderLayout();
	BorderLayout bsud = new BorderLayout();
	
	JScrollPane defil;
	
	JLabel lnom = new JLabel();
	JLabel lcom = new JLabel();
	JLabel lauteur = new JLabel();
	JLabel lemail = new JLabel();
	JLabel lchamps_oblig = new JLabel();
	
	JTextField nom = new JTextField(Application.getApplication().getTraduction("new_dp"));
	JTextArea com = new JTextArea();
	JTextField auteur = new JTextField(Application.getApplication().getTraduction("new_author"));
	JTextField email = new JTextField("iepp@free.fr");
	
	JButton validerButton = new JButton();
	JButton cancelButton = new JButton();
	
	FenetreChoixProcessus fenetreChoixProc = null;
	/**
	 * Indique si un processus a ?t? cr?? ou pas
	 */
	private boolean estCree = false;
	
	/**
	 * Cr?e une fen?tre Nouveau Processus
	 * @param parent
	 */
	
	public FenetreCreerProcessus(FenetrePrincipale parent)
	{
		super(parent,Application.getApplication().getTraduction("FCreer_processus"),true);
		try
		{
		  jbInit();
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}	
		
		// affichage
		this.setResizable(false);
		this.pack();
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		this.setVisible(true);	
	}
	
	/**
	 * Cr?e une fen?tre Nouveau processus
	 * @param parent
	 * @param fcp ( suite au chargement d'un r?f?rentiel ) 
	 */
	public FenetreCreerProcessus(FenetrePrincipale parent, FenetreChoixProcessus fcp)
	{
		super(parent,Application.getApplication().getTraduction("FCreer_processus"),true);
		this.fenetreChoixProc = fcp;
		try
		{
		  jbInit();
		}
		catch(Exception e)
		{
		  e.printStackTrace();
		}	
		
		// affichage
		this.setResizable(false);
		this.pack();
		Rectangle bounds = parent.getBounds();
		this.setLocation(bounds.x+ (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
		this.setVisible(true);
	}
	
	public void jbInit(){

		pfenetreCentre.setLayout(bcentre);
		pfenetreSud.setLayout(bsud);
		pfenetreCentrelNom.setLayout(bcentreNom);
		pfenetreCentrelCom.setLayout(bcentreCom);
		pfenetreCentreSud.setLayout(lfenetreCentreSud);
		lfenetreCentreSud.setRows(2);
		pfenetreCentrelAuteur.setLayout(bcentreAuteur);
		pfenetreCentrelEmail.setLayout(bcentreMail);
		pfenetreCentreNom.setLayout(fcentreNom);
		pfenetreCentreCom.setLayout(fcentreCom);
		pfenetreCentreAuteur.setLayout(fcentreAuteur);
		pfenetreCentreEmail.setLayout(fcentreEmail);
		
		// propriétés du label nom
		lnom.setDisplayedMnemonic('P');
		lnom.setLabelFor(nom);
		lnom.setText(Application.getApplication().getTraduction("Nom_proc"));
		lnom.setPreferredSize(new Dimension(125,25));
   		
   		// prori?t?s du label commentaires
		lcom.setDisplayedMnemonic('C');
		lcom.setLabelFor(com);
		lcom.setText("  "+Application.getApplication().getTraduction("Commentaire_proc"));
		lcom.setPreferredSize(new Dimension(125,25));
   
   		// propri?t?s du label auteur
		lauteur.setDisplayedMnemonic('A');
		lauteur.setLabelFor(auteur);
		lauteur.setText(Application.getApplication().getTraduction("Auteur_proc"));
		lauteur.setPreferredSize(new Dimension(125,25));
		
		// propri?t?s du label mail
		lemail.setDisplayedMnemonic('E');
		lemail.setLabelFor(email);
		lemail.setText(Application.getApplication().getTraduction("E_mail_proc"));
		lemail.setPreferredSize(new Dimension(125,25));
		
		// int?gration dans un BorderLayout : ouest label nom, centre champs de saisie
		pfenetreCentrelNom.add(lnom, BorderLayout.WEST);
		pfenetreCentreNom.add(nom,null);
		nom.setPreferredSize(new Dimension(350,25));
		pfenetreCentrelNom.add(pfenetreCentreNom, BorderLayout.CENTER);
		pfenetreCentre.add(pfenetreCentrelNom, BorderLayout.NORTH);
	
		// int?gration dans un BorderLayout : ouest label com, centre champs de saisie
		pfenetreCentrelCom.add(lcom, BorderLayout.WEST);
		
		// ajout d'une barre de d?filement au champs de saisie com
		defil = new JScrollPane(com);
		defil.setPreferredSize(new Dimension(350,95));
		pfenetreCentreCom.add(defil);
		pfenetreCentreCom.setPreferredSize(new Dimension(350,100));				
		pfenetreCentrelCom.add(pfenetreCentreCom, BorderLayout.CENTER);
		pfenetreCentrelCom.setPreferredSize(new Dimension(375,105));
		pfenetreCentre.add(pfenetreCentrelCom, BorderLayout.CENTER);
		
		// int?gration dans un BorderLayout : ouest label auteur, centre champs de saisie	
		pfenetreCentrelAuteur.add(lauteur, BorderLayout.WEST);
		auteur.setPreferredSize(new Dimension(350,25));
		pfenetreCentreAuteur.add(auteur, BorderLayout.CENTER);
		pfenetreCentrelAuteur.add(pfenetreCentreAuteur, BorderLayout.CENTER);
		
		// int?gration dans un BorderLayout : ouest label email, centre champs de saisie
		pfenetreCentrelEmail.add(lemail, BorderLayout.WEST);
		email.setPreferredSize(new Dimension(350,25));
		pfenetreCentreEmail.add(email, BorderLayout.CENTER);
		pfenetreCentrelEmail.add(pfenetreCentreEmail, BorderLayout.CENTER);
		pfenetreCentreSud.add(pfenetreCentrelAuteur, null);
		pfenetreCentreSud.add(pfenetreCentrelEmail, null);
		pfenetreCentre.add(pfenetreCentreSud, BorderLayout.SOUTH);
		this.getContentPane().add(pfenetreCentre, BorderLayout.CENTER);
		
		lchamps_oblig.setText(Application.getApplication().getTraduction("Champs_obligatoires"));
		
		// cr?ation du bouton valider
		validerButton.setMnemonic('I');
		validerButton.setText(Application.getApplication().getTraduction("Valider"));
		validerButton.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			valider(e);
		  }
		});
		
		// cr?ation du bouton annuler
		cancelButton.setMnemonic('N');
		cancelButton.setText(Application.getApplication().getTraduction("Annuler"));
		cancelButton.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			cancel(e);
		  }
		});
		
		// positionnement des boutons pr?cedents
		pfenetreSudButton.add(validerButton, null);
		pfenetreSudButton.add(cancelButton, null);
		pfenetreSud.add(pfenetreSudButton, BorderLayout.CENTER);
		pfenetreSud.add(lchamps_oblig, BorderLayout.SOUTH);
		this.getContentPane().add(pfenetreSud,  BorderLayout.SOUTH);
		
		nom.addKeyListener(this);
	}
	
	/**
	 * Action sur le bouton valider
	 * @param e
	 */
	public void valider(ActionEvent e)
	{
		// si tous les champs obligatoires ont ?t? saisis correctement ...
		if(this.verifierDonnees())
		{
			if(this.fenetreChoixProc != null) 
				this.fenetreChoixProc.dispose();
			this.dispose();
			// cr?ation du nouveau processus avec les informations rentr?es
			Application.getApplication().creerProjet();
			this.estCree = true ;
			// initialisation du projet
			Application.getApplication().getProjet().getDefProc().setAuteur(this.auteur.getText());
			Application.getApplication().getProjet().getDefProc().setCommentaires(this.com.getText());
			Application.getApplication().getProjet().getDefProc().setNomDefProc(this.nom.getText());
			Application.getApplication().getProjet().getDefProc().setEmailAuteur(this.email.getText());
			Application.getApplication().getFenetrePrincipale().majEtat();
			
			// on met le nom du processus au niveau de la racine de l'arbre
			Application.getApplication().getReferentiel().getRacine().setNomElement(this.nom.getText());
			
			// on renomme le noeud du scenario dans le referentiel
			String nomNoeud = Application.getApplication().getTraduction("scenarios");
			Application.getApplication().getReferentiel().getNoeudScenarios().setNomElement(nomNoeud);
			// on renomme le nom du modele statique
			((GraphModelView)Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().elementAt(0)).setNomDiagModel(this.nom.getText());
			
		}
	}
		
	public void cancel(ActionEvent e)
	{
		this.estCree = false;
		this.dispose();
	}
	
	/**
	 * V?rifie les informations saisies : les champs nom, auteur et mail ne doivent pas ?tre vides,
	 * le nom du processus ne doit pas contenir les caract?res /\":*<>|?
	 * @return
	 */
	public boolean verifierDonnees(){
		boolean atTrouve = false;
		boolean pointTrouve = false;
		if(this.nom.getText().equals(""))
		{
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_nomprocessus"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
			return false;
		}
		else
		{
			for(int j = 0; j < this.nom.getText().length(); j++)
			{
				char c = this.nom.getText().charAt(j);
				if(c=='/'||c=='\\'||c=='"'||c==':'||c=='*'||c=='<'||c=='>'||c=='|'||c=='?'||c=='+')
				{
					JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("ERR_Nom_Proc_Incorrect"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE); 
					return false;
				}
			}
		}
		if(this.auteur.getText().equals("")){
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_nomauteur"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if(this.email.getText().equals("")){
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_emailauteur"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
			return false;
		}
		for(int i = 0; i < email.getText().length(); i++){
			if(this.email.getText().charAt(i)== '@')
				atTrouve = true;
			if(this.email.getText().charAt(i)=='.')
				pointTrouve = true;
		}
		if(atTrouve == false || pointTrouve == false){
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_emailinvalide"),Application.getApplication().getTraduction("M_creer_proc_titre"),JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}
	public boolean isProcessusCree()
	{
		return this.estCree;
	}
	
	/**
	 * Action sur la fenetre par les touches du clavier
	 */	

	public void keyTyped(KeyEvent arg0) {}

	public void keyPressed(KeyEvent arg0) {}

	public void keyReleased(KeyEvent event) {
		if (event.getSource() == nom) {
			char caract = event.getKeyChar();
			if (caract == KeyEvent.VK_ENTER) {
				validerButton.doClick ();
			}			
			else if (caract == KeyEvent.VK_ESCAPE) {
				cancelButton.doClick ();
			}
		}
	}
}
