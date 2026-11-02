module pl.pollub.inwentaryzacja.client {
    requires javafx.controls;
    requires java.net.http;
    requires tools.jackson.databind;
    requires tools.jackson.dataformat.xml;

    exports pl.pollub.inwentaryzacja.client;
    exports pl.pollub.inwentaryzacja.client.api;
    exports pl.pollub.inwentaryzacja.client.model;
    exports pl.pollub.inwentaryzacja.client.serialization;
}
