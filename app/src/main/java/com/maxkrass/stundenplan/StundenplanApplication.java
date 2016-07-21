package com.maxkrass.stundenplan;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.orm.SugarContext;

public class StundenplanApplication extends MultiDexApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		SugarContext.init(this);
		DrawerImageLoader.init(new AbstractDrawerImageLoader() {
			@Override
			public void set(ImageView imageView, Uri uri, Drawable placeholder) {
				Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
			}

			@Override
			public void cancel(ImageView imageView) {
				Glide.clear(imageView);
			}

			@Override
			public Drawable placeholder(Context ctx, String tag) {
				//define different placeholders for different imageView targets
				//default tags are accessible via the DrawerImageLoader.Tags
				//custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
				if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
					return DrawerUIUtils.getPlaceHolder(ctx);
				} else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
					return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
				} else if ("customUrlItem".equals(tag)) {
					return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
				}

				//we use the default one for
				//DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

				return super.placeholder(ctx, tag);
			}
		});
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		SugarContext.terminate();
	}

}
