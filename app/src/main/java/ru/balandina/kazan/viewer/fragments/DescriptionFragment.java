package ru.balandina.kazan.viewer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.balandina.kazan.R;

public class DescriptionFragment extends Fragment {

    private static DescriptionFragment instance;
    private static String title;
    private static String description;
    private Unbinder unbinder;
    @BindView(R.id.placeTitle)
    TextView placeTitle;
    @BindView(R.id.placeText)
    TextView placeText;

    public static DescriptionFragment getInstance(String titleSet, String descriptionSet){
        title = titleSet;
        description = descriptionSet;
        if (instance == null){
            instance = new DescriptionFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_description, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        placeTitle.setText(title);
        placeText.setText(description);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null){
            unbinder.unbind();
        }
    }
}
