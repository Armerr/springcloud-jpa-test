package io.seata.sample.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TccBusinessServiceImpl implements TccBusinessService {
    @Override
    public void purchase(BusinessActionContext businessActionContext, String userId, String commodityCode, int orderCount) {

    }

    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        log.info("TCC commit方法已提交");
        return false;
    }

    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        log.info("TCC rollback方法已提交");
        return false;
    }
}
