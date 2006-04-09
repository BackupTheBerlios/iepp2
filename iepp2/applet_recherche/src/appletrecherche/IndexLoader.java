package appletrecherche;



import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import appletrecherche.util.ViewerUtilities;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import appletrecherche.util.Translations;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Locale;

public class IndexLoader
{
    public void delFile(String fileName){
          File fich = new File(fileName);
          if (fich.exists()) {
            while (! (fich.delete()))
              delFile( (fich.listFiles())[0].getAbsolutePath());
            }
        }



    public void go()
    {
        String base = m_docBase;

        base = base.substring( 0, base.lastIndexOf( "/" ) );

        base = base + "/index/index.jar";
        try
        {
            URL url = new URL( base );
            url = ViewerUtilities.handleURLForUNC( url );
            BufferedInputStream inputS = new BufferedInputStream( url.openStream() );
            //System.out.println(m_indexFolder);
            delFile(m_indexFolder);
            File index = new File( m_indexFolder + File.separator + "index.jar" );

            index.getParentFile().mkdirs();
            BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( index ) );
            byte[] read = new byte[128];
            int len = 128;
            while ( ( len = inputS.read( read ) ) > 0 )
            {
                out.write( read, 0, len );
            }

            out.flush();
            out.close();
            inputS.close();
            String newIndexFolderName = m_indexFolder;
            //System.out.println(m_indexFolder);
            ZipInputStream zStream = new ZipInputStream(new FileInputStream(m_indexFile));
            for(ZipEntry zEntry = zStream.getNextEntry(); zEntry != null; zEntry = zStream.getNextEntry())
            {
                File file = new File(newIndexFolderName, zEntry.getName());
                if(zEntry.isDirectory())
                {
                    file.mkdir();
                } else
                {
                    file.getParentFile().mkdirs();
                    FileOutputStream outs = new FileOutputStream(file);
                    while(zStream.available() > 0)
                    {
                        int num = zStream.read(read);
                        if(num > 0)
                            outs.write(read, 0, num);
                    }
                    outs.close();
                }
            }
            zStream.close();
            FileReader readVersion = new FileReader(m_indexFolder + "/langue.txt");
            BufferedReader bRead = new BufferedReader(readVersion);
            String langue = bRead.readLine();
            bRead.close();
            readVersion.close();
            if(langue.equals(Translations.FR))
            {
                Locale.setDefault(Locale.FRENCH);
            }
            else
            {
                Locale.setDefault(Locale.ENGLISH);

            }
            //System.out.println(Locale.getDefault());
        }
        catch ( Exception e1 )
        {
            e1.printStackTrace();
        }
    }

    public IndexLoader( String documentBase, String indexFolder, String indexFile )
    {
        m_docBase = documentBase;
        m_indexFolder = indexFolder;
        m_indexFile = indexFile;
    }
    protected String m_docBase;
    protected String m_indexFolder;
    protected String m_indexFile;
    protected String indexArchive;
    protected int fileSize;
}
