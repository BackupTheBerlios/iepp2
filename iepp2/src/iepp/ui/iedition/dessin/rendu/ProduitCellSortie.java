package iepp.ui.iedition.dessin.rendu;

import iepp.domaine.IdObjetModele;

/* IEPP: Isi Engineering Process Publisher
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

public class ProduitCellSortie extends ProduitCell {

	protected ComposantCell compParent;
	
	protected boolean CellLiee;
	
	public ProduitCellSortie(IdObjetModele prod, int x, int y, ComposantCell comp) {
		super(prod,x,y);
		compParent = comp;
		CellLiee = false;
	}
	
	public ProduitCellSortie(IdObjetModele prod, int x, int y, ComposantCell comp, int l, int h, String imageprod) {
		super(prod,x,y,l,h,imageprod);
		compParent = comp;
		CellLiee = false;
	}

	/**
	 * @return Returns the compParent.
	 */
	public ComposantCell getCompParent() {
		return compParent;
	}
	/**
	 * @param compParent The compParent to set.
	 */
	public void setCompParent(ComposantCell compParent) {
		this.compParent = compParent;
	}
	
	public boolean isCellLiee() {
		return CellLiee;
	}
	
	public void setCellLiee(boolean l) {
		CellLiee = l;
	}

	public void masquer(){
		this.masquer = true;
		this.compParent.setContientProdMasquer();
	}

}
