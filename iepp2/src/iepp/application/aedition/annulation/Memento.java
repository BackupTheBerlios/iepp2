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

/**
 * Classe Memento qui permet de mémoriser à un instant donné
 * l'état d'un objet annulable 
 */
public class Memento
{
	/**
	 * AJOUTER COMMENTAIRES avec @ param
	 */
	private ObjetAnnulable objetSauve;
	private String etat;
	
	/**
	 * Constructeur de l'obet memento avec en paramètre la référence de l'objet à mémoriser
	 */
	public Memento(ObjetAnnulable objetSauve)
	{
		this.objetSauve = objetSauve;
	}
	
	/**
	 * Méthode permettant la sauvegarde de l'état de l'objet
	 */
	public void sauverEtat(String etat)
	{
		this.etat = etat;
	}
	
	/**
	 * Méthode permettant la restauration de l'objet
	 */
	public void restaurerEtat()
	{
		objetSauve.restaurer(etat);
	}
	
}