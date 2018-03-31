package march.marchappl.predictions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import march.marchappl.R;
import march.marchappl.model.Matches;
import march.marchappl.model.Prediction;
import march.marchappl.utils.ColorUtility;

import static march.marchappl.utils.GlobalConstants.COLOR_NORMAL;
import static march.marchappl.utils.GlobalConstants.COLOR_SELECTED;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private int lastPosition = -1;
    Context context;
    private List<Matches> mList;
    private List<Prediction> mPredictions;
    private OnMatchRequestClickedListener mListener;


    public DataAdapter(List<Matches> posts, OnMatchRequestClickedListener listener, Context ctx) {
        setHasStableIds(true);
        this.mList = posts;
        mListener = listener;
        context = ctx;
    }

    public void setList(List<Matches> list, List<Prediction> predictions) {
        mList = list;
        mPredictions = predictions;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_matches, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Matches match = mList.get(position);

        holder.cardView.setBackgroundColor(ColorUtility.getColor((position % 2 == 0) ? COLOR_SELECTED : COLOR_NORMAL));

        holder.tvTeam1.setText(match.getTeamName1());
        holder.tvTeam2.setText(match.getTeamName2());
        holder.tvScore1.setText("_");
        holder.tvScore2.setText("_");

        if (!mPredictions.isEmpty()) {
            for (int i = 0; i < mPredictions.size(); i++) {
                if (mPredictions.get(i).getTeamId1() == match.getTeamId1() && mPredictions.get(i).getTeamId2() == match.getTeamId2()) {
                    holder.tvScore1.setText(String.valueOf(mPredictions.get(i).getScore1()));
                    holder.tvScore2.setText(String.valueOf(mPredictions.get(i).getScore2()));
                }
            }
        }

        final ColorDrawable[] color = {
                new ColorDrawable(getRandomDarkerHSVColor()),
                new ColorDrawable(getRandomDarkerHSVColor())
        };

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize a new transition drawable
                TransitionDrawable trans = new TransitionDrawable(color);
                // Animate the background color of card view
                holder.cardView.setBackground(trans);
                trans.startTransition(1500);

                mListener.onProfileRequestClicked(position, match);
            }
        });

        setItemAnimation(holder.itemView, position);
    }

    private void setItemAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(199));
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getId();
    }

    public void myNotify(List<Matches> currentShows) {
        this.mList = currentShows;
        notifyDataSetChanged();
    }

    public int getRandomDarkerHSVColor() {

        int hue = new Random().nextInt(361);
        float saturation = 1.0f;
        float value = 0.8f;
        int alpha = 255;
        int color = Color.HSVToColor(alpha, new float[]{hue, saturation, value});
        return color;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTeam1, tvTeam2, tvScore1, tvScore2;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            tvTeam1 = (TextView) view.findViewById(R.id.listitem_tv_team1);
            tvTeam2 = (TextView) view.findViewById(R.id.listitem_tv_team2);
            tvScore1 = (TextView) view.findViewById(R.id.listitem_tv_score1);
            tvScore2 = (TextView) view.findViewById(R.id.listitem_tv_score2);
        }
    }
}