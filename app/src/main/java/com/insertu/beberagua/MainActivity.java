package com.insertu.beberagua;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnNotificar;
    private EditText edtTempo;
    private TimePicker timePicker;

    private int hora;
    private int minutos;
    private int intervalo;

    private boolean btAtivado = false;

    private SharedPreferences bancoLocal;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNotificar = findViewById(R.id.btnNotificar);
        edtTempo = findViewById(R.id.edtTempo);
        timePicker = findViewById(R.id.timePicker);


        //Configurando para um relogio de 24 horas
        timePicker.setIs24HourView(true);


        //definindo o banco de dados
        bancoLocal = getSharedPreferences("bdLocal", Context.MODE_PRIVATE);


        //verificando se existe um banco salvo
        btAtivado = bancoLocal.getBoolean("ativado", false);

        if (btAtivado){

            ColorStateList color = ContextCompat.getColorStateList(this, android.R.color.black);
            btnNotificar.setBackgroundTintList(color);
            btnNotificar.setText(R.string.pause);
            btAtivado = true;


            int intervalo = bancoLocal.getInt("intervalo", 0);
            int hora = bancoLocal.getInt("hora", timePicker.getCurrentHour());
            int minutos = bancoLocal.getInt("minuto", timePicker.getCurrentMinute());

            
            edtTempo.setText(String.valueOf(intervalo));
            timePicker.setCurrentHour(hora);
            timePicker.setCurrentMinute(minutos);

        }


        btnNotificar.setOnClickListener(view -> {
            String sIntervalo = edtTempo.getText().toString();

            if (sIntervalo.isEmpty()){
                Toast.makeText(this, R.string.error_msg, Toast.LENGTH_SHORT).show();
                return;
            }


            hora = timePicker.getCurrentHour();
            minutos = timePicker.getCurrentMinute();
            intervalo = Integer.parseInt(sIntervalo);


            ColorStateList color;
            if (!btAtivado) {
                color = ContextCompat.getColorStateList(this, android.R.color.black);
                btnNotificar.setText(R.string.pause);
                btAtivado = true;

                //escrevendo no banco de dados/salvando os dados
                SharedPreferences.Editor editor = bancoLocal.edit();
                editor.putBoolean("ativado", true);
                editor.putInt("intervalo", intervalo);
                editor.putInt("hora", hora);
                editor.putInt("minuto", minutos);
                editor.apply();


                //Confirgurando a notificação
                Intent notificacaoIntent = new Intent(MainActivity.this, NotificacaoPublicador.class);
                notificacaoIntent.putExtra(NotificacaoPublicador.CHAVE_NOTIFICACAO_ID, 1);
                notificacaoIntent.putExtra(NotificacaoPublicador.CHAVE_NOTIFICACAO, "Hora de beber água");

                PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0,
                        notificacaoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                long tempoMilissegundos = SystemClock.elapsedRealtime() + (intervalo * 1000);
                AlarmManager gerenciadorAlarme = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                gerenciadorAlarme.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, tempoMilissegundos, broadcast);


            }
             else{
                color = ContextCompat.getColorStateList(this, R.color.colorAccent);
                btnNotificar.setText(R.string.notify);
                btAtivado = false;


                //Removendo o banco atual salvo
                SharedPreferences.Editor editor = bancoLocal.edit();
                editor.putBoolean("ativado", false);
                editor.remove("intervalo");
                editor.remove("hora");
                editor.remove("minuto");
                editor.apply();

            }
            btnNotificar.setBackgroundTintList(color);


            Log.d("Teste", "hora: " + hora + " minutos: " + minutos + " intervalo: " + intervalo);
        });
    }


}