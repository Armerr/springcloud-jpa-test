package io.seata.sample.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;


@Slf4j
@Component
public class AccountFeignClientImpl implements  AccountFeignClient{
    @Override
    public Boolean debit(String userId, BigDecimal money) {
        log.warn("AccountFeign已经熔断");
//        try {
//            GlobalTransactionContext.reload(RootContext.getXID()).rollback();
//        } catch (TransactionException e) {
//            e.printStackTrace();
//        }
        return false;
    }
}
