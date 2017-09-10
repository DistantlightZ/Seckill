package com.zgy.seckill.dao;

import com.zgy.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        long seckillId = 1000L;
        long phone = 13151569768L;
        int insertCount = successKilledDao.insertSuccessKilled(seckillId,phone);
        System.out.println(insertCount);

    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long seckillId = 1000L;
        long phone = 13151569768L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, phone);
        System.out.println(successKilled);
    }

}