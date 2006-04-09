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
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FenetreProprieteScenario extends JDialog
{
	JPanel pfenetreCentre = new JPanel();
	JPanel pfenetreCentrelCom = new JPanel();
	JPanel pfenetreSud = new JPanel();
	JPanel pfenetreCentreSud = new JPanel();
	JPanel pfenetreSudButton = new JPanel();
	JPanel pfenetreCentreCom = new JPanel();
		
	GridLayout lfenetreCentreSud = new GridLayout();
	 
	FlowLayout fcentreCom = new FlowLayout();
		
	BorderLayout bcentre = new BorderLayout();
	BorderLayout bcentreCom = new BorderLayout();
	BorderLayout bsud = new BorderLayout();
		
	JScrollPane defil;
	
	private JLabel lcom = new JLabel();
	
	private JLabel nomFusion = new JLabel(Application.getApplication().getTraduction("Propriete_Scenario"));
	private JTextArea commentaireScenario= new JTextArea();
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private String ancienCom;
	private GraphModelView dynamique;	
	
	public FenetreProprieteScenario(JFrame parent, GraphModelView dyn)
	{
		super(parent,Application.getApplication().getTraduction("Propriete_Scenario"),true);
		Rectangle bounds = parent.getBounds();
		this.setPreferredSize(new Dimension(500,200));
		this.setLocation(bounds.x+ (int) bounds.width / 2 - 250, bounds.y + bounds.height / 2 - 100);
		
		this.setResizable(false);
		this.pack();
		
		pfenetreCentre.setLayout(bcentre);
		pfenetreSud.setLayout(bsud);
		pfenetreCentrelCom.setLayout(bcentreCom);
		pfenetreCentreSud.setLayout(lfenetreCentreSud);
		lfenetreCentreSud.setRows(2);

		pfenetreCentreCom.setLayout(fcentreCom);
	
  	 		// prori?t?s du label commentaires
		lcom.setDisplayedMnemonic('C');
		lcom.setText("  "+Application.getApplication().getTraduction("Commentaire_scen"));
		lcom.setPreferredSize(new Dimension(125,25));
   
			
		// int?gration dans un BorderLayout : ouest label com, centre champs de saisie
		pfenetreCentrelCom.add(lcom, BorderLayout.WEST);
		
		// ajout d'une barre de d?filement au champs de saisie com
		defil = new JScrollPane(commentaireScenario);
		
		defil.setPreferredSize(new Dimension(350,95));
		pfenetreCentreCom.add(defil);
		pfenetreCentreCom.setPreferredSize(new Dimension(350,100));				
		pfenetreCentrelCom.add(pfenetreCentreCom, BorderLayout.CENTER);
		pfenetreCentrelCom.setPreferredSize(new Dimension(375,105));
		pfenetreCentre.add(pfenetreCentrelCom, BorderLayout.CENTER);
	
		pfenetreCentre.add(pfenetreCentreSud, BorderLayout.SOUTH);
		this.getContentPane().add(pfenetreCentre, BorderLayout.CENTER);
		
					
		// positionnement des boutons pr?cedents
		pfenetreSudButton.add(okButton, null);
		pfenetreSudButton.add(cancelButton, null);
		pfenetreSud.add(pfenetreSudButton, BorderLayout.CENTER);
		this.getContentPane().add(pfenetreSud,  BorderLayout.SOUTH);
		
		// ancien
		
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		this.dynamique=dyn;
		this.ancienCom=dynamique.getCommmentaire_scenario();
		commentaireScenario.setText(ancienCom);
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
		
				
	}
	
	/**
	 * Action effectu?e par le bouton de validation.
	 * Affecte le nom choisi au produit
	 */
	public void okAction()
	{ 
		
		this.dynamique.setCommmentaire_scenario(this.commentaireScenario.getText());
		Application.getApplication().getProjet().setModified(true);
		this.dispose();
	}
}
