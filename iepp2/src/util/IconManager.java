/*
 * APES is a Process Engineering Software
 * Copyright (C) 2002-2003 IPSquad
 * team@ipsquad.tuxfamily.org
 *
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
 */

package util;

import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.Toolkit;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.HashMap;

/**
 * Easily create Icon resources from an image.
 * It is implemented as a singleton.
 *
 * @version $Revision: 1.1 $
 */
public class IconManager
{
	private static IconManager mInstance = new IconManager();
	// a modifier
	// private static final String msBasePath = "web";
	private static final String msBasePath = "";

	// image cache
	private HashMap mImageMap = new HashMap();

	private IconManager() { }

	/**
	 * Retrieve the unique instance of IconManager
	 *
	 * @return the instance
	 */
	static public IconManager getInstance()
	{
		return mInstance;
	}

	/**
	 * Get an Icon resource
	 *
	 * @param resourceName the name of the file containing the icon
	 * @return the icon
	 */
	public Icon getIcon(String resourceName)
	{
		if(resourceName == null)
		{
			throw new RuntimeException("Null resource name!");
		}

		return new ImageIcon(getImageResource(msBasePath+"/"+resourceName));
	}


	/**
	 * Get an Image resource
	 *
	 * @param resourceName the name of the file containing the icon
	 * @return the image
	 */
	public Image getImageResource(String resourceName)
	{
		/*
		if(resourceName.charAt(0) != '/')
		{
			resourceName = '/' + resourceName;
		}*/

		Image img = null;
		if((img = (Image)mImageMap.get(resourceName)) == null)
		{
			Toolkit kit = Toolkit.getDefaultToolkit();
			URL url = getClass().getResource(resourceName);
			
			if(url!=null)
			{
				try
				{
					img = kit.createImage((ImageProducer)url.getContent());
				}
				catch(Throwable t)
				{
					t.printStackTrace();
				}
			}

			if(img == null)
			{
				throw new RuntimeException("Resource not found " + resourceName);
			}
			else
			{
				mImageMap.put(resourceName, img);
			}

		} 

		return img;
	}
}
