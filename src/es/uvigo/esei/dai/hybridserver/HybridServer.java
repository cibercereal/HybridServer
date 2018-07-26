package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.ws.Endpoint;



public class HybridServer {
	private int SERVICE_PORT = 8888;
	private String dburl;
	private String dbuser;
	private String dbpassword;
	private Thread serverThread;
	private boolean stop;
	private String webService;
	private Endpoint endpoint;
	private int numClients = 50;
	private List<ServerConfiguration> listServicios;
	ExecutorService exec;

	public HybridServer() {
		// TODO Auto-generated constructor stub
		this.dburl = "jdbc:mysql://localhost:3306/hstestdb";
		this.dbuser = "hsdb";
		this.dbpassword = "hsdbpass";
		//this.dao = new HtmlController(new HtmlDBDAO(this.dburl,this.dbuser,this.dbpassword));
	}



	public HybridServer(Properties properties) {
		// TODO Auto-generated constructor stub
		this.numClients = Integer.parseInt(properties.getProperty("numClients"));
		this.SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
		this.dburl = properties.getProperty("db.url");
		this.dbuser = properties.getProperty("db.user");
		this.dbpassword = properties.getProperty("db.password");
		
		//this.dao = new HtmlController(new HtmlDBDAO(this.dburl,this.dbuser,this.dbpassword));
	}
	


	public HybridServer(Configuration load) {
		// TODO Auto-generated constructor stub
		this.numClients = load.getNumClients();
		this.SERVICE_PORT = load.getHttpPort();
		this.dburl = load.getDbURL();
		this.dbuser = load.getDbUser();
		this.dbpassword = load.getDbPassword();
		this.webService = load.getWebServiceURL();
		this.listServicios = load.getServers();
			
	}
	
	

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		
		this.exec = Executors.newFixedThreadPool(numClients);	
		
		if(webService != null) {
			endpoint = Endpoint.publish(webService,new webServicesImp(dburl,dbuser,dbpassword));
		}
		this.serverThread = new Thread() {
		

			@Override
			public void run() {
				Socket socket = null;
				try (ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
					while(true) {
					   try {
						   	socket= serverSocket.accept();
						   	if(stop) 
						   		break;
						   	exec.execute(new HybridService(socket,dburl,dbuser,dbpassword, listServicios));
				         } catch (IOException e) {
				             throw new RuntimeException(
				                "Error accepting client connection", e);
				         }
					   
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
			this.exec.shutdown();
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;
		if(webService != null)
			endpoint.stop();
	}
}
