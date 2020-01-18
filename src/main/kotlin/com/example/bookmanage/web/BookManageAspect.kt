package com.example.bookmanage.web

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

/**
 * 書籍管理システムのAspect
 *
 * コントローラとサービスのメソッドの実行時間をログ出力する。
 */
@Aspect
@Component
class BookManageAspect {

    companion object {
        val log = LoggerFactory.getLogger(this::class.java.enclosingClass)!!
    }

    /**
     * web層(Controller,ExceptionHandler)の実行時間をログ出力する。
     *
     * @param pjp JoinPoint
     * @return JoinPoint実行時の戻り値
     * @throws Throwable JoinPoint実行時の例外
     */
    @Around("execution(* com.example.bookmanage.web.*.*(..))")
    @Throws(Throwable::class)
    fun inWebLayer(pjp: ProceedingJoinPoint): Any {
        val stopWatch = StopWatch()
        stopWatch.start()
        val result: Any
        result = try {
            pjp.proceed()
        } finally {
            stopWatch.stop()
            BookManageAspect.log.trace("{} : {} ms", pjp.signature, stopWatch.totalTimeMillis)
        }
        return result
    }

    /**
     * service層(Service)の実行時間をログ出力する。
     *
     * @param pjp JoinPoint
     * @return JoinPoint実行時の戻り値
     * @throws Throwable JoinPoint実行時の例外
     */
    @Around("execution(* com.example.bookmanage.service.*.*(..))")
    @Throws(Throwable::class)
    fun inServiceLayer(pjp: ProceedingJoinPoint): Any {
        val stopWatch = StopWatch()
        stopWatch.start()
        val result: Any
        result = try {
            pjp.proceed()
        } finally {
            stopWatch.stop()
            BookManageAspect.log.trace("{} : {} ms", pjp.signature, stopWatch.totalTimeMillis)
        }
        return result
    }
}