package sunmi.dsc.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候, 由该类来接管程序,并记录发送错误报告.
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String APP_CACHE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/sunmi/dsc/log/";

    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // CrashHandler实例
    private static CrashHandler instance;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (instance == null)
            instance = new CrashHandler();
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Log.e("MyApplication", "error : ", e);
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        // 保存日志文件
        try {
            ex.printStackTrace(new PrintStream(createErrorFile()));
        } catch (Exception e) {
            Log.e("MyApplication", "crate file fail");
        }

        return true;
    }

    /**
     * 创建异常文件
     */
    private File createErrorFile() throws Exception {
        File dir = new File(APP_CACHE_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        String fileName = getCurrentDateString() + ".txt";
        return new File(dir, fileName);
    }

    /**
     * 获取当前日期
     */
    private String getCurrentDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss",
                Locale.getDefault());
        Date nowDate = new Date();
        return sdf.format(nowDate);
    }

}