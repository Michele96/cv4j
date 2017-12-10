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
package com.cv4j.core.datamodel;
/**
 * The Rect class of DataModel
 */
public class Rect {

    /**
     * Coordinate x
     */
    public int x;
    
    /**
     * Coordinate y
     */
    public int y;
    
    /**
     * Rectangle width
     */
    public int width;
    
    /**
     * Rectangle height
     */
    public int height;
    
    /**
     * Label for ccl.
     * Just use it for ccl 
     */
    public int labelIdx;

    /**
     * Return point tl
     */
    public Point tl() {
        return new Point(x, y);
    }

    /**
     * Return point br
     */
    public Point br() {
        return new Point(x+width, y+height);
    }
}
