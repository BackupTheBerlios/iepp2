package Test.JUnit.iepp.infrastructure.jsx;

import iepp.Projet;
import iepp.domaine.DefinitionProcessus;
import iepp.infrastructure.jsx.ChargeurDP;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import util.MonitoredTaskBase;
import util.Vecteur;

import junit.framework.TestCase;

public class ChargeurDPTest extends TestCase {
	private File mFile = null;
	private File fichier;
	private ChargeurDP cdp;
	private Projet pc;
	private DefinitionProcessus dp;
	private Vector elts,liens,liste;
	private int derIDused;
	private HashMap hm;
	private MonitoredTaskBase mtb;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		hm = new HashMap(0);
		liste=new Vector(0);
		fichier = new File("test.txt");
		cdp = new ChargeurDP(fichier);
		dp = new DefinitionProcessus();
		dp.setAuteur("Auteur");
		dp.setCommentaires("Commentaires");
		dp.setEmailAuteur("email");
		dp.setFicContenu("fichier");
		dp.setListeAGenerer(liste);
		dp.setListeAssociationSRole(hm);
		dp.setNomDefProc("nom");
		dp.setNomElement("chaine",1,2);
		dp.setPiedPage("piedpage");
		dp.setRepertoireGeneration("repgen");
		dp.setRepertoireGenerationNotChanged("repgenNC");
		elts = new Vector(0);
		liens = new Vector(0);
		derIDused=0;
		pc = new Projet(dp,elts,liens,derIDused);
		cdp.setprojetCharge(pc);
		mFile=(File)((cdp.getChargeurDPAttributs()).get(0));
		
		
		
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'iepp.infrastructure.jsx.ChargeurDP.chargerDP()'
	 */
	public void testChargerDP() {
	
	}

	/*
	 * Test method for 'iepp.infrastructure.jsx.ChargeurDP.getProjetCharge()'
	 */
	public void testGetProjetCharge() {
		
		assertEquals(cdp.getProjetCharge(),pc);
	}
	
	
	
	
	
	
	private DataInputStream findData(String fileName) throws IOException
	{	
		ZipInputStream zipFile = new ZipInputStream( new FileInputStream(new File(mFile.getAbsolutePath())));
		ZipEntry zipEntry = zipFile.getNextEntry();
		while( zipEntry != null )
		{
			DataInputStream data = new DataInputStream( new BufferedInputStream(zipFile) );
			if( zipEntry.getName().equals(fileName) )
			{
				return data;
			}
			else
			{
				zipEntry = zipFile.getNextEntry();
			}
		}
		zipFile.close();
		return null;
	}

	}
