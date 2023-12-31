package my.chu.service;

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
    public List<Index> fetchIndexData() {

        List<Map> mapList = restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json", List.class);

        return map2Index(mapList);
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
