package app.com.arresto.arresto_connect.custom_views;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;

import static com.bumptech.glide.util.Preconditions.checkNotNull;


public class AppBarLayoutNoEmptyScrollBehavior extends AppBarLayout.Behavior {

    AppBarLayout mAppBarLayout;
    RecyclerView mRecyclerView;
    public AppBarLayoutNoEmptyScrollBehavior(AppBarLayout appBarLayout, RecyclerView recyclerView) {
        mAppBarLayout = appBarLayout;
        mRecyclerView = recyclerView;
    }

    public boolean isRecylerViewScrollable(RecyclerView recyclerView) {
        int recyclerViewHeight = recyclerView.getHeight(); // Height includes RecyclerView plus AppBarLayout at same level
        int appCompatHeight    = mAppBarLayout != null ? mAppBarLayout.getHeight() : 0;
        recyclerViewHeight -= appCompatHeight;

        return recyclerView.computeVerticalScrollRange() > recyclerViewHeight;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        if (isRecylerViewScrollable(mRecyclerView)) {
            return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
        }
        return false;
    }

}