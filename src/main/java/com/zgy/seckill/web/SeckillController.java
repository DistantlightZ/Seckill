package com.zgy.seckill.web;

import com.zgy.seckill.dto.Exposer;
import com.zgy.seckill.dto.SeckillExcution;
import com.zgy.seckill.dto.SeckillResult;
import com.zgy.seckill.entity.Seckill;
import com.zgy.seckill.enums.SeckillStateEnum;
import com.zgy.seckill.exception.RepeatKillException;
import com.zgy.seckill.exception.SeckillCloseException;
import com.zgy.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {

        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("list", seckillList);
        return "list";
    }


    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId")Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }



    @RequestMapping(
            value = "/{seckillId}/{md5}/excution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"}

    )
    @ResponseBody
    public SeckillResult<SeckillExcution> excute(@PathVariable("seckillId")Long seckillId,
                                                 @PathVariable("md5")String md5,
                                                 @CookieValue(value = "killPhone",required = false)Long phone
                                                 ) {
        if (phone == null) {
            return new SeckillResult<SeckillExcution>(false,"未注册");
        }
        SeckillResult<SeckillExcution> result;
        try {
            SeckillExcution seckillExcution = seckillService.excuteSeckill(seckillId, phone, md5);
            result = new SeckillResult<SeckillExcution>(true, seckillExcution);
        } catch (RepeatKillException e) {
            SeckillExcution seckillExcution = new SeckillExcution(seckillId, SeckillStateEnum.REPEAT);
            result = new SeckillResult<SeckillExcution>(true, seckillExcution);
        } catch (SeckillCloseException e) {
            SeckillExcution seckillExcution = new SeckillExcution(seckillId, SeckillStateEnum.END);
            result = new SeckillResult<SeckillExcution>(true, seckillExcution);
        } catch (Exception e) {
            SeckillExcution seckillExcution = new SeckillExcution(seckillId, SeckillStateEnum.INNOERROR);
            result = new SeckillResult<SeckillExcution>(true, seckillExcution);
        }
        return result;
    }


    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult(true,now.getTime());
    }


}
