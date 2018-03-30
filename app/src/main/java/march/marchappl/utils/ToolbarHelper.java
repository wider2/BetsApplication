package march.marchappl.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.animation.SpringAnimation;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import march.marchappl.R;


public class ToolbarHelper {

    private static final String LOG_TAG = ToolbarHelper.class.getSimpleName();
    private final ProgressBar progressBar;
    private Button btNext = null;
    private TextView tvTitle = null;

    final Activity a;
    final View toolbar;
    final Resources res;
    private Context mContext;

    public ToolbarHelper(final Activity activity, View v) {
        a = activity;
        res = a.getResources();
        if (v != null) {
            toolbar = v.findViewById(R.id.toolbar);
        } else {
            toolbar = a.findViewById(R.id.toolbar);
        }
        tvTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) toolbar.findViewById(R.id.toolbar_progressbar);
        btNext = (Button) toolbar.findViewById(R.id.toolbar_bt_next);

        //if toolbar gets top insets only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    WindowInsets topInset = insets.replaceSystemWindowInsets(new Rect(0, insets.getSystemWindowInsetTop(), 0, 0));
                    return v.onApplyWindowInsets(topInset);
                }
            });
        }
    }

    public static ToolbarHelper from(Activity a) {
        return new ToolbarHelper(a, null);
    }

    public static ToolbarHelper from(Activity a, View v) {
        return new ToolbarHelper(a, v);
    }

    public ToolbarHelper title(int titleResId) {
        return title(res.getString(titleResId));
    }

    public ToolbarHelper title(String title) {
        if (tvTitle != null) {
            if (title == null) {
                Log.e(LOG_TAG, "title is null");
                title = "";
            }
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        return this;
    }

    public ToolbarHelper colorRes(@ColorRes int resId) {
        return colorInt(res.getColor(resId));
    }

    public ToolbarHelper colorInt(int color) {
        if (toolbar != null) {
            toolbar.setBackgroundColor(color);
        }
        return this;
    }

    public ToolbarHelper progressBar(boolean show) {
        if (progressBar != null) {
            if (show) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
        return this;
    }

    public ToolbarHelper setContext(Context ctx) {
        mContext = ctx;
        return this;
    }


    public ToolbarHelper progressBarColorRes(@ColorRes int colorRes) {
        if (progressBar != null) {
            progressBar.getIndeterminateDrawable().setColorFilter(ColorUtility.getColor(colorRes), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        return this;
    }

    public ToolbarHelper nextModeName(String title) {
        if (tvTitle != null) {
            if (title == null) {
                title = "";
            }
            btNext.setVisibility(View.VISIBLE);
            btNext.setText(title);
        }
        return this;
    }

    public ToolbarHelper nextModeListener(final View.OnClickListener listener) {
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
        return this;
    }

    public ToolbarHelper titleListener(final View.OnClickListener listener) {
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
            }
        });
        return this;
    }


    public ToolbarHelper insetsFrom(@IdRes int parentViewId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View contentView = a.findViewById(parentViewId);
            if (contentView != null) {
                contentView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        return v.onApplyWindowInsets(toolbar.onApplyWindowInsets(insets));
                    }
                });
            } else {
                Log.e(ToolbarHelper.class.getSimpleName(), "Can't find view to apply insets listener");
            }
        }
        return this;
    }


    public static void insetTopFromParent(final View child, View parent) {
        if (child != null && parent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                parent.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        WindowInsets topInset = insets.replaceSystemWindowInsets(new Rect(0, insets.getSystemWindowInsetTop(), 0, 0));
                        return v.onApplyWindowInsets(child.onApplyWindowInsets(topInset));
                    }
                });
            }
        }
    }

}