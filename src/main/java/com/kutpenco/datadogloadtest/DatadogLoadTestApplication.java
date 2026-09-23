package com.kutpenco.datadogloadtest;

import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DatadogLoadTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatadogLoadTestApplication.class, args);
    }

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> httpsConnector() {
        return factory -> factory.addAdditionalTomcatConnectors(httpsConnector(8443));
    }

    private org.apache.catalina.connector.Connector httpsConnector(int port) {
        var connector = new org.apache.catalina.connector.Connector(Http11NioProtocol.class.getName());
        connector.setPort(port);
        connector.setScheme("https");
        connector.setSecure(true);
        connector.setProperty("SSLEnabled", "true");

        var sslHostConfig = new SSLHostConfig();
        var certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);
        certificate.setCertificateKeystoreFile("/certs/keystore.p12");
        certificate.setCertificateKeystorePassword("changeit");
        certificate.setCertificateKeystoreType("PKCS12");
        certificate.setCertificateKeyAlias("localhost");
        sslHostConfig.addCertificate(certificate);
        connector.addSslHostConfig(sslHostConfig);
        return connector;
    }
}
