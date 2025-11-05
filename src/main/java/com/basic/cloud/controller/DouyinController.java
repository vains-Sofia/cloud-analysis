package com.basic.cloud.controller;

import com.basic.cloud.domain.DouyinVideoResponse;
import com.basic.cloud.service.DouyinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 抖音视频提取
 *
 * @author vains
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/douyin")
public class DouyinController {

    private final DouyinService douyinService;

    @GetMapping("/extract")
    public Mono<DouyinVideoResponse> extractVideo(String shareUrl) {
        return douyinService.extractVideo(shareUrl);
    }

}
