package com.zgy.seckill.service.impl;

import com.sun.mail.smtp.DigestMD5;
import com.sun.xml.internal.bind.v2.TODO;
import com.zgy.seckill.dao.SeckillDao;
import com.zgy.seckill.dao.SuccessKilledDao;
import com.zgy.seckill.dto.Exposer;
import com.zgy.seckill.dto.SeckillExcution;
import com.zgy.seckill.entity.Seckill;
import com.zgy.seckill.entity.SuccessKilled;
import com.zgy.seckill.exception.RepeatKillException;
import com.zgy.seckill.exception.SeckillCloseException;
import com.zgy.seckill.exception.SeckillException;
import com.zgy.seckill.service.SeckillService;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

public class SeckillServiceImpl implements SeckillService {

    @Resource
    private SeckillDao seckillDao;

    @Resource
    private SuccessKilledDao successKilledDao;

    //md5盐值字符串，用于混淆md5
    private final String salt = "afisfiuhao18921hdqhq9d182";

    private final String getMd5(long seckillId) {
        String base = seckillId + "/" + salt;
        //利用工具类生成md5
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 10);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();

        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Override
    public SeckillExcution excuteSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        Date nowDate = new Date();
        //执行秒杀操作，减少库存
        //输入秒杀id和时间
        int updateCount = seckillDao.reduceNum(seckillId,nowDate);
        if (updateCount <= 0) {
            //没有更新到记录
            throw new SeckillCloseException("seckill is closed");
        } else {
            //秒杀成功，记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("repeat seckill");
            } else {
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExcution(seckillId,1,"秒杀成功",successKilled);
            }
        }
                
    }
}
