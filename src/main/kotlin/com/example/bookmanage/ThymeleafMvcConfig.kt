package com.example.bookmanage

import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import java.util.*

/**
 * 書籍管理システムのThymeleafのConfiguration<br></br>
 * thymeleaf.extras.springsecurity5をMVCコントローラで使用するために設定している。
 */
@Configuration
class ThymeleafMvcConfig {
    @Bean
    fun templateEngine(): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.setAdditionalDialects(additionalDialects())
        templateEngine.setTemplateResolver(templateResolver())
        templateEngine.setTemplateEngineMessageSource(messageSource())
        return templateEngine
    }

    @Bean
    fun templateResolver(): SpringResourceTemplateResolver {
        val templateResolver = SpringResourceTemplateResolver()
        templateResolver.prefix = "classpath:/templates/"
        templateResolver.suffix = ".html"
        templateResolver.setTemplateMode("HTML5")
        templateResolver.isCacheable = false
        return templateResolver
    }

    @Bean
    fun additionalDialects(): Set<IDialect> {
        return HashSet(listOf(SpringSecurityDialect(), LayoutDialect()))
    }

    @Bean
    fun thymeleafViewResolver(): ThymeleafViewResolver {
        val resolver = ThymeleafViewResolver()
        resolver.templateEngine = templateEngine()
        resolver.characterEncoding = "UTF-8"
        resolver.order = 1
        return resolver
    }

    @Bean
    fun messageSource(): ResourceBundleMessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setDefaultEncoding("UTF-8")
        messageSource.setBasename("messages")
        return messageSource
    }
}