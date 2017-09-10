-- 数据初始化脚本

-- 创建数据库

CREATE DATABASE seckill ;

-- 使用数据库
USE seckill;

-- 创建秒杀库存表
CREATE TABLE seckill(
  `seckill_id` bigint not null auto_increment comment '商品库存id',
  `name` VARCHAR(120) NOT NULL COMMENT '商品名称',
  `number` int NOT NULL COMMENT '库存数量',
  `start_time` TIMESTAMP NOT NULL COMMENT '秒杀开启时间',
  `end_time` TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT current_timestamp COMMENT '秒杀创建时间',
  PRIMARY KEY (seckill_id),
  key idx_start_time(start_time),
  key idx_end_time(end_time),
  KEY idx_creat_time(create_time)

)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

  -- 初始化数据
INSERT INTO
  seckill(name, number, start_time, end_time)
VALUES
  ('1000元秒杀iphone7',100,'2017-3-1','2017-3-2'),
  ('500元秒杀ipad2',200,'2017-3-1','2017-3-2'),
  ('300元秒杀小米5',300,'2017-3-1','2017-3-2'),
  ('200元秒杀红米note',50,'2017-3-1','2017-3-2');

-- 秒杀成功明细表

CREATE TABLE success_killed(
 `seckill_id` BIGINT NOT NULL COMMENT '秒杀商品id',
  `user_phone` BIGINT NOT NULL COMMENT '用户手机',
  `state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态表示：-1无效，0成功，1已付款，2已发货',
  `create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`), /*联合主键*/
  key idx_create_time(`create_time`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀明细表' ;


