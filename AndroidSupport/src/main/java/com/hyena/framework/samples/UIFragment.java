package com.hyena.framework.samples;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.ImageFetcher;

/**
 * Created by yangzc on 16/8/5.
 */
public class UIFragment extends BaseUIFragment {

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(true);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_uifragment, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        final ImageView imageView = (ImageView) view.findViewById(R.id.image);
        final String url = "http://img.pconline.com.cn/images/upload/upc/tx/wallpaper/1408/07/c0/37179063_1407421362265_800x600.jpg";
        ImageFetcher.getImageFetcher().loadImage(url, imageView, 0);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUIFragmentHelper().showPicture(imageView, url);
            }
        });
    }
}
