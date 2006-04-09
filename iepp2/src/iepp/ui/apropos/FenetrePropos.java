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
package iepp.ui.apropos;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import iepp.Application;
import iepp.ui.FenetrePrincipale;
import iepp.ui.apropos.PanneauAuteurs;
import iepp.ui.apropos.PanneauLibrairies;
import iepp.ui.apropos.PanneauLicences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FenetrePropos extends JDialog {
  /**
   * Panneau sur lequel s'affiche l'arbre et la partie de droite
   * qui change selon l'option choisie
   */
  private JPanel panneau;

  private PanneauInformations panneauInfos;
  private PanneauAuteurs panneauAuteurs;
  private PanneauLibrairies panneauLibrairies;
  private PanneauLicences panneauLicences;

  private JButton ok;

  public static final int TYPE_AUTEURS = 0;
  public static final int TYPE_LIBRAIRIES = 1;
  public static final int TYPE_LICENCES = 2;

  public FenetrePropos(FenetrePrincipale parent) {
    super(parent, true);
    this.setSize(780, 600);
    this.setTitle(Application.getApplication().getTraduction("Propos"));

    // gestionnaire de mise en page
    this.getContentPane().setLayout(new BorderLayout());

    // creer l'arbre de navigation
    ArbrePropos tree = new ArbrePropos(this);

    Container panelMarge = getContentPane();
    // creer le panneau qui va changer
    this.panneau = new JPanel();
    this.panneau.setLayout(new BorderLayout());
    this.panneau.add(tree, BorderLayout.WEST);

    Box bottom = Box.createHorizontalBox();
    this.ok = new JButton(Application.getApplication().getTraduction("Fermer"));
    this.ok.setMnemonic('E');
    this.ok.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        dispose();
      }
    });

    bottom.add(Box.createHorizontalGlue());
    bottom.add(this.ok);

    tree.setBorder(BorderFactory.createLoweredBevelBorder());
    this.panneau.add(bottom, BorderLayout.SOUTH);

    //this.panneauInfos = new PanneauInformations();
    //this.panneau.add(this.panneauInfos, BorderLayout.CENTER);

    // create the panel
    panelMarge.add(this.panneau, BorderLayout.CENTER);
    panelMarge.add(new JLabel(" "), BorderLayout.NORTH);
    panelMarge.add(new JLabel("   "), BorderLayout.EAST);
    panelMarge.add(new JLabel("   "), BorderLayout.WEST);
    panelMarge.add(new JLabel(" "), BorderLayout.SOUTH);

    this.initProposDialog();

    Rectangle bounds = parent.getBounds();
    this.setLocation(bounds.x + (int) bounds.width / 2 - this.getWidth() / 2,
                     bounds.y + bounds.height / 2 - this.getHeight() / 2);
    this.setResizable(false);
    this.setVisible(true);
  }

  public void initProposDialog() {
    this.panneauInfos = new PanneauInformations(this.panneau);
    this.panneauInfos.setVisible(true);

    this.panneauAuteurs = new PanneauAuteurs(Application.getApplication().getTraduction(PanneauAuteurs.AUTEURS_KEY));
    this.panneauAuteurs.setVisible(false);

    this.panneauLibrairies = new PanneauLibrairies(Application.getApplication().getTraduction(PanneauLibrairies.LIBRAIRIES_KEY));
    this.panneauLibrairies.setVisible(false);

    this.panneauLicences = new PanneauLicences(Application.getApplication().getTraduction(PanneauLicences.LICENCES_KEY));
    this.panneauLicences.setVisible(false);
  }

  public void setInnerPanel(int panel, String key) {
    this.panneauInfos.setVisible(false);

    if (panel == ProposTreeItem.AUTEURS_PANEL)
    {
      panneau.add(this.panneauAuteurs.openPanel(key), BorderLayout.CENTER);
    }
    else
    {
      if (panel == ProposTreeItem.LIBRAIRIES_PANEL)
      {
        panneau.add(this.panneauLibrairies.openPanel(key), BorderLayout.CENTER);
      }
      else
      {
        if (panel == ProposTreeItem.LICENCES_PANEL)
        {
          panneau.add(this.panneauLicences.openPanel(key), BorderLayout.CENTER);
        }
      }
    }
  }

  public PanneauAuteurs getAuteursPanel() {
    return this.panneauAuteurs;
  }

  public PanneauLibrairies getLibrairiesPanel() {
    return this.panneauLibrairies;
  }

  public PanneauLicences getLicencesPanel() {
    return this.panneauLicences;
  }

}
