package crimson.application;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import crimson.application.filter.JwtAuthenticationFilter;
import crimson.application.service.UserService;

@Configuration
@Order(value = 0)
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableWebSecurity
public class SpringSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(encoder())
				.usersByUsernameQuery("select email,password,is_Active from user where email=?")
				.authoritiesByUsernameQuery("select email,role from user where email=?");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors();
		http.csrf().disable().authorizeRequests().antMatchers("/user/**").hasRole("USER").antMatchers("/admin/**")
				.hasAnyRole("ADMIN", "OWNER").antMatchers("/owner/**").hasRole("OWNER").antMatchers("/profile/**")
				.hasAnyRole("ADMIN", "USER").antMatchers("/orders/**").hasAnyRole("ADMIN", "OWNER", "USER")
				.antMatchers("/products","/categories").authenticated()
				.and().formLogin().loginPage("/?login").usernameParameter("email").passwordParameter("password")
				.loginProcessingUrl("/processlogin").defaultSuccessUrl("/", true).failureUrl("/?login=error").and()
				.logout().logoutUrl("/logout").logoutSuccessUrl("/?login");
		System.out.println("Web Configureed");
	}

	@Bean
	public static PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
