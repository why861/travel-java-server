package org.example.traveljavaserver.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamErrorVO {
    private String error;
    public static StreamErrorVO of(String error) {
        return new StreamErrorVO(error);
    }
}
