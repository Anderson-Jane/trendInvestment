package my.chu.service;

import cn.hutool.core.collection.CollectionUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import my.chu.pojo.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {
    @Autowired
    RestTemplate restTemplate;

    private List<Index> indices;
    @HystrixCommand(fallbackMethod = "third_part_service_timeout")
    public List<Index> fetchIndexData() {

        List<Map> mapList = restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json", List.class);

        return map2Index(mapList);
    }

    public List<Index> third_part_service_timeout() {
        // todo: 常量定义后续common模块实现
        System.out.println("third_part_service_timeout()");
        Index index= new Index();
        index.setCode("000000");
        index.setName("第三方服务连接超时");
        return CollectionUtil.toList(index);
    }

    private List<Index> map2Index(List<Map> mapList) {
        List<Index> indices = new ArrayList<>();
        // todo : 后续改为其他调用方法进行转换
        for(Map map: mapList) {
            String code = map.get("code").toString();
            String name = map.get("name").toString();
            Index index= new Index();
            index.setCode(code);
            index.setName(name);
            indices.add(index);
        }
        return indices;
    }
}
