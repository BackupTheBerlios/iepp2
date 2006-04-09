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


package iepp.application;

import util.*;
import iepp.Application;
import java.io.File;
import org.apache.fop.messaging.MessageEvent;
import twoxmi.utils.MessagerPDF;

/**
 * <p>TacheExportationPDF : </p>
 *
 * <p>Tache permettant l'exportation pdf: </p>
 *
 * <p>Description: </p>
 *
 */
public class TacheExportationPDF extends MonitoredTaskBase
{
    private TaskMonitorDialog mTask = null;
    private boolean generationReussie = false;
    private File fichierSource = null;
    private File fichierDestination = null;


    public static MessagerPDF messager;


    public TacheExportationPDF()
    {
    }

    /**
     * processingTask
     *
     * @return Object
     */
    protected Object processingTask()
    {
        this.exportPDF();
        return null;
    }

    /**
     * exportPDF
     */
    private void exportPDF()
    {
        //appel de la passerelle et de l'exportation avec les arguments pour le pdf
        Passerelle2Xmi passerelle2Xmi = null;
        try
        {
            passerelle2Xmi = new Passerelle2Xmi();
        }
        catch (Throwable ex)
        {
        }
        passerelle2Xmi.setOutputDir (Application.getApplication().getConfigPropriete("chemin_repertoireDefautExport"));
        if(this.fichierSource != null && this.fichierDestination!=null)
        {
            this.messager= new MessagerPDF();
            this.messager.setTacheExportPDF(this);
            this.generationReussie = (passerelle2Xmi.exporter("iepp", "pdf", "pdf", this.fichierSource, fichierDestination) == 0 );
        }
    }

    //-------------------------------------------
    // Extends MonitoredTaskBase
    //-------------------------------------------
    public void setTask(TaskMonitorDialog task)
    {
        this.mTask = task;
    }

    public void print(String msg)
    {
        this.setMessage(msg);
        if (this.mTask != null)
        {
            this.mTask.forceRefresh();
        }
    }

    /**
     * isGenerationReussie
     *
     * @return boolean
     */
    public boolean isGenerationReussie()
    {
        return this.generationReussie;
    }

    public void setFichierSource(File fichierSource)
    {
        this.fichierSource = fichierSource;
    }

    public void setFichierDestination(File fichierDestination)
    {
        this.fichierDestination = fichierDestination;
    }

    public File getFichierSource()
    {
        return fichierSource;
    }

    public File getFichierDestination()
    {
        return fichierDestination;
    }
}
