package com.maxkrass.stundenplan.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.maxkrass.stundenplan.R;
import com.maxkrass.stundenplan.objects.Subject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.roboguice.shaded.goole.common.base.Preconditions.checkNotNull;

public class GradeCalculatorFragment extends Fragment implements OnClickListener {
	private HashMap<String, Subject> subjects;
	private String                   uuid;
	private Button                   b0;
	private Button                   b1;
	private Button                   b10;
	private Button                   b11;
	private Button                   b12;
	private Button                   b13;
	private Button                   b14;
	private Button                   b15;
	private Button                   b2;
	private Button                   b3;
	private Button                   b4;
	private Button                   b5;
	private Button                   b6;
	private Button                   b7;
	private Button                   b8;
	private Button                   b9;
	private Button                   back;
	private Button                   delete;
	private TextView                 fachListe;
	private File                     file;
	private File                     file2;
	private ArrayList<String>        list2;
	private ArrayList<String>        list2kurz;
	private TextView                 nListe;
	private ArrayList<Integer>       noten;
	private TextView                 notenD;
	private TextView                 punkteD;

	public GradeCalculatorFragment() {
		this.subjects = new HashMap<>();
	}

	public static GradeCalculatorFragment newInstance(String uuid) {
		GradeCalculatorFragment fragment = new GradeCalculatorFragment();
		checkNotNull(uuid);
		fragment.uuid = uuid;
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_grade_calculator, container, false);
		this.list2 = new ArrayList<>();
		this.list2kurz = new ArrayList<>();
		this.list2.add("Biologie");
		this.list2kurz.add("BI");
		this.list2.add("Chemie");
		this.list2kurz.add("CH");
		this.list2.add("Deutsch");
		this.list2kurz.add("D");
		this.list2.add("Englisch");
		this.list2kurz.add("E");
		this.list2.add("Erdkunde");
		this.list2kurz.add("EK");
		this.list2.add("Ev. Religion");
		this.list2kurz.add("ER");
		this.list2.add("Franz\u00f6sisch");
		this.list2kurz.add("F");
		this.list2.add("Geschichte");
		this.list2kurz.add("GE");
		this.list2.add("Informatik");
		this.list2kurz.add("IF");
		this.list2.add("Kath. Religion");
		this.list2kurz.add("KR");
		this.list2.add("Kunst");
		this.list2kurz.add("KU");
		this.list2.add("Mathe");
		this.list2kurz.add("M");
		this.list2.add("Musik");
		this.list2kurz.add("MU");
		this.list2.add("P\u00e4dagogik");
		this.list2kurz.add("EW");
		this.list2.add("Philosophie");
		this.list2kurz.add("PL");
		this.list2.add("Physik");
		this.list2kurz.add("PH");
		this.list2.add("Spanisch");
		this.list2kurz.add("S0");
		this.list2.add("SoWi");
		this.list2kurz.add("SW");
		this.list2.add("Sport");
		this.list2kurz.add("SP");
		this.noten = new ArrayList<>();
		this.notenD = (TextView) v.findViewById(R.id.notenD);
		this.punkteD = (TextView) v.findViewById(R.id.punkteD);
		this.nListe = (TextView) v.findViewById(R.id.notenListe);
		this.fachListe = (TextView) v.findViewById(R.id.fachListeTxt);
		this.b0 = (Button) v.findViewById(R.id.b0);
		this.b1 = (Button) v.findViewById(R.id.b1);
		this.b2 = (Button) v.findViewById(R.id.b2);
		this.b3 = (Button) v.findViewById(R.id.b3);
		this.b4 = (Button) v.findViewById(R.id.b4);
		this.b5 = (Button) v.findViewById(R.id.b5);
		this.b6 = (Button) v.findViewById(R.id.b6);
		this.b7 = (Button) v.findViewById(R.id.b7);
		this.b8 = (Button) v.findViewById(R.id.b8);
		this.b9 = (Button) v.findViewById(R.id.b9);
		this.b10 = (Button) v.findViewById(R.id.b10);
		this.b11 = (Button) v.findViewById(R.id.b11);
		this.b12 = (Button) v.findViewById(R.id.b12);
		this.b13 = (Button) v.findViewById(R.id.b13);
		this.b14 = (Button) v.findViewById(R.id.b14);
		this.b15 = (Button) v.findViewById(R.id.b15);
		this.back = (Button) v.findViewById(R.id.bBack);
		this.delete = (Button) v.findViewById(R.id.bDelete);
		this.b0.setOnClickListener(this);
		this.b1.setOnClickListener(this);
		this.b2.setOnClickListener(this);
		this.b3.setOnClickListener(this);
		this.b4.setOnClickListener(this);
		this.b5.setOnClickListener(this);
		this.b6.setOnClickListener(this);
		this.b7.setOnClickListener(this);
		this.b8.setOnClickListener(this);
		this.b9.setOnClickListener(this);
		this.b10.setOnClickListener(this);
		this.b11.setOnClickListener(this);
		this.b12.setOnClickListener(this);
		this.b13.setOnClickListener(this);
		this.b14.setOnClickListener(this);
		this.b15.setOnClickListener(this);
		this.back.setOnClickListener(this);
		this.delete.setOnClickListener(this);
		this.b0.setOnClickListener(this);

		FirebaseDatabase
				.getInstance()
				.getReference()
				.child("users")
				.child(uuid)
				.child("subjects")
				.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						subjects = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Subject>>() {
						});
						setUpUI();
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});

		return v;
	}

	public void setUpUI() {
		if (this.noten.isEmpty()) {
			this.punkteD.setText("Punkte \u00d8: 0,00");
			this.notenD.setText("Noten \u00d8: 0,00");
		} else {
			double punkte = 0.0d;
			for (Integer aNoten : this.noten) {
				punkte += (double) aNoten;
			}
			double pDurch = punkte / ((double) this.noten.size());
			this.punkteD.setText("Punkte \u00d8: " + String.format("%.2f", Double.valueOf(pDurch)));
			double nDurch = (17.0d - pDurch) / 3.0d;
			this.notenD.setText("Noten \u00d8: " + String.format("%.2f", Double.valueOf(nDurch)));
		}
		this.nListe.setText(this.noten.toString());
		if (this.subjects.isEmpty()) {
			this.fachListe.setText("Keine F\u00e4cher in den Einstellungen eingetragen...");
			return;
		}
		this.fachListe.setText("");
		int i = 0;
		for (Map.Entry<String, Subject> subject : subjects.entrySet()) {
			if (i < this.noten.size()) {
				this.fachListe.append(String.valueOf(subject.getValue().getName() + " (" + this.noten.get(i) + "), "));
			} else {
				this.fachListe.append(String.valueOf(subject.getValue().getName() + ", "));
			}
			i++;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.b15 /*2131427458*/:
				this.noten.add(15);
				break;
			case R.id.b14 /*2131427459*/:
				this.noten.add(14);
				break;
			case R.id.b13 /*2131427460*/:
				this.noten.add(13);
				break;
			case R.id.b12 /*2131427463*/:
				this.noten.add(12);
				break;
			case R.id.b11 /*2131427464*/:
				this.noten.add(11);
				break;
			case R.id.b10 /*2131427465*/:
				this.noten.add(10);
				break;
			case R.id.b9 /*2131427468*/:
				this.noten.add(9);
				break;
			case R.id.b8 /*2131427469*/:
				this.noten.add(8);
				break;
			case R.id.b7 /*2131427470*/:
				this.noten.add(7);
				break;
			case R.id.b6 /*2131427473*/:
				this.noten.add(6);
				break;
			case R.id.b5 /*2131427474*/:
				this.noten.add(5);
				break;
			case R.id.b4 /*2131427475*/:
				this.noten.add(4);
				break;
			case R.id.b3 /*2131427478*/:
				this.noten.add(3);
				break;
			case R.id.b2 /*2131427479*/:
				this.noten.add(2);
				break;
			case R.id.b1 /*2131427480*/:
				this.noten.add(1);
				break;
			case R.id.b0 /*2131427483*/:
				this.noten.add(0);
				break;
			case R.id.bBack /*2131427484*/:
				if (!this.noten.isEmpty()) {
					this.noten.remove(this.noten.size() - 1);
					break;
				}
				break;
			case R.id.bDelete /*2131427485*/:
				this.noten.clear();
				break;
		}
		//writeToFileSWAG();
		setUpUI();
	}

	public void writeToFileSWAG() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.file2));
			oos.writeObject(this.noten);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Integer> readFromFile2() {
		ArrayList<Integer> list = new ArrayList<>();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.file2));
			list = (ArrayList) ois.readObject();
			ois.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	public ArrayList<String> readFromFile() {
		ArrayList<String> list = new ArrayList<>();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.file));
			list = (ArrayList) ois.readObject();
			ois.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}
}