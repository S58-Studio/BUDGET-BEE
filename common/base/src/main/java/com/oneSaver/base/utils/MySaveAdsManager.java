package com.oneSaver.base.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;

import com.anythink.core.api.ATAdInfo;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.UnsupportedEncodingException;

import timber.log.Timber;

public class MySaveAdsManager {
    static MySaveAdsManager Instance;
    InterstitialAd mInterstitialAd;
    private ATInterstitial atInterstitial;
    private boolean isAdLoaded = false;
    private boolean isAdLoading = false;

    static public MySaveAdsManager getInstance() {
        if (Instance != null) {
            return Instance;
        }
        return new MySaveAdsManager();
    }


    public void displayAds(Activity activity, OnAdsCallback callback) throws UnsupportedEncodingException {
        String adNetworkProvider = SharedPreferencesHelper.getAdNetworkProvider(activity);
        //Log.d("ADNETWORK","Ad network provider is " + adNetworkProvider);
        // Load the last ad network displayed (AdMob or Meta) from SharedPreferences
        boolean lastAdWasAdMob = SharedPreferencesHelper.getLastAdNetworkWasAdMob(activity);
        Timber.tag("ADNETWORK").d("Ad network provider is %s", adNetworkProvider);

        if (isNetworkConnected(activity) && !PremiumUserUtils.INSTANCE.isPurchased(activity)) {
            if (adNetworkProvider.equals("Dynamic")) {
                // Alternate between AdMob and Meta Ads
                if (lastAdWasAdMob) {
                    showToponMetaAds(activity, callback);
                } else {
                    showAdmobAds(activity, callback);
                }
                // Toggle the flag and store it back in SharedPreferences
                SharedPreferencesHelper.setLastAdNetworkWasAdMob(activity, !lastAdWasAdMob);

            } else if (adNetworkProvider.equals("AdmobAds")) {
                // Always show Meta Ads
                showAdmobAds(activity, callback);
            } else if (adNetworkProvider.equals("Premium")) {
                callback.moveNext();
            } else {
                // Default is MetaAds
                showToponMetaAds(activity, callback);
            }
        } else {
            callback.moveNext();
        }
    }

    public void showAdmobAds(Activity activity, OnAdsCallback callback) throws UnsupportedEncodingException {
        try {
            // Create and show the custom loader
            CustomLoader customLoader = new CustomLoader(activity);
            customLoader.show();
            AdRequest adRequest = new AdRequest.Builder().build();
            String onBoardingInters = MySaveConstants.GOOGLE_ADMOB_AD; //4 Onboarding
            InterstitialAd.load(activity, onBoardingInters, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when fullscreen content is dismissed.
                                    Timber.tag("ADMOB").d("The ad was dismissed.");
                                    try {
                                        customLoader.dismiss();
                                        callback.moveNext();
                                    } catch (UnsupportedEncodingException e) {
                                        Timber.e(e, "Error occurred while moving to the next callback.");
                                    }
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    // Called when fullscreen content failed to show.
                                    Timber.tag("TAG").d("The ad failed to show.");
                                    try {
                                        customLoader.dismiss();
                                        callback.moveNext();
                                    } catch (UnsupportedEncodingException e) {
                                        Timber.e(e, "Error occurred while moving to the next callback.");}
                                }


                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when fullscreen content is shown.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    customLoader.dismiss();
                                    mInterstitialAd = null;
                                    Timber.tag("TAG").d("The ad was shown.");
                                    // callback.moveNext();
                                }
                            });
                            if (mInterstitialAd != null) {
                                mInterstitialAd.show(activity);
                            } else {
                                Timber.tag("TAG").d("The interstitial ad wasn't ready yet.");
                            }
                            Timber.tag("TAG").i("onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Timber.tag("TAG").i(loadAdError.getMessage());
                            try {
                                customLoader.dismiss();
                                callback.moveNext();
                            } catch (UnsupportedEncodingException e) {
                                Timber.e(e, "Error occurred while moving to the next callback.");
                            }

                        }
                    });
        }catch (Exception e) {
            Timber.e(e, "Error occurred while moving to the next callback.");
            callback.moveNext();
        }
    }

    // Display the ad if preloaded, else load and show directly
    public void showToponMetaAds(Activity activity, OnAdsCallback callback) {
        if (isAdLoaded && atInterstitial != null) {
            Timber.tag("Topon").d("Displaying preloaded ad");
            atInterstitial.show(activity);

            atInterstitial.setAdListener(new ATInterstitialListener() {
                @Override
                public void onInterstitialAdClose(ATAdInfo atAdInfo) {
                    Timber.tag("Topon").d("Ad dismissed, preloading next ad");
                    preLoadToponMetAd(activity);
                    try {
                        callback.moveNext();
                    } catch (UnsupportedEncodingException e) {throw new RuntimeException(e);}
                }

                @Override
                public void onInterstitialAdLoadFail(com.anythink.core.api.AdError adError) {
                    Timber.tag("Topon").d("Error during ad display: %s", adError.getFullErrorInfo());
                    isAdLoaded = false;
                    isAdLoading = false;
                    showFallbackGoogleAd(activity, callback);
                }

                @Override
                public void onInterstitialAdClicked(ATAdInfo atAdInfo) {}
                @Override
                public void onInterstitialAdShow(ATAdInfo atAdInfo) {}
                @Override
                public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {}
                @Override
                public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {}
                @Override
                public void onInterstitialAdVideoError(com.anythink.core.api.AdError adError) {}
                @Override
                public void onInterstitialAdLoaded() {}
            });

            isAdLoaded = false; // Reset after showing
        } else {
            Timber.tag("Topon").d("No preloaded ad, loading and showing directly");
            // Create and show the custom loader
            CustomLoader customLoader = new CustomLoader(activity);
            customLoader.show();

            loadAndShowToponMetAd(activity, callback, customLoader);
        }
    }

    // Load and show ad directly when no preloaded ad is available
    private void loadAndShowToponMetAd(Activity activity, OnAdsCallback callback, CustomLoader customLoader) {
        atInterstitial = new ATInterstitial(activity, MySaveConstants.TOPON_META_AD);
        atInterstitial.setAdListener(new ATInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {
                Timber.tag("Topon").d("Ad loaded successfully, displaying now");
                customLoader.dismiss();
                atInterstitial.show(activity);
                isAdLoaded = false;
                isAdLoading = false;
            }

            @Override
            public void onInterstitialAdLoadFail(com.anythink.core.api.AdError adError) {
                Timber.tag("Topon").d("Error during load and show: %s", adError.getFullErrorInfo());
                customLoader.dismiss();
                isAdLoaded = false;
                isAdLoading = false;
                showFallbackGoogleAd(activity, callback);
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo atAdInfo) {
                Timber.tag("Topon").d("Ad dismissed after direct load and show");
                preLoadToponMetAd(activity);
                try {
                    callback.moveNext();
                } catch (UnsupportedEncodingException e) {throw new RuntimeException(e);}
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo atAdInfo) {}
            @Override
            public void onInterstitialAdShow(ATAdInfo atAdInfo) {}
            @Override
            public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {}
            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {}
            @Override
            public void onInterstitialAdVideoError(com.anythink.core.api.AdError adError) {}
        });
        isAdLoading = true;
        atInterstitial.load();
    }

    // Preload the ad
    public void preLoadToponMetAd(Activity activity) {
        if (isAdLoaded || isAdLoading) return;

        Timber.tag("META").d("Preloading Topon ad");
        atInterstitial = new ATInterstitial(activity, MySaveConstants.TOPON_META_AD);
        isAdLoading = true;

        atInterstitial.setAdListener(new ATInterstitialListener() {
            @Override
            public void onInterstitialAdLoaded() {
                Timber.tag("META").d("Ad preloaded successfully");
                isAdLoaded = true;
                isAdLoading = false;
            }

            @Override
            public void onInterstitialAdLoadFail(com.anythink.core.api.AdError adError) {
                Timber.tag("META").d("Preload failed: %s", adError.getFullErrorInfo());
                isAdLoaded = false;
                isAdLoading = false;
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo atAdInfo) {}
            @Override
            public void onInterstitialAdShow(ATAdInfo atAdInfo) {}
            @Override
            public void onInterstitialAdClose(ATAdInfo atAdInfo) {}
            @Override
            public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {}
            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {}
            @Override
            public void onInterstitialAdVideoError(com.anythink.core.api.AdError adError) {}
        });

        atInterstitial.load();
    }

    //fallback
    private void showFallbackGoogleAd(Activity activity, OnAdsCallback callback) {
        Timber.tag("ADMOB").d("THIS IS FROM FALLBACK ADMOB");
        CustomLoader customLoader = new CustomLoader(activity);
        customLoader.show();
        AdRequest adRequest = new AdRequest.Builder().build();
        String onBoardingInters = MySaveConstants.GOOGLE_FALLBACK_AD; // Fallback
        InterstitialAd.load(activity, onBoardingInters, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Timber.tag("ADMOB").d("The ad was dismissed.");
                                try {
                                    customLoader.dismiss();
                                    callback.moveNext();
                                } catch (UnsupportedEncodingException e) {Timber.e(e, "Error occurred while moving to the next callback.");}
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                Timber.tag("TAG").d("The ad failed to show.");
                                try {
                                    customLoader.dismiss();
                                    callback.moveNext();
                                } catch (UnsupportedEncodingException e) {Timber.e(e, "Error occurred while moving to the next callback.");}
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                customLoader.dismiss();
                                mInterstitialAd = null;
                                preLoadToponMetAd(activity);
                                Timber.tag("TAG").d("The ad was shown.");
                            }
                        });
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(activity);
                        } else {
                            Timber.tag("TAG").d("The interstitial ad wasn't ready yet.");
                        }
                        Timber.tag("TAG").i("onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Timber.tag("TAG").i(loadAdError.getMessage());
                        try {
                            customLoader.dismiss();
                            callback.moveNext();
                        } catch (UnsupportedEncodingException e) {Timber.e(e, "Error occurred while moving to the next callback.");}
                    }
                });
    }

    //internet connection
    private boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    //ADS INTERFACE
    public interface OnAdsCallback {
        void moveNext() throws UnsupportedEncodingException;
    }
}
