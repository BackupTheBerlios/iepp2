package iepp.ui;

import iepp.Application;
import iepp.application.areferentiel.ElementReferentiel;
import iepp.application.areferentiel.Referentiel;
import iepp.ui.iedition.dessin.GraphModelView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FenetreCreerScenario extends JDialog{
	JPanel pfenetreCentre = new JPanel();
	JPanel pfenetreCentrelNom = new JPanel();
	JPanel pfenetreCentrelCom = new JPanel();
	JPanel pfenetreSud = new JPanel();
	JPanel pfenetreCentreSud = new JPanel();
	//JPanel pfenetreCentrelAuteur = new JPanel();
	//JPanel pfenetreCentrelEmail = new JPanel();
	JPanel pfenetreSudButton = new JPanel();
	JPanel pfenetreCentreNom = new JPanel();
	JPanel pfenetreCentreCom = new JPanel();
	//JPanel pfenetreCentreAuteur = new JPanel();
	//JPanel pfenetreCentreEmail = new JPanel();
	
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
	//JLabel lauteur = new JLabel();
	//JLabel lemail = new JLabel();
	JLabel lchamps_oblig = new JLabel();
	
	JTextField nom = new JTextField(Application.getApplication().getTraduction("new_dp_scen"));
	JTextArea com = new JTextArea();
	//JTextField auteur = new JTextField(Application.getApplication().getTraduction("new_author_scen"));
	//JTextField email = new JTextField("iepp@free.fr");
	
	JButton validerButton = new JButton();
	JButton cancelButton = new JButton();
	
	/**
	 * Indique si un scénario a été créé ou pas
	 */
	private boolean estCree = false;
	
	/**
	 * Crée une fenêtre Nouveau Scenario
	 * @param parent
	 */
	
	public FenetreCreerScenario(FenetrePrincipale parent)
	{
		super(parent,Application.getApplication().getTraduction("FCreer_scenario"),true);
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
	 * Crée une fenêtre Nouveau scenario
	 * @param parent
	 * @param fcp ( suite au chargement d'un référentiel ) 
	 */
	
	
	public void jbInit(){

		pfenetreCentre.setLayout(bcentre);
		pfenetreSud.setLayout(bsud);
		pfenetreCentrelNom.setLayout(bcentreNom);
		pfenetreCentrelCom.setLayout(bcentreCom);
		pfenetreCentreSud.setLayout(lfenetreCentreSud);
		lfenetreCentreSud.setRows(2);
//		pfenetreCentrelAuteur.setLayout(bcentreAuteur);
//		pfenetreCentrelEmail.setLayout(bcentreMail);
		pfenetreCentreNom.setLayout(fcentreNom);
		pfenetreCentreCom.setLayout(fcentreCom);
//		pfenetreCentreAuteur.setLayout(fcentreAuteur);
//		pfenetreCentreEmail.setLayout(fcentreEmail);
		
		// propriétés du label nom
		lnom.setDisplayedMnemonic('P');
		lnom.setLabelFor(nom);
		lnom.setText(Application.getApplication().getTraduction("Nom_scen"));
		lnom.setPreferredSize(new Dimension(125,25));
   		
   		// proriétés du label commentaires
		lcom.setDisplayedMnemonic('C');
		lcom.setLabelFor(com);
		lcom.setText("  "+Application.getApplication().getTraduction("Commentaire_scen"));
		lcom.setPreferredSize(new Dimension(125,25));
   
   		// propriétés du label auteur
//		lauteur.setDisplayedMnemonic('A');
//		lauteur.setLabelFor(auteur);
//		lauteur.setText(Application.getApplication().getTraduction("Auteur_scen"));
//		lauteur.setPreferredSize(new Dimension(125,25));
		
		// propriétés du label mail
//		lemail.setDisplayedMnemonic('E');
//		lemail.setLabelFor(email);
//		lemail.setText(Application.getApplication().getTraduction("E_mail_scen"));
//		lemail.setPreferredSize(new Dimension(125,25));
		
		// intégration dans un BorderLayout : ouest label nom, centre champs de saisie
		pfenetreCentrelNom.add(lnom, BorderLayout.WEST);
		pfenetreCentreNom.add(nom,null);
		nom.setPreferredSize(new Dimension(350,25));
		pfenetreCentrelNom.add(pfenetreCentreNom, BorderLayout.CENTER);
		pfenetreCentre.add(pfenetreCentrelNom, BorderLayout.NORTH);
	
		// intégration dans un BorderLayout : ouest label com, centre champs de saisie
		pfenetreCentrelCom.add(lcom, BorderLayout.WEST);
		
		// ajout d'une barre de défilement au champs de saisie com
		defil = new JScrollPane(com);
		defil.setPreferredSize(new Dimension(350,95));
		pfenetreCentreCom.add(defil);
		pfenetreCentreCom.setPreferredSize(new Dimension(350,100));				
		pfenetreCentrelCom.add(pfenetreCentreCom, BorderLayout.CENTER);
		pfenetreCentrelCom.setPreferredSize(new Dimension(375,105));
		pfenetreCentre.add(pfenetreCentrelCom, BorderLayout.CENTER);
		
		// intégration dans un BorderLayout : ouest label auteur, centre champs de saisie	
//		pfenetreCentrelAuteur.add(lauteur, BorderLayout.WEST);
//		auteur.setPreferredSize(new Dimension(350,25));
//		pfenetreCentreAuteur.add(auteur, BorderLayout.CENTER);
//		pfenetreCentrelAuteur.add(pfenetreCentreAuteur, BorderLayout.CENTER);
		
		// intégration dans un BorderLayout : ouest label email, centre champs de saisie
//		pfenetreCentrelEmail.add(lemail, BorderLayout.WEST);
//		email.setPreferredSize(new Dimension(350,25));
//		pfenetreCentreEmail.add(email, BorderLayout.CENTER);
//		pfenetreCentrelEmail.add(pfenetreCentreEmail, BorderLayout.CENTER);
//		pfenetreCentreSud.add(pfenetreCentrelAuteur, null);
//		pfenetreCentreSud.add(pfenetreCentrelEmail, null);
		pfenetreCentre.add(pfenetreCentreSud, BorderLayout.SOUTH);
		this.getContentPane().add(pfenetreCentre, BorderLayout.CENTER);
		
		lchamps_oblig.setText(Application.getApplication().getTraduction("Champs_obligatoires"));
		
		// création du bouton valider
		validerButton.setMnemonic('I');
		validerButton.setText(Application.getApplication().getTraduction("Valider"));
		validerButton.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			valider(e);
		  }
		});
		
		// création du bouton annuler
		cancelButton.setMnemonic('N');
		cancelButton.setText(Application.getApplication().getTraduction("Annuler"));
		cancelButton.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			cancel(e);
		  }
		});
		
		// positionnement des boutons précedents
		pfenetreSudButton.add(validerButton, null);
		pfenetreSudButton.add(cancelButton, null);
		pfenetreSud.add(pfenetreSudButton, BorderLayout.CENTER);
		pfenetreSud.add(lchamps_oblig, BorderLayout.SOUTH);
		this.getContentPane().add(pfenetreSud,  BorderLayout.SOUTH);
	}
	
	/**
	 * Action sur le bouton valider
	 * @param e
	 */
	public void valider(ActionEvent e)
	{
		// si tous les champs obligatoires ont été saisis correctement ...
		if(this.verifierDonnees())
		{
			this.dispose();
			Referentiel ref=Application.getApplication().getReferentiel();
			int count=0;
			// on verifie que le nouveau nom n'est pas deja utillisé par un autre scenario
			for (int i=0;i<ref.getNoeudScenarios().getChildCount();i++)
			{
				ElementReferentiel eltRef=(ElementReferentiel)ref.getNoeudScenarios().getChildAt(i);
				if ((eltRef.getNomElement().equals(nom.getText()))||(nom.getText().equals("DefinitionProcessus")))
				{
					count++;
				}
			}
			if (count==0)
			{
				GraphModelView jg=new GraphModelView(this.nom.getText(),false);
				jg.setCommmentaire_scenario(this.com.getText());
				Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setModel(jg);
				// Modif Julie 05.02.06 met en place le bon outil(dynamique ou statique) pour la gestion des popups
				Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().setOutilSelection();
				Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().add(jg);
				ref.getNoeudScenarios().add(new ElementReferentiel(jg.getNomDiagModel(), Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().getModelesDiagrammes().size()+100, ".", 7));
				
				ref.majObserveurs(Referentiel.ELEMENT_INSERTED);
				Application.getApplication().getProjet().getFenetreEdition().setDynamique();
				Application.getApplication().getProjet().setModified(true);
			}
		}
	}
		
	public void cancel(ActionEvent e)
	{
		this.estCree = false;
		this.dispose();
	}
	
	/**
	 * Vérifie les informations saisies : les champs nom, auteur et mail ne doivent pas être vides,
	 * le nom du processus ne doit pas contenir les caractères /\":*<>|?
	 * @return
	 */
	public boolean verifierDonnees(){
		boolean atTrouve = false;
		boolean pointTrouve = false;
		if(this.nom.getText().equals(""))
		{
			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_nomscenario_scen"),Application.getApplication().getTraduction("M_creer_scen_titre"),JOptionPane.WARNING_MESSAGE);
			return false;
		}
		else
		{
			for(int j = 0; j < this.nom.getText().length(); j++)
			{
				char c = this.nom.getText().charAt(j);
				if(c=='/'||c=='\\'||c=='"'||c==':'||c=='*'||c=='<'||c=='>'||c=='|'||c=='?'||c=='+')
				{
					JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("ERR_Nom_Scen_Incorrect"),Application.getApplication().getTraduction("M_creer_scen_titre"),JOptionPane.WARNING_MESSAGE); 
					return false;
				}
			}
			if (Application.getApplication().getProjet().getFenetreEdition().getVueDPGraphe().ChercherModel(this.nom.getText())!=null)
			{
				JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("ERR_Nom_Scen_Existe"),Application.getApplication().getTraduction("M_creer_scen_titre"),JOptionPane.WARNING_MESSAGE); 
				return false;
			}
		}
//		if(this.auteur.getText().equals("")){
//			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_nomauteur_scen"),Application.getApplication().getTraduction("M_creer_scen_titre"),JOptionPane.WARNING_MESSAGE);
//			return false;
//		}
//		if(this.email.getText().equals("")){
//			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_emailauteur_scen"),Application.getApplication().getTraduction("M_creer_scen_titre"),JOptionPane.WARNING_MESSAGE);
//			return false;
//		}
//		for(int i = 0; i < email.getText().length(); i++){
//			if(this.email.getText().charAt(i)== '@')
//				atTrouve = true;
//			if(this.email.getText().charAt(i)=='.')
//				pointTrouve = true;
//		}
//		if(atTrouve == false || pointTrouve == false){
//			JOptionPane.showMessageDialog(this,Application.getApplication().getTraduction("M_emailinvalide_scen"),Application.getApplication().getTraduction("M_creer_scen_titre"),JOptionPane.WARNING_MESSAGE);
//			return false;
//		}
		return true;
	}
	public boolean isScenarioCree()
	{
		return this.estCree;
	}



}
