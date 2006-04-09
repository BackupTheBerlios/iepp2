

package appletrecherche.util;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ViewerUtilities
{

    private ViewerUtilities()
    {
    }

    public static String handleNetscapeFilePrefix(String filename)
    {
        String convertedName = null;
        if(filename.startsWith("file:///"))
            convertedName = "file://" + filename.substring("file:///".length(), filename.length());
        else
            convertedName = filename;
        return convertedName;
    }

    public static URL handleURLForUNC(URL originalUrl)
    {
        URL newUrl = null;
        try
        {
            if(originalUrl.toString().startsWith("file://"))
            {
                String extractedString = originalUrl.toString().substring(5);
                newUrl = new URL("file", "", "///" + extractedString);
            } else
            {
                newUrl = originalUrl;
            }
        }
        catch(MalformedURLException mue)
        {
            newUrl = originalUrl;
        }
        return newUrl;
    }

    public static String convertFilename(String filename)
    {
        String convertedName = null;
        String tempName = null;
        if(filename.startsWith("file:/"))
        {
            tempName = filename.substring("file:/".length(), filename.length());
            int index = tempName.indexOf(":");
            if(index == -1)
                tempName = "/" + tempName;
        } else
        {
            tempName = filename;
        }
        convertedName = tempName.replace("/".charAt(0), File.separator.charAt(0));
        return convertedName;
    }

    public static String convertFilenameToUNC(String filename)
    {
        String convertedName = filename.replace(File.separator.charAt(0), "/".charAt(0));
        String finalName = convertFileSeparator(convertedName);
        return finalName;
    }

    public static String convertToUNC(String url)
    {
        String convertedUrl = null;
        if(url.startsWith("file://"))
            convertedUrl = "file:/" + url.substring("file://".length(), url.length());
        else
            convertedUrl = url;
        return convertedUrl;
    }

    public static String convertFileSpacing(String filename)
    {
        int start = 0;
        String newFilename = "";
        int index;
        for(; start < filename.length(); start = index + 3)
        {
            index = filename.indexOf("%20", start);
            if(index == -1)
                index = filename.length();
            newFilename = newFilename + filename.substring(start, index);
            newFilename = newFilename + " ";
        }

        newFilename = newFilename.trim();
        return newFilename;
    }

    public static String convertFileSeparator(String filename)
    {
        int start = 0;
        String newFilename = "";
        int index;
        for(; start < filename.length(); start = index + 3)
        {
            index = filename.indexOf("%5C", start);
            if(index == -1)
            {
                newFilename = filename.trim();
                break;
            }
            newFilename = newFilename + filename.substring(start, index);
            newFilename = newFilename + "/";
        }

        newFilename = newFilename.trim();
        String windowsFileSeparator = "\\";
        if(!File.separator.equals(windowsFileSeparator) && File.separator.equals("/"))
            newFilename = newFilename.replace(windowsFileSeparator.charAt(0), "/".charAt(0));
        return newFilename;
    }

    public static Vector getFileList(String directory, String suffix)
    {
        if(directory.startsWith("http"))
            return getFileListFromRemote(directory, suffix);
        String tempDir = convertFilename(directory);
        File documentDirectory = new File(convertFileSpacing(tempDir));
        String fileNameList[] = documentDirectory.list();
        Vector finalList = new Vector();
        for(int i = 0; i < fileNameList.length; i++)
            if(fileNameList[i].endsWith(suffix))
                finalList.addElement(fileNameList[i]);

        return finalList;
    }

    private static Vector getFileListFromRemote(String directory, String suffix)
    {
        Vector filenames = new Vector();
        try
        {
            URL remoteUrl = new URL(directory);
            InputStream inStream = remoteUrl.openStream();
            StringBuffer result = new StringBuffer();
            int c;
            while((c = inStream.read()) != -1)
                result.append((char)c);
            inStream.close();
            String directoryResult = result.toString();
            directoryResult = directoryResult.replaceAll("href", "HREF");
            directoryResult = directoryResult.replaceAll("</a", "</A");
            directoryResult = directoryResult.replaceAll("<tt>", "");
            directoryResult = directoryResult.replaceAll("</tt>", "");
            directoryResult = directoryResult.replaceAll("<TT>", "");
            directoryResult = directoryResult.replaceAll("</TT>", "");
            StringTokenizer lineTokenizer = new StringTokenizer(directoryResult);
            Vector lines = new Vector();
            int i;
            try
            {
                do
                {
                    lineTokenizer.nextToken("HREF");
                    String begin = lineTokenizer.nextToken(">");
                    String line = lineTokenizer.nextToken(">");
                    int index = line.indexOf("</A");
                    if(index != -1 && index != 0)
                    {
                        String filename = line.substring(0, index);
                        lines.addElement(filename);
                    } else
                    {
                        lines.addElement(line);
                    }
                } while(true);
            }
            catch(NoSuchElementException nosuchelementexception)
            {
                i = 0;
            }
            for(; i < lines.size(); i++)
            {
                String line = (String)lines.elementAt(i);
                if(line.endsWith(suffix))
                    filenames.addElement(line);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return filenames;
    }

    public static final String UNC_FILE_PREFIX = "file:/";
    public static final String ALTERNATE_UNC_FILE_PREFIX = "file://";
    public static final String NETSCAPE_UNC_FILE_PREFIX = "file:///";
    public static final String UNC_FILE_ONLY = "file:";
    public static final String UNC_SEPARATOR = "/";
}
