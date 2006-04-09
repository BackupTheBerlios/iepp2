package util;

import java.io.DataInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class xpath {

	private static Document document;
	private static XPath xpath;
	
	public xpath(DataInputStream data) {
		DocumentBuilderFactory fabriqueDOM = DocumentBuilderFactory.newInstance();
		DocumentBuilder analyseur;
		try {
			analyseur = fabriqueDOM.newDocumentBuilder();
			document = analyseur.parse(data);
			XPathFactory fabriqueXPath = XPathFactory.newInstance();
			xpath = fabriqueXPath.newXPath();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
	}

	public NodeList ListeNoeuds(Object ob, String expression){
		// Renvoit la liste de noeud correspondant a l'expression xpath
		NodeList liste = null;
		try{
			//?valuation de l'expression XPath
			liste = (NodeList)xpath.evaluate(expression, ob ,XPathConstants.NODESET);
		}catch(XPathExpressionException xpee){
			xpee.printStackTrace();
		}
		return liste;
	}
	
	public Node Noeud(Object ob, String expression){
		// Renvoit le noeud correspondant a l'expression xpath
		Node noeud = null;
		try{
			//?valuation de l'expression XPath
			noeud = (Node)xpath.evaluate(expression, ob ,XPathConstants.NODE);
		}catch(XPathExpressionException xpee){
			xpee.printStackTrace();
		}
		return noeud;
	}

	public String valeur(Object ob, String expression){
		// Renvoit la valeur d'une expression xpath
		String valeur = "";
		try{
			//?valuation de l'expression XPath
			XPathExpression e = xpath.compile(expression);
			valeur = e.evaluate(ob);
		}catch(XPathExpressionException xpee){
			xpee.printStackTrace();
		}
		return valeur;
	}

	public Document getDocument() {
		return document;
	}
}
