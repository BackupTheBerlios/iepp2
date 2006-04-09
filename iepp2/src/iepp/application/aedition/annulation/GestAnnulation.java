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
package iepp.application.aedition.annulation;

import java.util.Stack;
import java.util.EmptyStackException;
import iepp.application.*;

/**
 * Classe gestionnaire annulation comportant les  piles UNDO/REDO
 */

public class GestAnnulation
{
	private Stack pileUndo;
	private Stack pileRedo;
	private int	nbCommandes;

	/**
	 * Constructeur avec en paramètre le nombre maximal de commandes annulables
	 * @param int nbCommandes
	 */
	public GestAnnulation(int nbCommandes)
	{
		pileUndo = new Stack();
		pileRedo = new Stack();
		this.nbCommandes = nbCommandes;
	}
	
	/**
	 * Ajout d'une commande dans la pile UNDO
	 * @param CommandeAnnulable Commande a ajouter dans la pile UNDO
	 */
	public void ajouterCommande(CommandeAnnulable laCommande)
	{
		if (pileUndo.size() == nbCommandes)
			pileUndo.removeElementAt(0);
		pileUndo.push(laCommande);
		pileRedo.clear();
		
	}
	
	/**
	 * Méthode permettant l'annulation d'une commande
	 */
	public void annuler()
	{
		try
		{
			CommandeAnnulable laCommande;
			laCommande = (CommandeAnnulable)pileUndo.pop();
			laCommande.defaire();
			pileRedo.push(laCommande);			
		}
		catch (EmptyStackException E)
		{
			System.out.println("Impossible d'annuler, la pile est vide !!");
		}
	}
	
	/**
	 * Méthode permettant la restauration de la commande
	 */
	public void refaire()
	{
		try
		{
			CommandeAnnulable laCommande;
			laCommande = (CommandeAnnulable)pileRedo.pop();
			laCommande.refaire();
			pileUndo.push(laCommande);		
		}
		catch (EmptyStackException E)
		{
			System.out.println("Impossible de refaire, la pile est vide !!");
		}
	}
	
	/**
	 * Indique si la pile des annulations est vide
	 */
	public boolean estAnnulerVide()
	{
		return (pileUndo.isEmpty());
	}

	/**
	 * Indique si la pile des redos est vide
	 */
	public boolean estRefaireVide()
	{
		return (pileRedo.isEmpty());
	}	
}