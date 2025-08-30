package com.example.PetApp.common.util;

import java.time.LocalDate;
import java.time.Period;

public class AgeUtil {

    public static int CalculateAge(LocalDate dogBirthDate) {
        return Period.between(dogBirthDate, LocalDate.now()).getYears();
    }
}
