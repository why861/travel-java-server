package org.example.traveljavaserver.vo;

import lombok.Data;

@Data
public class StreamDoneVO {
    private Boolean done = true;

    public static StreamDoneVO of() {
        return new StreamDoneVO();
    }
}
