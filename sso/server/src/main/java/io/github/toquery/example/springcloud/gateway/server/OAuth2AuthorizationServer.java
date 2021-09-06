package io.github.toquery.example.springcloud.gateway.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient("gateway")
                .secret("secret")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .authorities("uaa.resource")
                .scopes("openid", "profile", "email", "resource.read")
                .resourceIds("gateway")
                // 自动授权，无需人工手动点击 approve
                .autoApprove(true)
                .redirectUris("http://localhost:8080/login/oauth2/code/gateway")
                .accessTokenValiditySeconds(120)
                .refreshTokenValiditySeconds(240000);
    }
}
