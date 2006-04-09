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

import iepp.Application;
import sun.awt.VerticalBagLayout;
import util.IconManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JTable;

public class PanneauInformations extends PanneauOptionPropos
{
  public static final String INFOS_KEY="informationsTitle";
  private JPanel panneauInfo;
  private JPanel sousPanneauInfo;
  private JPanel panelIcons;
  private JPanel panelTexte;
  private JLabel iconeIepp = new JLabel();
  private JLabel icone2xmi = new JLabel();
  private JLabel titre = new JLabel();
  private JLabel titre2xmi = new JLabel();
  private JLabel titrenit = new JLabel();

  public PanneauInformations(JPanel panel)
  {
    this.panneauInfo = new JPanel();
    this.panneauInfo.setLayout(new BorderLayout());
    
    this.sousPanneauInfo = new JPanel();
    this.sousPanneauInfo.setLayout(new BorderLayout());

    this.panelIcons = new JPanel();
    iconeIepp.setIcon(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "iepp_logo_mini1.jpg"));
    this.panelIcons.add(iconeIepp, BorderLayout.NORTH);
    icone2xmi.setIcon(IconManager.getInstance().getIcon(Application.getApplication().getConfigPropriete("dossierIcons") + "2xmi_logo.gif"));
    this.panelIcons.add(icone2xmi, BorderLayout.EAST);
    this.panneauInfo.add(this.panelIcons, BorderLayout.NORTH);

    titre.setText("IEPP : Isi Engineering Process Publisher    Version " + Application.NUMVERSION + "     http://iepp.free.fr");
    this.sousPanneauInfo.add(titre,BorderLayout.NORTH);

    titre2xmi.setText("2XMI : Bibliothèque d'exportation    Version " + Application.NUMVERSION_2XMI + "     http://2xmi.free.fr");
    this.sousPanneauInfo.add(titre2xmi,BorderLayout.CENTER);

    titrenit.setText("NIT : Génération de scénarii    Version " + Application.NUMVERSION_NIT + "     http://www.nit.free.fr");
    this.sousPanneauInfo.add(titrenit,BorderLayout.SOUTH);
    
    
    this.panneauInfo.add(this.sousPanneauInfo, BorderLayout.CENTER);
        
    this.add(panneauInfo, BorderLayout.CENTER);
    panel.add(this);
  }

  public PanneauOptionPropos openPanel(String key)
  {
    this.setName(Application.getApplication().getTraduction(key));
    return this;
  }
}
