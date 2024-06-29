package com.jwt.example.models;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JwtResponse {
//response we send token

    private String jwtToken;
    private String username;


}
