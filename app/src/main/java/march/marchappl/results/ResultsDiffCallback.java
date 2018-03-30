package march.marchappl.results;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import march.marchappl.model.Matches;

import java.util.List;

public class ResultsDiffCallback extends DiffUtil.Callback {

    private final List<Matches> mOldMatchList;
    private final List<Matches> mNewMatchList;

    public ResultsDiffCallback(List<Matches> oldMatchList, List<Matches> newMatchList) {
        this.mOldMatchList = oldMatchList;
        this.mNewMatchList = newMatchList;
    }

    @Override
    public int getOldListSize() {
        return mOldMatchList != null ? mOldMatchList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewMatchList != null ? mNewMatchList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldMatchList.get(oldItemPosition).getId() == mNewMatchList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Matches oldMatch = mOldMatchList.get(oldItemPosition);
        final Matches newMatch = mNewMatchList.get(newItemPosition);
        return oldMatch.getTeamId1() == (newMatch.getTeamId1());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {

        Matches newItem = mNewMatchList.get(newItemPosition);
        Matches oldItem = new Matches("", 0, "", 0);
        if (oldItemPosition < mOldMatchList.size()) oldItem = mOldMatchList.get(oldItemPosition);

        Bundle diff = new Bundle();
        if (newItem.getTeamId1() != (oldItem.getTeamId1())) {
            diff.putString("team1", newItem.getTeamName1());
        }
        if (newItem.getTeamId2() != (oldItem.getTeamId2())) {
            diff.putString("team2", newItem.getTeamName2());
        }
        if (diff.size() == 0) {
            return null;
        }
        return diff;
    }
}
