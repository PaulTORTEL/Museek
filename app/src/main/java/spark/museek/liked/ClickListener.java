package spark.museek.liked;

import android.view.View;

public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }