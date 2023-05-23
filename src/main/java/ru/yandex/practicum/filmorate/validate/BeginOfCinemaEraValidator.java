package ru.yandex.practicum.filmorate.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BeginOfCinemaEraValidator implements ConstraintValidator<BeginOfCinemaEra, LocalDate> {

    private LocalDate date;

    public void initialize(BeginOfCinemaEra annotation) {
        date = LocalDate.parse(annotation.value());
    }

    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        boolean valid = true;
        if (value != null) {
            if (!value.isAfter(date)) {
                valid = false;
            }
        }
        return valid;
    }
}