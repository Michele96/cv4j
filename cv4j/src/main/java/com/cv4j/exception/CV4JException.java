/*
 * Copyright (c) 2017-present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.exception;

/**
 * The basic exception for CV4J related components.
 */
public class CV4JException extends RuntimeException {

    private static final long serialVersionUID = -2565764903880816387L;

    public CV4JException(String message) {
        super(message);
    }

    public CV4JException(Throwable throwable) {
        super(throwable);
    }

    public CV4JException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
