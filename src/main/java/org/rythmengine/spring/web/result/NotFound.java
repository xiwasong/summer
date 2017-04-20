package org.rythmengine.spring.web.result;

import org.springframework.http.HttpStatus;

/**
 * Created by luog on 30/12/13.
 */
@SuppressWarnings("serial")
public class NotFound extends Result {

    /**
     * @param why a description of the problem
     */
    public NotFound(String why) {
        super(HttpStatus.NOT_FOUND, why);
    }

    public NotFound() {
        super(HttpStatus.NOT_FOUND);
    }

}
