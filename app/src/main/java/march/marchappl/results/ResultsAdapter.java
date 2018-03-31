package march.marchappl.results;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import march.marchappl.R;
import march.marchappl.model.Matches;
import march.marchappl.model.Prediction;
import march.marchappl.model.Results;
import march.marchappl.utils.ColorUtility;

import java.util.ArrayList;
import java.util.List;

import static march.marchappl.utils.GlobalConstants.COLOR_SELECTED;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private List<Matches> mList = new ArrayList<>();
    private List<Prediction> mPredictions;
    private List<Results> mResults;

    public ResultsAdapter(List<Matches> employeeList) {
        this.mList.addAll(employeeList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.cardview_results, parent, false);
        return new ViewHolder(view);
    }

    //trigger DiffUtil to detect changes.
    public void updateListItems(@NonNull List<Matches> List, @NonNull List<Prediction> predictions, @NonNull List<Results> results) {

        //calling the CallBack class and getting the difference between old and new list and dispatching it to the adapter.
        final ResultsDiffCallback diffCallback = new ResultsDiffCallback(this.mList, List);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.mList.clear();
        this.mList.addAll(List);
        this.mPredictions = predictions;
        this.mResults = results;

        //adapter will receive all the corresponding notifyItemRange events
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            if (bundle.size() != 0) {
                String team1 = bundle.getString("team1");
                if (team1 != null) {
                    holder.tvTeam1.setText(team1);
                }
                String team2 = bundle.getString("team2");
                if (team2 != null) {
                    holder.tvTeam2.setText(team2);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Matches match = mList.get(position);

        holder.tvTeam1.setText(match.getTeamName1());
        holder.tvTeam2.setText(match.getTeamName2());
        holder.tvScore1.setText("_");
        holder.tvScore2.setText("_");

        if (!mPredictions.isEmpty()) {
            for (int i = 0; i < mPredictions.size(); i++) {
                if (mPredictions.get(i).getTeamId1() == match.getTeamId1() && mPredictions.get(i).getTeamId2() == match.getTeamId2()) {
                    holder.tvScore1.setText(String.valueOf(mPredictions.get(i).getScore1()));
                    holder.tvScore2.setText(String.valueOf(mPredictions.get(i).getScore2()));
                    holder.cardView.setBackgroundColor(ColorUtility.getColor(COLOR_SELECTED));
                }
            }
        }
        if (!mResults.isEmpty()) {
            for (int i = 0; i < mResults.size(); i++) {
                if (mResults.get(i).getTeamId1() == match.getTeamId1() && mResults.get(i).getTeamId2() == match.getTeamId2()) {
                    holder.tvRealScore1.setText(String.valueOf(mResults.get(i).getTeam_points1()));
                    holder.tvRealScore2.setText(String.valueOf(mResults.get(i).getTeam_points2()));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTeam1, tvTeam2, tvScore1, tvScore2, tvRealScore1, tvRealScore2;

        private ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tvTeam1 = (TextView) itemView.findViewById(R.id.listitem_tv_team1);
            tvTeam2 = (TextView) itemView.findViewById(R.id.listitem_tv_team2);
            tvScore1 = (TextView) itemView.findViewById(R.id.listitem_tv_score1);
            tvScore2 = (TextView) itemView.findViewById(R.id.listitem_tv_score2);
            tvRealScore1 = (TextView) itemView.findViewById(R.id.listitem_tv_real_score1);
            tvRealScore2 = (TextView) itemView.findViewById(R.id.listitem_tv_real_score2);
        }
    }
}
