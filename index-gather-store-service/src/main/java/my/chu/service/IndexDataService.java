package my.chu.service;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.chu.SpringContextUtil;
import my.chu.pojo.IndexData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
 
@Service
@CacheConfig(cacheNames="index_datas")
public class IndexDataService {
    private Map<String, List<IndexData>> indexDatas=new HashMap<>();
    @Autowired RestTemplate restTemplate;

    @Autowired IndexDataService indexDataService;
 
    @HystrixCommand(fallbackMethod = "third_part_service_timeout")
    public List<IndexData> fresh(String code) {
        List<IndexData> indexeDatas =fetchIndexData(code);

        indexDatas.put(code, indexeDatas);
         
        System.out.println("code:"+code);
        System.out.println("indexeDatas:"+indexDatas.get(code).size());

        indexDataService.remove(code);
        return indexDataService.store(code);
    }
     
    @CacheEvict(key="'indexData-code-'+ #p0")
    public void remove(String code){
    }
 
    @CachePut(key="'indexData-code-'+ #p0")
    public List<IndexData> store(String code){
        return indexDatas.get(code);
    }
 
    @Cacheable(key="'indexData-code-'+ #p0")
    public List<IndexData> get(String code){
        return CollUtil.toList();
    }
     
    public List<IndexData> fetchIndexData(String code){
        List<Map> temp= restTemplate.getForObject("http://127.0.0.1:8090/indexes/"+code+".json",List.class);
        return map2IndexData(temp);
    }
     
    private List<IndexData> map2IndexData(List<Map> temp) {
        List<IndexData> indexDatas = new ArrayList<>();
        for (Map map : temp) {
            String date = map.get("date").toString();
            float closePoint = Convert.toFloat(map.get("closePoint"));
            IndexData indexData = new IndexData();
            indexData.setDate(date);
            indexData.setClosePoint(closePoint);
            indexDatas.add(indexData);
        }
         
        return indexDatas;
    }
 
    public List<IndexData> third_part_service_timeout(String code){
        System.out.println("third_part_service_timeout()");
        IndexData index= new IndexData();
        index.setClosePoint(0);
        index.setDate("n/a");
        return CollectionUtil.toList(index);
    }
         
}