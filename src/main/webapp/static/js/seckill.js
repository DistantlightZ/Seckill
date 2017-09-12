//存放主要逻辑代码
//javascript模块化
var seckill = {
    //秒杀ajax相关URL
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        excution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/excution';
        }
    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    //获取秒杀地址，控制显示逻辑，执行秒杀
    handleSeckill: function (seckillId, node) {
        node.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //秒杀已开启
                    var killUrl = seckill.URL.excution(exposer['seckillId'], exposer['md5']);
                    //绑定一次点击事件
                    $('#killBtn').one('click', function () {
                        //绑定执行秒杀请求
                        $(this).addClass('disabled');
                        //发送秒杀请求
                        $.post(killUrl, {}, function (result) {
                            console.log(result);
                            if (result && result['success']) {
                                var excution = result['data'];
                                var state = excution['state'];
                                var stateInfo = excution['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-default">'+stateInfo+'</span>');
                            }
                        })
                    });
                    node.show();
                } else {
                    //由于时间偏差，秒杀未开启
                    var nowTime = exposer['nowTime'];
                    var startTime = exposer['startTime'];
                    var endTime = exposer['endTime'];
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                }
            } else {
                console.log('result:' + result);
            }
        })
    },
    //倒计时
    countDown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            //秒杀结束
            seckillBox.html('秒杀结束!');
        } else if (nowTime < startTime) {
            var killTime = new Date(startTime + 1000);
            //秒杀未开始,计时
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //时间完成后回调事件
            }).on('finish countdown', function () {
                seckill.handleSeckill(seckillId, seckillBox);
            })
        } else {
            seckillBox.html('秒杀进行中!');
            seckill.handleSeckill(seckillId, seckillBox);
        }

    },
    //秒杀相关逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证和登录，计时交互
            //规划交互流程
            //在cookie中查询手机号
            //获取数据
            var killPhone = $.cookie("killPhone");

            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                //如果未登录或者手机号码不匹配
                //控制弹出
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//开启位置关闭，如果是关闭，使用'static'
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        //将电话写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误</label>').show(300);
                    }
                });
            }


            //验证结束，开始计时交互
            var seckillId = params['seckillId'];
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result:' + result);
                }
            })


        }
    }
}