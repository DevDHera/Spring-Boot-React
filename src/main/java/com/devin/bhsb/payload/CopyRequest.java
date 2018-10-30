package com.devin.bhsb.payload;

import javax.validation.constraints.NotBlank;

public class CopyRequest {
    @NotBlank
    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
