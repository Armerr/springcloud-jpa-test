package io.seata.sample.feign;

import com.codingapi.txlcn.tc.core.DTXLocalContext;
import com.codingapi.txlcn.tc.core.DTXLocalControl;
import com.codingapi.txlcn.tc.support.DTXUserControls;
import org.springframework.stereotype.Component;

@Component("orderFeignClient")
public class OrderFeignClientImpl implements OrderFeignClient{

    @Override
    public void create(String userId, String commodityCode, Integer count) {
        DTXUserControls.rollbackCurrentGroup();
        System.out.println("fallback");
    }
}
