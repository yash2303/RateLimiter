package com.yashasvi;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TimestampCounter {
    private Long timestamp;
    private AtomicInteger counter;
}
