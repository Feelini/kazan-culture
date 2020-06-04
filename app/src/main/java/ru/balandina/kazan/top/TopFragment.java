package ru.balandina.kazan.top;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.balandina.kazan.R;
import ru.balandina.kazan.models.ObjectEntity;

public class TopFragment extends Fragment {

    public interface OnViewOnMapClickListener{
        void onViewOnMapClick(LatLng position);
    }

    @BindView(R.id.topRecycler)
    RecyclerView topRecycler;
    private static TopFragment instance;
    private Unbinder unbinder;
    private OnViewOnMapClickListener onViewOnMapClickListener;
    private TopAdapter adapter;
    private static List<ObjectEntity> objectEntities;

    public static TopFragment newInstance(List<ObjectEntity> objectEntityList){
        objectEntities = objectEntityList;
        if (instance == null){
            instance = new TopFragment();
        }
        return instance;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnViewOnMapClickListener){
            onViewOnMapClickListener = (OnViewOnMapClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (onViewOnMapClickListener != null){
            onViewOnMapClickListener = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        showTopList(objectEntities);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    private void showTopList(List<ObjectEntity> objectEntities){
        adapter = new TopAdapter(objectEntities, getContext());
        topRecycler.setAdapter(adapter);
        topRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    public class TopAdapter extends RecyclerView.Adapter<TopAdapter.TopViewHolder>{

        private List<ObjectEntity> objectEntityList;
        private Context context;

        public TopAdapter(List<ObjectEntity> objectEntityList, Context context) {
            this.context = context;
            this.objectEntityList = objectEntityList;
        }

        @NonNull
        @Override
        public TopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_top, parent, false);
            return new TopViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TopViewHolder holder, int position) {
            holder.bindData(context, objectEntityList.get(position));
        }

        @Override
        public int getItemCount() {
            return objectEntityList != null ? objectEntityList.size() : 0;
        }

        public class TopViewHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.itemTopImage)
            ImageView itemTopImage;
            @BindView(R.id.itemTopTitle)
            TextView itemTopTitle;
            @BindView(R.id.itemTopDescription)
            TextView itemTopDescription;
            @BindView(R.id.itemTopBtn)
            Button itemTopBtn;

            public TopViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(Context context, ObjectEntity objectEntity){
                String filePath = "top/" + objectEntity.getId() + ".jpg";
                itemTopImage.setImageBitmap(getBitmapFromAsset(context, filePath));
                itemTopTitle.setText(objectEntity.getTitle());
                itemTopDescription.setText(objectEntity.getDescription());
                if (onViewOnMapClickListener != null){
                    itemTopBtn.setOnClickListener(v -> {
                        onViewOnMapClickListener.onViewOnMapClick(new LatLng(objectEntity.getLocation().get(0), objectEntity.getLocation().get(1)));
                    });
                }
            }
        }
    }

    private Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream inputStream;
        Bitmap bitmap = null;
        try {
            inputStream = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = getRoundedCornerBitmap(bitmap);
        } catch (IOException e) {

        }

        return bitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
