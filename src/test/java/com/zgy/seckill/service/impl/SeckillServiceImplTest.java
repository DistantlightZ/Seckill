package com.zgy.seckill.service.impl;

import com.zgy.seckill.dto.Exposer;
import com.zgy.seckill.dto.SeckillExcution;
import com.zgy.seckill.entity.Seckill;
import com.zgy.seckill.exception.RepeatKillException;
import com.zgy.seckill.exception.SeckillCloseException;
import com.zgy.seckill.exception.SeckillException;
import com.zgy.seckill.service.SeckillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    @Resource
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> seckillList = seckillService.getSeckillList();
        System.out.println(seckillList);
    }

    @Test
    public void getById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        System.out.println(seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        System.out.println(exposer);
    }

    @Test
    public void excuteSeckill() throws Exception {
        long id = 1000;
        long phone = 13151569768L;
        String md5 = "7e29dc19e6372f20ab02a3ab65fef137";
        SeckillExcution seckillExcution = null;
        try {
            seckillExcution = seckillService.excuteSeckill(id,phone,md5);
        } catch (RepeatKillException e) {
            System.out.println(e.getMessage());
        }catch (SeckillCloseException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(seckillExcution);
    }

    //测试完整逻辑，注意可重复执行
    @Test
    public void SeckillLogic() throws Exception{
        long id = 1000;
        long phone = 13151569757L;
        Exposer exposer = seckillService.exportSeckillUrl(id);

        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExcution seckillExcution = null;
            try {
                seckillExcution = seckillService.excuteSeckill(id,phone,md5);
            } catch (RepeatKillException e) {
                System.out.println(e.getMessage());
            }catch (SeckillCloseException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(seckillExcution);
        }else {
            System.out.println("秒杀未开启");
        }

    }

}