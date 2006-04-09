package appletrecherche;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import javax.swing.event.HyperlinkListener;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import javax.swing.JApplet;
import javax.swing.JEditorPane;
import javax.swing.AbstractButton;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URLDecoder;
import java.io.*;
import appletrecherche.util.ViewerUtilities;
import appletrecherche.util.Translations;

public class AppletRecherche extends JApplet
{

    public class SearchButtonListener implements ActionListener
    {

        public void actionPerformed( ActionEvent e )
        {
            getContentPane().setCursor( new Cursor( 3 ) );
            try
            {
                doWork( e );
            }
            finally
            {
                getContentPane().setCursor( new Cursor( 0 ) );
            }
        }

        protected void doWork( ActionEvent e )
        {
            lancerRecherche();
        }

        public SearchButtonListener( AbstractButton b )
        {
            b.addActionListener( this );
        }
    }

    public class LinkListener implements HyperlinkListener
    {
        public void hyperlinkUpdate( HyperlinkEvent e )
        {
            if ( _target == null )
            {
                _target = getParameter( "target" );
            }
            try
            {
                if ( e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED )
                {
                    //System.out.println(e.getDescription());

                    URL url = new URL( e.getDescription() );

                    getAppletContext().showDocument( url, _target );
                }
            }
            catch ( Exception e2 )
            {
                e2.printStackTrace();
            }
        }

        String _target;

        public LinkListener()
        {
            this._target = "basefrm";
        }
    }

    public AppletRecherche()
    {
        _enabled = false;
    }

    public void init()
    {
        super.init();
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch ( Exception e1 )
        {
            e1.printStackTrace();
        }
        _hitsPerPage = 10;
    }

    public void start()
    {
        super.start();



        super.start();
        String userFolder = System.getProperty( "user.home" ) + File.separator + ".apes2";
        String indexFolder = userFolder + File.separator + "index";
        _indexPath = indexFolder;
        String indexFile = indexFolder + File.separator + "index.jar";
        String dBase = getDocumentBase().toString();
        IndexLoader loader = new IndexLoader( dBase, indexFolder, indexFile );
        loader.go();
        createAdvancedSearchPane();
        getContentPane().add( _advancedPane );
        getContentPane().validate();
        _champRecherche.requestFocus();
        _enabled = true;


    }


    protected void createAdvancedSearchPane()
    {
        _advancedPane = new JPanel();
        GridBagLayout gLayout = new GridBagLayout();
        GridBagConstraints gConstraints = new GridBagConstraints();
        GridBagLayout outerLayout = new GridBagLayout();
        _advancedPane.setLayout( outerLayout );

        gConstraints.fill = GridBagConstraints.BOTH;
        gConstraints.insets = new Insets( 2, 8, 2, 8 );

        JLabel topLabel = new JLabel( Translations.getText("titreFenetre") );
        outerLayout.setConstraints( topLabel, gConstraints );
        _advancedPane.add( topLabel );
        JPanel interiorPane = new JPanel();
        interiorPane.setBackground( Color.white );
        interiorPane.setLayout( gLayout );

        JLabel searchLabel = new JLabel( Translations.getText("champRechercher") );
        gConstraints.weightx = 1.0D;
        gConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gLayout.setConstraints( searchLabel, gConstraints );
        interiorPane.add( searchLabel );

        _champRecherche = new JTextField();
        gConstraints.fill = GridBagConstraints.HORIZONTAL;
        gConstraints.gridwidth = GridBagConstraints.RELATIVE;
        gLayout.setConstraints( _champRecherche, gConstraints );
        interiorPane.add( _champRecherche );

        _searchButton = new JButton( Translations.getText("boutonRechercher") );
        getRootPane().setDefaultButton( _searchButton );
        gConstraints.weightx = 0.2;
        gLayout.setConstraints( _searchButton, gConstraints );
        interiorPane.add( _searchButton );
        SearchButtonListener slisten = new SearchButtonListener( _searchButton );

        gConstraints.gridy = 1;

        interiorPane.setBorder( BorderFactory.createEtchedBorder() );
        gConstraints.anchor = 10;
        gConstraints.weightx = 1.0D;
        gConstraints.weighty = 0.02D;
        gConstraints.gridx = 0;
        gConstraints.gridy = 1;
        gConstraints.ipady = 3;
        gConstraints.fill = 1;
        outerLayout.setConstraints( interiorPane, gConstraints );
        _advancedPane.add( interiorPane );

        _resultsPane = new JEditorPane();
        _resultsPane.setEditable( false );
        String encoding = System.getProperty( "file.encoding" );
        if ( encoding.equals( "MS932" ) )
        {
            encoding = "Shift_jis";
        }
        _resultsPane.setContentType( "text/html; charset=" + encoding );
        LinkListener listenToLinks = new LinkListener();
        _resultsPane.addHyperlinkListener( listenToLinks );
        JScrollPane resultsScrollPane = new JScrollPane( _resultsPane );
        gConstraints.gridy = 3;
        gConstraints.weighty = 1.0D;
        outerLayout.setConstraints( resultsScrollPane, gConstraints );
        _advancedPane.add( resultsScrollPane );
    }

    protected void updateOutput( String output )
    {
        _resultsPane.setText( output );
        _resultsPane.setCaretPosition( 0 );
    }

    protected void lancerRecherche()
    {
        String requete = _champRecherche.getText();
        Recherche recherche = new Recherche( requete );
        _output = recherche.executer( this.getDocumentBase().toString() );
        updateOutput( _output );
    }

    protected JPanel _advancedPane;
    protected JTextField _champRecherche;
    protected JEditorPane _resultsPane;
    protected JButton _searchButton;
    protected String _output;
    protected int _numHits;
    protected int _currentPage;
    protected int _hitsPerPage;
    protected boolean _enabled;
    protected String _indexPath;
}
