package my.chu.controller;
 
import java.util.List;

import my.chu.pojo.Index;
import my.chu.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
  
@RestController
public class IndexController {
    @Autowired
    IndexService indexService;
 
    @GetMapping("/getCodes")
    public List<Index> get() throws Exception {
        return indexService.fetchIndexData();
    }
}