package es.uvigo.esei.dai.hybridserver;

import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.Oneway;
import javax.jws.soap.SOAPBinding;

@WebService (endpointInterface = "es.uvigo.esei.dai.hybridserver.webServices",
targetNamespace= "http://hybridserver.dai.esei.uvigo.es/",
serviceName = "HybridServerService")
public class webServicesImp implements webServices{
	
	private HtmlDBDAO htmlDAO;
	private XmlDBDAO xmlDAO;
	private XsdDBDAO xsdDAO;
	private XsltDBDAO xsltDAO;
	
	public webServicesImp(String dburl, String dbuser, String dbpassword  ) {
		this.htmlDAO = new HtmlDBDAO(dburl,dbuser,dbpassword);
		this.xmlDAO = new XmlDBDAO(dburl,dbuser,dbpassword);
		this.xsdDAO = new XsdDBDAO(dburl,dbuser,dbpassword);
		this.xsltDAO = new XsltDBDAO(dburl,dbuser,dbpassword);
		
	}

	@Override
	public List<String> getUUIDsHTML() {
		return htmlDAO.getList();
	}

	@Override
	public List<String> getUUIDsXML() {
		return xmlDAO.getList();
	}

	@Override
	public List<String> getUUIDsXSD() {
		return xsdDAO.getList();
	}

	@Override
	public List<String> getUUIDsXSLT() {
		return xsltDAO.getList();
	}

	@Override
	public String getContentHTML(String uuid) {
		
		return htmlDAO.getContentPage(uuid);
	}

	@Override
	public String getContentXML(String uuid) {
		return xmlDAO.getContentPage(uuid);
	}

	@Override
	public String getContentXSD(String uuid) {
		return xsdDAO.getContentPage(uuid);
	}

	@Override
	public String getContentXSLT(String uuid) {
		return xsltDAO.getContentPage(uuid);
	}

	@Override
	public String getXSDAsoc(String xslt) {
		return xsltDAO.getXsd(xslt);
	}
	
}
