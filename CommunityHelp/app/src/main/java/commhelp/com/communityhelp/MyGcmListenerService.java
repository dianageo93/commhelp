package commhelp.com.communityhelp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private static final int requestCodeMap = 0;
    private static final int requestCodeAccept = 1;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString("type");
        if (type.equals("helprequest")) {
            openMapWithLocation(data);
        } else if (type.equals("acceptrequest")) {
            openAcceptRequestActivity(data);
        }

    }

    public void openMapWithLocation(Bundle data) {
        String name = data.getString("name");
        double latitude = Double.parseDouble(data.getString("lat"));
        double longitude = Double.parseDouble(data.getString("lng"));
        String label = name + " needs help!";
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCodeMap, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.emergency)
                .setContentTitle("New community emergency")
                .setContentText(name + " needs help")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(requestCodeMap, notificationBuilder.build());
    }

    public void openAcceptRequestActivity(Bundle data) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.emergency)
                .setContentTitle("New accept request notification")
                .setContentText("Will you help " + data.getString("name") +"?")
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        Intent intent = new Intent(this, AcceptActivity.class);
        intent.putExtra("victim_uid", data.getString("uid"));
        intent.putExtra("victim_name", data.getString("name"));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AcceptActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder
                .getPendingIntent(requestCodeAccept, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(requestCodeAccept, notificationBuilder.build());
    }
}