package twoxmi.utils;

import javax.xml.transform.sax.SAXResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Iterator;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.Toolkit;
import java.util.Map.Entry;

//Avalon
import org.apache.avalon.framework.logger.Logger;

//FOP
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.fop.messaging.MessageHandler;
import util.TaskMonitorDialog;

/**
 * Classe servant de bibliothèque d'attributs communs ou de services
 * élémentaires
 *
 * <p>Copyright: NoCopyright 2004 ;-) No right reserved. </p>
 *
 * <p>Company: SDTM</p>
 *
 * @author chaoukhi MHAMEDI
 * @version 1.1
 */
public class Utilitaire
{
    /**
     * A utiliser au lieu de <code>System.getProperty("file.separator")</code>
     * Utilisé dans le chemin d'un fichier (sépare les répertoires)
     */
    public static final String FS = System.getProperty("file.separator");

    /**
     * A utiliser au lieu de <code>System.getProperty("line.separator")</code>
     * Utilisé dans les fichiers pour défnir la fin de la ligne
     */
    public static final String LS = System.getProperty("line.separator");

    /**
     * Méthode pour compter le nombre de fichiers dans un répertoire (sous répertoires
     * inclus)
     *
     * @param _repertoire Répertoire en question
     * @return Retourne le nombre de fichiers dans le répertoire
     */
    public static int countFilesIn(File _repertoire)
    {
        //Logger.entering (new Object[]{}) ;
        int resultat = 0;
        if (_repertoire.exists() && _repertoire.isDirectory())
        {
            File[] fichiers = _repertoire.listFiles();
            for (int i = 0; i < fichiers.length; i++)
            {
                if (fichiers[i].isFile())
                {
                    resultat++;
                }
                else if (fichiers[i].isDirectory())
                {
                    resultat += Utilitaire.countFilesIn(fichiers[i]);
                }
            }
        }
        return resultat;
    }

    /**
     * copyFile
     *
     * @param file File
     * @param string String
     * @return File
     */
    public static File copyFile(File _original, File _parentDirectory)
    {
        File resultat = null;
        if (!_parentDirectory.exists() && !_original.getParent().equalsIgnoreCase(_parentDirectory.getAbsolutePath()))
        {
            if (_parentDirectory.mkdir())
            {
                resultat = new File(_parentDirectory.getAbsolutePath() + Utilitaire.FS + _original.getName());
                FileInputStream fis = null;
                FileOutputStream fos = null;
                try
                {
                    fis = new FileInputStream(_original);
                    fos = new FileOutputStream(resultat);
                    byte[] buffer = new byte[1024];
                    int lu; // octets lus
                    while ( (lu = fis.read(buffer)) != -1)
                    {
                        fos.write(buffer, 0, lu);
                    }
                    fis.close();
                    fos.close();
                }
                catch (FileNotFoundException _fileNotFoundException)
                {
                    _fileNotFoundException.printStackTrace();
                }
                catch (IOException _ioException)
                {
                    _ioException.printStackTrace();
                }
            }
        }
        return resultat;
    }

    /**
     * Supprime un répertoire en supprimant récursivement les sous répertoires
     * et les fichiers contenus
     *
     * @param _chemin Chemin du répertoire à supprimer
     * @param _deleteRoot Supprime le répertoire si vrai, que le contenu sinon
     * @return Retourne le nombre de fichiers ou répertoires non supprimé <br>
     * Retourne 0 si la suppression s'est bien déroulée
     * @version 2.0 Le paramètre de sortie passe de boolean à int
     */
    public static int deleteDirectory(File _chemin, boolean _deleteRoot)
    {
        //Logger.entering (new Object[]{_chemin, new Boolean (_deleteRoot)}) ;
        int resultat = 0; // nombre d'erreurs
        if (_chemin != null && _chemin.exists() && _chemin.isDirectory())
        {
            File[] fichiers = _chemin.listFiles();
            if (fichiers != null && fichiers.length > 0)
            {
                for (int i = 0; i < fichiers.length; i++)
                {
                    if (fichiers[i].isDirectory())
                    {
                        resultat += deleteDirectory(fichiers[i], true); // on vire tout!
                    }
                    else // c'est un fichier
                    {
                        if (!fichiers[i].delete())
                        {
                            resultat++;
                        }
                    }
                }
            }

            if (_deleteRoot)
            {
                if (!_chemin.delete()) // le dossier est vide (normalement)
                {
                    resultat++;
                }
            }
        }
        else // paramètre chemin non valide
        {
            resultat++;
        }

        return resultat;
    } // fin deleteDirectory

    /**
     * Génération de fichier à partir de fichiers XML et XSL
     * Déclarer cette méthode synchronized permet de gagner en performance
     * lorsque plusieurs transformations sont lancées simultanément
     *
     * @param _formatCible Le format cible (xml, pdf, ...)
     * @param _xmlFileName Le fichier XML
     * @param _xslFileName Le fichier XSL
     * @param _outputName Fichier résultat
     * @param _param Paramètres pour la transformation
     *
     */
    //Sébastien il faudrait ajouté à la fonction un paramètre messager pour recupérer les messages de la transformation pdf
    //pour l'instant un messager de iepp est directement appelé
    public static synchronized void transformationXSL(String _formatCible, String _xmlFileName, String _xslFileName, String _outputName, Map _param) throws TransformerFactoryConfigurationError, TransformerException, TransformerConfigurationException, FileNotFoundException
    {
        Tracer.entering(new Object[]
                        {_xmlFileName, _xslFileName, _outputName, _param});

        StreamSource source = null;
        StreamResult result = null;
        try
        {
            File fileIn = new File(_xmlFileName);
            File xslFile = new File(_xslFileName);
            File fileOut = new File(_outputName);

            //mise en place du processeur de transformation
            TransformerFactory factory = TransformerFactory.newInstance();
            Templates template = factory.newTemplates(new StreamSource(new FileInputStream(xslFile)));
            Transformer transformer = template.newTransformer();

            //creation du flux devant être tranformé
            FileInputStream fis = new FileInputStream(fileIn);
            source = new StreamSource(fis);

            //creation du flux de sortie
            FileOutputStream fos = new FileOutputStream(fileOut);
            result = new StreamResult(fos);

            // récupération des paramètres
            if (_param != null)
            {
                for (Iterator it = _param.entrySet().iterator(); it.hasNext(); )
                {
                    Map.Entry entry = (Map.Entry) it.next();
                    String value = (String) entry.getValue();
                    transformer.setParameter(entry.getKey().toString(), value);
                }
            }
            //selon le format de sortie
            if ("xml".equalsIgnoreCase(_formatCible))
            {
                transformer.transform(source, result);
            }
            else if ("pdf".equalsIgnoreCase(_formatCible))
            {
                //Construction driver FOP
                Driver driver = new Driver();

                //affichage des messages de transformation desactiver pour le moment
                //a voir pour affichage erreurs
                Logger logger = new PDFLogger(PDFLogger.LEVEL_INFO);
                driver.setLogger(logger);
                MessageHandler.setScreenLogger(logger);
                MessageHandler.setOutputMethod(MessageHandler.EVENT);
                MessageHandler.addListener(iepp.application.TacheExportationPDF.messager);

                //Sélection du Renderer de sortie (ici pdf)
                driver.setRenderer(Driver.RENDER_PDF);
                driver.setOutputStream(fos);

                //le resultat de la transformation xsl est traitée par FOP
                Result res = new SAXResult(driver.getContentHandler());

                //débute transformation XSLT et FOP
                transformer.setErrorListener(iepp.application.TacheExportationPDF.messager);
                transformer.transform(source, res);
            }

        }
        catch (FileNotFoundException _fileNotFoundException)
        {
            throw _fileNotFoundException;
        }
        catch (TransformerConfigurationException _transformerConfigurationException)
        {
            // erreur dans le fichier xsl
            throw _transformerConfigurationException;
        }
        catch (TransformerException _transformerException)
        {
            // erreur durant la transformation
            throw _transformerException;
        }
        catch (Throwable _ex)
        {
            iepp.application.TacheExportationPDF.messager.fatalError(_ex.toString());
            _ex.printStackTrace();
        }
        finally
        {
            try
            {
                result.getOutputStream ().close () ;
                source.getInputStream ().close () ;
            }
            catch (IOException _iOException)
            {
                _iOException.printStackTrace();
            }
        }
    }


    /**
     * Emet un bip
     */
    public static void bip()
    {
        Toolkit.getDefaultToolkit().beep();
    }
}
