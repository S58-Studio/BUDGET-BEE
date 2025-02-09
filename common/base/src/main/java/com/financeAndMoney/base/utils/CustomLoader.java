package com.financeAndMoney.base.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.financeAndMoney.base.R;

public class CustomLoader {

    private final Dialog dialog;

    public CustomLoader(Activity activity) {
        // Create a dialog and set the custom layout
        dialog = new Dialog(activity);
        // Inflate your custom layout
        View view = LayoutInflater.from(activity).inflate(R.layout.custom_loading_animation, null, false);

        // Wrap your view in a FrameLayout to apply margins programmatically
        FrameLayout frameLayout = new FrameLayout(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(26, 0, 16, 0); // Apply margins (start, top, end, bottom)
        view.setLayoutParams(params);
        frameLayout.addView(view);

        dialog.setContentView(frameLayout);

        // Set the dialog to match the parent's width
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(null); // Remove default dialog background
        }

        // Set dialog properties
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // Ensure the Lottie animation is ready to play
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setAnimation(R.raw.loading_animation);
        lottieAnimationView.playAnimation();
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

