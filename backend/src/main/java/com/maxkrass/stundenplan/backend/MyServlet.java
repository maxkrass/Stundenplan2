/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.maxkrass.stundenplan.backend;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.security.token.TokenGenerator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		checkPlan(resp);

	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		checkPlan(resp);
	}

	@Override
	public void init() throws ServletException {
		super.init();


		//FirebaseOptions options = new FirebaseOptions.Builder()
		//		.setServiceAccount(getServletContext().getResourceAsStream("/json/Stundenplan-9b17507267a4.json"))
		//		.setDatabaseUrl("https://fourth-return-136820.firebaseio.com/")
		//		.setDatabaseAuthVariableOverride(auth)
		//		.build();
		//FirebaseApp.initializeApp(options);
	}

	public void checkPlan(final HttpServletResponse resp) throws IOException {
		resp.getWriter().println("checkPlan");

		//Firebase latestSubstitutionPlans = firebase
		//		.child("stundenplan")
		//		.child("latestSubstitutionPlans");


		Map<String, Object> auth = new HashMap<>();
		auth.put("uid", "my-service-worker");

		String token = new TokenGenerator("44x1Tgl9EHL1mBj9gJZt4l7QhZgVlKGuyBmHyptW").createToken(auth);

		final Firebase firebase = new Firebase("https://fourth-return-136820.firebaseio.com/stundenplan/latestSubstitutionPlans");
		firebase.authWithCustomToken(token, new Firebase.AuthResultHandler() {
			@Override
			public void onAuthenticated(AuthData authData) {
				firebase.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						try {
							resp.getWriter().println("Here");
							URL firebase = new URL("https://fcm.googleapis.com/fcm/send");
							HttpURLConnection connection = (HttpURLConnection) firebase.openConnection();
							connection.setRequestProperty("Content-Type", "application/json");
							connection.setRequestProperty("Authorization", "key=AIzaSyDZmWcTz_u64dufwXjedcsDOZ8Fk3M77Lo");

							connection.setDoOutput(true);

							String input = "{\"notification\" : {\"title\" : \"Success\"}, \"to\":\"/topics/checkPlan\"}";

							OutputStream os = connection.getOutputStream();
							os.write(input.getBytes());
							os.flush();
							os.close();

							int responseCode = connection.getResponseCode();
							resp.getWriter().println("Post parameters : " + input);
							resp.getWriter().println("Response Code : " + responseCode);

							BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							String inputLine;
							StringBuilder response = new StringBuilder();

							while ((inputLine = in.readLine()) != null) {
								response.append(inputLine);
							}
							in.close();

							// print result
							resp.getWriter().println(response.toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
							// the plan has already been checked at some point
							try {
								Connection.Response response = Jsoup.connect("http://www.mpg-plan.max-planck-gymnasium-duesseldorf.de/Vertretungsplan/Moodle/SII/t1/subst_001.htm").execute();
								Document day1 = response.parse();
								response = Jsoup.connect("http://www.mpg-plan.max-planck-gymnasium-duesseldorf.de/Vertretungsplan/Moodle/SII/t2/subst_001.htm").execute();
								Document day2 = response.parse();
								response = Jsoup.connect("http://www.mpg-plan.max-planck-gymnasium-duesseldorf.de/Vertretungsplan/Moodle/SII/t3/subst_001.htm").execute();
								Document day3 = response.parse();
								String updateDate1 = fetchUpdateDateFromDocument(day1);
								String updateDate2 = fetchUpdateDateFromDocument(day2);
								String updateDate3 = fetchUpdateDateFromDocument(day3);

								// now we have the latest dates for the three dates
								// lets check if have those in the database

								DataSnapshot dates = dataSnapshot.child("dates");

								String databaseDate1 = dates.child("day1").getValue(String.class);
								String databaseDate2 = dates.child("day1").getValue(String.class);
								String databaseDate3 = dates.child("day1").getValue(String.class);

								if (!Objects.equals(databaseDate1, updateDate1) || !Objects.equals(databaseDate2, updateDate2) || !Objects.equals(databaseDate3, updateDate3)) {
									// The plan has been updated
									// Get the new plans and push them to the users
									ArrayList<SubstitutionEvent> substitutionEventsDay1 = fetchEventsFromDocument(day1);
									ArrayList<SubstitutionEvent> substitutionEventsDay2 = fetchEventsFromDocument(day2);
									ArrayList<SubstitutionEvent> substitutionEventsDay3 = fetchEventsFromDocument(day3);

									String date1 = fetchDateFromDocument(day1);
									String date2 = fetchDateFromDocument(day2);
									String date3 = fetchDateFromDocument(day3);


								}
							} catch (IOException e) {
								e.printStackTrace();
							}

						} else {
						}
					}

					@Override
					public void onCancelled(FirebaseError databaseError) {
						try {
							URL firebase = new URL("https://fcm.googleapis.com/fcm/send");
							HttpURLConnection connection = (HttpURLConnection) firebase.openConnection();
							connection.setRequestProperty("Content-Type", "application/json");
							connection.setRequestProperty("Authorization", "key=AIzaSyDZmWcTz_u64dufwXjedcsDOZ8Fk3M77Lo");

							connection.setDoOutput(true);

							String input = "{\"notification\" : {\"title\" : \"Error\"}, \"to\":\"/topics/checkPlan\"}";

							OutputStream os = connection.getOutputStream();
							os.write(input.getBytes());
							os.flush();
							os.close();

							int responseCode = connection.getResponseCode();
							resp.getWriter().println("Post parameters : " + input);
							resp.getWriter().println("Response Code : " + responseCode);

							BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							String inputLine;
							StringBuilder response = new StringBuilder();

							while ((inputLine = in.readLine()) != null) {
								response.append(inputLine);
							}
							in.close();

							// print result
							resp.getWriter().println(response.toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}

			@Override
			public void onAuthenticationError(FirebaseError firebaseError) {
				try {
					URL firebase = new URL("https://fcm.googleapis.com/fcm/send");
					HttpURLConnection connection = (HttpURLConnection) firebase.openConnection();
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setRequestProperty("Authorization", "key=AIzaSyDZmWcTz_u64dufwXjedcsDOZ8Fk3M77Lo");

					connection.setDoOutput(true);

					String input = "{\"notification\" : {\"title\" : \"Authentication Error\"}, \"to\":\"/topics/checkPlan\"}";

					OutputStream os = connection.getOutputStream();
					os.write(input.getBytes());
					os.flush();
					os.close();

					int responseCode = connection.getResponseCode();
					resp.getWriter().println("Post parameters : " + input);
					resp.getWriter().println("Response Code : " + responseCode);

					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuilder response = new StringBuilder();

					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					// print result
					resp.getWriter().println(response.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private String fetchUpdateDateFromDocument(Document document) {
		Element table = document.select("table.mon_head").first();
		Element row = table.select("td[align=right]").first();
		String rowContent = row.select("p").first().ownText();
		String date = rowContent.substring(rowContent.indexOf("Stand:"));
		return date.substring(date.indexOf(" ")).trim();
	}

	private String fetchDateFromDocument(Document document) {
		return document.select("div.mon_title").first().ownText().trim();
	}

	private ArrayList<SubstitutionEvent> fetchEventsFromDocument(Document document) {
		Element table = document.select("table.mon_list").first();
		Elements rows = table.select(".odd, .even");

		ArrayList<SubstitutionEvent> events = new ArrayList<>();
		outerLoop:
		for (int i1 = 0; i1 < rows.size(); i1++) {
			Element element = rows.get(i1);
			SubstitutionEvent event = new SubstitutionEvent();
			for (int i = 0; i < element.childNodeSize(); i++) {
				switch (i) {
					case 0:
						String grade = element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim().toUpperCase();
						if (!grade.isEmpty())
							event.setGrade(SubstitutionEvent.Grade.valueOf(grade));
						else {
							if (i1 > 0)
								events.get(events.size() - 1).setAnnotation((events.get(events.size() - 1).getAnnotation() + " " + element.child(7).ownText()).trim());
							continue outerLoop;
						}
						break;
					case 1:
						event.setPeriod(element.child(i).ownText());
						break;
					case 2:
						String struckSubject = element.child(i).html();
						if (struckSubject.startsWith("<strike>")) {
							struckSubject = struckSubject.replace("<strike>", "").replace("</strike>", "");
						}
						event.setSubject(struckSubject.replaceAll("&nbsp;", "").replaceAll("\\s+", " ").replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim().toUpperCase());
						break;
					case 3:
						event.setType(SubstitutionEvent.SubstitutionType.getTypeByString(element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim()));
						break;
					case 4:
						String struckOldTeacher = element.child(i).html();
						if (struckOldTeacher.startsWith("<strike>")) {
							struckOldTeacher = struckOldTeacher.replace("<strike>", "").replace("</strike>", "");
						}
						event.setOldTeacher(struckOldTeacher.replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
						break;
					case 5:
						if (!Objects.equals(event.getType(), SubstitutionEvent.SubstitutionType.Cancelled)) {
							String struckSub = element.child(i).html();
							if (struckSub.startsWith("<strike>")) {
								struckSub = struckSub.replace("<strike>", "").replace("</strike>", "");
							}
							event.setSub(struckSub.replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
						}
						break;
					case 6:
						if (!Objects.equals(event.getType(), SubstitutionEvent.SubstitutionType.Cancelled)) {
							event.setNewLocation(element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
						}
						break;
					case 7:
						event.setAnnotation(element.child(i).ownText().replaceAll("&nbsp;", "").replaceAll("\\s+", " ").trim());
				}
			}
			events.add(event);

		}
		return events;
	}

}
