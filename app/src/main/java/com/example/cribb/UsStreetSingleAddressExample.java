package com.example.cribb;

import com.smartystreets.api.StaticCredentials;
import com.smartystreets.api.exceptions.SmartyException;
import com.smartystreets.api.us_street.*;
import com.smartystreets.api.ClientBuilder;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;

public class UsStreetSingleAddressExample {
    public static String run(String address,String city, String state) {
        String authId = "e364ec93-0e7e-2f4c-8b91-edc5c6168c98";
        String authToken = "uMFMctU3tpuT7sJ2bY64";

        // We recommend storing your secret keys in environment variables instead---it's safer!
        //String authId = System.getenv("SMARTY_AUTH_ID");
//        String authToken = System.getenv("SMARTY_AUTH_TOKEN");


        StaticCredentials credentials = new StaticCredentials(authId, authToken);
        Client client = new ClientBuilder(credentials)
//                .withProxy(Proxy.Type.HTTP, "localhost", 8080) // Uncomment this line to try it with a proxy
                .buildUsStreetApiClient();

        // Documentation for input fields can be found at:
        // https://smartystreets.com/docs/us-street-api#input-fields

        Lookup lookup = new Lookup();
        lookup.setInputId("24601"); // Optional ID from your system
        lookup.setAddressee("John Doe");
        lookup.setStreet("1600 Amphitheatre Pkwy");
        lookup.setStreet2("closet under the stairs");
        lookup.setSecondary("APT 2");
        lookup.setUrbanization(""); // Only applies to Puerto Rico addresses
        lookup.setCity("Mountain View");
        lookup.setState("CA");
        lookup.setZipCode("94043");
        lookup.setMaxCandidates(3);
        lookup.setMatch(MatchType.INVALID); // "invalid" is the most permissive match

        try {
            client.send(lookup);
        }
        catch (SmartyException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        ArrayList<Candidate> results = lookup.getResult();

        if (results.isEmpty()) {
            System.out.println("No candidates. This means the address is not valid.");
            return "No candidates. This means the address is not valid.";
        }
        else{
            Candidate firstCandidate = results.get(0);

            System.out.println("Address is valid. (There is at least one candidate)\n");
            System.out.println("Input ID: " + firstCandidate.getInputId());
            System.out.println("ZIP Code: " + firstCandidate.getComponents().getZipCode());
            System.out.println("County: " + firstCandidate.getMetadata().getCountyName());
            System.out.println("Latitude: " + firstCandidate.getMetadata().getLatitude());
            System.out.println("Longitude: " + firstCandidate.getMetadata().getLongitude());
            return "Address is valid. (There is at least one candidate)\n";
        }

    }
}
