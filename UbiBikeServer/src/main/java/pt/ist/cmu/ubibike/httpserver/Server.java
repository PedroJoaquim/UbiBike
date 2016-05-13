package pt.ist.cmu.ubibike.httpserver;

import com.sun.net.httpserver.HttpServer;
import pt.ist.cmu.ubibike.httpserver.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {


    /**
     * Registers the handlers for the defined contexts
     *
     * @param server the http server object being created
     */
    private void registerHandlers(HttpServer server){
        server.createContext("/auth", new AuthenticationHandler());
        server.createContext("/registration", new RegistrationHandler());
        server.createContext("/PublicKeyToken", new TokenRequestHandler());
        server.createContext("/BikeBooking", new BikeBookingHandler());
        server.createContext("/BikePickDrop", new BikePickDropHandler());
        server.createContext("/Trajectory", new NewTrajectoryHandler());
        server.createContext("/Stations", new StationsInfoHandler());
        server.createContext("/BikeUnbooking", new UnbookingHandler());
        server.createContext("/User", new UserInfoHandler());
        server.createContext("/PointsTransaction", new PointsTransactionHandler());

    }

    /**
     * Configures the http server object being created
     *      defines the handlers for the contexts
     *      defines a cached thread pool as the executors
     *          cached -> creates threads as needed and reuses existing ones if available
     *
     * @throws IOException
     */
    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        this.registerHandlers(server);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("SERVER RUNNING ON PORT: " + port);
    }


    /**
     * @param args args passed in execution
     *             0 = server port
     */
    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s port%n", Server.class.getName());
            return;
        }

       Server server = new Server();
       server.start(Integer.valueOf(args[0]));
    }

}
