package com.athena.service;

import com.athena.model.AbstractMessage;

/**
 * Created by Tommy on 2017/11/18.
 */
public interface MessageService {
    void send(AbstractMessage message);
}
