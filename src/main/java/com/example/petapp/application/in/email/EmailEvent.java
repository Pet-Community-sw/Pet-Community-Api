package com.example.petapp.application.in.email;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailEvent {
    Long id;
    String toEmail;
    String subject;
    String code;
}
