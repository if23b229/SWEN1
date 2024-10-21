package com.mtcg.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class MTCGServer {
    public static void main(String[] args) throws IOException {
        // Erstelle einen HTTP-Server auf Port 10001
        HttpServer server = HttpServer.create(new InetSocketAddress(10001), 0);

        // Endpunkte hinzufügen
        server.createContext("/users", new RegisterHandler());  // Benutzerregistrierung
        server.createContext("/sessions", new LoginHandler());  // Login-Handler
        server.createContext("/packages", new PackageHandler());  // Paketverwaltung
        server.createContext("/transactions/packages", new BuyPackageHandler());  // Pakete kaufen

        server.createContext("/cards", new CardsHandler());  // Kartenverwaltung
        server.createContext("/stats", new StatsHandler());  // Statistikverwaltung
        server.createContext("/deck", new DeckHandler());    // Deckverwaltung
        // Standard-Executor verwenden
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 10001");
    }

    // Handler für den /users Endpunkt (Benutzerregistrierung)
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Request Body auslesen
                InputStream requestBody = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
                String body = reader.lines().collect(Collectors.joining("\n"));

                // Logik zur Benutzerregistrierung (hier: nur Konsolenausgabe)
                System.out.println("User registration data: " + body);

                // Erfolgsantwort zurückgeben
                String response = "User registered successfully";
                exchange.sendResponseHeaders(201, response.length());  // HTTP 201 Created
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);  // 405 Method Not Allowed
            }
        }
    }

    // Handler für den /sessions Endpunkt (Login)
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Request Body auslesen
                InputStream requestBody = exchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
                String body = reader.lines().collect(Collectors.joining("\n"));

                // Logik zur Authentifizierung (hier: nur Konsolenausgabe)
                System.out.println("Login attempt: " + body);

                // Erfolgsantwort zurückgeben
                String response = "User logged in successfully with token: user-mtcgToken";
                exchange.sendResponseHeaders(200, response.length());  // HTTP 200 OK
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);  // 405 Method Not Allowed
            }
        }
    }

    // Handler für den /packages Endpunkt (Pakete erstellen)
    static class PackageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Prüfe, ob der Admin-Token vorhanden ist
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                if (authHeader != null && authHeader.equals("Bearer admin-mtcgToken")) {
                    // Request Body auslesen (Paketdaten)
                    InputStream requestBody = exchange.getRequestBody();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
                    String body = reader.lines().collect(Collectors.joining("\n"));

                    // Logik zum Erstellen von Paketen (hier: nur Konsolenausgabe)
                    System.out.println("Package data: " + body);

                    // Erfolgsantwort zurückgeben
                    String response = "Package created successfully";
                    exchange.sendResponseHeaders(201, response.length());  // HTTP 201 Created
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(403, -1);  // 403 Forbidden (kein Token oder falscher Token)
                }
            } else {
                exchange.sendResponseHeaders(405, -1);  // 405 Method Not Allowed
            }
        }
    }

    // Handler für den /transactions/packages Endpunkt (Pakete kaufen)
    static class BuyPackageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Prüfe, ob ein Benutzer-Token vorhanden ist
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                if (authHeader != null && authHeader.contains("-mtcgToken")) {
                    // Erfolgsantwort für Paketkauf
                    String response = "Package purchased successfully";
                    exchange.sendResponseHeaders(201, response.length());  // HTTP 201 Created
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(403, -1);  // 403 Forbidden (kein Token oder falscher Token)
                }
            } else {
                exchange.sendResponseHeaders(405, -1);  // 405 Method Not Allowed
            }
        }

    }
    static class CardsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                if (authHeader != null && authHeader.contains("-mtcgToken")) {
                    // Logik zum Abrufen von Karten (hier kannst du Karten aus der Datenbank oder aus einer Liste abrufen)
                    String response = "List of all acquired cards";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(403, -1);  // Forbidden (kein Token oder falscher Token)
                }
            } else {
                exchange.sendResponseHeaders(405, -1);  // Method Not Allowed
            }
        }
    }

    static class DeckHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("PUT".equals(exchange.getRequestMethod())) {
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                if (authHeader != null && authHeader.contains("-mtcgToken")) {
                    InputStream requestBody = exchange.getRequestBody();
                    String body = new BufferedReader(new InputStreamReader(requestBody))
                            .lines().collect(Collectors.joining("\n"));
                    System.out.println("Configuring deck: " + body);

                    // Logik zum Konfigurieren des Decks
                    String response = "Deck configured successfully";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(403, -1);  // Forbidden (kein Token oder falscher Token)
                }
            } else {
                exchange.sendResponseHeaders(405, -1);  // Method Not Allowed
            }
        }
    }
    static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                if (authHeader != null && authHeader.contains("-mtcgToken")) {
                    // Logik zum Abrufen der Statistiken
                    String response = "User stats: Wins: 5, Losses: 2";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    exchange.sendResponseHeaders(403, -1);  // Forbidden (kein Token oder falscher Token)
                }
            } else {
                exchange.sendResponseHeaders(405, -1);  // Method Not Allowed
            }
        }
    }
}
