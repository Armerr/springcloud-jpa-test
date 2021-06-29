package io.seata.sample.service;

import io.seata.sample.entity.Storage;
import io.seata.sample.repository.StorageDAO;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019-04-04
 */
@Service
public class StorageService {

    @Autowired
    private StorageDAO storageDAO;


    @Transactional
    public void deduct(String commodityCode, int count) {
        Storage storage = storageDAO.findByCommodityCode(commodityCode);
        storage.setCount(storage.getCount() - count);

        storageDAO.save(storage);
    }

    @GlobalLock
    public void deduct2() {
        Storage storage = storageDAO.findByCommodityCode("2001");
        storage.setCount(storage.getCount() - 1);

        storageDAO.save(storage);
    }
}
