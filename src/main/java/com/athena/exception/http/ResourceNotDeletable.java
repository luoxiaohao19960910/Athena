package com.athena.exception.http;

import com.athena.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tommy on 2017/10/12.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResourceNotDeletable extends BaseException {
    public List notDeletable;

    public ResourceNotDeletable(Object notDeletable){
        this(new ArrayList());
        this.notDeletable = new ArrayList();
        this.notDeletable.add(notDeletable);
    }

    public ResourceNotDeletable(List notDeletable) {
        this.notDeletable = notDeletable;
        this.code = 403;
        this.code = 4031;
        this.message = "cannot delete certain resource because some other resource is not deletable now";
    }
}
