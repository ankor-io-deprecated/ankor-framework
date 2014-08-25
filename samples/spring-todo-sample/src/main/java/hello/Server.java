package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class Server implements EmbeddedServletContainerCustomizer {
    
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        // serve static resources in utf-8 encoding. May not be necessary for your app.
        MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
        mappings.add("html", "text/html;charset=utf-8");
        container.setMimeMappings(mappings);
    }
}
