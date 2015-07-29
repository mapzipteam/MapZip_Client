package com.example.ppangg.mapzipproject;

/**
 * Created by ljs93kr on 2015-07-27.
 * ���� ������� ��� �����͸� ��� �̱��� Ŭ����
 */
public class UserData {
    private static UserData ourInstance;

    private boolean LoginPermission; // �α����㰡
    String UserID; // ����� ���̵�
    String UserName; // ����� �̸�

    public static UserData getInstance() {
        if(ourInstance == null) {
            ourInstance = new UserData();
        }
        return ourInstance;
    }

    private UserData() {
        init();
    }

    private void init(){
        LoginPermission = false; // ó�� instanceȭ �Ҷ��� �α����㰡 false
        UserID = null;
        UserName = null;
    }

    public void LoginOK(){
        LoginPermission = true;
    }

    public boolean getLoginPermission(){
        return LoginPermission;
    }



}
