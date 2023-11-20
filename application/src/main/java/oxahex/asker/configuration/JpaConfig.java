package oxahex.asker.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
//@EntityScan(basePackages = "oxahex.asker.domain")
//@EnableJpaRepositories(basePackages = "oxahex.asker.domain")
@EnableJpaAuditing
public class JpaConfig {

}

