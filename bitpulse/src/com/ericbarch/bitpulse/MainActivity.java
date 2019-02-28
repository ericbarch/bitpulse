package com.ericbarch.bitpulse;

import java.lang.reflect.Field;
import java.util.Date;

import com.ericbarch.bitpulse.settings.SettingsActivity;
import com.ericbarch.bitpulselib.util.Utilities;
import com.ericbarch.bitpulselib.Exchanges;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainActivity extends Activity implements ActionBar.OnNavigationListener, IDisplayView {
	// TextViews that we update
	private TextView bidText;
	private TextView askText;
	private TextView lastText;
	private TextView volumeText;
	private TextView lastChangeText;
	private TextView connectionText;

	private String currentExchange;
	private String[] exchanges;
	private SharedPreferences prefs;
	
	private boolean navMenuBuilt = false;
	private ActionBar actionBar;
	private Typeface appFont;
	
	private UpdateManager updateManager;
	
	@SuppressWarnings("unused")
	private static final String TAG = "MainActivity";
	
	private void setupActionDropdown() {
		// Set up the action bar to show a dropdown list.
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("bitpulse");
		actionBar.setSubtitle("(beta)");
		
		exchanges = new String[] {
			getString(R.string.coinbase),
			getString(R.string.btce),
			getString(R.string.bitstamp),
			getString(R.string.bitfinex),
			getString(R.string.bitcoin_average),
			getString(R.string.btcchina),
			getString(R.string.campbx),
			getString(R.string.kraken),
			getString(R.string.vault_of_satoshi),
			getString(R.string.anx)
		};

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(new ArrayAdapter<String>(getActionBar().getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1, exchanges), this);
		
		for (int i=0; i<exchanges.length; i++) {
			if (exchanges[i].equals(currentExchange)) {
				actionBar.setSelectedNavigationItem(i);
				break;
			}
		}
		
		navMenuBuilt = true;
	}
	
	private void getAllTextViews() {
		bidText = (TextView)findViewById(R.id.bid);
		askText = (TextView)findViewById(R.id.ask);
		lastText = (TextView)findViewById(R.id.last);
		volumeText = (TextView)findViewById(R.id.volume);
		lastChangeText = (TextView)findViewById(R.id.lastChange);
		connectionText = (TextView)findViewById(R.id.connection);
	}
	
	private void setFonts() {
		appFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
		
		bidText.setTypeface(appFont);
		askText.setTypeface(appFont);
		lastText.setTypeface(appFont);
		volumeText.setTypeface(appFont);
		lastChangeText.setTypeface(appFont);
		connectionText.setTypeface(appFont);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get a reference to all text views
		getAllTextViews();
		
		// force menu btn
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// ignore
		}
		
		// Restore preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		currentExchange = prefs.getString("exchange", "coinbase (usd only)");
		
		// we have removed mtgox, clear out any setting still poiting to mtgox
		if (currentExchange.equalsIgnoreCase("mtgox") || currentExchange.equalsIgnoreCase("mtgox live")) {
			SharedPreferences.Editor editor = prefs.edit();
	    	editor.putString("exchange", "coinbase (usd only)");
	    	currentExchange = "coinbase (usd only)";
	    	editor.commit();
		}
		
		// set our font on all textviews
		setFonts();
		
		// keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// action bar config
		setupActionDropdown();
		
		// create new exchange manager
		updateManager = new UpdateManager(this, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 @Override
     public boolean onOptionsItemSelected(MenuItem item)
     {
         switch(item.getItemId())
         {
         	case R.id.action_about:
         		createAboutDialog();
                return true;
            case R.id.action_settings:
            	Intent intent = new Intent(this, SettingsActivity.class);
 		        startActivity(intent);
            default:
            	return super.onOptionsItemSelected(item);
         }
     }
	 
	 private void createAboutDialog() {
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 
		 String bitpulseVersion = "Bitpulse";
		 try {
			 PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			 bitpulseVersion = bitpulseVersion + " (v" + pInfo.versionName + ")";
		 } catch (NameNotFoundException e) {}
		 
		 builder.setMessage(getString(R.string.about_text)).setTitle(bitpulseVersion);
		 
		 builder.setNegativeButton("Donate BTC", new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int id) {
				 String url = "https://coinbase.com/checkouts/ac3014d7064f7de95e3cb3546af4410e";
				 Intent i = new Intent(Intent.ACTION_VIEW);
				 i.setData(Uri.parse(url));
				 startActivity(i);
			 }
		 });

		 // Add the buttons
		 builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int id) {
				 
			 }
		 });

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();
	 }
	
	@Override
	public void onPause() {
		// Always call the superclass method first
	    super.onPause();
	    
	    updateManager.endUpdates();
	}
	
	@Override
	public void onResume() {
		// Always call the superclass method first
	    super.onResume();
	    
	    // make sure we show we are in a disconnected state
	    onDisconnect();
	    
	    startUpdates(prefs.getString("exchange", "coinbase (usd only)"), prefs.getString("currency", "USD"));
	}
	
	private void startUpdates(String exchange, String currency) {
		if (updateManager.isActive()) {
			updateManager.endUpdates();
		}
		
		int currencyCode = Utilities.currencyForString(currency);
		int exchangeCode = -1;
		
		if (exchange.equalsIgnoreCase("coinbase (usd only)")) {
			exchangeCode = Exchanges.COINBASE;
		} else if (exchange.equalsIgnoreCase("bitstamp (usd only)")) {
			exchangeCode = Exchanges.BITSTAMP;
		} else if (exchange.equalsIgnoreCase("btce")) {
			exchangeCode = Exchanges.BTCE;
		} else if (exchange.equalsIgnoreCase("bitfinex (usd only)")) {
			exchangeCode = Exchanges.BITFINEX;
		} else if (exchange.equalsIgnoreCase("btcchina (cny only)")) {
			exchangeCode = Exchanges.BTCCHINA;
		} else if (exchange.equalsIgnoreCase("bitcoin average")) {
			exchangeCode = Exchanges.BITCOIN_AVERAGE;
		} else if (exchange.equalsIgnoreCase("campbx (usd only)")) {
			exchangeCode = Exchanges.CAMPBX;
		} else if (exchange.equalsIgnoreCase("kraken")) {
			exchangeCode = Exchanges.KRAKEN;
		} else if (exchange.equalsIgnoreCase("vault of satoshi")) {
			exchangeCode = Exchanges.VAULTOFSATOSHI;
		} else if (exchange.equalsIgnoreCase("anx")) {
			exchangeCode = Exchanges.ANX;
		}
		
		updateManager.beginUpdates(exchangeCode, currencyCode);
	}
	
	private int getTextSizeFitInBounds(String text) {
		int textSize = 1;
		
		TextView myText = new TextView(MainActivity.this);
		myText.setText(text);
        myText.setTypeface(appFont);
        myText.setTextSize(textSize);
        
        myText.measure(lastText.getWidth(), lastText.getHeight());
        
        while (myText.getMeasuredWidth() < lastText.getWidth() || myText.getMeasuredWidth() < lastText.getWidth()) {
        	textSize++;
        	myText.setTextSize(textSize);
        	myText.measure(lastText.getWidth(), lastText.getHeight());
        }
        
        textSize--;
        
        return textSize;
	}
	
	private void resetAllText() {
		bidText.setText(getString(R.string.loading_bid));
		askText.setText(getString(R.string.loading_ask));
		lastText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
		lastText.setTextColor(getResources().getColor(android.R.color.white));
		lastText.setText(getString(R.string.contacting_text));
		volumeText.setText(getString(R.string.loading_vol));
		lastChangeText.setText(getString(R.string.dots));
		connectionText.setText(getString(R.string.disconnected));
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (navMenuBuilt) {
			currentExchange = exchanges[itemPosition];
	
	    	SharedPreferences.Editor editor = prefs.edit();
	    	editor.putString("exchange", currentExchange);
	    	editor.commit();
	    	
	    	resetAllText();
	    	
	    	startUpdates(prefs.getString("exchange", "coinbase (usd only)"), prefs.getString("currency", "USD"));
		}
		
		return true;
	}
	
	private void updateLastChange() {
		// update last change text
		@SuppressWarnings("deprecation")
		String lastTrade = new Date().toLocaleString();
		lastChangeText.setText(lastTrade);
	}
	
	private void flashView(View view, int color) {
		ValueAnimator colorAnim = ObjectAnimator.ofInt((TextView)view, "backgroundColor", android.graphics.Color.TRANSPARENT, color);
        colorAnim.setDuration(400);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(1);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
	}

	@Override
	public void onBidChange(String bidString) {
		bidText.setText("bid - " + bidString);
		flashView(bidText, 0x5500CC00);
		updateLastChange();
	}

	@Override
	public void onAskChange(String askString) {
		askText.setText(askString + " - ask");
		flashView(askText, 0x55CC0000);
		updateLastChange();
	}

	@Override
	public void onDisconnect() {
		connectionText.setText(getString(R.string.disconnected));
		resetAllText();
	}

	@Override
	public void onConnect() {
		connectionText.setText(getString(R.string.connected));
	}

	@Override
	public void onLastChange(String tradeString, boolean valueUp) {
		int fontSize = getTextSizeFitInBounds(tradeString);
		lastText.setTextSize(fontSize);
		lastText.setText(tradeString);
		if (valueUp) {
			lastText.setTextColor(android.graphics.Color.GREEN);
			flashView(lastText, 0x3300CC00);
		} else {
			lastText.setTextColor(android.graphics.Color.RED);
			flashView(lastText, 0x33CC0000);
		}
		updateLastChange();
	}

	@Override
	public void onVolumeChange(String volString) {
		volumeText.setText("vol: " + volString);
		flashView(volumeText, 0x55CCCCCC);
		updateLastChange();
	}

}
