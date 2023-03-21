package se.vgregion.vatskenutrition.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.InetOrgPersonContextMapper;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import se.vgregion.vatskenutrition.security.JWTAuthenticationFilter;
import se.vgregion.vatskenutrition.security.JWTAuthorizationFilter;
import se.vgregion.vatskenutrition.service.JwtTokenFactory;
import se.vgregion.vatskenutrition.util.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {
	public static final String AUTHENTICATION_HEADER_NAME = "Authorization";

	@Value("${ldap.url}")
	private String ldapUrl;

	@Value("${ldap.base}")
	private String ldapBase;

	@Value("${ldap.userDn}")
	private String ldapUserDn;

	@Value("${ldap.password}")
	private String ldapPassword;

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${default.admin.username}")
	private String defaultAdminUsername;

	@Value("${default.admin.encoded.password}")
	private String defaultAdminEncodedPassword;

	@Autowired
	private JwtTokenFactory jwtTokenFactory;

	public WebSecurity() {
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeRequests()
				.antMatchers("/year/currentYear", "/year/additionalHeadingText", "/article/startPageArticle/currentYear", "/auth/token", "/article/year/currentYear", "/article/*", "/article/*/*", "/image/**")
				.permitAll()
				.anyRequest().authenticated()
				.and()
				.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtTokenFactory))
				.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtSecret))
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

		BindAuthenticator authenticator = new BindAuthenticator(contextSource());
		authenticator.setUserSearch(new FilterBasedLdapUserSearch("OU=usr-Pers", "(cn={0})", contextSource()));

		LdapAuthenticationProvider authenticationProvider = new LdapAuthenticationProvider(authenticator, getLdapAuthoritiesPopulator());
		authenticationProvider.setUserDetailsContextMapper(new InetOrgPersonContextMapper());
		auth.authenticationProvider(new AbstractUserDetailsAuthenticationProvider() {
			@Override
			protected void additionalAuthenticationChecks(UserDetails userDetails,
														  UsernamePasswordAuthenticationToken authentication)
					throws AuthenticationException {

			}

			@Override
			protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
					throws AuthenticationException {
				String passwordInput = (String) authentication.getCredentials();

				if (username.equals(defaultAdminUsername)
						&& PasswordEncoder.getInstance().matches(passwordInput, defaultAdminEncodedPassword)) {

					return new User(username, (String) authentication.getCredentials(), new ArrayList<>());
				}
				throw new AuthenticationCredentialsNotFoundException("Invalid credentials.");
			}
		}).authenticationProvider(authenticationProvider);

	}

	@Bean
	public LdapAuthoritiesPopulator getLdapAuthoritiesPopulator() {
		return new LdapAuthoritiesPopulator() {

			@Override
			public Collection<? extends GrantedAuthority> getGrantedAuthorities(
					DirContextOperations dirContextOperations,
					String cn) {

				HashMap<String, List<String>> attributes = new HashMap<>();

				attributes.put("displayName", Collections.singletonList(
						dirContextOperations.getStringAttribute("displayName")));

				attributes.put("userId", Collections.singletonList(
						dirContextOperations.getStringAttribute("cn")));

				attributes.put("mail", Collections.singletonList(
						dirContextOperations.getStringAttribute("mail")));

				attributes.put("dn", Collections.singletonList(dirContextOperations.getDn().toString()));

				GrantedAuthority authority = new LdapAuthority("user", cn, attributes);

				return Collections.singletonList(authority);
			}
		};
	}

	@Bean
	public DefaultSpringSecurityContextSource contextSource() {
		DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(ldapUrl);

		contextSource.setBaseEnvironmentProperties(
				Collections.singletonMap("com.sun.jndi.ldap.connect.timeout", "1000"));

		contextSource.setBase(ldapBase);
		contextSource.setUserDn(ldapUserDn);
		contextSource.setPassword(ldapPassword);
		contextSource.setReferral("follow");

		return contextSource;
	}
}
