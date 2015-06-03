package com.mycompany.app.model;


import org.osgl.util.C;
import org.osgl.util.S;

import java.util.List;

public class MyCsvModel {

    private String name = S.random(5);
    private String email = randomEmail();
    private String address = S.random(30);

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    private static String randomEmail() {
        return S.builder(S.random(5)).append('@').append(S.random(6)).append('.').append("com").toString();
    }

    public static List<MyCsvModel> generateData(int size) {
        List<MyCsvModel> retLst = C.newList();
        for (int i = 0; i < size; ++i) {
            retLst.add(new MyCsvModel());
        }
        return retLst;
    }
}
