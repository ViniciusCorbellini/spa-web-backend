package com.manocorbas.dev_web_backend.config;

import com.manocorbas.dev_web_backend.security.JwtAuthenticationFilter;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;

	@Value("${application.FRONTEND_ORIGIN}")
	private String frontendOrigin;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
			AuthenticationProvider authenticationProvider) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.authenticationProvider = authenticationProvider;
	}

	//Cadeia de filtros:
	// 	Requisição chega;
	// 	CORS preflight/headers são verificados (se aplicável);
	// 	jwtAuthFilter roda primeiro:
	// 		Se houver Authorization: Bearer <token>, valida o token;
	// 		Se token ok: cria Authentication e coloca no SecurityContext;
	// 		Se não: apenas segue, sem autenticar (ou pode lançar/exigir);
	// 	Outros filtros do Spring (p.ex. UsernamePasswordAuthenticationFilter) podem rodar — mas se o SecurityContext já tiver autenticação válida, a autorização 
	// 	já permite acessar endpoints protegidos;
	// 	Controle de autorização: as regras de requestMatchers e anyRequest().authenticated() são aplicadas;
	// 	Se a rota exige autenticação e não existe Authentication válida: retorna 401;
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		String[] publicEndpoints = {
				"/swagger-ui.html",
				"/swagger-ui/**",
				"/v3/api-docs/**",
				"/health",
				"/auth/**"
		};

		return http
				.csrf(AbstractHttpConfigurer::disable)
				// Dizemos ao Spring para usar a configuração definida no Bean abaixo
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(publicEndpoints).permitAll()
						.anyRequest().authenticated())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	/* Obs: o CorsConfig foi apagado, visto que a configuração do spring sec abaixo sobrescreve essa classe */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Define as origens permitidas: Localhost && Prod
		// OBS: Se frontendOrigin estiver vazio ou nulo, o Arrays.asList pode reclamar
		configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173", frontendOrigin));
		// OBS2: Se frontendOrigin for definida como '*' nas variáveis de ambiente do render vai dar ruim
		// ou mais especificamente -> 
		// 	java.lang.IllegalArgumentException: When allowCredentials is true, allowedOrigins cannot contain the special value
		//  	"*" since that cannot be set on the "Access-Control-Allow-Origin" response header. To allow credentials 
		// 		to a set of origins, list them explicitly or consider using "allowedOriginPatterns" instead.
		// por conta disso, usarei setAllowedOriginPatterns ao invés de setAllowedOrigins pra enganar o navegador

		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("Authorization"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L); // Tempo máximo de preflight que o navegador pode cachear 
		// ^^^ preflight: OPTIONS

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // 'roteador' do cors
		source.registerCorsConfiguration("/**", configuration); // aplica o cors para todas as rotas da aplicação
		return source;
	}
}
