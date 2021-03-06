package antifraud.Config;

import antifraud.Service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityImpl extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetails;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetails).passwordEncoder(encoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.PUT,"/api/antifraud/transaction").hasAuthority("SUPPORT")
                .mvcMatchers(HttpMethod.POST,"/api/antifraud/transaction").hasAuthority("MERCHANT")

                .mvcMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasAnyAuthority("SUPPORT")
                .mvcMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/*").hasAnyAuthority("SUPPORT")
                .mvcMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasAnyAuthority("SUPPORT")

                .mvcMatchers("/api/antifraud/history").hasAnyAuthority("SUPPORT")
                .mvcMatchers("/api/antifraud/history/*").hasAnyAuthority("SUPPORT")

                .mvcMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasAnyAuthority("SUPPORT")
                .mvcMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/*").hasAnyAuthority("SUPPORT")
                .mvcMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasAnyAuthority("SUPPORT")

                .mvcMatchers(HttpMethod.PUT, "/api/auth/role", "/api/auth/access").hasAuthority("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasAuthority("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .mvcMatchers("/api/auth/list").hasAnyAuthority("SUPPORT", "ADMINISTRATOR")

                .and()
                .csrf().disable().headers().frameOptions().disable()
                .and().httpBasic();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}

