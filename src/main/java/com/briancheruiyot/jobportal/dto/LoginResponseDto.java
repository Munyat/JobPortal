package com.briancheruiyot.jobportal.dto;

public record LoginResponseDto(String message, UserDto user, String jwtToken) {

}
