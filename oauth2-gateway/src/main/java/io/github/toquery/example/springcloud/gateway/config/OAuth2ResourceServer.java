package io.github.toquery.example.springcloud.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;


@Configuration
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/api/v1/**").authenticated();
    }

   /*
   需要与 OAuth2AuthorizationServer#configure 的 resourceIds 配合
   @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("example-spring-security-oauth2-base").stateless(true);;
    }
    */
}
