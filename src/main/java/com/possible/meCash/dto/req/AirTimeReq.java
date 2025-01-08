package com.possible.meCash.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AirTimeReq {

    private String sourceAccount;
    private String networkProvider;
    private String amount;
    private String phoneNumber;
    // TODO add validation
}
