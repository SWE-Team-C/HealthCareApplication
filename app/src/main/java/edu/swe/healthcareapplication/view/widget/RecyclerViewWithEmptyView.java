package edu.swe.healthcareapplication.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class RecyclerViewWithEmptyView extends RecyclerView {

  private View mEmptyView;

  private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
    @Override
    public void onChanged() {
      super.onChanged();
      checkIsEmpty();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      super.onItemRangeInserted(positionStart, itemCount);
      checkIsEmpty();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      super.onItemRangeRemoved(positionStart, itemCount);
      checkIsEmpty();
    }
  };

  public RecyclerViewWithEmptyView(
      @NonNull Context context) {
    super(context);
  }

  public RecyclerViewWithEmptyView(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public RecyclerViewWithEmptyView(@NonNull Context context, @Nullable AttributeSet attrs,
      int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public void setAdapter(@Nullable Adapter adapter) {
    final Adapter oldAdapter = getAdapter();
    if (oldAdapter != null) {
      oldAdapter.unregisterAdapterDataObserver(mDataObserver);
    }
    super.setAdapter(adapter);
    if (adapter != null) {
      adapter.registerAdapterDataObserver(mDataObserver);
    }
    checkIsEmpty();
  }

  private void checkIsEmpty() {
    if (mEmptyView != null && getAdapter() != null) {
      if (getAdapter().getItemCount() == 0) {
        mEmptyView.setVisibility(VISIBLE);
      } else {
        mEmptyView.setVisibility(GONE);
      }
    }
  }

  public void setEmptyView(View emptyView) {
    this.mEmptyView = emptyView;
    checkIsEmpty();
  }
}
