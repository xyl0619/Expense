package com.in6206.payload;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyTotalDto(YearMonth month, BigDecimal totalAmount) {
}
