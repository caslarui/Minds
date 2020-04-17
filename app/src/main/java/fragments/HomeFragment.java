package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.minds.R;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private final String mFragmentName = "Main Page";

    private ListView mCourseList;
    private TextView topMenuText;
    private Button   mBtnYear1;
    private Button   mBtnYear2;
    private Button   mBtnYear3;
    private Button   mBtnYear4;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        initializeControllers(v);

        topMenuText.setText(mFragmentName);

        mBtnYear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseList(v, 1);
            }
        });
        mBtnYear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseList(v, 2);
            }
        });
        mBtnYear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseList(v, 3);
            }
        });
        mBtnYear4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseList(v, 4);
            }
        });

        return v;
    }
    private void showCourseList(View v, int year) {
        String[] mListItem;
        if (year == 1) {
            mListItem = getResources().getStringArray(R.array.Year1_list);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.course_item, R.id.courseName,  mListItem);
            mCourseList.setAdapter(adapter);
        }
        if (year == 2) {
            mListItem = getResources().getStringArray(R.array.Year2_list);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.course_item, R.id.courseName,  mListItem);
            mCourseList.setAdapter(adapter);
        }
        if (year == 3) {
            mListItem = getResources().getStringArray(R.array.Year3_list);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.course_item, R.id.courseName,  mListItem);
            mCourseList.setAdapter(adapter);
        }
        if (year == 4) {
            mListItem = getResources().getStringArray(R.array.Year4_list);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.course_item, R.id.courseName,  mListItem);
            mCourseList.setAdapter(adapter);
        }
    }
    private void initializeControllers(View v) {
        mCourseList = v.findViewById(R.id.coursesList);
        mBtnYear1 = v.findViewById(R.id.btnYear1);
        mBtnYear2 = v.findViewById(R.id.btnYear2);
        mBtnYear3 = v.findViewById(R.id.btnYear3);
        mBtnYear4 = v.findViewById(R.id.btnYear4);
        topMenuText = (TextView) Objects.requireNonNull(getActivity()).findViewById(R.id.topMenu_text);
    }


}
