package com.baidu.shop.web;

import com.baidu.shop.service.TemplateService;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @ClassName PageController
 * @Description: TODO
 * @Author Mr.Zheng
 * @Date 2020-09-23 19:32
 * @Version V1.0
 **/
//@Controller
//@RequestMapping(value = "item")
public class PageController {

    //@Autowired
    private TemplateService templateService;

    //@GetMapping(value = "/{spuId}.html")
    public String test(@PathVariable(value = "spuId") Integer spuId, ModelMap modelMap){

        Map<String, Object> map =templateService.getPageInfoBySpuId(spuId);
        modelMap.putAll(map);
        return "item";
    }
}
