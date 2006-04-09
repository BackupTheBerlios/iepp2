package util.index;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;

public class IndexHTML
{
    private static boolean deleting=false; // true during deletion pass
    private static IndexReader reader; // existing index
    private static IndexWriter writer; // new index being built

    private String index;
    private boolean creation;
    private File root;

    public IndexHTML(File f, String ind, boolean cr)
    {
        this.root=f;
        this.index=ind;
        this.index=this.root.toString()+"\\applet\\"+this.index;
        //System.out.println(this.index);
        this.creation=cr;
    }

    public void executer()
    {
        try
        {
            //Date start=new Date();

            writer=new IndexWriter(index, new StandardAnalyzer(), creation);
            writer.maxFieldLength=1000000;

            indexDocs(root); // add new docs

            //System.out.println("Optimisation de l'index...");
            writer.optimize();
            writer.close();

            //Date end=new Date();

            //System.out.print(end.getTime()-start.getTime());
            //System.out.println(" total milliseconds");
        } catch(Exception e)
        {
            System.out.println(" caught a "+e.getClass()+"\n with message: "+e.getMessage());
        }

    }

    private static void indexDocs(File file) throws Exception
    {

        if(file.isDirectory() && !file.getName().equals("contenu") && !file.getName().equals("applet") && !file.getName().equals("styles"))
        { // if a directory
            String[] files=file.list(); // list its files
            Arrays.sort(files); // sort the files
            for(int i=0; i<files.length; i++)
            { // recursively index them
                indexDocs(new File(file, files[i]));
            }

        } else
        {
            if(file.getPath().endsWith(".html")||file.getPath().endsWith(".htm")||file.getPath().endsWith(".txt"))
            { // index .html files,  index .htm files,  index .txt files

                // creating a new index
                Document doc=HTMLDocument.Document(file);
                //System.out.println("adding "+doc.get("url"));
                writer.addDocument(doc); // add docs unconditionally
            }
        }
    }
}
