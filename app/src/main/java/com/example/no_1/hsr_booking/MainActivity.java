package com.example.no_1.hsr_booking;

import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /*view object*/
    EditText etName,etEmail;
    TextView tvResult;
    RadioGroup rgGender;
    RadioButton rbMan,rbWoman,rbOthers;
    Spinner spStart,spDest,spChild,spAdult;
    CheckBox cbEmail;
    Button btnBook,btnCancel;

    /*values*/
    String bookingResult,name,gender,startPosition,destination,ticket,email;
    String station[] = new String[]{"","南港","台北","板橋","桃園","新竹","苗栗","台中","彰化","雲林"
                                    ,"嘉義","台南","左營"};
    String childNumber[]=new String[]{"0","1","2","3","4","5","6","7","8","9","10"};
    String adultNumber[]=new String[]{"0","1","2","3","4","5","6","7","8","9","10"};

    int numberOfChild,numberOfAdult;
    boolean sendEmail,stationFormatWrong,noTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*1.find view and reset values*/
        findView();
        reset();
        showResult();
        /*2.set radio group listener*/
        rgGender.setOnCheckedChangeListener(rgGenderListener);

        /*3.set spinner*/
        ArrayAdapter<String> adapterStartStations=new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item,station);
        ArrayAdapter<String> adapterDestination=new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item,station);
        ArrayAdapter<String> adapterChildNumber=new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item,childNumber);
        ArrayAdapter<String> adapterAdultNumber=new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item,adultNumber);

        adapterStartStations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDestination.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterChildNumber.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterAdultNumber.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spStart.setAdapter(adapterStartStations);
        spDest.setAdapter(adapterDestination);
        spChild.setAdapter(adapterChildNumber);
        spAdult.setAdapter(adapterAdultNumber);

        spStart.setOnItemSelectedListener(stationSelect);
        spDest.setOnItemSelectedListener(stationSelect);
        spChild.setOnItemSelectedListener(numberSelect);
        spAdult.setOnItemSelectedListener(numberSelect);

        /*4.set check box*/
        cbEmail.setOnCheckedChangeListener(cbCheck);

        /*5.set button*/
        btnBook.setOnClickListener(btnClick);
        btnCancel.setOnClickListener(btnClick);

    }

    void findView()//find object view
    {
        etName=(EditText)findViewById(R.id.editTextName);
        etEmail=(EditText)findViewById(R.id.editTextEmail);
        tvResult=(TextView)findViewById(R.id.textViewBookingResult);
        rgGender=(RadioGroup)findViewById(R.id.radioGroupGender);
        rbMan=(RadioButton)findViewById(R.id.radioButtonMan);
        rbWoman=(RadioButton)findViewById(R.id.radioButtonWoman);
        rbOthers=(RadioButton)findViewById(R.id.radioButtonOthers);
        spStart=(Spinner)findViewById(R.id.spinnerStart);
        spDest=(Spinner)findViewById(R.id.spinnerDest);
        spAdult=(Spinner)findViewById(R.id.spinnerAdultNumber);
        spChild=(Spinner)findViewById(R.id.spinnerChildNumber);
        cbEmail=(CheckBox)findViewById(R.id.checkBoxEmail);
        btnBook=(Button)findViewById(R.id.buttonBook);
        btnCancel=(Button)findViewById(R.id.buttonCancel);
    }

    /*initialize values*/
    void reset()
    {
        bookingResult=name=gender=email="";
        sendEmail=false;
        stationFormatWrong=false;
        noTicket=false;
        etName.setText("");
        etEmail.setText("");
        tvResult.setText("");
        cbEmail.setChecked(false);
        rgGender.clearCheck();
    }

    /*RadioGroup listener*/
    private RadioGroup.OnCheckedChangeListener rgGenderListener
            = new RadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            /*set gender*/
            if (checkedId==R.id.radioButtonMan)
                gender="男性";
            else if (checkedId==R.id.radioButtonWoman)
                gender="女性";
            else
                gender="其他";
            showResult();
        }
    };

    /*Spinner listener*/
    private Spinner.OnItemSelectedListener stationSelect = new Spinner.OnItemSelectedListener(){
        @Override
        /*station listener*/
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(parent==spStart)
                startPosition=station[position];
            else
                destination=station[position];
            showResult();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private Spinner.OnItemSelectedListener numberSelect = new Spinner.OnItemSelectedListener(){
        @Override
        /*number listener*/
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(parent==spChild)
                numberOfChild=Integer.valueOf(childNumber[position]);
            else
                numberOfAdult=Integer.valueOf(adultNumber[position]);
            ticket="兒童票:"+numberOfChild+" 張,全票:"+numberOfAdult+",張";
            showResult();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /*CheckBox listener*/
    private CheckBox.OnCheckedChangeListener cbCheck = new CheckBox.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(cbEmail.isChecked())//send Email
            {
                sendEmail=true;
            }
            else
                sendEmail=false;
        }
    };


    /*Button listener*/
    private Button.OnClickListener btnClick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.buttonBook)
            {
                if(startPosition.equals(destination)) //same start and end
                    stationFormatWrong=true;
                if(numberOfAdult+numberOfChild==0)
                    noTicket=true;
                if(stationFormatWrong)//station format is wrong
                {
                    Toast toast = Toast.makeText(MainActivity.this,
                            "起訖站相同或資料不完整",Toast.LENGTH_LONG);
                    reset();
                    toast.show();
                    return;
                }

                if(noTicket) //no ticket is booked
                {
                    Toast toast = Toast.makeText(MainActivity.this,"無訂票",Toast.LENGTH_LONG);
                    reset();
                    toast.show();
                    return;
                }

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("訂票確認")
                        .setMessage("確定要訂票嗎?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s = "訂票成功";
                                if(sendEmail)
                                    s+=",email已送出至"+email;
                                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                if(sendEmail) email=etEmail.getText().toString();
                showResult();
            }
            else
            {
                reset();
            }
        }
    };

    void showResult() //show the result of booking
    {
        name=etName.getText().toString();
        bookingResult="姓名:"+name+",性別:"+gender+",起站:"+startPosition+",迄站:"+destination
                +"\n票種:"+ticket;
        tvResult.setText(bookingResult);
    }
}
