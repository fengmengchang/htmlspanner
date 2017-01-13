package net.nightwhistler.htmlspanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView tv;
	HtmlSpanner htmlSpanner;
	ArrayList<String> imglist;
	 String html = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		imglist = new ArrayList<String>();
		htmlSpanner = new HtmlSpanner(this, dm.widthPixels, handler);
		tv = (TextView) findViewById(R.id.tv);
		 
		try {
			InputStream is = getAssets().open("enrz");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			html = new String(buffer, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				final Spannable spannable = htmlSpanner.fromHtml(html);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv.setText(spannable);
						tv.setMovementMethod(LinkMovementMethodExt.getInstance(handler, ImageSpan.class));
					}
				});
			}
		}).start();
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// 获取图片路径列表
				String url = (String) msg.obj;
				Log.e("jj", "url>>" + url);
				imglist.add(url);
				break;
			case 2:// 图片点击事件
				int position = 0;
				MyImageSpan span = (MyImageSpan) msg.obj;
				for (int i = 0; i < imglist.size(); i++) {
					if (span.getUrl().equals(imglist.get(i))) {
						position = i;
						break;
					}
				}
				Log.e("jj", "position>>" + position);
				Intent intent = new Intent(MainActivity.this, ImgPreviewActivity.class);
				Bundle b = new Bundle();
				b.putInt("position", position);
				b.putStringArrayList("imglist", imglist);
				intent.putExtra("b", b);
				startActivity(intent);
				break;
			}
		}

		;
	};
}
