package com.devin.bhsb.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AuthorRequest {
    @NotBlank
    @Size(max = 40)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
