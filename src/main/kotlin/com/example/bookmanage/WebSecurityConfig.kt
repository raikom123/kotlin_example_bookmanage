package com.example.bookmanage

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import javax.sql.DataSource

/**
 * 書籍管理システムのsecurityのconfiguration<br></br>
 *
 * 以下を実装している。<br></br>
 * 認証が不要なURLと認証が必要なURLの設定。<br></br>
 * ログイン処理、ログアウト処理の設定。<br></br>
 * 認証できるユーザ情報の設定。
 */
@Configuration
@EnableWebSecurity
class WebSecurityConfig(val dataSource: DataSource) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/css/**", "/js/**", "img/**")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests() // アクセス権限の無いURL
            .antMatchers("/", "/login", "/error").permitAll() // 認証済みでROLE_ADMIN権限を持っている場合のみ、アクセス可能
            .antMatchers("/admin").hasAuthority("ROLE_ADMIN") // その他はアクセス権限が必要
            .anyRequest().authenticated()
            .and()
            .formLogin() // ログイン画面
            .loginPage("/login") // 認証処理
            .loginProcessingUrl("/authenticate") // ログイン成功
            .defaultSuccessUrl("/books") // ログイン失敗
            .failureUrl("/loginfailure") // usernameのパラメータ名
            .usernameParameter("username") // passwordのパラメータ名
            .passwordParameter("password")
            .permitAll()
            .and()
            .logout() // ログアウト処理
            .logoutRequestMatcher(AntPathRequestMatcher("/logout")) // ログアウト成功時の遷移先
            .logoutSuccessUrl("/logoutsuccess") // ログアウト時に削除するクッキー名
            .deleteCookies("JSESSIONID") // ログアウト時のセッション破棄の有効化
            .invalidateHttpSession(true)
            .permitAll()
            .and()
            .sessionManagement() // セッションが無効な時の遷移先
            .invalidSessionUrl("/invalidsession")
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
//        val passwordEncoder = BCryptPasswordEncoder()
//        auth
//            .inMemoryAuthentication()
//            .withUser("user").password(passwordEncoder.encode("user")).authorities("ROLE_USER").and()
//            .withUser("admin").password(passwordEncoder.encode("admin")).authorities("ROLE_ADMIN").and()
//            .passwordEncoder(passwordEncoder)
        // DBを使う場合
        auth
          .jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username = ?")
            .authoritiesByUsernameQuery("SELECT username, authority FROM users WHERE username = ?")
            .passwordEncoder(BCryptPasswordEncoder())
    }
}