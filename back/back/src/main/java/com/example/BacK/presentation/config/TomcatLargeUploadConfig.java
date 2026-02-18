package com.example.BacK.presentation.config;

import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat configuration for handling large file uploads (1GB+).
 * 
 * This configuration addresses the "Broken pipe" issue when uploading large files
 * through Apache reverse proxy by:
 * 
 * 1. Disabling max swallow size - allows Tomcat to accept any size request body
 * 2. Increasing connection timeout - prevents timeout during slow uploads
 * 3. Disabling request body size limits - no artificial limits on upload size
 * 4. Configuring keep-alive - maintains connection during long uploads
 */
@Configuration
public class TomcatLargeUploadConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                if (connector.getProtocolHandler() instanceof Http11NioProtocol protocol) {
                    
                    // Disable max swallow size - critical for large uploads
                    // When set to -1, Tomcat will not abort the connection if the client
                    // continues to send data after the server has sent a response
                    protocol.setMaxSwallowSize(-1);
                    
                    // Connection timeout: 1 hour (in milliseconds)
                    // Prevents Tomcat from closing the connection during slow uploads
                    protocol.setConnectionTimeout(3600000);
                    
                    // Keep-alive timeout: 5 minutes
                    // Allows connection reuse between requests
                    protocol.setKeepAliveTimeout(300000);
                    
                    // Max keep-alive requests: unlimited
                    // Prevents connection cycling during uploads
                    protocol.setMaxKeepAliveRequests(-1);
                    
                    // Disable compression for upload endpoints (already binary)
                    protocol.setCompression("off");
                }
                
                // Disable max post size limit (-1 = unlimited)
                connector.setMaxPostSize(-1);
            });
        };
    }
}
