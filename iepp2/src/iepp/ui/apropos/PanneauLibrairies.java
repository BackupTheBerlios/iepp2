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

public class PanneauLibrairies extends PanneauOptionPropos
{
  private JTextArea mDescriptionLabel ;
  private JTable listeLibrairies;
  private String[] columnNames = {Application.getApplication().getTraduction("colonne_lib_nom"), Application.getApplication().getTraduction("colonne_lib_exp")};
  private Object[][] data;
  private int nb_lib = 10 ; //nombre de librairies utilisées par IEPP

  public static final String LIBRAIRIES_KEY="librairiesTitle";

  public PanneauLibrairies(String name)
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
    this.setDescription();
    gridbag.setConstraints(this.mDescriptionLabel, c);
    mPanel.add(this.mDescriptionLabel);

    //linefeed
    c.weighty=0;
    c.gridwidth=GridBagConstraints.REMAINDER; //end row
    makeLabel(" ", gridbag, c);

    //liste des librairies
    c.weighty=0;
    c.weightx=0;
    c.fill=GridBagConstraints.BOTH;
    c.gridwidth=GridBagConstraints.REMAINDER;

    data=new Object[nb_lib][2];
    data[0][0]="avalon-framework-4.2.0.jar";
    data[0][1]=Application.getApplication().getTraduction("lib_avalon");

    data[1][0]="batik-1.5.1.jar";
    data[1][1]=Application.getApplication().getTraduction("lib_batik");

    data[2][0]="fop.jar";
    data[2][1]=Application.getApplication().getTraduction("lib_fop");

    data[3][0]="Tidy.jar";
    data[3][1]=Application.getApplication().getTraduction("lib_tidy");

    data[4][0]="JimiProClasses.zip";
    data[4][1]=Application.getApplication().getTraduction("lib_jimi");

    data[5][0]="jgraph.jar";
    data[5][1]=Application.getApplication().getTraduction("lib_jgraph");

    data[6][0]="JSX-java1.4.jar";
    data[6][1]=Application.getApplication().getTraduction("lib_jsx");

    data[7][0]="lucene-1.4.3.jar";
    data[7][1]=Application.getApplication().getTraduction("lib_lucene");
    
    data[8][0]="jdom.jar";
    data[8][1]=Application.getApplication().getTraduction("lib_jdom");
    
    data[9][0]="ostermillerutils_1_06_00.jar";
    data[9][1]=Application.getApplication().getTraduction("lib_ostermillerutils");

    this.listeLibrairies=new JTable(data, columnNames);

    gridbag.setConstraints(this.listeLibrairies, c);
    mPanel.add(this.listeLibrairies);


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

  public void setDescription()
  {
     this.mDescriptionLabel.setText(Application.getApplication().getTraduction("label_librairie"));
  }
}
