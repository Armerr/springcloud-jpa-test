package io.seata.sample.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.sample.entity.Account;
import io.seata.sample.repository.AccountDAO;
import io.seata.sample.util.ResultHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

@Slf4j
@Component
public class TCCAccountServiceImpl implements TCCAccountService {

    @Autowired
    private AccountDAO accountDAO;

    @Transactional
    @Override
    public void purchase(BusinessActionContext businessActionContext, String userId, BigDecimal money) {
        log.info("减少账户金额，第一阶段锁定金额，userId="+userId+"， money="+money);

        Account account = accountDAO.findByUserId(userId);
        account.setMoney(account.getMoney().subtract(money));
        accountDAO.save(account);

        if (1 == 2) {
            throw new RuntimeException("模拟异常");
        }

        //保存标识
        ResultHolder.setResult(getClass(), businessActionContext.getXid(), "p");
    }

    @Transactional
    @Override
    public boolean commit(BusinessActionContext businessActionContext) {

        String userId = businessActionContext.getActionContext("userId").toString();
        BigDecimal money =  new BigDecimal(businessActionContext.getActionContext("money").toString());
        log.info("减少账户金额，第二阶段，提交，userId="+userId+"， money="+money);
        Account account = accountDAO.findByUserId(userId);
        account.setMoney(money);
        //防止重复提交
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }
        accountDAO.save(account);
        //删除标识
        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
        return true;
    }

    @Transactional
    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        String userId = businessActionContext.getActionContext("userId").toString();
        BigDecimal money =  new BigDecimal(businessActionContext.getActionContext("money").toString());

        //防止重复提交
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }

        log.info("减少账户金额，第二阶段，回滚，userId="+userId+"， money="+money);

        Account account = accountDAO.findByUserId(userId);
        account.setMoney(money);
        accountDAO.save(account);
        //删除标识
        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
        return true;
    }
}
