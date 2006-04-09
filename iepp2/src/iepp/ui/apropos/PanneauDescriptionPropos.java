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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class PanneauDescriptionPropos extends PanneauOptionPropos
{
        private JLabel mDescriptionLabel ;

        public static final String AUTEURS_KEY = "auteursTitle" ;
        public static final String LIBRAIRIES_KEY = "librairiesTitle" ;
        public static final String LICENCES_KEY = "licencesTitle" ;

        public PanneauDescriptionPropos(String name)
        {
                this.mTitleLabel = new JLabel (name) ;
                this.setLayout(new BorderLayout());
                mPanel = new JPanel() ;
                GridBagLayout gridbag = new GridBagLayout();
                mPanel.setLayout(gridbag);
                GridBagConstraints c = new GridBagConstraints();

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

                c.weighty = 0;
                c.weightx = 0 ;
                c.fill = GridBagConstraints.BOTH;
                c.gridwidth = GridBagConstraints.REMAINDER;;//next-to-last in row


                this.mDescriptionLabel = new JLabel();
                this.setDescription(name);
                gridbag.setConstraints(this.mDescriptionLabel, c);
                mPanel.add(this.mDescriptionLabel);

                //		linefeed
                c.fill = GridBagConstraints.VERTICAL;
                c.weighty = 2.0;
                 c.gridwidth = GridBagConstraints.REMAINDER; //end row
                makeLabel(" ", gridbag, c);

                this.add(new JLabel("    "),BorderLayout.WEST);
                this.add(mPanel,BorderLayout.CENTER);
        }

        public PanneauOptionPropos openPanel(String key)
        {
                this.setName(Application.getApplication().getTraduction(key)) ;
                this.setDescription (key);
                return this ;
        }

        public void setDescription(String key)
        {
                if (key.equals(AUTEURS_KEY))
                {
                        this.mDescriptionLabel.setText(Application.getApplication().getTraduction("AuteursTitleDesc"));
                }
                else if (key.equals(LIBRAIRIES_KEY))
                {
                        this.mDescriptionLabel.setText(Application.getApplication().getTraduction("LibrairiesTitleDesc"));
                }
                else if (key.equals(LICENCES_KEY))
                {
                        this.mDescriptionLabel.setText(Application.getApplication().getTraduction("LicencesTitleDesc"));
                }
        }

}
