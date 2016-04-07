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
    public void test() throws Exception {
        HandlerThread thread1=new HandlerThread("s");
        System.out.println("原本" + Thread.currentThread().getId());
        thread1.start();
        android.os.Handler handler1=new android.os.Handler(thread1.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                System.out.println("我"+Thread.currentThread().getId());
                super.handleMessage(msg);
            }

        };
        handler1.sendEmptyMessage(1);

        System.out.println("你" + Thread.currentThread().getId());
        Threadss thread2=new Threadss();
        handler2.sendEmptyMessage(3);

    }
    private android.os.Handler handler2=null;
    class Threadss extends Thread{
        @Override
        public void run() {
            Looper.prepare();
            handler2= new android.os.Handler() {
                @Override
                public void handleMessage(Message msg) {
                    System.out.println("handle2" + Thread.currentThread().getId());
                    super.handleMessage(msg);
                }
            };
        }
    }

}