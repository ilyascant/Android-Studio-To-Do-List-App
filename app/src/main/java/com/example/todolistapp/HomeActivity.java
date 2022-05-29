package com.example.todolistapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;


    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;

    private String key = "", task, description;
    private String date = DateFormat.getDateInstance().format(new Date());
    private final int[] dates = new int[3];


    static int alarmId = 0;
    HashMap<String,Integer> alarmHash = new  HashMap<String,Integer>();
    AlarmManager alarmManager;
    private Calendar calendar;

    private final Calendar mcurrentTime = Calendar.getInstance();
    private final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
    private final int minute = mcurrentTime.get(Calendar.MINUTE);
    private String time = String.format("%02d:%02d",hour,minute);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        createNotification();

        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        String name= "NULL";
        try {
            Bundle extras = getIntent().getExtras();
            String eMail = extras.getString("EMAIL");
            String[] eMailPart = eMail.split("@");
            name = eMail.substring(0,eMailPart[0].length()).toUpperCase();
        }catch (Exception e){
            name = "NULL";
        }
        getSupportActionBar().setTitle("ToDoList App, Welcome "+name);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });
    }

    private void addTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_file, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();

        final EditText task = myView.findViewById(R.id.task);
        final EditText description = myView.findViewById(R.id.description);

        calendar = Calendar.getInstance();

        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);
        Button editDate = myView.findViewById(R.id.datePickerButton);
        Button editTime = myView.findViewById(R.id.timePickerButton);

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayofMonth) {
                        Aylar.setAy(month);
                        date = dayofMonth + " " + Aylar.getsAy() + " " + year;
                        dates[0] = dayofMonth;
                        dates[1] = month;
                        dates[2] = year;
                        editDate.setText(date);

                        calendar.set(Calendar.YEAR,dates[2]);
                        calendar.set(Calendar.MONTH,dates[1]);
                        calendar.set(Calendar.DAY_OF_MONTH,dates[0]);

                    }
                }
                        , Calendar.getInstance().get(Calendar.YEAR)
                        , Calendar.getInstance().get(Calendar.MONTH)
                        , Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                dpd.show();
            }
        });

        editTime.setOnClickListener(new View.OnClickListener() {
            final Calendar mcurrentTime = Calendar.getInstance();
            final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            final int minute = mcurrentTime.get(Calendar.MINUTE);

            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = new TimePickerDialog(HomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time = String.format("%02d:%02d",selectedHour,selectedMinute);
                        editTime.setText(time);

                        calendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                        calendar.set(Calendar.MINUTE,selectedMinute);
                        calendar.set(Calendar.SECOND,0);
                        calendar.set(Calendar.MILLISECOND,0);
                    }
                },hour, minute, true);
                tpd.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editDate.getText().toString().equals("Select Date")) {
                    Toast.makeText(HomeActivity.this,"Date is Required",Toast.LENGTH_SHORT).show();

                }else{
                    Log.d("DATE ----", editDate.getText().toString());
                    String mTask = task.getText().toString().trim();
                    String mDescription = description.getText().toString().trim();
                    String id = reference.push().getKey();
                    alarmHash.put(id,getAlarmId());

//                String date = DateFormat.getDateInstance().format(new Date());

                    if (TextUtils.isEmpty(mTask)) {
                        task.setError("Task is required");
                        return;
                    }
                    if (TextUtils.isEmpty(mDescription)) {
                        description.setError("Desription is required");
                        return;
                    } else {
                        loader.setMessage("Adding your Data");
                        loader.setCanceledOnTouchOutside(false);
                        loader.show();

                        Model model = new Model(mTask, mDescription, id, date,time);
                        reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    setAlarm(alarmHash.get(id),mTask,mDescription);
                                    Toast.makeText(HomeActivity.this, "Task has been inserted succesfully", Toast.LENGTH_SHORT).show();
                                    loader.dismiss();
                                } else {
                                    String error = task.getException().toString();
                                    Toast.makeText(HomeActivity.this, "Failed" + error, Toast.LENGTH_SHORT).show();
                                    loader.dismiss();
                                }
                            }
                        });
                    }
                    dialog.dismiss();
                }

                }

        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference, Model.class)
                .build();

        FirebaseRecyclerAdapter<Model, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull final Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDesc(model.getDescription());
                holder.setTime(model.getTime());

                Calendar today = Calendar.getInstance();
                String[] myDates = model.getDate().split(" ");
                String[] myTimes = model.getTime().split(":");

                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR,Integer.parseInt(myDates[2]));
                myCalendar.set(Calendar.MONTH,Aylar.getsAyInNumber(myDates[1]));
                myCalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(myDates[0]));
                myCalendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(myTimes[0]));
                myCalendar.set(Calendar.MINUTE,Integer.parseInt(myTimes[1]));
                myCalendar.set(Calendar.SECOND,0);
                myCalendar.set(Calendar.MILLISECOND,0);


                CardView card = holder.mView.findViewById(R.id.cardView);
                CheckBox cb = holder.mView.findViewById(R.id.checkbox);
                ColorStateList cardColor = card.getCardBackgroundColor();

                checkDate(cb, card, today, myCalendar, cardColor,getRef(holder.getBindingAdapterPosition()).getKey());
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkDate(cb, card, today, myCalendar, cardColor,getRef(holder.getBindingAdapterPosition()).getKey());
                    }
                });

                ImageView alarmOff = holder.mView.findViewById(R.id.alarmOff);

                alarmOff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            cancelAlarm(alarmHash.get(getRef(holder.getBindingAdapterPosition()).getKey()));
                        }catch(Exception e) {
                            cancelAlarm(0);
                            return;
                        }

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(holder.getBindingAdapterPosition()).getKey();
                        task = model.getTask();
                        description = model.getDescription();
                        date = model.getDate();
                        time = model.getTime();
                        Log.d("KEY CLICK",key);

                        updateTask();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrived_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void checkDate(CheckBox _cb, CardView _card, Calendar _today, Calendar _myDate, ColorStateList _cardColor,String id){
        alarmHash.put(id,getAlarmId());
        if(_cb.isChecked()){
            _card.setCardBackgroundColor(Color.rgb(117, 230, 145));
        }else if(!_cb.isChecked() && _myDate.compareTo(_today) < 0){
            _card.setCardBackgroundColor(Color.rgb(180, 180, 180));
        }
        else if (!_cb.isChecked() && _myDate.compareTo(_today) > 0){
            _card.setCardBackgroundColor(_cardColor);

        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTask(String task) {
            TextView taskTectView = mView.findViewById(R.id.taskTv);
            taskTectView.setText(task);
        }


        public void setDesc(String desc) {
            TextView descTectView = mView.findViewById(R.id.descriptionTv);
            descTectView.setText(desc);
        }

        public void setDate(String date) {
            TextView dateTextView = mView.findViewById(R.id.dateTv);
            dateTextView.setText(date);
        }

        public void setTime(String time) {
            TextView dateTextView = mView.findViewById(R.id.timeTv);
            dateTextView.setText(time);
        }


    }

    private void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.updata_data, null);
        myDialog.setView(view);

        AlertDialog dialog = myDialog.create();

        EditText mTask = view.findViewById(R.id.mEditTask);
        EditText mDescription = view.findViewById(R.id.mEditDescription);

        Button delButton = view.findViewById(R.id.deleteBtn);
        Button updateButton = view.findViewById(R.id.updateBtn);
        Button updateDate = view.findViewById(R.id.mDatePickerButton);
        Button updateTime = view.findViewById(R.id.mTimePickerButton);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        updateDate.setText(date);
        updateTime.setText(time);

        String sDates[] =  date.toString().split(" ");
        String sTimes[] = time.toString().split(":");

        Log.d("sdates0",sDates[0]);
        Log.d("sdates1",String.valueOf(Aylar.getsAyInNumber(sDates[1])));
        Log.d("sdates2",sDates[2]);

        Log.d("stimes0",sTimes[0]);
        Log.d("stimes1",sTimes[1]);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,Integer.parseInt(sDates[2]));
        calendar.set(Calendar.MONTH,Aylar.getsAyInNumber(sDates[1]));
        calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(sDates[0]));
        calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(sTimes[0]));
        calendar.set(Calendar.MINUTE,Integer.parseInt(sTimes[1]));
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        updateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayofMonth) {
                        Aylar.setAy(month);
                        date = dayofMonth + " " + Aylar.getsAy() + " " + year;
                        dates[0] = dayofMonth;
                        dates[1] = month;
                        dates[2] = year;
                        updateDate.setText(date);

                        calendar.set(Calendar.YEAR,dates[2]);
                        calendar.set(Calendar.MONTH,dates[1]);
                        calendar.set(Calendar.DAY_OF_MONTH,dates[0]);

                    }
                }

                        , Calendar.getInstance().get(Calendar.YEAR)
                        , Calendar.getInstance().get(Calendar.MONTH)
                        , Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });


        updateTime.setOnClickListener(new View.OnClickListener() {
            final Calendar mcurrentTime = Calendar.getInstance();
            final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            final int minute = mcurrentTime.get(Calendar.MINUTE);




            @Override
            public void onClick(View view) {
                TimePickerDialog tpd = new TimePickerDialog(HomeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {



                        time = String.format("%02d:%02d",selectedHour,selectedMinute);
                        updateTime.setText(time);

//                        calendar = Calendar.getInstance();
//                        calendar.set(Calendar.YEAR,Integer.parseInt(dates[2]));
//                        calendar.set(Calendar.MONTH,Integer.parseInt(dates[1]));
//                        calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(dates[0]));
                        calendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                        calendar.set(Calendar.MINUTE,selectedMinute);
                        calendar.set(Calendar.SECOND,0);
                        calendar.set(Calendar.MILLISECOND,0);

                    }
                },hour, minute, true);
                tpd.show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = mTask.getText().toString().trim();
                description = mDescription.getText().toString().trim();

                Model model = new Model(task, description, key, date,time);

                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> _task) {

                        if (_task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Data has been updated successfully", Toast.LENGTH_SHORT).show();
                            setAlarm(alarmHash.get(key),task,description);
                        } else {
                            String err = _task.getException().toString();
                            Toast.makeText(HomeActivity.this, "update failed " + err, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.dismiss();

            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("KEY DELETE",key);
                            Toast.makeText(HomeActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String err = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "Failed to delete task " + err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNotification(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ilyas can turali";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ilyascant",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static int getAlarmId(){
        alarmId++;
        return alarmId;
    }

    private void setAlarm(int requestCode, String task, String description){
        Log.d("request code ->",String.valueOf(requestCode));
        AlarmNotification.setId(requestCode);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,AlarmNotification.class);
        intent.putExtra("ID",requestCode);
        intent.putExtra("TASK",task);
        intent.putExtra("DESCRIPTION",description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        Log.d("ALARM ZAMANI",calendar.getTime().toString());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

        Toast.makeText(this,"Alarm Set",Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm(int requestCode){
        if(alarmManager == null){
            Toast.makeText(this,"Alarm wasn't set",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this,AlarmNotification.class);
        intent.putExtra("ID",requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent,0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this,"Alarm Cancelled",Toast.LENGTH_SHORT).show();
    }

}

































