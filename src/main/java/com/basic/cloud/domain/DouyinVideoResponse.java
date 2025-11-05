package com.basic.cloud.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 抖音视频响应bean
 *
 * @author vains
 */
@Data
public class DouyinVideoResponse implements Serializable {

    /**
     * 视频描述
     */
    private String desc;

    /**
     * 作者昵称
     */
    private String authorNickname;

    /**
     * 视频无水印地址
     */
    private String videoUrl;

    /**
     * 视频封面
     */
    private String videoCover;

}
