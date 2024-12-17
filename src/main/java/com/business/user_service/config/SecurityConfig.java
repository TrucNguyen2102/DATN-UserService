package com.business.user_service.config;

import com.business.user_service.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    @Autowired
    private final UserDetailsService userDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/users/admins/total-api-calls").permitAll()
                                .requestMatchers("/api/users/endpoints").permitAll() //cho tất cả người dùng
                                .requestMatchers("/api/users/create-guest").permitAll() //(mới)
                        .requestMatchers("/api/users/login").permitAll() //cho tất cả người dùng
                        .requestMatchers("/api/users/{id}/logout").permitAll() //cho tất cả người dùng
                                .requestMatchers("/api/users/{id}").permitAll() //cho tất cả người dùng
                                .requestMatchers("/api/users/fullName").permitAll()
                                .requestMatchers("/api/users/fullNameUser").permitAll()
                                .requestMatchers("/api/users/{id}/email").permitAll()
                                .requestMatchers("/api/users/search").permitAll() //cho tất cả người dùng
                                .requestMatchers("/api/users/update/{userId}").permitAll() //cho tất cả người dùng
                                .requestMatchers("/api/users/change-password/{userId}").permitAll()
                                .requestMatchers("/api/users/{id}/updateAt").permitAll()


                                .requestMatchers("/api/users/{userId}/lock").permitAll()
                                .requestMatchers("/api/users/{userId}/unlock").permitAll()
                        .requestMatchers("/api/users/all").hasAuthority("ADMIN")// xem ds tất cả user
                                .requestMatchers("/api/users/{userId}/role").hasAuthority("ADMIN")// xem ds tất cả user
                        .requestMatchers("/api/users/admins/total-users").hasAuthority("ADMIN") // đếm số user
                        .requestMatchers("/api/users/admins/total-api-calls").hasAuthority("ADMIN") // đếm số api đc gọi
                        .requestMatchers("/api/users/admins/managers/add").hasAuthority("ADMIN")
                                .requestMatchers("/api/users/admins/managers/update/{id}").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/admins/roles/all").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/admins/permissions/all").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/admins/permissions/add").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/admins/permissions/update/{id}").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/admins/permissions/delete/{id}").hasAuthority("ADMIN")
                        .requestMatchers("/api/users/admins/permissions/check-name/{name}").hasAuthority("ADMIN")
                                .requestMatchers("/api/users/admins/managers/lock/{id}").hasAuthority("ADMIN")
                                .requestMatchers("/api/users/admins/managers/unlock/{id}").hasAuthority("ADMIN")

                                .requestMatchers("/api/users/managers/customers/all").hasAuthority("MANAGER")
                                .requestMatchers("/api/users/managers/customers/pages/all").hasAuthority("MANAGER")
                                .requestMatchers("/api/users/managers/staffs/all").hasAuthority("MANAGER")
                                .requestMatchers("/api/users/managers/staffs/pages/all").hasAuthority("MANAGER")
                                .requestMatchers("/api/users/managers/staffs/add").hasAuthority("MANAGER")
                                .requestMatchers("/api/users/managers/staffs/lock/{id}").hasAuthority("MANAGER")
                                .requestMatchers("/api/users/managers/staffs/unlock/{id}").hasAuthority("MANAGER")
                                .requestMatchers("/api/users/managers/staffs/update/{id}").hasAuthority("MANAGER")
//                        .requestMatchers("/api/users/admins/register").permitAll()
                        .requestMatchers("/api/users/customers/register").permitAll()
                                .requestMatchers("/api/users/admins/active/count").permitAll()
                                .requestMatchers("/api/users/admins/in_active/count").permitAll()
                                .requestMatchers("/api/users/admins/lock/count").permitAll()
                                .requestMatchers("/api/users/admins/guest/count").permitAll()

                        .anyRequest().authenticated()
                );

        // Thêm bộ lọc JWT
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
