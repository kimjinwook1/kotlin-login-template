package hello.kotlinlogintemplate.global.config

import hello.kotlinlogintemplate.global.jwt.JwtSecurityConfig
import hello.kotlinlogintemplate.global.jwt.TokenProvider
import hello.kotlinlogintemplate.global.jwt.error.JwtAccessDeniedHandler
import hello.kotlinlogintemplate.global.jwt.error.JwtAuthenticationEntryPoint
import hello.kotlinlogintemplate.global.oauth.CustomOAuth2UserService
import hello.kotlinlogintemplate.global.oauth.OAuth2LoginFailureHandler
import hello.kotlinlogintemplate.global.oauth.OAuth2LoginSuccessHandler
import hello.kotlinlogintemplate.global.security.CustomAuthenticationProvider
import hello.kotlinlogintemplate.global.security.CustomUserDetailsService
import jakarta.servlet.DispatcherType
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.CorsFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val corsFilter: CorsFilter,
    private val tokenProvider: TokenProvider,
    private val authenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val accessDeniedHandler: JwtAccessDeniedHandler,
    private val customOAuth2UserService: CustomOAuth2UserService,
) {

    @Bean
    fun customAuthenticationProvider(): CustomAuthenticationProvider? {
        return CustomAuthenticationProvider()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        val excludePaths = arrayOf(
            "/docs/**",
            "/",
            "/h2-console",
            "/loginForm",
            "/joinForm",
            "/index.html",
            "/test", // /test.html을 호출하기 위한 임시 설정
            "/test.html",
        )

        http.csrf { it.disable() }
            .headers { header -> header.frameOptions { it.disable() } }
            .formLogin { it.disable() }

            // CORS 설정
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter::class.java)

            .exceptionHandling {
                // 인증/인가 예외가 발생하면 진입
                it.authenticationEntryPoint(authenticationEntryPoint)
                // 인가 예외가 발생하면 진입
                it.accessDeniedHandler(accessDeniedHandler)
            }

            .sessionManagement {
                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests {
                it.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                it.requestMatchers(*excludePaths).permitAll()
                // 정적 리소스에 대해서는 인증/인가 처리하지 않음
                it.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // 회원가입, 로그인, 토큰 재발급은 허용
                it.requestMatchers(HttpMethod.POST, "/members").permitAll()
                it.requestMatchers(HttpMethod.POST, "/auths/refresh").permitAll()
                it.requestMatchers(HttpMethod.POST, "/auths").permitAll()
                it.anyRequest().authenticated()
            }

            .userDetailsService(customUserDetailsService)
            .with(JwtSecurityConfig(tokenProvider), Customizer.withDefaults())

            // OAuth2 로그인 설정
            .oauth2Login { oauth2Login ->
                oauth2Login.userInfoEndpoint { userInfoEndpoint ->
                    userInfoEndpoint.userService(customOAuth2UserService)
                }
                oauth2Login.successHandler(OAuth2LoginSuccessHandler(tokenProvider))
                oauth2Login.failureHandler(OAuth2LoginFailureHandler())
            }

        return http.build()
    }
}
