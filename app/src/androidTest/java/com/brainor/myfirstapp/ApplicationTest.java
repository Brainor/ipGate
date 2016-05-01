package com.brainor.myfirstapp;

import android.app.Application;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import java.io.Console;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


}