package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }

    public static class Database {
        private static final String URL = "jdbc:postgresql://localhost:5432/mtcg";  // Pfad zu deiner Datenbank
        private static final String USER = "postgres";  // PostgreSQL-Benutzername, z.B. postgres
        private static final String PASSWORD = "12312";  // Passwort für PostgreSQL

        public static Connection connect() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }

    public static class MTCGServer {
        public static void main(String[] args) throws IOException {
            // Erstelle einen HTTP-Server auf Port 10001
            HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);
            server.createContext("/test", new MyHandler()); // Test-Endpunkt
            server.createContext("/dbtest", new DbTestHandler()); // Datenbank-Test-Endpunkt
            server.setExecutor(null); // Verwendet den default Executor
            server.start();
            System.out.println("Server started on port 10001");
        }

        // /test Endpunkt: Dieser bleibt wie bisher
        static class MyHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response = "This is the response";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        // /dbtest Endpunkt: Testet die Verbindung zur Datenbank
        static class DbTestHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String response;
                try (Connection conn = Database.connect()) { // Datenbankverbindung aufbauen
                    if (conn != null) {
                        response = "Connected to the PostgreSQL server successfully.";
                    } else {
                        response = "Failed to make connection!";
                    }
                } catch (SQLException e) {
                    response = "Database error: " + e.getMessage();
                }
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}