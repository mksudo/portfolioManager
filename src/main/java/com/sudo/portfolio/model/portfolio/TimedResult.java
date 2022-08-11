package com.sudo.portfolio.model.portfolio;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;


@Data
@Builder
public class TimedResult<T> {
    private Instant time;
    private T data;
}
