package com.basic.cloud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 测试
 *
 * @author vains
 */
public class Test01 {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        String url = "https://v.douyin.com/6kZKQ0kIjXs";

        // 创建 Netty HttpClient 并启用自动重定向
        HttpClient httpClient = HttpClient.create()
                .headers(h -> h.add("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1"))
                .followRedirect(true); // 自动跟随 301 / 302 / 307 / 308

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        // 这个接口会重定向两次
        String string = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(html -> {
                    String jsonText = extractJson(html);
                    if (jsonText == null) {
                        return Mono.error(new IllegalStateException("解析视频 JSON 失败"));
                    }

                    try {
                        JsonNode root = objectMapper.readTree(jsonText);
                        JsonNode dataNode = root
                                .path("loaderData")
                                .findPath("video_(id)/page")
                                .path("videoInfoRes")
                                .path("item_list")
                                .get(0);

                        if (dataNode == null || dataNode.isMissingNode()) {
                            return Mono.error(new IllegalStateException("未找到视频数据"));
                        }

                        String videoUrl = dataNode
                                .path("video")
                                .path("play_addr")
                                .path("url_list")
                                .get(0)
                                .asText()
                                .replace("playwm", "play");

                        return Mono.just(videoUrl);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("解析 JSON 失败", e));
                    }
                }).block();
        System.out.println(string);
    }

    private static String extractJson(String html) {
        Pattern pattern = Pattern.compile("window\\._ROUTER_DATA\\s*=\\s*(.*?)</script>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

}
