package GUI;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleService.People;
import com.google.api.services.people.v1.model.Person;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleAuthenticator {
    private static final String CLIENT_SECRET_FILE = "/client_secret_293312627554-hih49uj0j9m59862mst44prhm180aegr.apps.googleusercontent.com.json";
    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/userinfo.email", "profile");
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public Credential authenticate() throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = GoogleAuthenticator.class.getResourceAsStream(CLIENT_SECRET_FILE);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        // Retrieve user's email
        PeopleService peopleService = new PeopleService.Builder(httpTransport, JSON_FACTORY, credential).build();
        People people = peopleService.people();
        Person profile = people.get("people/me").setPersonFields("emailAddresses").execute();
        String email = profile.getEmailAddresses().get(0).getValue();

        return credential;
    }
}
