package br.com.ifsp.tcc.infra.http;

import io.javalin.Javalin;

public final class HttpServer {

    private final Javalin app;

    public HttpServer() {
        this.app = Javalin.create(config -> {
            config.useVirtualThreads = true;
            config.showJavalinBanner = false;
        });

        Routes.register(this.app);
    }

    public void start(int port) {
        this.app.start(port);
        System.out.println("HTTP server listening on port " + port);
    }

    public void stop() {
        this.app.stop();
    }
}