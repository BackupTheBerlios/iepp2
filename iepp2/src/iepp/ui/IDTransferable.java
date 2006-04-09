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

package iepp.ui;


import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

public class IDTransferable implements Transferable
{
	public IDTransferable( Object o )
	{
		this.o = o;
		
		if ( dataFlavors == null )
		{
			try
			{
				dataFlavors = new DataFlavor[ 1 ];
				flavor = new DataFlavor( mimeType, null );
				dataFlavors[ 0 ] = flavor;
			}
			catch ( Exception e )
			{
				e.printStackTrace( System.err );
			}
		}
	}

	// Transferable interface
	private static final String mimeType = DataFlavor.javaJVMLocalObjectMimeType + "; class=java.lang.Object";
	private static DataFlavor flavor = null;
	private static DataFlavor[] dataFlavors = null;
	
	public static DataFlavor getFlavor()
	{
		return flavor; 
	}
	
	public DataFlavor[] getTransferDataFlavors()
	{
		return dataFlavors; 
	}
	
	public boolean isDataFlavorSupported( DataFlavor flavor )
	{
		return ( flavor == IDTransferable.flavor );
	}
	
	public Object getTransferData( DataFlavor flavor ) throws UnsupportedFlavorException
	{
		if ( flavor != IDTransferable.flavor )	throw new UnsupportedFlavorException( flavor );
		return o;
	}
	
	private Object o;
}
