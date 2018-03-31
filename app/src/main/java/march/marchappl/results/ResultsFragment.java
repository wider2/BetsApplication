package march.marchappl.results;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import march.marchappl.model.Results;
import march.marchappl.predictions.PredictionsFragment;
import march.marchappl.utils.ToolbarHelper;


public class ResultsFragment extends Fragment implements IResultsFragment {

    private static final String TAG = "BETS_RESULTS";
    ResultsFragmentPresenter mPresenter;
    private Unbinder unbinder;
    ResultsAdapter adapterRv;
    //ScoresAdapter adapterRv;
    LinearLayoutManager layoutManager;
    List<Matches> mMatches;
    List<Prediction> mPredictions;
    List<Results> mResults;
    private Handler mPlHandler = new Handler();
    ToolbarHelper mToolbarHelper;

    @BindView(R.id.rv_results)
    RecyclerView mRecyclerView;

    @BindView(R.id.results_srl)
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_results, container, false);
        unbinder = ButterKnife.bind(this, v);

        mToolbarHelper = ToolbarHelper.from(getActivity(), v.findViewById(R.id.toolbar))
                .title(getString(R.string.app_name))
                .titleListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       refreshPrediction();
                                   }
                               }
                )
                .nextModeName(getString(R.string.restart))
                .nextModeListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          restartPredictions();
                                      }
                                  }
                )
                .progressBarColorRes(R.color.yellow)
                .insetsFrom(R.id.ll_root)
                .colorRes(R.color.n_orange);

        try {
            //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("layout_state", "1");
            editor.apply();

            mPresenter = new ResultsFragmentPresenter(this, getContext());

            mMatches = new ArrayList<>();

            layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.setItemPrefetchEnabled(true);
            layoutManager.setInitialPrefetchItemCount(25);
            mRecyclerView.setLayoutManager(layoutManager);

            adapterRv = new ResultsAdapter(mMatches);
            //adapterRv = new ScoresAdapter(mMatches);
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


    private void initData() {
        //mPresenter.getResultsList();
        mPresenter.checkLastSession();
    }


    SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initData();
        }
    };


    public void updateRv() {
        mRecyclerView.invalidate();

        adapterRv.updateListItems(mMatches, mPredictions, mResults);
    }

    public void restartPredictions() {
        mPresenter.deleteMatchesPredictions();
        updateRv();
    }

    @UiThread
    @Override
    public void refreshResult(List<Matches> resultList, List<Prediction> predictions, List<Results> results) {
        try {
            mMatches = resultList;
            mPredictions = predictions;
            mResults = results;

            if (resultList.size() <= 0) {
                tvOutput.setText(getString(R.string.not_found));
                mRecyclerView.setVisibility(View.INVISIBLE);
            } else {
                tvOutput.setText(getString(R.string.total_found) + ": " + Integer.toString(resultList.size()));
                mRecyclerView.setVisibility(View.VISIBLE);
            }

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
    public void refreshPrediction() {

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.home_fragment_container, new PredictionsFragment(), PredictionsFragment.class.getName())
                .addToBackStack(PredictionsFragment.class.getName())
                .commit();
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

}