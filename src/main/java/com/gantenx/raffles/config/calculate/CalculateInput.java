package com.gantenx.raffles.config.calculate;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateInput implements Serializable {

    private static final long serialVersionUID = -1153906651043317309L;

    private int numberFirst;

    private int numberSecond;
}
