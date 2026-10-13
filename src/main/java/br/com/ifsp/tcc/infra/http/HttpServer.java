package br.com.ifsp.tcc.infra.http;

import br.com.ifsp.tcc.infra.http.controllers.HealthHttpController;
import io.javalin.Javalin;

public final class HttpServer {

    private final Javalin app;
    private final HealthHttpController healthHttpController;

    public HttpServer() {
        this(new HealthHttpController());
    }

    public HttpServer(HealthHttpController healthHttpController) {
        this.healthHttpController = healthHttpController;
        this.app = Javalin.create(config -> {
            config.useVirtualThreads = true;
            config.showJavalinBanner = false;
        });

        registerRoutes();
    }

    private void registerRoutes() {
        this.app.get("/health", this.healthHttpController);
    }

    public void start(int port) {
        this.app.start(port);
        System.out.println("HTTP server listening on port " + port);
    }

    public void stop() {
        this.app.stop();
    }
}
