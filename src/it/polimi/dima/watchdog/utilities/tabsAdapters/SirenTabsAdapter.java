package it.polimi.dima.watchdog.utilities.tabsAdapters;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.factory.FeatureEnum;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

public class SirenTabsAdapter extends TabsAdapter {

	private int[] sirenIcons = { R.drawable.sirenon, R.drawable.sirenoff, R.drawable.sirenoff };

	public SirenTabsAdapter(FragmentManager fm, int tabsNum,
			FeatureEnum mFeatureEnum, Context context) {
		super(fm, tabsNum, mFeatureEnum, context);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Resources res = super.context.getResources();
		Drawable myDrawable = res.getDrawable(sirenIcons[position]);
		SpannableStringBuilder sb;

		switch (position) {
		case 0:
			sb = new SpannableStringBuilder("  Siren On");
			break;
		case 1:
			sb = new SpannableStringBuilder("  Siren Off");
			break;
		case 2:
			sb = new SpannableStringBuilder("  Local Siren Off");
			break;
		default:
			sb = new SpannableStringBuilder("  Siren");
			break;
		}
		// space added before text for convenience

		myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(),
				myDrawable.getIntrinsicHeight());
		ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
		sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return sb;
	}
}
