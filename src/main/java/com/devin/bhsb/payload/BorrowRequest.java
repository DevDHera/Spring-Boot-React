package com.devin.bhsb.payload;

import javax.validation.constraints.NotNull;

public class BorrowRequest {
    @NotNull
    private Long copyId;

    public Long getCopyId() {
        return copyId;
    }

    public void setCopyId(Long copyId) {
        this.copyId = copyId;
    }
}
