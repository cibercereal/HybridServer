/**
 *  HybridServer
 *  Copyright (C) 2017 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLConfigurationLoader {

	private int httpPort;
	private int numClients;
	private String webServiceURL;

	private String dbUser;
	private String dbPass;
	private String dbURL;

	private List<ServerConfiguration> server = new ArrayList<ServerConfiguration>();

	public Configuration load(File xmlFile) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document document = builder.parse(xmlFile);

		Node http = (Node) (XPathFactory.newInstance().newXPath().evaluate("/configuration/connections/http", document,
				XPathConstants.NODE));
		Node numCli = (Node) (XPathFactory.newInstance().newXPath().evaluate("/configuration/connections/numClients",
				document, XPathConstants.NODE));
		Node webSer = (Node) (XPathFactory.newInstance().newXPath().evaluate("/configuration/connections/webservice",
				document, XPathConstants.NODE));
		Node user = (Node) (XPathFactory.newInstance().newXPath().evaluate("/configuration/database/user", document,
				XPathConstants.NODE));
		Node pass = (Node) (XPathFactory.newInstance().newXPath().evaluate("/configuration/database/password", document,
				XPathConstants.NODE));
		Node url = (Node) (XPathFactory.newInstance().newXPath().evaluate("/configuration/database/url", document,
				XPathConstants.NODE));

		NodeList list = document.getElementsByTagName("server");

		for (int i = 0; i < list.getLength(); i++) {
			String name = list.item(i).getAttributes().getNamedItem("name").getNodeValue();
			String wsdl = list.item(i).getAttributes().getNamedItem("wsdl").getNodeValue();
			String namespace = list.item(i).getAttributes().getNamedItem("namespace").getNodeValue();
			String service = list.item(i).getAttributes().getNamedItem("service").getNodeValue();
			String httpAddress = list.item(i).getAttributes().getNamedItem("httpAddress").getNodeValue();

			ServerConfiguration serverConfiguration = new ServerConfiguration(name, wsdl, namespace, service,
					httpAddress);
			server.add(serverConfiguration);
		}

		httpPort = Integer.parseInt(http.getTextContent());
		numClients = Integer.parseInt(numCli.getTextContent());
		webServiceURL = webSer.getTextContent();
		dbUser = user.getTextContent();
		dbPass = pass.getTextContent();
		dbURL = url.getTextContent();

		Configuration conf = new Configuration(httpPort, numClients, webServiceURL, dbUser, dbPass, dbURL, server);

		return conf;

		
	}

	

}
