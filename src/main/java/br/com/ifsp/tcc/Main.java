package br.com.ifsp.tcc;

import br.com.ifsp.tcc.infra.config.Configuration;
import br.com.ifsp.tcc.infra.http.HttpServer;

public final class Main {

    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();

        Runtime.getRuntime().addShutdownHook(new Thread(httpServer::stop, "shutdown-hook"));

        httpServer.start(Configuration.HTTP_PORT);
    }
}
