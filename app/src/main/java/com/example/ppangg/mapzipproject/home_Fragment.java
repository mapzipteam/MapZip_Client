package com.example.ppangg.mapzipproject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class home_Fragment extends Fragment {
    private TextView topstate ;
    private UserData user;

	public home_Fragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        user = UserData.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View v = inflater.inflate(R.layout.homelayout, container, false);

        topstate = (TextView)v.findViewById(R.id.topstate);
        topstate.setText(user.getUserName());
        topstate.append("(");
        topstate.append(user.getUserID());
        topstate.append(")");

        //���ǳ�
        Spinner spinner = (Spinner)v.findViewById(R.id.spinner);
        //����� ����
        //�� ���� ���� ��� string,xml�� ����Ʈ�� �߰��� ���� �� ����Ʈ�� �ҷ��´�.
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_number, android.R.layout.simple_spinner_item);
        //���ǳʿ� ����� ����
        spinner.setAdapter(adapter);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }
}
