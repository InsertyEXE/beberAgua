package com.insertu.beberagua;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificacaoPublicador extends BroadcastReceiver {


    public static final String CHAVE_NOTIFICACAO = "chave_notificacao";
    public static final String CHAVE_NOTIFICACAO_ID = "chave_notificacao_id";

    @Override
    public void onReceive(Context context, Intent intent) {


        NotificationManager gerenciadorNotificacao
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        //Criando uma notificação
        String mensagem = intent.getStringExtra(CHAVE_NOTIFICACAO);
        int id = intent.getIntExtra(CHAVE_NOTIFICACAO_ID, 0);
        final Intent intencao = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent preIntencao = PendingIntent.getActivity(context, 0, intencao, 0);
        Notification notificacao = getNotofication(context, mensagem, gerenciadorNotificacao, preIntencao);


        //enviando uma notificação
        gerenciadorNotificacao.notify(id, notificacao);
    }

    //Função para criar a notificação (a mensagem de alerta)
    private Notification getNotofication(Context context, String conteudo,
                                         NotificationManager manager, PendingIntent intent) {


        //Construindo a notificação
        Notification.Builder builder =
                new Notification.Builder(context.getApplicationContext())
                        .setContentText(conteudo)
                        .setTicker("Titulo")
                        .setAutoCancel(false)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(intent);

        /*
         * Em algumas versões (a partir da Oreo) as notificações PRECISAM ser
         * filtradas criando um canal de notificação com o comando a baixo
         *
         */

        //Criando canal de notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String canalId = "YOUR_CHANNEL_ID";
            NotificationChannel canal =
                    new NotificationChannel(canalId, "Canal", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(canal);
            builder.setChannelId(canalId);

        }


        return builder.build();

    }


}
