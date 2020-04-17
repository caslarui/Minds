package custom_class;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class CourseListAdapter extends ArrayAdapter<String> {
    private Context mContext;

    public CourseListAdapter(@NonNull Context context, int resource, @NonNull String[] objects, Context mContext) {
        super(context, resource, objects);
        this.mContext = mContext;
    }
}
