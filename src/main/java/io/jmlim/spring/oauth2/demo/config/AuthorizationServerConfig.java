package io.jmlim.spring.oauth2.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * 토큰 발급 해주고.. 토큰 리프레시 해주는 Config
 */
@RequiredArgsConstructor
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * 기본 inmemory (추후 database 기반으로 변경 필요)
     */
    private final TokenStore tokenStore;

    private final JwtAccessTokenConverter accessTokenConverter;

    /**
     * authenticationManager는 아래 configure의 withClient 와 secret 를 보고 실제 인증을 처리함.
     */
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
        configurer
                .inMemory()
                .withClient("jmlim-client") // client Id
                .secret(passwordEncoder.encode("jmlim-password")) // client password
                // 엑세스 토큰 발급 가능한 인증타입.
                .authorizedGrantTypes("password",
                        "authorization_code",
                        "refresh_token",
                        "implicit")
                // 이 클라이언트로 접근할 수 있는 범위 제한.
                // 해당 클라이언트로 API 접근했을 때 접근 범위를 제한시키는 속성.
                .scopes("read", "write", "trust")
                // access 토큰 만료 시간
                .accessTokenValiditySeconds(1 * 60 * 60)
                // refresh token 만료시간
                .refreshTokenValiditySeconds(6 * 60 * 60);
    }

    /**
     * 엔드포인트 configure
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .accessTokenConverter(accessTokenConverter)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }
}