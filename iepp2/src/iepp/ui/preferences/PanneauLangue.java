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
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.util.Locale;

public class PanneauLangue
    extends PanneauOption {

  private JList mList;
  private String mOldLanguage;
  public static final String LANGUAGE_PANEL_KEY = "LanguageTitle";

  public PanneauLangue(String name) {
    mOldLanguage = Application.getApplication().getConfigPropriete("langueCourante");
    boolean trouve = false;

    this.mTitleLabel = new JLabel(name);
    this.setLayout(new BorderLayout());
    mPanel = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    mPanel.setLayout(gridbag);
    GridBagConstraints c = new GridBagConstraints();

    // Title
    c.weightx = 1.0;
    c.weighty = 0;
    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = GridBagConstraints.REMAINDER; //end row			//	title
    this.mTitleLabel = new JLabel(name);
    TitledBorder titleBor = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK));
    titleBor.setTitleJustification(TitledBorder.CENTER);
    mTitleLabel.setBorder(titleBor);
    gridbag.setConstraints(mTitleLabel, c);
    mPanel.add(mTitleLabel);

    // linefeed
    makeLabel(" ", gridbag, c);

    c.weightx = 0;
    makeLabel(Application.getApplication().getTraduction("Choix_langue"), gridbag, c);
    makeLabel(" ", gridbag, c);

    // liste langues
    Vector listeLangues = Application.getApplication().getLangues();
    mList = new JList(listeLangues);
    gridbag.setConstraints(mList, c);
    mPanel.add(mList);

    c.fill = GridBagConstraints.VERTICAL;
    c.weighty = 2.0;
    makeLabel(" ", gridbag, c);

    this.add(new JLabel("    "), BorderLayout.WEST);
    this.add(mPanel, BorderLayout.CENTER);
    for (int i = 0; i < mList.getModel().getSize() && !trouve; i++) {
      if (mOldLanguage.equals( ( (String) mList.getModel().getElementAt(i)))) {
        mList.setSelectedIndex(i);
        trouve = true;
      }
    }
    if (!trouve && mList.getModel().getSize() > 0) {
      mList.setSelectedIndex(0);
    }
  }

  public PanneauOption openPanel(String key) {
    this.setName(Application.getApplication().getTraduction(key));

    return this;
  }

  public void save() {

    String idLangue = mList.getSelectedValue().toString();
    //English ou Francais

    Application.getApplication().setLangueCourante(idLangue);
    /*
     debut modif 2xmi chaouk
     */
    if (idLangue.equals("Francais")) {
      Locale.setDefault(Locale.FRENCH);
    }else{
      Locale.setDefault(Locale.ENGLISH);
    }

    /*
     fin modif 2xmi chaouk
     */
  }

  public boolean hasLanguageChanged() {
    boolean result = !mOldLanguage.equals(Application.getApplication().getConfigPropriete("langueCourante"));
    mOldLanguage = Application.getApplication().getConfigPropriete("langueCourante");
    return result;
  }

}
