package org.msc.anomalydetection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class EmployeeServiceController {

    @RequestMapping("/index")
    public String index() {

        return "Welcome";
    }

    @GetMapping("/getData")
    public HashMap<String, Object> getData() {

        HashMap<String, Object> map = new HashMap();
        map.put("key1", "value1");
        return map;
    }
}
