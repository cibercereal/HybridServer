package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HybridService implements Runnable {

	private Socket socket;
	private Controller controller;
	private String dburl;
	private String dbuser;
	private String dbpassword;
	private List<ServerConfiguration> listServicios;

	public HybridService(Socket s, String dburl, String dbuser, String dbpassword,
			List<ServerConfiguration> listServicios) {
		// TODO Auto-generated constructor stub
		socket = s;
		this.dburl = dburl;
		this.dbuser = dbuser;
		this.dbpassword = dbpassword;
		this.listServicios = listServicios;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {

			// Recibir request
			HTTPRequest req = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			HTTPRequestMethod method = req.getMethod();
			String[] resourcesPath = req.getResourcePath();
			String resource = "";
			if (resourcesPath.length > 0) // si hay recursos cogemos el ultimo
				resource = resourcesPath[resourcesPath.length - 1].trim();

			// Crear response
			HTTPResponse res = new HTTPResponse();
			res.setVersion(req.getHttpVersion());
			OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());

			String content = "";
			String uuid = "";
			String link = "";
			String xsd = "";
			String xsltTransform = "";
			String xmlTransformed = "";
			String ResourceName = req.getResourceName();
			Controller controllerXsd;
			XsltController controllerXslt;
			boolean xmlValido = false;

			switch (ResourceName) {

			case "html":

				controller = new HtmlController(new HtmlDBDAO(this.dburl, this.dbuser, this.dbpassword), listServicios);
				res.putParameter("Content-Type", "text/html");
				content = req.getResourceParameters().get("html");
				link = "<a href=\"html?uuid=";
				break;

			case "xml":

				controller = new XmlController(new XmlDBDAO(this.dburl, this.dbuser, this.dbpassword), listServicios);
				res.putParameter("Content-Type", "application/xml");
				content = req.getResourceParameters().get("xml");

				xsltTransform = req.getResourceParameters().get("xslt");

				if (xsltTransform != null) {
					uuid = req.getResourceParameters().get("uuid");
					content = controller.getContentPage(uuid);
					controllerXslt = new XsltController(new XsltDBDAO(this.dburl, this.dbuser, this.dbpassword),
							listServicios);

					if (content != null && controllerXslt.getContentPage(xsltTransform) != null) {

						xsd = controllerXslt.getXsd(xsltTransform);
						controllerXsd = new XsdController(new XsdDBDAO(this.dburl, this.dbuser, this.dbpassword),
								listServicios);

						// Validar xml con xsd
						try {

							if ((xsd = controllerXsd.getContentPage(xsd)) != null) {
								loadAndValidateWithExternalXSD(content, xsd);

								xmlValido = true;
								xmlTransformed = transformWithXSLT(content,
										controllerXslt.getContentPage(xsltTransform));
							} else {

								res.setStatus(HTTPResponseStatus.S400);
								res.setContent("<h1>400 Bad Request: Error el recurso solicitado no existe</h1>");
								res.putParameter("Content-Type", "text/html");
								res.print(output);
								output.flush();
								return;
							}

						} catch (ParserConfigurationException | SAXException | TransformerException e) {

							res.setStatus(HTTPResponseStatus.S400);
							res.setContent("<h1>400 Bad Request: Error el recurso solicitado no existe</h1>");
							res.putParameter("Content-Type", "text/html");
							res.print(output);
							output.flush();
							return;

						}

						// Transformar xml en html con xslt

						res.putParameter("Content-Type", "text/html");

					} else {
						res.setStatus(HTTPResponseStatus.S404);
						res.setContent("<h1>404 Not Found: Error el recurso solicitado no existe</h1>");
						res.putParameter("Content-Type", "text/html");
						res.print(output);
						output.flush();
						return;

					}
				}
				link = "<a href=\"xml?uuid=";
				break;

			case "xsd":

				controller = new XsdController(new XsdDBDAO(this.dburl, this.dbuser, this.dbpassword), listServicios);
				res.putParameter("Content-Type", "application/xml");
				content = req.getResourceParameters().get("xsd");
				link = "<a href=\"xsd?uuid=";
				break;

			case "xslt":

				controller = new XsltController(new XsltDBDAO(this.dburl, this.dbuser, this.dbpassword), listServicios);
				controllerXsd = new XsdController(new XsdDBDAO(this.dburl, this.dbuser, this.dbpassword),
						listServicios);
				res.putParameter("Content-Type", "application/xml");
				content = req.getResourceParameters().get("xslt");
				xsd = req.getResourceParameters().get("xsd");

				if (xsd == null) {
					res.setStatus(HTTPResponseStatus.S400);
					res.setContent("<h1>400 Bad Request: Error el recurso solicitado no existe</h1>");
					content = null;
					break;
				}

				if (controllerXsd.getContentPage(xsd) == null) {
					res.setStatus(HTTPResponseStatus.S404);
					res.setContent("<h1>404 Not Found: Error el recurso solicitado no existe</h1>");
					res.putParameter("Content-Type", "text/html");
					res.print(output);
					output.flush();
					return;
				}

				link = "<a href=\"xslt?uuid=";

				break;

			default:

				if (resource.equals("")) {

					res.setStatus(HTTPResponseStatus.S200);
					res.setContent("<html><body><h1>Hybrid Server</h1><ul><li><h2>Bruno Cruz Gonzalez</h2></li>"
							+ "<li><h2> Nicolas David Forero Arevalo </h2></li></ul></body></html>");
					res.putParameter("Content-Type", "text/html");

				} else {

					res.setStatus(HTTPResponseStatus.S400);
					res.setContent("<h1>400 Bad Request: Error el recurso solicitado no existe</h1>");
					res.putParameter("Content-Type", "text/html");

				}

				res.print(output);
				output.flush();
				return;
			}

			switch (method) {

			case GET:

				try {

					if (req.getResourceChain().contains("uuid")/* && req.getResourceName().equals("html") */) {

						// Obtener uuid del Request
						uuid = req.getResourceParameters().get("uuid");

						if (controller.containsPage(uuid) && (content = controller.getContentPage(uuid)) != null) {
							// devolver contenido correspondiente al uuid
							res.setStatus(HTTPResponseStatus.S200);
							if (xmlValido) {
								res.setContent(xmlTransformed);
							} else {
								res.setContent(content);
							}

						} else {

							res.setStatus(HTTPResponseStatus.S404);
							res.setContent("<h1>404 Not found: Error el recurso solicitado no existe</h1>");
							res.putParameter("Content-Type", "text/html");
						}

					} else {

						res.setStatus(HTTPResponseStatus.S200);
						res.setContent(transformList(controller.getList()));
						res.putParameter("Content-Type", "text/html");

					}

				} catch (RuntimeException e) {

					res.setStatus(HTTPResponseStatus.S500);
					res.setContent("<h1>500 Internal Server Error</h1>");
					res.putParameter("Content-Type", "text/html");
				}

				// Responde al cliente

				res.print(output);
				output.flush();
				break;

			case POST:
				try {
					if (content != null) {

						// Crear nuevo uuid
						UUID randomUuid = UUID.randomUUID();
						uuid = randomUuid.toString();

						link += uuid + "\">" + uuid + "</a>";
						// Introducir nueva pagina en la base de datos
						controller.putPages(uuid, content);

						res.setStatus(HTTPResponseStatus.S200);
						res.setContent(link);

					} else {

						res.setStatus(HTTPResponseStatus.S400);
						res.setContent("<h1>400 Bad Request: Error el recurso solicitado no existe</h1>");
						res.putParameter("Content-Type", "text/html");

					}
				} catch (RuntimeException e) {

					res.setStatus(HTTPResponseStatus.S500);
					res.setContent("<h1>500 Internal Server Error</h1>");
					res.putParameter("Content-Type", "text/html");
				}

				res.print(output);
				output.flush();
				break;

			case DELETE:

				try {

					if (req.getResourceChain().contains("uuid")) {

						uuid = req.getResourceParameters().get("uuid");

						if (controller.containsPage(uuid)) {

							controller.removePages(uuid);
							res.setStatus(HTTPResponseStatus.S200);

						} else {

							res.setStatus(HTTPResponseStatus.S404);
							res.setContent("<h1>404 Not found: Error el recurso solicitado no existe</h1>");
							res.putParameter("Content-Type", "text/html");
						}

					} else {

						throw new HTTPParseException();

					}

				} catch (RuntimeException e) {

					res.setStatus(HTTPResponseStatus.S500);
					res.setContent("<h1>500 Internal Server Error</h1>");
					res.putParameter("Content-Type", "text/html");
				}

				res.print(output);
				output.flush();
				break;

			default:
				break;
			}

		} catch (HTTPParseException | IOException e) {
			e.printStackTrace();
		}
	}

	public String transformList(List<String> list) {
		String toRet = "";
		for (String string : list) {
			toRet += string + "\n";
		}
		return toRet;
	}

	public static String transformWithXSLT(String xml, String xslt) throws TransformerException, IOException {

		File xslFile = createFile("file.xsl", xslt);
		File xmlFile = createFile("file.xml", xml);

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslFile));
		StringWriter writer = new StringWriter();
		transformer.transform(new StreamSource(xmlFile), new StreamResult(writer));
		return writer.toString();
	}

	public static Document loadAndValidateWithExternalXSD(String xml, String xsd)
			throws ParserConfigurationException, SAXException, IOException {

		File xsdFile = createFile("file.xsd", xsd);
		File xmlFile = createFile("file.xml", xml);
		// Construcción del schema
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(xsdFile);
		// Construcción del parser del documento. Se establece el esquema y se
		// activa la validación y comprobación de namespaces
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		// Se añade el manejador de errores
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new SimpleErrorHandler());
		return builder.parse(xmlFile);
	}

	private static File createFile(String ruta, String content) throws IOException {

		File archivo = new File(ruta);
		BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
		bw.write(content);
		bw.close();
		return archivo;
	}

}
