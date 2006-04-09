

package util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


//classe permettant de créer un jar des fichiers index
public class JarCreator
{

    public JarCreator()
    {
    }

    public static void jarFolder(String foldername)
    {
        try
        {
            File jarDir = new File(foldername);
            File files[] = jarDir.listFiles();
            File jarFile = new File(foldername + File.separator + "index.jar");
            if(jarFile.exists())
                jarFile.delete();
            BufferedOutputStream bStream = new BufferedOutputStream(new FileOutputStream(jarFile));
            ZipOutputStream zipperStream = new ZipOutputStream(bStream);
            byte bytes[] = new byte[4096];
            for(int i = 0; i < files.length; i++)
            {
                File currentFile = files[i];
                ZipEntry currEntry = new ZipEntry(currentFile.getName());
                zipperStream.putNextEntry(currEntry);
                BufferedInputStream biStream;
                int num;
                for(biStream = new BufferedInputStream(new FileInputStream(currentFile)); biStream.available() > 0; zipperStream.write(bytes, 0, num))
                    num = biStream.read(bytes);

                biStream.close();
                zipperStream.closeEntry();
            }

            zipperStream.close();
            bStream.close();
        }
        catch(Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public static final String INDEX_JAR = "index.jar";
}
