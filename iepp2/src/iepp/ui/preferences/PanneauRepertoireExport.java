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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


public class PanneauRepertoireExport
    extends PanneauOption
{
        private JTextField mDefaultPath;
        private JButton browseButton;

        public static final String EXPORT_DIRECTORY_PANEL_KEY = "DefaultExportDirectoryTitle";


  public PanneauRepertoireExport(String name)
        {
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

                // repertoire
                c.weighty = 0;
                c.weightx = 0 ;
                c.fill = GridBagConstraints.BOTH;
                c.gridwidth = GridBagConstraints.REMAINDER;
                JLabel label = new JLabel(Application.getApplication().getTraduction("CheminRepertoireExportDefaut"));
                gridbag.setConstraints(label, c);
                mPanel.add(label);
                c.weightx = 3 ;
                c.gridwidth = GridBagConstraints.RELATIVE;
                mDefaultPath = new JTextField(25);
                mDefaultPath.setText(Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport"));
                mPanel.add(mDefaultPath);
                gridbag.setConstraints(mDefaultPath, c);
                c.weightx = 0 ;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                this.browseButton = new JButton(Application.getApplication().getTraduction("Parcourir"));
                browseButton.addActionListener(man);
                gridbag.setConstraints(browseButton, c);
                mPanel.add(browseButton);

                //linefeed
                c.weighty = 0;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                makeLabel(" ", gridbag, c);


                //linefeed
                c.weighty = 0;
                c.gridwidth = GridBagConstraints.REMAINDER; //end row
                makeLabel(" ", gridbag, c);

                c.fill = GridBagConstraints.VERTICAL;
                c.weighty = 2.0;
                // linefeed
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
          Application.getApplication().setConfigPropriete("chemin_repertoireDefautExport", mDefaultPath.getText());
        }

        private class ManagerButton implements ActionListener
        {
                public void actionPerformed(ActionEvent e)
                {
                        //r\uFFFDcup\uFFFDrer l'objet source de l'\uFFFDv\uFFFDnement re\uFFFDu
                        Object source = e.getSource();
                        // selon cet objet, r\uFFFDagir en cons\uFFFDquence
                        if (source == PanneauRepertoireExport.this.browseButton)
                        {
                                JFileChooser fileChooser = new JFileChooser(PanneauRepertoireExport.this.mDefaultPath.getText());
                                fileChooser.setDialogTitle(Application.getApplication().getTraduction("titre_choix_rep"));
                                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                int res = fileChooser.showDialog(PanneauRepertoireExport.this, Application.getApplication().getTraduction("OK"));
                                if(res == JFileChooser.APPROVE_OPTION)
                                        PanneauRepertoireExport.this.mDefaultPath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                        }
                }
        }
}
