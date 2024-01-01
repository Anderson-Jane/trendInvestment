package my.chu;
 
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
 
@SpringBootApplication
@EnableEurekaClient
public class IndexGatherStoreApplication {
    public static void main(String[] args) {

        int port = 8001;
        int eurekaServerPort = 8761;

        if (NetUtil.isUsableLocalPort(eurekaServerPort)) {
            System.err.printf("检查到端口%d 未启用，判断 eureka 服务器没有启动，本服务无法使用，故退出%n", eurekaServerPort);
            System.exit(1);
        }

        if (args != null) {
            port = parsePort(args, port);
        }

        if (!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("端口%d被占用了，无法启动%n", port);
            System.exit(1);
        }
        new SpringApplicationBuilder(IndexGatherStoreApplication.class).properties("server.port=" + port).run(args);
    }

    private static int parsePort(String[] args, int defaultPort) {
        return Arrays.stream(args)
                .filter(arg -> arg.startsWith("port="))
                .findFirst()
                .map(arg -> StrUtil.subAfter(arg, "port=", true))
                .filter(NumberUtil::isNumber)
                .map(Convert::toInt)
                .orElse(defaultPort);
    }
     
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}