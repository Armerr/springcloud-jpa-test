package io.seata.sample.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.sample.entity.Order;
import io.seata.sample.repository.OrderDAO;
import io.seata.sample.util.ResultHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Slf4j
public class TCCOrderServiceImpl implements TCCOrderService{
    @Autowired
    private OrderDAO orderDAO;

    @Transactional
    @Override
    public boolean prepareCreateOrder(BusinessActionContext businessActionContext, Long orderId, String userId, String commodityCode, Integer count, BigDecimal money) {
        log.info("创建 order 第一阶段， - "+businessActionContext.getXid());

        Order order = new Order(userId, commodityCode, money, count);
        orderDAO.save(order);

        //事务成功，保存一个标识，供第二阶段进行判断
        ResultHolder.setResult(getClass(), businessActionContext.getXid(), "p");
        return true;
    }

    @Transactional
    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        log.info("创建 order 第二阶段提交，- "+businessActionContext.getXid());

        // 防止幂等性，如果commit阶段重复执行则直接返回
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }

        //Long orderId = (Long) businessActionContext.getActionContext("orderId");


        //提交成功是删除标识
        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
        return true;
    }

    @Transactional
    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        log.info("创建 order 第二阶段回滚，删除订单 - "+businessActionContext.getXid());

        //第一阶段没有完成的情况下，不必执行回滚
        //因为第一阶段有本地事务，事务失败时已经进行了回滚。
        //如果这里第一阶段成功，而其他全局事务参与者失败，这里会执行回滚
        //幂等性控制：如果重复执行回滚则直接返回
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }

        //Long orderId = (Long) businessActionContext.getActionContext("orderId");
        long orderId = Long.parseLong(businessActionContext.getActionContext("orderId").toString());
        orderDAO.delete(orderId);

        //回滚结束时，删除标识
        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
        return true;
    }
}
