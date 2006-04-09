package appletrecherche;

import java.io.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.document.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;
import java.applet.Applet;
import java.text.MessageFormat;
import appletrecherche.util.Translations;

public class Recherche
{
    public static final String REPERTOIRE_INDEX = "index";

    private String requete;

    public Recherche( String _requete )
    {
        this.requete = _requete;
    }

    public String executer( String cheminApplet )
    {
        String output = null;
        StringBuffer result = new StringBuffer( "<body><font face=\"Arial Unicode MS\">" );
        try
        {
            //on récupère le repertoire courant
            cheminApplet = cheminApplet.substring( 0, cheminApplet.lastIndexOf( "/" ) );
            String cheminSite = cheminApplet.substring( 0, cheminApplet.indexOf( "/applet" ) );

            String cheminIndex = System.getProperty( "user.home" ) + File.separator + ".apes2" + File.separator + REPERTOIRE_INDEX;

            //System.out.println("Chemin Applet " + cheminApplet);

            Searcher searcher = new IndexSearcher( cheminIndex );

            Analyzer analyzer = new StandardAnalyzer();

            Query query = QueryParser.parse( this.requete, "contents", analyzer );
            //System.out.println( "Searching for: " + query.toString( "contents" ) );

            Hits hits = searcher.search( query );
            int nbReponses = hits.length();
            //System.out.println( hits.length() + " total matching documents" );

            result.append( nbReponses + " " + Translations.getText("Resultat") + " " + this.requete + "<br>" );

            int numIndex = 1;
            //on affiche ds un 1er tps les elements avec un titre
            for ( int i = 0; i < nbReponses; i++ )
            {
                Document doc = hits.doc( i );
                String titre = doc.get( "title" );
                if ( !titre.equals( "" ) )
                {
                    String url = doc.get( "url" );
                    if ( url != null )
                    {
                        url = cheminSite + "/" + url;
                        result.append( numIndex + " <b><a href=\"" + url + "\">" + titre + "</a></b><br>" );
                        result.append( "   - " + url + "<br>" );
                    }
                    else
                    {
                        result.append( numIndex + ". " + Translations.getText("ResultatVide") );
                    }
                    numIndex++;
                }
            }
            //on affiche le reste
            for ( int i = 0; i < nbReponses; i++ )
            {
                Document doc = hits.doc( i );
                String titre = doc.get( "title" );
                if ( titre.equals( "" ) )
                {
                    String url = doc.get( "url" );
                    if ( url != null )
                    {
                        url = cheminSite + "/" + url;
                        result.append( numIndex + " <b><a href=\"" + url + "\">" + url + "</a></b><br>" );
                        result.append( "   - " + url + "<br>" );
                    }
                    else
                    {
                        result.append( numIndex + ". " + Translations.getText("ResultatVide") );
                    }
                    numIndex++;
                }
            }
            searcher.close();

        }
        catch ( Exception e )
        {
            System.out.println( " caught a " + e.getClass() + "\n with message: " + e.getMessage() );
        }
        output = result.toString();
        return output;
    }

    public void setRequete( String requete )
    {
        this.requete = requete;
    }

    public String getRequete()
    {
        return requete;
    }
}
