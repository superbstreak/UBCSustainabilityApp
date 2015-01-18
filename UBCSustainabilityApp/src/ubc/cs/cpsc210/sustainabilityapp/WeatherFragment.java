package ubc.cs.cpsc210.sustainabilityapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ubc.cs.cpsc210.sustainabilityapp.model.PointOfInterest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherFragment extends Fragment{
	//===================DEFINE VALUES==================
		String temp;
		String date;
		String cond;
		String wind;
		String humd;
		TextView TVtemp;
		TextView TVdate;
		TextView TVcond;
		TextView TVwind;
		TextView TVhumd;
		ImageView wpic = null; //weather picture
		List<String> wdata = new ArrayList<String>();
		//======================START========================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view;
		view = inflater.inflate(R.layout.weather, container, false);
		return view;
	}
	@Override
	 public void onActivityCreated(Bundle savedInstanceState) {
		 super.onActivityCreated(savedInstanceState);
			TVtemp = (TextView) getActivity().findViewById(R.id.tv_temp);
			TVdate = (TextView) getActivity().findViewById(R.id.tv_date);
			TVcond = (TextView) getActivity().findViewById(R.id.tv_cond);
			TVwind = (TextView) getActivity().findViewById(R.id.tv_wind);
			TVhumd = (TextView) getActivity().findViewById(R.id.tv_humd);
			wpic =  (ImageView) getActivity().findViewById(R.id.iv_wpic);
			TextView ref = (TextView) getActivity().findViewById(R.id.refresh);
			ref.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "REFRESHING DATA", Toast.LENGTH_LONG).show();
					new WeatherActivity().getWeatherService(TVtemp, TVdate, TVcond, TVwind, TVhumd, wpic);
				}
			});
			new WeatherActivity().getWeatherService(TVtemp, TVdate, TVcond, TVwind, TVhumd, wpic);
	 }
	 @Override
	public void onResume() {
		super.onResume();
	}
	 @Override
		public void onPause() {
			super.onPause();
		}
}
	

