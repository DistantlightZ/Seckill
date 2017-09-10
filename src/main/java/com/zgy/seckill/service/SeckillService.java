package com.zgy.seckill.service;

import com.zgy.seckill.dto.Exposer;
import com.zgy.seckill.dto.SeckillExcution;
import com.zgy.seckill.entity.Seckill;
import com.zgy.seckill.exception.RepeatKillException;
import com.zgy.seckill.exception.SeckillCloseException;
import com.zgy.seckill.exception.SeckillException;

import java.util.List;

public interface SeckillService {

    /**
     * 获取所有秒杀的信息
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 根据id获取单个秒杀信息
     * @param id
     * @return
     */
    Seckill getById(long seckillId);


    /**
     * 秒杀开始时输出秒杀地址，否则输出系统时间和秒杀时间
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     */
    SeckillExcution excuteSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException,RepeatKillException,SeckillCloseException;


}
