package es.uvigo.esei.dai.hybridserver;

import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.Oneway;
import javax.jws.soap.SOAPBinding;

@WebService

public interface webServices {
	@WebMethod
	public List<String> getUUIDsHTML();
	@WebMethod
	public List<String> getUUIDsXML();
	@WebMethod
	public List<String> getUUIDsXSD();
	@WebMethod
	public List<String> getUUIDsXSLT();
	@WebMethod
	public String getContentHTML(String uuid);
	@WebMethod
	public String getContentXML(String uuid);
	@WebMethod
	public String getContentXSD(String uuid);
	@WebMethod
	public String getContentXSLT(String uuid);
	@WebMethod
	public String getXSDAsoc(String xslt);
	
}
