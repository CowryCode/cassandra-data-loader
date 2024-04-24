package com.cowrycode.cassandradataloader;

import com.cowrycode.cassandradataloader.author.Author;
import com.cowrycode.cassandradataloader.author.AuthorResository;
import com.cowrycode.cassandradataloader.connection.DataStaxAstraProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.file.Path;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class CassandraDataLoaderApplication {
	@Value("${datadump.location.works}")
	private String worksDumpLocation;
	@PostConstruct
	public void start(){
		System.out.println("Apllication Start!! " + worksDumpLocation );

	}

	public static void main(String[] args) {

		SpringApplication.run(CassandraDataLoaderApplication.class, args);
	}

	/**
	 * This is necessary to have the Spring Boot app use the Astra secure bundle
	 * to connect to the database
	 */
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	// TEMPORARY USERS TO TEST THE API IN DEV, TO BE REMOVED IN PRODUCTION.
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails user1 = User.withUsername("user1")
				.password(passwordEncoder().encode("user1Pass"))
				.roles("USER")
				.build();
		UserDetails user2 = User.withUsername("user2")
				.password(passwordEncoder().encode("user2Pass"))
				.roles("USER")
				.build();
		UserDetails admin = User.withUsername("admin")
				.password(passwordEncoder().encode("adminPass"))
				.roles("ADMIN")
				.build();
		return new InMemoryUserDetailsManager(user1, user2, admin);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}
