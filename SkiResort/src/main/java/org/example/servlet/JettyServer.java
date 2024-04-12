package org.example.servlet;

import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.example.servlet.MainServlet;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        // Configuration parameters
        int maxThreads = 50;
        int minThreads = 10;
        int idleTimeout = 120;
        int port = 8080;

        // Thread pool configuration
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

        // Create the Jetty server instance
        Server server = new Server(threadPool);
        try (ServerConnector connector = new ServerConnector(server)) {
            connector.setPort(port);
            server.setConnectors(new Connector[]{connector});
        }

        // Create a WebAppContext to define the web application
        String contextPath = "/dss"; // Context path for the web application
        URI webResourceBase = findWebResourceBase(server.getClass().getClassLoader()); // Find the base resource directory
        System.err.println("Using BaseResource: " + webResourceBase);
        WebAppContext context = new WebAppContext();
        context.setBaseResource(Resource.newResource(webResourceBase));
        context.setContextPath(contextPath);
        context.setParentLoaderPriority(true); // Use the parent class loader
        // Add new SkiServlet
        context.addServlet(MainServlet.class, "/skiers/*");

        // Set the handler for the server
        server.setHandler(context);

        // Start the server
        server.start();
        server.join(); // Wait for the server to finish execution
    }

    // Method to find the base resource directory
    private static URI findWebResourceBase(ClassLoader classLoader) {
        String webResourceRef = "WEB-INF/web.xml"; // Reference to the web.xml file

        try {
            // Look for the resource in the classpath
            URL webXml = classLoader.getResource('/' + webResourceRef);
            if (webXml != null) {
                // Resolve the URI and normalize it
                URI uri = webXml.toURI().resolve("..").normalize();
                System.err.printf("WebResourceBase (Using ClassLoader reference) %s%n", uri);
                return uri;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad ClassPath reference for: " + webResourceRef, e);
        }

        // Look for the resource in common file system paths
        try {
            Path pwd = new File(System.getProperty("user.dir")).toPath().toRealPath(); // Get the current working directory
            FileSystem fs = pwd.getFileSystem();

            // Try the generated maven path first
            PathMatcher matcher = fs.getPathMatcher("glob:**/*");
            try (DirectoryStream<Path> dir = Files.newDirectoryStream(pwd.resolve("target"))) {
                for (Path path : dir) {
                    if (Files.isDirectory(path) && matcher.matches(path)) {
                        // Found a potential directory
                        Path possible = path.resolve(webResourceRef);
                        // Does it have what we need?
                        if (Files.exists(possible)) {
                            URI uri = path.toUri();
                            System.err.printf("WebResourceBase (Using discovered /target/ Path) %s%n", uri);
                            return uri;
                        }
                    }
                }
            }

            // Try the source path next
            Path srcWebapp = pwd.resolve("src/main/webapp/" + webResourceRef);
            if (Files.exists(srcWebapp)) {
                URI uri = srcWebapp.getParent().toUri();
                System.err.printf("WebResourceBase (Using /src/main/webapp/ Path) %s%n", uri);
                return uri;
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to find web resource in file system: " + webResourceRef, t);
        }

        // If the resource is not found, throw an exception
        throw new RuntimeException("Unable to find web resource ref: " + webResourceRef);
    }
}