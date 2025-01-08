package com.possible.mecash.dto.req;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmailDto {
    private String fromAddress;
    private List<String> toAddress;
    private List<String> ccAddress;
    private List<String> bccAddress;
    private String subject;
    private String content;
    private String templateName;
}
