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

import java.awt.Component;
import javax.swing.JFileChooser;
import java.io.File;

/**
 * File chooser that will remember the users last selected directory
 */
public class SmartChooser
    extends JFileChooser {
  private static SmartChooser chooser = new SmartChooser();
  protected File lastDirectory = new File(System.getProperty("user.dir"));


  /**
   * SmartChooser
   *
   * @param _repertoire String
   */
  public SmartChooser(String _repertoire) {
    super();
    this.setCurrentDirectory(new File (_repertoire));
  }

  /**
   * SmartChooser
   *
   * @param _repertoire String
   */
  public SmartChooser() {
    super();
  }


  /**
   * Get the JFileChooser shared among all FileActions
   */
  public static SmartChooser getChooser() {
    // Remove all the filters before letting another object reuse it
    chooser.setSelectedFile(null);
    chooser.resetChoosableFileFilters();
    chooser.setFileSelectionMode(FILES_AND_DIRECTORIES);
    return chooser;
  }


  /**
   * Get the JFileChooser shared among all FileActions
   */
  public static SmartChooser getChooser(String _baseDir) {
    // Remove all the filters before letting another object reuse it
    chooser.setSelectedFile(null);
    chooser.resetChoosableFileFilters();
    chooser.setFileSelectionMode(FILES_AND_DIRECTORIES);
    chooser.setCurrentDirectory(new File (_baseDir));
    return chooser;
  }


  /**
   * Remember last directory
   */
  public void approveSelection() {
    super.approveSelection();

    // Remember last directory
    lastDirectory = getSelectedFile();

    if (!lastDirectory.isDirectory()) {
      lastDirectory = lastDirectory.getParentFile();
    }
  }

  /**
   * Switch to last used directory
   */
  public int showDialog(Component parent, String approveButtonText) {
    setCurrentDirectory(lastDirectory);
    return super.showDialog(parent, approveButtonText);
  }

  public String getLastDirectory() {
    return lastDirectory.getAbsolutePath();
  }
}
