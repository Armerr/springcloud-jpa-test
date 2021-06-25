package io.seata.sample.Aspect;

import io.seata.core.context.RootContext;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class OrderAspect {
    //io.seata.saga.engine.invoker
    @Before("execution(* io.seata.sample.service.*.*(..))")
    public void before(JoinPoint joinPoint) throws io.seata.core.exception.TransactionException {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        GlobalTransaction tx = GlobalTransactionContext.getCurrentOrCreate();
        tx.begin(300000, "test-client");
        log.info("**********创建分布式事务完毕" + tx.getXid());
    }

    @AfterThrowing(throwing = "e", pointcut = "execution(* io.seata.sample.service.*.*(..))")
    public void doRecoveryActions(Throwable e) throws  io.seata.core.exception.TransactionException {
        log.info("方法执行异常:{}", e.getMessage());
        if (!StringUtils.isBlank(RootContext.getXID())) {
            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
        }
    }
}
