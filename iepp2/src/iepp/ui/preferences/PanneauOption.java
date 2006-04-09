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


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public abstract class PanneauOption extends JPanel 
{
	protected JPanel mPanel ;
	protected JLabel mTitleLabel ;

	protected void makeButton(String name, GridBagLayout gridbag,
							  GridBagConstraints c) 
	{
		JButton button = new JButton(name);
		gridbag.setConstraints(button, c);
		mPanel.add(button);
	}
	protected void makeLabel(String name,
							  GridBagLayout gridbag,
							  GridBagConstraints c) 
	{
		JLabel label = new JLabel(name);
		gridbag.setConstraints(label, c);
		mPanel.add(label);
	}
	public String getName ()				
	{
			return this.mTitleLabel.getText() ;
	}
	public void setName (String name)				
	{
			this.mTitleLabel.setText(name) ;
	}
}
