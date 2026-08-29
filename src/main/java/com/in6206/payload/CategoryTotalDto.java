package com.in6206.payload;

import java.math.BigDecimal;

public record CategoryTotalDto(String category, BigDecimal totalAmount, BigDecimal percentage) {
}
