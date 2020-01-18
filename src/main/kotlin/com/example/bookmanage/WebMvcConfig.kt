package com.example.bookmanage

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.filter.HiddenHttpMethodFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*

/**
 * 書籍管理システムのConfiguration
 *
 * 以下を実装している。
 * PUT/DELETEをPOSTするためにHiddenHttpMethodFilterをFilterとして設定する。
 * validationで使用するメッセージプロパティのエンコードをUTF-8に設定する。
 */
@Configuration
class WebMvcConfig : WebMvcConfigurer {
    /**
     * HiddenHttpMethodFilterをFilterに設定するためのBeanを返却する。
     *
     * @return HiddenHttpMethodFilterをFilterに設定するためのBean
     */
    @Bean
    fun hiddenHttpMethodFilter(): FilterRegistrationBean<HiddenHttpMethodFilter> {
        val filter =
            HiddenHttpMethodFilter()
        val filterRegBean =
            FilterRegistrationBean(filter)
        filterRegBean.urlPatterns = listOf("/*")
        return filterRegBean
    }

    /**
     * validationで使用するメッセージプロパティのエンコードにUTF-8を設定したLocalValidatorFactoryBeanを返却する。
     *
     * @return LocalValidatorFactoryBean
     */
    override fun getValidator(): Validator? {
        val validator = LocalValidatorFactoryBean()
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setBasename("classpath:ValidationMessages")
        messageSource.setDefaultEncoding("UTF-8")
        validator.setValidationMessageSource(messageSource)
        return validator
    }
}