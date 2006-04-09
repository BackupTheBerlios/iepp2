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


package twoxmi.utils;

import iepp.application.TacheExportationPDF;
import org.apache.fop.messaging.MessageEvent;
import org.apache.fop.messaging.MessageListener;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import iepp.application.ageneration.ArbreGeneration;
import iepp.application.CExporterProcessusPDF;

/**
 * <p>MessagerPDF : </p>
 *
 * <p>Classe utilisée par le MessageHandler de FOP pour afficher des messages
 * dans la TaskMonitorDialog de TacheExportationPDF
 * </p>
 */
public class MessagerPDF extends TacheExportationPDF implements MessageListener, ErrorListener
{
    //tache en cours
    private TacheExportationPDF tacheExportPDF;

    public MessagerPDF()
    {
    }

    /**
     * Methode implémentée de MessageListener
     * @param messageEvent MessageEvent
     */
    public void processMessage(MessageEvent messageEvent)
    {
        //System.out.println(messageEvent.getMessage());
        CExporterProcessusPDF.listeMessages.add(messageEvent.getMessage());
        this.tacheExportPDF.print(messageEvent.getMessage());
    }

    /**
     * Methodes utilisées par PDFLogger pour afficher des commentaires
     * @param _message String
     */
    public void print(String _message)
    {
        //System.out.println(_message);
        CExporterProcessusPDF.listeMessages.add(_message);
        this.tacheExportPDF.print(_message);
    }

    public void warning(String _message)
    {
        //System.out.println(_message);
        CExporterProcessusPDF.listeMessages.add(_message);
        this.tacheExportPDF.print(_message);
    }

    public void error(String _message)
    {
        //System.out.println(_message);
        CExporterProcessusPDF.listeMessages.add(_message);
        this.tacheExportPDF.print(_message);
    }

    public void fatalError(String _message)
    {
        //System.out.println(_message);
        CExporterProcessusPDF.listeMessages.add(_message);
        CExporterProcessusPDF.erreurFatale = true;
        this.tacheExportPDF.print(_message);
    }

    public void setTacheExportPDF(TacheExportationPDF _tacheExportPDF)
    {
        this.tacheExportPDF = _tacheExportPDF;
    }

    /**
     * Methode implémentée de ErrorListener
     * @param exception TransformerException
     * @throws TransformerException
     */
    public void warning(TransformerException exception) throws TransformerException
    {
        //System.out.println(exception.getMessage());
        CExporterProcessusPDF.listeMessages.add(exception.getMessage());
        this.tacheExportPDF.print(exception.getMessage());
    }

    /**
     * Methode implémentée de ErrorListener
     * @param exception TransformerException
     * @throws TransformerException
     */
    public void error(TransformerException exception) throws TransformerException
    {
        //System.out.println(exception.getMessage());
        CExporterProcessusPDF.listeMessages.add(exception.getMessage());
        this.tacheExportPDF.print(exception.getMessage());
    }

    /**
     * Methode implémentée de ErrorListener
     * @param exception TransformerException
     * @throws TransformerException
     */
    public void fatalError(TransformerException exception) throws TransformerException
    {
        //System.out.println(exception.getMessage());
        CExporterProcessusPDF.listeMessages.add(exception.getMessage());
        CExporterProcessusPDF.erreurFatale = true;
        this.tacheExportPDF.print(exception.getMessage());
    }
}
