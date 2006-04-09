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
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class PanneauAuteurs extends PanneauOptionPropos
{
  private JTextArea mDescriptionLabel ;

  private JTable listeEtudiants;
  private String[] columnNames = {Application.getApplication().getTraduction("colonne_nom"), Application.getApplication().getTraduction("colonne_contact"), Application.getApplication().getTraduction("colonne_role")};
  private Object[][] data;
  private int nb_etudiants = 24; //nombre d'etudiants ayant travaille sur IEPP depuis 2004

  public static final String AUTEURS_KEY="auteursTitle";

  public PanneauAuteurs(String name)
  {
    this.mTitleLabel=new JLabel(name);
    this.setLayout(new BorderLayout());
    mPanel=new JPanel();
    GridBagLayout gridbag=new GridBagLayout();
    mPanel.setLayout(gridbag);
    GridBagConstraints c=new GridBagConstraints();

    // Title
    c.weightx=1.0;
    c.weighty=0;
    c.fill=GridBagConstraints.BOTH;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row			//	title
    this.mTitleLabel=new JLabel(name);
    TitledBorder titleBor=BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK));
    titleBor.setTitleJustification(TitledBorder.CENTER);
    mTitleLabel.setBorder(titleBor);
    gridbag.setConstraints(mTitleLabel, c);
    mPanel.add(mTitleLabel);

    // linefeed
    c.weighty=0;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row
    makeLabel(" ", gridbag, c);

    c.weighty=0;
    c.weightx=0;
    c.fill=GridBagConstraints.BOTH;
    c.gridwidth=GridBagConstraints.REMAINDER; //next-to-last in row

    this.mDescriptionLabel=new JTextArea();
    this.setDescription(name);
    gridbag.setConstraints(this.mDescriptionLabel, c);
    mPanel.add(this.mDescriptionLabel);

    //linefeed
    c.weighty=0;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row
    makeLabel(" ", gridbag, c);

    //liste des etudiants
    c.weighty=0;
    c.weightx=0;
    c.fill=GridBagConstraints.BOTH;
    c.gridwidth=GridBagConstraints.REMAINDER;

    data=new Object[nb_etudiants][3];
    data[0][0]="Sandra Poulain";
    data[0][1]="sandrapoulain@free.fr";
    data[0][2]=Application.getApplication().getTraduction("chefProjet");

    data[1][0]="Amandine Jean";
    data[1][1]="2xmi@free.fr";
    data[1][2]=Application.getApplication().getTraduction("chefProjet");
    
    data[2][0]="Guillaume Moulin";
    data[2][1]="gmoulin.be@gmail.com";
    data[2][2]=Application.getApplication().getTraduction("chefProjet"); 

    data[3][0]="Sylvain Lavalley";
    data[3][1]="sylvain.lavalley@club-internet.fr";
    data[3][2]=Application.getApplication().getTraduction("architecte");

    data[4][0]="Chaouki Mhamedi";
    data[4][1]="2xmi@free.fr";
    data[4][2]=Application.getApplication().getTraduction("architecte");

    data[5][0]="Stéphane Jidouard";
    data[5][2]=Application.getApplication().getTraduction("architecte");
    
    data[6][0]="Nicolas Michel";
    data[6][1]="njlmichel@free.fr";
    data[6][2]=Application.getApplication().getTraduction("specialiste_outil");

    data[7][0]="Jean Gaston";
    data[7][1]="2xmi@free.fr";
    data[7][2]=Application.getApplication().getTraduction("specialiste_outil");

    data[8][0]="Hubert Nouhen";
    data[8][1]="hnouhen.be@gmail.com";
    data[8][2]=Application.getApplication().getTraduction("specialiste_outil");
    
    data[9][0]="Nicolas Pujos";
    data[9][1]="nicolaspujos@hotmail.com";
    data[9][2]=Application.getApplication().getTraduction("ing_processus");

    data[10][0]="Sébastien René";
    data[10][1]="2xmi@free.fr";
    data[10][2]=Application.getApplication().getTraduction("ing_processus");
    
    data[11][0]="Julien Sanmartin";
    data[11][2]=Application.getApplication().getTraduction("ing_processus");

    data[12][0]="Natalia Boursier";
    data[12][2]=Application.getApplication().getTraduction("analyste_dev");

    data[13][0]="Robin Eysseric";
    data[13][2]=Application.getApplication().getTraduction("analyste_dev");

    data[14][0]="Cédric Bouhours";
    data[14][2]=Application.getApplication().getTraduction("analyste_dev");

    data[15][0]="Vinvent Marillaud";
    data[15][2]=Application.getApplication().getTraduction("analyste_dev");

    data[16][0]="Stéphane Anrigo";
    data[16][2]=Application.getApplication().getTraduction("analyste_dev");

    data[17][0]="Mourad Sadok";
    data[17][2]=Application.getApplication().getTraduction("analyste_dev");

    data[18][0]="Youssef Mounasser";
    data[18][2]=Application.getApplication().getTraduction("analyste_dev");

    data[19][0]="Julie Tayac";
    data[19][2]=Application.getApplication().getTraduction("analyste_dev");
    
    data[20][0]="Mohamed-Amine Bouchikhi";
    data[20][2]=Application.getApplication().getTraduction("analyste_dev");
    
    data[21][0]="Damien Ghiles";
    data[21][2]=Application.getApplication().getTraduction("analyste_dev");
    
    data[22][0]="Emilien Perico";
    data[22][2]=Application.getApplication().getTraduction("analyste_dev");
    
    data[23][0]="Olivier Egger";
    data[23][2]=Application.getApplication().getTraduction("analyste_dev");
    this.listeEtudiants=new JTable(data, columnNames);

    gridbag.setConstraints(this.listeEtudiants, c);
    mPanel.add(this.listeEtudiants);

    //		linefeed
    c.fill=GridBagConstraints.VERTICAL;
    c.weighty=2.0;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row
    makeLabel(" ", gridbag, c);

    this.add(new JLabel("    "), BorderLayout.WEST);
    this.add(mPanel, BorderLayout.CENTER);

  }

  public PanneauOptionPropos openPanel(String key)
  {
    this.setName(Application.getApplication().getTraduction(key));
    return this;
  }

  public void setDescription(String key)
  {
     this.mDescriptionLabel.setText(Application.getApplication().getTraduction("etudiants_texte1") + Application.getApplication().getTraduction("etudiants_texte2"));
  }

}