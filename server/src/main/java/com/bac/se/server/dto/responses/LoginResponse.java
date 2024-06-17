package com.bac.se.server.dto.responses;

public record LoginResponse(String accessToken, String refreshToken) {
}
