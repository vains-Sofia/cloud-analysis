package com.basic.cloud.service;

import com.basic.cloud.domain.DouyinVideoResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抖音服务
 *
 * @author vains
 */
@Service
public class DouyinService {

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public DouyinService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        HttpClient httpClient = HttpClient.create()
                .headers(h -> h.add("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1"))
                // 自动跟随 301 / 302 / 307 / 308
                .followRedirect(true);

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    /**
     * 根据分析链接获取真实视频地址
     *
     * @param shareUrl 分享链接
     * @return 视频信息
     */
    public Mono<DouyinVideoResponse> extractVideo(String shareUrl) {
        return webClient.get()
                .uri(shareUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(html -> {
                    String jsonText;
                    Pattern pattern = Pattern.compile("window\\._ROUTER_DATA\\s*=\\s*(.*?)</script>", Pattern.DOTALL);
                    Matcher matcher = pattern.matcher(html);
                    if (matcher.find()) {
                        jsonText = matcher.group(1).trim();
                    } else {
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

                        String desc = dataNode.path("desc").asText();
                        String nickname = dataNode.path("author").path("nickname").asText();

                        String videoCover = dataNode
                                .path("video")
                                .path("cover")
                                .path("url_list")
                                .get(0)
                                .asText();

                        DouyinVideoResponse douyinVideoResponse = new DouyinVideoResponse();
                        douyinVideoResponse.setDesc(desc);
                        douyinVideoResponse.setVideoUrl(videoUrl);
                        douyinVideoResponse.setVideoCover(videoCover);
                        douyinVideoResponse.setAuthorNickname(nickname);
                        return Mono.just(douyinVideoResponse);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("解析 JSON 失败", e));
                    }
                });
    }

}
