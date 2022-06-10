package com.example.cardclub;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardclub.serverHandling.ServerInput;
import com.example.cardclub.serverHandling.ServerOutput;

import java.io.IOException;
import java.net.Socket;

/**
 * The main view of the application
 */
public class MainActivity extends AppCompatActivity {

    private Socket socket = null;
    private String address = "10.108.137.77";
    private int port = 5004;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Method start connecting to server
     *
     * @param view view to listen from
     */
    public void connectBtnClicked(View view) {
        /*setContentView(R.layout.game);
        setupDragAndDrop(findViewById(R.id.DragView));
        setupDragAndDrop(findViewById(R.id.DropView));*/
        new Thread(this::connect).start();
    }

    /**
     * Method to connect to the game server
     */
    public void connect() {
        try {
            if (!String.valueOf(((EditText) findViewById(R.id.IpText)).getText()).equals("Ip Address")) {
                address = String.valueOf(((EditText) findViewById(R.id.IpText)).getText());
            }
            if (Integer.parseInt(String.valueOf(((EditText) findViewById(R.id.PortText)).getText())) != port) {
                port = Integer.parseInt(String.valueOf(((EditText) findViewById(R.id.PortText)).getText()));
            }
            socket = new Socket(address, port);
            new Thread(new ServerInput(this, socket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to send data string to server on button click
     */
    public void sendMessage(String msg) {
        new Thread(new ServerOutput(socket, msg)).start();
    }

    public void login_OnClick(){
        String username = String.valueOf(((EditText) findViewById(R.id.LoginUsernameText)).getText());
        String password = String.valueOf(((EditText) findViewById(R.id.LoginPasswordText)).getText());
        String msg = "login;" + username + ";" + password;
        sendMessage(msg);
    }

    public void register_OnClick(){
        String username = String.valueOf(((EditText) findViewById(R.id.RegUsernameText)).getText());;
        String playerName = String.valueOf(((EditText) findViewById(R.id.RegPasswordText)).getText());
        String password = String.valueOf(((EditText) findViewById(R.id.RegPlayerNameText)).getText());

        String msg = "register;" + username + ";" + playerName + ";" + password;
        sendMessage(msg);
    }

    public void redirectToRegister(){
        setContentView(R.layout.register);
    }

    /**
     * Method to set a TextView
     *
     * @param msg String to put into the TextView
     */
    public void setMessageView(String msg) {
        (((TextView) findViewById(R.id.textView))).setText(msg);
    }

    private void setupDragAndDrop(ImageView imageView) {
        final boolean[] hasTheData = {false};
        imageView.setLongClickable(true);

        // Sets a long click listener for the ImageView using an anonymous listener object that
        // implements the OnLongClickListener interface.
        imageView.setOnLongClickListener(v -> {

            // Create a new ClipData.
            // This is done in two steps to provide clarity. The convenience method
            // ClipData.newPlainText() can create a plain text ClipData in one step.

            // Create a new ClipData.Item from the ImageView object's tag.
            ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This creates a new ClipDescription object within the
            // ClipData and sets its MIME type to "text/plain".
            ClipData dragData = new ClipData(
                    (CharSequence) v.getTag(),
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                    item);

            // Instantiate the drag shadow builder.
            View.DragShadowBuilder myShadow = new MyDragShadowBuilder(imageView);

            // Start the drag.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData,  // The data to be dragged
                        myShadow,  // The drag shadow builder
                        null,      // No need to use local data
                        0          // Flags (not currently used, set to 0)
                );
            }

            // Indicate that the long-click was handled.
            return true;
        });


        //View DragView = findViewById(R.id.DropView);

        // Set the drag event listener for the View.
        imageView.setOnDragListener((v, e) -> {

            // Handles each of the expected events.
            switch (e.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED:

                    // Determines if this View can accept the dragged data.
                    if (e.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        // As an example of what your application might do, applies a blue color tint
                        // to the View to indicate that it can accept data.
                        ((ImageView) v).setColorFilter(Color.BLUE);

                        // Invalidate the view to force a redraw in the new tint.
                        v.invalidate();

                        // Returns true to indicate that the View can accept the dragged data.
                        return true;

                    }

                    // Returns false to indicate that, during the current drag and drop operation,
                    // this View will not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:

                    // Applies a green tint to the View.
                    ((ImageView) v).setColorFilter(Color.GREEN);

                    // Invalidates the view to force a redraw in the new tint.
                    v.invalidate();

                    // Returns true; the value is ignored.
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    // Ignore the event.
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:

                    hasTheData[0] = false;

                    // Resets the color tint to blue.
                    ((ImageView) v).setColorFilter(Color.BLUE);

                    // Invalidates the view to force a redraw in the new tint.
                    v.invalidate();

                    // Returns true; the value is ignored.
                    return true;

                case DragEvent.ACTION_DROP:

                    hasTheData[0] = true;

                    // Gets the item containing the dragged data.
                    ClipData.Item item = e.getClipData().getItemAt(0);

                    // Gets the text data from the item.
                    CharSequence dragData = item.getText();

                    // Displays a message containing the dragged data.
                    Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_LONG).show();

                    // Turns off any color tints.
                    ((ImageView) v).clearColorFilter();

                    // Invalidates the view to force a redraw.
                    v.invalidate();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    if (hasTheData[0]) {

                        // Turns off any color tinting.
                        ((ImageView) v).clearColorFilter();

                        // Invalidates the view to force a redraw.
                        v.invalidate();
                    }


                    // Does a getResult(), and displays what happened.
                    if (e.getResult()) {
                        Toast.makeText(this, "The drop was handled.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_LONG).show();
                    }

                    // Returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example", "Unknown action type received by View.OnDragListener.");
                    break;
            }

            return false;

        });
    }


}