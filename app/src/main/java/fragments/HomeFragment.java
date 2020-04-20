package fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minds.R;

import java.util.Objects;

import custom_class.Constants;

public class HomeFragment extends Fragment {

    private final String    mFragmentName = "Main Page";
    private String[]        mListItem;


    private ListView mCourseList;
    private TextView topMenuText;
    private Button   mBtnYear1;
    private Button   mBtnYear2;
    private Button   mBtnYear3;
    private Button   mBtnYear4;
    private String   link = Constants.DB_UPLOADS + "/";
    String year = "";


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
                year = "Y1/";
            }
        });
        mBtnYear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseList(v, 2);
                year = "Y2/";
            }
        });
        mBtnYear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseList(v, 3);
                year = "Y3/";
            }
        });
        mBtnYear4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCourseList(v, 4);
                year = "Y4/";
            }
        });

        mCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String[] courseWords = mListItem[position].split(" ");
                StringBuilder ret = new StringBuilder();
                for(String word : courseWords) {
                    ret.append(word.charAt(0));
                }
                link += year;
                link += ret.toString();

                openFragment(new CoursePageList(mListItem[position], link));
            }
        });

        return v;
    }
    private void showCourseList(View v, int year) {
        ArrayAdapter<String> adapter;
        switch (year) {
            case 1 :
                mListItem = getResources().getStringArray(R.array.Year1_list);
                break;
            case 2 :
                mListItem = getResources().getStringArray(R.array.Year2_list);
                break;
            case 3 :
                mListItem = getResources().getStringArray(R.array.Year3_list);
                break;
            case 4 :
                mListItem = getResources().getStringArray(R.array.Year4_list);
                break;
            default:
                Toast.makeText(getContext(), "Error Showing course List\nIncorrect year",
                        Toast.LENGTH_SHORT).show(); return;
        }
        adapter = new ArrayAdapter<>(getContext(), R.layout.course_item, R.id.courseName,  mListItem);
        mCourseList.setAdapter(adapter);
    }
    private void initializeControllers(View v) {
        mCourseList = v.findViewById(R.id.coursesList);
        mBtnYear1 = v.findViewById(R.id.btnYear1);
        mBtnYear2 = v.findViewById(R.id.btnYear2);
        mBtnYear3 = v.findViewById(R.id.btnYear3);
        mBtnYear4 = v.findViewById(R.id.btnYear4);
        topMenuText = (TextView) Objects.requireNonNull(getActivity()).findViewById(R.id.topMenu_text);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
