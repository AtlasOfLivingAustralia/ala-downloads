/*
 *  Copyright (C) 2010 Atlas of Living Australia
 *  All Rights Reserved.
 * 
 *  The contents of this file are subject to the Mozilla Public
 *  License Version 1.1 (the "License"); you may not use this file
 *  except in compliance with the License. You may obtain a copy of
 *  the License at http://www.mozilla.org/MPL/
 * 
 *  Software distributed under the License is distributed on an "AS
 *  IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  rights and limitations under the License.
 */

package org.ala.util;

import java.io.IOException;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for ImageUtils
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
public class ImageUtilsTest {
    ImageUtils imageUtils;
    String fileName = "/penguin.jpg";
    String filePath;

    public ImageUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        URL url = this.getClass().getResource(fileName);
        //System.out.println("Pengiun image URL = "+url.getPath());
        imageUtils = new ImageUtils();
        filePath = url.getPath();
        assertNotNull("Failed to find test image ("+fileName+") on classpath:", filePath);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of load method, of class ImageUtils.
     */
    @Test
    public void testLoad() throws Exception {
        imageUtils.load(filePath);
        System.out.println("original image has dimentions: "+imageUtils.getOriginalImage().getHeight()+"|"+imageUtils.getOriginalImage().getWidth());
        assertEquals("image file is not expected height", 279, imageUtils.getOriginalImage().getHeight());
        assertEquals("image file is not expected width", 176, imageUtils.getOriginalImage().getWidth());
    }

    /**
     * Test of square method, of class ImageUtils.
     */
    @Test
    public void testSquare() throws IOException {
        imageUtils.load(filePath);
        imageUtils.square();
        System.out.println("square image has dimentions: "+imageUtils.getModifiedImage().getHeight()+"|"+imageUtils.getModifiedImage().getWidth());
        assertEquals("image file is not expected height", 176, imageUtils.getModifiedImage().getHeight());
        assertEquals("image file is not expected width", 176, imageUtils.getModifiedImage().getWidth());
    }

    /**
     * Test of crop method, of class ImageUtils.
     */
    @Test
    public void testCrop() throws IOException {
        imageUtils.load(filePath);
        imageUtils.crop(76, 79);
        System.out.println("crop image has dimentions: "+imageUtils.getModifiedImage().getHeight()+"|"+imageUtils.getModifiedImage().getWidth());
        assertEquals("image file is not expected height", 200, imageUtils.getModifiedImage().getHeight());
        assertEquals("image file is not expected width", 100, imageUtils.getModifiedImage().getWidth());
    }

    /**
     * Test of thumbnail method, of class ImageUtils.
     */
    @Test
    public void testThumbnail() throws IOException {
        imageUtils.load(filePath);
        imageUtils.thumbnail(100);
        System.out.println("thumbnail image has dimentions: "+imageUtils.getModifiedImage().getHeight()+"|"+imageUtils.getModifiedImage().getWidth());
        assertEquals("image file is not expected height", 100, imageUtils.getModifiedImage().getHeight());
        assertEquals("image file is not expected width", 63, imageUtils.getModifiedImage().getWidth());
    }

    /**
     * Test of square + thumbnail method, of class ImageUtils.
     */
    @Test
    public void testSquareThumbnail() throws IOException {
        long start = System.currentTimeMillis();
        imageUtils.load(filePath);
        imageUtils.square();
        imageUtils.thumbnail(100);
        long finish = System.currentTimeMillis();
        System.out.println("square thumbnail image has dimentions: "+imageUtils.getModifiedImage().getHeight()+"|"+imageUtils.getModifiedImage().getWidth());
        System.out.println("generated in: "+ (finish-start)+" ms");
        assertEquals("image file is not expected height", 100, imageUtils.getModifiedImage().getHeight());
        assertEquals("image file is not expected width", 100, imageUtils.getModifiedImage().getWidth());
    }

    /**
     * Test of smoothThumbnail method, of class ImageUtils.
     */
    @Test
    public void testSmoothThumbnail() throws IOException {
        imageUtils.load(filePath);
        imageUtils.smoothThumbnail(100);
        System.out.println("smooth thumbnail image has dimentions: "+imageUtils.getModifiedImage().getHeight()+"|"+imageUtils.getModifiedImage().getWidth());
        assertEquals("image file is not expected height", 100, imageUtils.getModifiedImage().getHeight());
        assertEquals("image file is not expected width", 63, imageUtils.getModifiedImage().getWidth());
    }

    /**
     * Test of square + smoothThumbnail method, of class ImageUtils.
     */
    @Test
    public void testSquareSmoothThumbnail() throws IOException {
        long start = System.currentTimeMillis();
        imageUtils.load(filePath);
        imageUtils.square();
        imageUtils.smoothThumbnail(100);
        long finish = System.currentTimeMillis();
        System.out.println("square smooth thumbnail image has dimentions: "+imageUtils.getModifiedImage().getHeight()+"|"+imageUtils.getModifiedImage().getWidth());
        System.out.println("generated in: "+ (finish-start)+" ms");
        assertEquals("image file is not expected height", 100, imageUtils.getModifiedImage().getHeight());
        assertEquals("image file is not expected width", 100, imageUtils.getModifiedImage().getWidth());
    }

    /**
     * Test of writeResult method, of class ImageUtils.
     */
    @Test
    public void testWriteResult() throws Exception {
    }

    /**
     * Test of getModifiedImage method, of class ImageUtils.
     */
    @Test
    public void testGetModifiedImage() {
    }

    /**
     * Test of getOriginalImage method, of class ImageUtils.
     */
    @Test
    public void testGetOriginalImage() {
    }

}