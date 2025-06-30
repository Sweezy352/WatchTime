package kg.sweezy.watchtime.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(
        title = "WatchTime project",
        version = "0.0.1",
        description = "проект для просмотра контента в Кыргызстане, и соревнования между каналами"
))

@SecurityScheme(
        name = "basic",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@Configuration
public class SwaggerConfig {
}
