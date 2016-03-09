package pt.ist.cmu.ubibike.httpserver;


import com.sun.net.httpserver.HttpServer;
import pt.ist.cmu.ubibike.httpserver.handlers.AuthenticationHandler;
import pt.ist.cmu.ubibike.httpserver.handlers.RegistrationHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {

    /*teho1967*/

    private static final int PORT = 8000;

    /**
     * Registers the handlers for the defined contexts
     *
     * @param server the http server object being created
     */
    private void registerHandlers(HttpServer server){
        server.createContext("/auth", new AuthenticationHandler());
        server.createContext("/registration", new RegistrationHandler());
    }


    /**
     * Configures the http server object being created
     *      defines the handlers for the contexts
     *      defines a cached thread pool as the executors
     *          cached -> creates threads as needed and reuses existing ones if available
     *
     * @throws IOException
     */
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.registerHandlers(server);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }


    /**
     * @param args args passed in execution
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }

}
