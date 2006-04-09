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

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Simple file filter
 */
public class SimpleFileFilter extends FileFilter
{
	private String[] extensions;
	private String description;

	public SimpleFileFilter(String extension, String description)
	{
		this(new String[]{extension}, description);
	}

	public SimpleFileFilter(String[] extensions, String description)
	{
		this.extensions = extensions;
		this.description = description;
	}

	public boolean accept(File file)
	{
		if(file.isDirectory())
		{
			return true;
		}

		// Match an extension
		String name = file.getName().toLowerCase();
		
		for(int i=0; i<extensions.length; i++)
		{
			if(name.endsWith("." + extensions[i]))
			{
		  		return true;
			}
		}

		return false;
	}

	public String getDescription()
	{
		StringBuffer buf = new StringBuffer(description);

		buf.append('(');

		for(int i=0; i<extensions.length; i++)
		{

			buf.append("*.").append(extensions[i]);
			
			if(i < (extensions.length-1))
			{
	  			buf.append(", ");
			}
		}

		buf.append(')');

		return buf.toString();
	}
}
