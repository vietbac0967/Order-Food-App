package com.bac.se.server.dto.requests;


public record RegisterRequest(String username,
                              String email,
                              String password,
                              String address,
                              String phone

) {

}
