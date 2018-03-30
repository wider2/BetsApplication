package march.marchappl.predictions;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import march.marchappl.BetsApplication;
import march.marchappl.R;
import march.marchappl.dagger.AppModule;
import march.marchappl.dagger.DaggerAppComponent;
import march.marchappl.model.Matches;
import march.marchappl.model.Prediction;
import march.marchappl.model.StatusSelector;
import march.marchappl.results.ResultsFragment;
import march.marchappl.utils.ToolbarHelper;


public class PredictionsFragment extends Fragment implements IPredictionsFragment {

    private static final String TAG = "BETS_MATCHES";
    PredictionsFragmentPresenter mPresenter;
    private Unbinder unbinder;
    DataAdapter adapterRv;
    LinearLayoutManager layoutManager;
    List<Matches> mMatches;
    List<Prediction> mPredictions;
    private Handler mPlHandler = new Handler();
    ToolbarHelper mToolbarHelper;
    private Dialog dAdd;
    int teamId1 = 0, teamId2 = 0, iScore1 = 0, iScore2 = 0;
    StatusSelector statusSelector;

    @BindView(R.id.rv_matches)
    RecyclerView mRecyclerView;

    @BindView(R.id.matches_srl)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.tv_output)
    TextView tvOutput;

    @BindView(R.id.toolbar_progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.bt_reload)
    Button btReload;

    @Inject
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BetsApplication app = BetsApplication.getApp();
        DaggerAppComponent.builder().appModule(new AppModule(app)).build().inject(this);
    }

    @AfterViews
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_predictions, container, false);
        unbinder = ButterKnife.bind(this, v);

        mToolbarHelper = ToolbarHelper.from(getActivity(), v.findViewById(R.id.toolbar))
                .title(getString(R.string.app_name))
                .nextModeName(getString(R.string.get_results))
                .nextModeListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          resultsDisplayMode();
                                      }
                                  }
                )
                .progressBarColorRes(R.color.yellow)
                .insetsFrom(R.id.ll_root)
                .colorRes(R.color.n_orange);

        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("layout_state", "0");
            editor.apply();

            statusSelector = new StatusSelector();
            mPresenter = new PredictionsFragmentPresenter(this, getContext());

            mMatches = new ArrayList<>();

            layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.setItemPrefetchEnabled(true);
            layoutManager.setInitialPrefetchItemCount(25);
            mRecyclerView.setLayoutManager(layoutManager);

            adapterRv = new DataAdapter(mMatches, mProductListener, getContext());
            mRecyclerView.setAdapter(adapterRv);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemViewCacheSize(25);
            mRecyclerView.setDrawingCacheEnabled(true);
            mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        mSwipeRefreshLayout.setEnabled(false);
                    } else {
                        mSwipeRefreshLayout.setEnabled(true);
                    }
                }
            });

            mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);

            adapterRv.notifyDataSetChanged();

            initData();

        } catch (Exception ex) {
            ex.printStackTrace();
            tvOutput.setText(ex.getMessage());
        }
        return v;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void resultsDisplayMode() {

        if (statusSelector.getStatus() == StatusSelector.IDLE) {
            tvOutput.setText(getString(R.string.no_bets_found));
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.home_fragment_container, new ResultsFragment(), ResultsFragment.class.getName())
                    .addToBackStack(ResultsFragment.class.getName())
                    .commit();
        }
    }

    private void initData() {
        tvOutput.setText(getString(R.string.please_wait_loading));
        mPresenter.checkLastSession();
    }


    SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initData();
        }
    };


    OnMatchRequestClickedListener mProductListener = new OnMatchRequestClickedListener() {
        @Override
        public void onProfileRequestClicked(int position, Matches match) {
            Log.wtf(TAG, "Profile clicked: " + position);

            initDialogAdd(position);
        }
    };

    protected void postAndNotifyAdapter(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recyclerView != null && !recyclerView.isComputingLayout()) {
                    adapter.notifyDataSetChanged();
                } else {
                    postAndNotifyAdapter(handler, recyclerView, adapter);
                }
            }
        }, 500);
    }

    public void updateRv() {
        mRecyclerView.invalidate();
        adapterRv.setList(mMatches, mPredictions);
        adapterRv.myNotify(mMatches);
        adapterRv.notifyItemRangeChanged(0, mMatches.size());
        postAndNotifyAdapter(mPlHandler, mRecyclerView, adapterRv);
    }

    private void checkStatus() {
        if (mPredictions.size() == 0) {
            statusSelector.setStatus(StatusSelector.IDLE);
        } else if (mPredictions.size() == 1) {
            statusSelector.setStatus(StatusSelector.LONELY);
        } else if (mPredictions.size() >= 2) {
            statusSelector.setStatus(StatusSelector.GROUP);
        } else if (mPredictions.size() >= 5) {
            statusSelector.setStatus(StatusSelector.MASSIVE);
        }
    }

    @UiThread
    @Override
    public void refreshResult(List<Matches> resultList, List<Prediction> predictions) {
        try {
            mMatches = resultList;
            mPredictions = predictions;

            if (resultList.size() <= 0) {
                tvOutput.setText(getString(R.string.not_found));
                mRecyclerView.setVisibility(View.GONE);
            } else {
                tvOutput.setText(getString(R.string.total_found) + ": " + Integer.toString(resultList.size()));
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            checkStatus();
            updateRv();

            mProgressBar.setVisibility(View.GONE);
            btReload.setVisibility(View.GONE);
            mSwipeRefreshLayout.setEnabled(false);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @UiThread
    @Override
    public void refreshPrediction(List<Prediction> predictions) {
        mPredictions = predictions;
        checkStatus();
        updateRv();
    }

    @UiThread
    @Override
    public void showException(Exception ex) {
        tvOutput.setText(getString(R.string.server_response) + ": " + ex.getMessage() + "\n" + ex.getStackTrace().toString());
    }

    @UiThread
    @Override
    public void showErrorServerResponse(Throwable response) {
        String msg = response.getMessage();
        tvOutput.setText(getString(R.string.server_response) + ": " + msg + "\n" + response.getStackTrace());
        if (msg.contains("Unable to resolve host") || msg.contains("No address associated with hostname") || msg.contains("403 Forbidden"))
            tvOutput.append("\n\n" + getString(R.string.no_response));

        mProgressBar.setVisibility(View.GONE);
        btReload.setVisibility(View.VISIBLE);
        btReload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                initData();
            }
        });
    }


    private void initDialogAdd(int position) {
        String teamName1, teamName2;
        iScore1 = 0;
        iScore2 = 0;

        dAdd = new Dialog(getContext(), R.style.DialogNoPaddingNoTitle);
        dAdd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dAdd.setContentView(R.layout.dialog_add);
        dAdd.setCancelable(true);
        dAdd.setCanceledOnTouchOutside(true);

        final TextView tvScore1 = (TextView) dAdd.findViewById(R.id.add_dialog_tv_score1);
        final TextView tvScore2 = (TextView) dAdd.findViewById(R.id.add_dialog_tv_score2);

        final Button tvScoreMinus1 = (Button) dAdd.findViewById(R.id.add_dialog_tv_score1_minus);
        tvScoreMinus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iScore1 > 0) iScore1 -= 1;
                tvScore1.setText(String.valueOf(iScore1));
            }
        });

        final Button tvScorePlus1 = (Button) dAdd.findViewById(R.id.add_dialog_tv_score1_plus);
        tvScorePlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iScore1 += 1;
                tvScore1.setText(String.valueOf(iScore1));
            }
        });


        final Button tvScoreMinus2 = (Button) dAdd.findViewById(R.id.add_dialog_tv_score2_minus);
        tvScoreMinus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iScore2 > 0) iScore2 -= 1;
                tvScore2.setText(String.valueOf(iScore2));
            }
        });

        final Button tvScorePlus2 = (Button) dAdd.findViewById(R.id.add_dialog_tv_score2_plus);
        tvScorePlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iScore2 += 1;
                tvScore2.setText(String.valueOf(iScore2));
            }
        });

        final TextView tvTeam1 = (TextView) dAdd.findViewById(R.id.add_dialog_team1);
        final TextView tvTeam2 = (TextView) dAdd.findViewById(R.id.add_dialog_team2);

        if (!mMatches.isEmpty()) {
            for (int i = 0; i < mMatches.size(); i++) {
                if (i == position) {
                    teamId1 = mMatches.get(i).getTeamId1();
                    teamId2 = mMatches.get(i).getTeamId2();
                    teamName1 = mMatches.get(i).getTeamName1();
                    teamName2 = mMatches.get(i).getTeamName2();
                    if (teamName1 != null) tvTeam1.setText(teamName1);
                    if (teamName2 != null) tvTeam2.setText(teamName2);
                }
            }
        }
        if (!mPredictions.isEmpty()) {
            for (Prediction item : mPredictions) {
                if (teamId1 == item.getTeamId1() || teamId2 == item.getTeamId2()) {
                    iScore1 = item.getScore1();
                    iScore2 = item.getScore2();
                    tvScore1.setText(String.valueOf(iScore1));
                    tvScore2.setText(String.valueOf(iScore2));
                }
            }
        }

        dAdd.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //When you touch outside of dialog bounds, the dialog gets canceled and this method executes.
                        dAdd.dismiss();
                        mRecyclerView.invalidate();
                        adapterRv.notifyDataSetChanged();
                    }
                }
        );

        final Button tvAdd = (Button) dAdd.findViewById(R.id.add_dialog_bt_add);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prediction prediction = new Prediction();
                prediction.setTeamId1(teamId1);
                prediction.setTeamId2(teamId2);
                prediction.setScore1(iScore1);
                prediction.setScore2(iScore2);
                mPresenter.saveMatchPrediction(prediction);

                dAdd.dismiss();
            }
        });

        Window window = dAdd.getWindow();
        //window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dAdd.show();
    }

}