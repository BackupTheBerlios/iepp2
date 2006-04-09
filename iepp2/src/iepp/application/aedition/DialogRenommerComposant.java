package iepp.application.aedition;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import iepp.Application;
import iepp.domaine.*;
import iepp.ui.FenetrePrincipale;

// 2xmi - Classe entièrement refaite - Hubert

public class DialogRenommerComposant extends JDialog
{
  private JPanel pFenetreCentre = new JPanel();
  private JPanel pFenetreSud = new JPanel();
  private GridLayout gFenetreCentre = new GridLayout();

  private JLabel texte = new JLabel();
  private JTextField champs = new JTextField();

  private JButton bRenommer = new JButton();
  private JButton bAnnuler = new JButton();

  private IdObjetModele compProc ;
  private JFrame parent;

  public DialogRenommerComposant(FenetrePrincipale parent, IdObjetModele id)
  {
    super(parent, Application.getApplication().getTraduction("Renommer_Composant"), true);
    this.compProc = id;
    this.parent=parent;

    jbInit();

    // affichage centré de la Boîte de Dialogue
    this.pack();
    this.setResizable(false);
    Rectangle bounds = parent.getBounds();
    this.setLocation(bounds.x + (int) bounds.width / 2 - this.getWidth() / 2, bounds.y + bounds.height / 2 - this.getHeight() / 2);
    this.setVisible(true);
  }

  /**
   * Initialise les éléments de la boîte de dialogue
   */

  private void jbInit()
  {
    pFenetreCentre.setLayout(gFenetreCentre);
    gFenetreCentre.setRows(2);
    gFenetreCentre.setColumns(1);

    // champs nom
    texte.setDisplayedMnemonic('c');
    texte.setText(Application.getApplication().getTraduction("Renommer_Composant"));
    texte.setLabelFor(champs);
    
    champs.addKeyListener(new KeyListener()
    		{
 				public void keyTyped(KeyEvent arg0) {
				}
				public void keyPressed(KeyEvent arg0) {
				}
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode()==KeyEvent.VK_ENTER){
						renommer(null);
					}
				}
    		});
    
    champs.setText(this.compProc.toString());
    
    pFenetreCentre.add(texte, null);
    pFenetreCentre.add(champs, null);
    this.getContentPane().add(pFenetreCentre, BorderLayout.CENTER);

    // creation du bouton valider
    bRenommer.setMnemonic('R');
    bRenommer.setText(Application.getApplication().getTraduction("Renommer"));
    bRenommer.setFocusable(true);
    bRenommer.addActionListener(new java.awt.event.ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            renommer(e);
        }
    });

   // création du bouton annuler
   bAnnuler.setMnemonic('l');
   bAnnuler.setText(Application.getApplication().getTraduction("Annuler"));
   bAnnuler.addActionListener(new java.awt.event.ActionListener()
   {
       public void actionPerformed(ActionEvent e)
       {
           cancel(e);
       }
   });

   // positionnement des deux boutons
   pFenetreSud.add(bRenommer, null);
   pFenetreSud.add(bAnnuler, null);
   this.getContentPane().add(pFenetreSud, BorderLayout.SOUTH);
 }

 private void renommer(ActionEvent e)
 {
   if (this.champs.getText().length() != 0)
   {
     CRenommerComposant co = new CRenommerComposant(this.compProc,this.champs.getText().trim());
     co.executer();
     this.dispose();
   }
 }

  /**
   * Action sur le bouton annuler
   * @param e
   */

  private void cancel(ActionEvent e)
  {
      this.dispose();
  }
}
