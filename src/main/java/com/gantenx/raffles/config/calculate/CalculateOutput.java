package com.gantenx.raffles.config.calculate;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateOutput implements Serializable {

    private static final long serialVersionUID = 3351123132946647886L;

    private long multiplyResult;

    private long addResult;
}
