package com.pedroalvesz.createurllambda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UrlDataDTO {

    private String originalUrl;

    private Long expirationTime;

}
