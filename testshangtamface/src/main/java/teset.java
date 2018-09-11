import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Looper.loop;

/**
 * Created by Tao on 2018/6/7 0007.
 */

public class teset {




    public static void main(String [] a){


        final Thread1 thread1 = new Thread1();

        thread1.start();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                thread1.close();

            }
        }).start();


    }

   static class  Thread1 extends  Thread{
        @Override
        public void run() {
            try {
            while (!isInterrupted()){

                System.out.println(" 线程执行 "  + Thread.currentThread());
                    sleep(500);
//                close();
            }
            } catch ( Exception e) {
                e.printStackTrace();
                System.out.println(" 线程异常 "  + Thread.currentThread());

            }finally {
                System.out.println(" 线程结束 "  + Thread.currentThread());
            }

        }

        public  void  close(){
            if (!isInterrupted() && isAlive())
                interrupt();
        }
    }

}
