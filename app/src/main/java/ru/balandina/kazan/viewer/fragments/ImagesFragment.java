package ru.balandina.kazan.viewer.fragments;

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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.balandina.kazan.R;

public class ImagesFragment extends Fragment {

    private static ImagesFragment instance;
    private static List<String> imagesPathList;
    private ImagesAdapter adapter;
    private Unbinder unbinder;
    @BindView(R.id.imagesRecycler)
    RecyclerView imagesRecycler;

    public static ImagesFragment getInstance(List<String> imagesList){
        imagesPathList = imagesList;
        if (instance == null){
            instance = new ImagesFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        showImageList(imagesPathList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null){
            unbinder.unbind();
        }
    }

    private void showImageList(List<String> imagesList) {
        adapter = new ImagesAdapter(imagesList);
        imagesRecycler.setAdapter(adapter);
        imagesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {

        private List<String> imagesPath;

        public ImagesAdapter(List<String> imagesPath){
            this.imagesPath = imagesPath;
        }

        @NonNull
        @Override
        public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images, parent, false);
            return new ImagesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
            holder.bindData(getContext(), imagesPath.get(position));
        }

        @Override
        public int getItemCount() {
            return imagesPath != null ? imagesPath.size() : 0;
        }

        public class ImagesViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.recyclerImage)
            ImageView recyclerImage;

            public ImagesViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(Context context, String filePath){
                recyclerImage.setImageBitmap(getBitmapFromAsset(context, filePath));
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
