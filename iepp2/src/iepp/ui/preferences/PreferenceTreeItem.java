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

public class PreferenceTreeItem {
  public static final int DIAGRAM_PANEL = 1;
  public static final int GENERATION_PANEL = 2;
  public static final int REPOSITORY_PANEL = 3;
  public static final int DESC_PANEL = 4;
  public static final int LANGUAGE_PANEL = 5;
  public static final int DP_PANEL = 6;
  public static final int DP_DESCRIPTION_PANEL = 7;
  public static final int DP_GENERATION_PANEL = 8;
  public static final int GENERATION_OPTION_PANEL_KEY = 9;
  public static final int EXPORT_DIRECTORY_PANEL = 10;
  //modif 2XMI Amandine
  public static final int ROLE_GENERATION_PANEL = 11;
  public static final int COMPOSANT_DESCRIPTION_RESP_PANEL = 12;
  public static final int COMPOSANT_DESCRIPTION_MAIL_PANEL = 13;
  public static final int COMPOSANT_DESCRIPTION_VERSION_PANEL = 14;
  public static final int PAQ_DESCRIPTION_PANEL = 15;

  private String mKey;
  private String mValue;
  private int mPanel;

  public PreferenceTreeItem(String key, String val, int panel) {
    this.mKey = key;
    this.mValue = val;
    this.mPanel = panel;
  }

  public String getKey() {
    return (this.mKey);
  }

  public int getPanel() {
    return (this.mPanel);
  }

  public String toString() {
    return (this.mValue);
  }
}
