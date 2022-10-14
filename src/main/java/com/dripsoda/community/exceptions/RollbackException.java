package com.dripsoda.community.exceptions;

import com.dripsoda.community.enums.CommonResult;
import com.dripsoda.community.interfaces.IResult;

public class RollbackException extends Exception {
    public final IResult result;

    public RollbackException() {
        this(CommonResult.FAILURE);
    }

    public RollbackException(IResult result) {
        super();
        this.result = result;
    }
}