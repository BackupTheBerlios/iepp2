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
package iepp.domaine;

import java.io.Serializable;
import java.util.Observable;

/**
 * @author s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ObjetModele extends Observable implements Serializable
{
	public boolean estComposant(int numrang, int numtype)
	{
		return (false);
	}
	
	public boolean estScenario(int numrang, int numtype)
	{
		return (false);
	}
	
	public boolean estDefProc(int numrang, int numtype)
	{
		return (false);
	}
	
	public boolean estDefinitionTravail(int numrang, int numtype)
	{
		return (false);
	}
	
	public boolean estProduit(int numrang, int numtype)
	{
		return (false);
	}

	public boolean estProduitEntree(int numrang, int numtype)
	{
		return (false);
	}
	
	public boolean estProduitSortie(int numrang, int numtype)
	{
		return (false);
	}
	
	public boolean estInterfaceRequise(int numrang, int numtype)
	{
		return (false);
	}
		
	public boolean estInterfaceFournie(int numrang, int numtype)
	{
		return (false);
	}
	
	public boolean estPaquetage(int numrang, int numtype)
	{
			return (false);
	}
		
	public boolean estActivite(int numrang, int numtype)
	{
			return (false);
	}
		
	public String toString(int numrang, int numtype)
	{
			return ("Methode toString à implementer");
	}
		
	
	public int getNbFils(int numrang, int numtype)
	{
			return (0);
	}
		
	public IdObjetModele getFils(int ieme, int numrang, int numtype)
	{
			return (null);
	}
		
	public void setNomElement(String chaine, int i, int j) {}

	public boolean estRole(int i, int j)
	{
		return (false);
	}

	public boolean estDiagramme(int i, int j)
	{
		return false;
	}
}
