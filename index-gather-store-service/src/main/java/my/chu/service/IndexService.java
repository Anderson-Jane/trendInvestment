package my.chu.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import my.chu.pojo.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames="indexes")
public class IndexService {
    @Autowired
    RestTemplate restTemplate;

    /**
    * 自我注入形式解决直接在类内部调用方法
    * （如 remove 和 store）不会触发 Spring 的代理相关功能，
    * 例如缓存相关的注解（@Cacheable、@CacheEvict）
    **/
    @Autowired
    private IndexService self; //

    private List<Index> indexes;

    public List<Index> fetchIndex() {

        List<Map> mapList = restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json", List.class);

        return map2Index(mapList);
    }
    @HystrixCommand(fallbackMethod = "third_part_service_timeout")
    public List<Index> fresh() {
        indexes =fetchIndex(); // 获取指数数据到成员变量中
        self.remove();
        return self.store();
    }
    @CacheEvict(allEntries=true)
    public void remove(){
    }

    @Cacheable(key="'all_codes'")
    public List<Index> get(){
        return CollUtil.toList();
    }

    @Cacheable(key="'all_codes'")
    public List<Index> store(){
        return indexes;
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
        List<Index> indexes = new ArrayList<>();
        // todo : 后续改为其他调用方法进行转换
        for(Map map: mapList) {
            String code = map.get("code").toString();
            String name = map.get("name").toString();
            Index index= new Index();
            index.setCode(code);
            index.setName(name);
            indexes.add(index);
        }
        return indexes;
    }
}
