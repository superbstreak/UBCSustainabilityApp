package ubc.cs.cpsc210.sustainabilityapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity
{
	//===================DEFINE VALUES==================
	String temp;
	String date;
	String cond;
	String wind;
	String humd;
	int wCode;
	Map forc = new HashMap<String, List<String>>();
	//======================START========================
	public void getWeatherService(TextView tvtemp, TextView tvdate,  TextView tvcond, TextView tvwind, TextView tvhumd, ImageView iv)  
	{
		InputStream inputXML = null;
		try{
			inputXML = new URL("http://weather.yahooapis.com/forecastrss?p=CAXX0518&u=c").openConnection().getInputStream();
		
			DocumentBuilderFactory processor = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = processor.newDocumentBuilder();
			Document doc = builder.parse(inputXML);
			NodeList atmosphere = doc.getElementsByTagName("yweather:atmosphere");
			NodeList condition = doc.getElementsByTagName("yweather:condition");
			NodeList windinfo = doc.getElementsByTagName("yweather:wind");
			NodeList forcast = doc.getElementsByTagName("yweather:forecast");
			if(atmosphere.getLength() > 0)
			{
				Element e = (Element)atmosphere.item(0);
				humd = "Humidity: "+e.getAttribute("humidity")+" %";
			}
			if(condition.getLength() > 0)
			{
				Element e = (Element)condition.item(0);
				cond = "Condition: "+e.getAttribute("text");
				temp = "Temperature: "+e.getAttribute("temp") + " C";
				date = "Update: "+e.getAttribute("date")+" from Yahoo! Weather. Images from merlinthered.deviantart.com/art/plain-weather-icons-157162192";
				wCode = Integer.parseInt(e.getAttribute("code"));
			}
			if(windinfo.getLength() > 0)
			{
				Element e = (Element)windinfo.item(0);
				wind = "Wind Speed: "+e.getAttribute("speed")+" km/h";
			}
			if (forcast.getLength() > 0)
			{
				//not going to implement
			}
		}
		catch(ParserConfigurationException e)
		{
			Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		}
		catch(SAXException e)
		{
			Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		}
		catch(IOException e)
		{
			Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_LONG).show();
		}
		finally {
			tvtemp.setText(temp);
			tvdate.setText(date);
			tvcond.setText(cond);
			tvwind.setText(wind);
			tvhumd.setText(humd);
			weatherDecode(wCode, iv);
			try
			{
				if(inputXML != null)
				{
					inputXML.close();
				}
			}
			catch(IOException e)
			{
				Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_LONG).show();
			}
		}
		
	}
	public void weatherDecode(int x, ImageView imgv)
	{
		if(x==3200)imgv.setImageResource(R.drawable.na); //service not working
		if(x==0)imgv.setImageResource(R.drawable.aa); 
		if(x==1)imgv.setImageResource(R.drawable.ab);
		if(x==2)imgv.setImageResource(R.drawable.ac);
		if(x==3)imgv.setImageResource(R.drawable.ad);
		if(x==4)imgv.setImageResource(R.drawable.ae);
		if(x==5)imgv.setImageResource(R.drawable.af);
		if(x==6)imgv.setImageResource(R.drawable.ag);
		if(x==7)imgv.setImageResource(R.drawable.ah);
		if(x==8)imgv.setImageResource(R.drawable.ai);
		if(x==9)imgv.setImageResource(R.drawable.aj);
		if(x==10)imgv.setImageResource(R.drawable.ak);
		if(x==11)imgv.setImageResource(R.drawable.al);
		if(x==12)imgv.setImageResource(R.drawable.am);
		if(x==13)imgv.setImageResource(R.drawable.an);
		if(x==14)imgv.setImageResource(R.drawable.ao);
		if(x==15)imgv.setImageResource(R.drawable.ap);
		if(x==16)imgv.setImageResource(R.drawable.aq);
		if(x==17)imgv.setImageResource(R.drawable.ar);
		if(x==18)imgv.setImageResource(R.drawable.as);
		if(x==19)imgv.setImageResource(R.drawable.at);
		if(x==20)imgv.setImageResource(R.drawable.au);
		if(x==21)imgv.setImageResource(R.drawable.av);
		if(x==22)imgv.setImageResource(R.drawable.aw);
		if(x==23)imgv.setImageResource(R.drawable.ax);
		if(x==24)imgv.setImageResource(R.drawable.ay);
		if(x==25)imgv.setImageResource(R.drawable.az);
		if(x==26)imgv.setImageResource(R.drawable.ba); 
		if(x==27)imgv.setImageResource(R.drawable.bb);
		if(x==28)imgv.setImageResource(R.drawable.bc);
		if(x==29)imgv.setImageResource(R.drawable.bd);
		if(x==30)imgv.setImageResource(R.drawable.be);
		if(x==31)imgv.setImageResource(R.drawable.bf);
		if(x==32)imgv.setImageResource(R.drawable.bg);
		if(x==33)imgv.setImageResource(R.drawable.bh);
		if(x==34)imgv.setImageResource(R.drawable.bi);
		if(x==35)imgv.setImageResource(R.drawable.bj);
		if(x==36)imgv.setImageResource(R.drawable.bk);
		if(x==37)imgv.setImageResource(R.drawable.bl);
		if(x==38)imgv.setImageResource(R.drawable.bm);
		if(x==39)imgv.setImageResource(R.drawable.bn);
		if(x==40)imgv.setImageResource(R.drawable.bo);
		if(x==41)imgv.setImageResource(R.drawable.bp);
		if(x==42)imgv.setImageResource(R.drawable.bq);
		if(x==43)imgv.setImageResource(R.drawable.br);
		if(x==44)imgv.setImageResource(R.drawable.bs);
		if(x==45)imgv.setImageResource(R.drawable.bt);
		if(x==46)imgv.setImageResource(R.drawable.bu);
		if(x==47)imgv.setImageResource(R.drawable.bv);
	}
}
