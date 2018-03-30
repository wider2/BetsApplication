package march.marchappl.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class StatusSelector {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IDLE, LONELY, GROUP, MASSIVE})
    public @interface StatusMode {}

    public static final int IDLE = 0;
    public static final int LONELY = 1;
    public static final int GROUP = 2;
    public static final int MASSIVE = 3;

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}