package io.seata.sample.service;


import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface TccBusinessService {
    @TwoPhaseBusinessAction(name="BusinessService")
    void purchase(BusinessActionContext businessActionContext,
                  @BusinessActionContextParameter(paramName = "userId") String userId,
                  @BusinessActionContextParameter(paramName = "commodityCode") String commodityCode,
                  @BusinessActionContextParameter(paramName = "orderCount") int orderCount) ;

    boolean commit(BusinessActionContext businessActionContext);

    boolean rollback(BusinessActionContext businessActionContext);
}
