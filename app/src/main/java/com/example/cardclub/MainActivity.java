package com.example.cardclub;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cardclub.pogos.Card;
import com.example.cardclub.pogos.Player;
import com.example.cardclub.serverHandling.ServerInput;
import com.example.cardclub.serverHandling.ServerOutput;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

/**
 * The main view of the application
 */
public class MainActivity extends AppCompatActivity {

    String lastServerResponse;
    private Socket socket = null;
    private String address = "192.168.8.106";
    private int port = 5004;
    private Player player;
    private final CardHandler cardhandler = new CardHandler();
    private LinearLayout cardHolder;
    private boolean yourTurn = false;
    private boolean playerWon = false;
    private boolean playerLost = false;

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
    public void connectBtnClicked(View view) throws InterruptedException {
        new Thread(this::baseConnect).start();
        checkConnection();
    }

    /**
     * Simplified connect method using static data
     */
    public void baseConnect() {
        try {
            socket = new Socket(address, port);
            new Thread(new ServerInput(this, socket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void checkConnection() throws InterruptedException {
        Thread.sleep(1000);
        if (lastServerResponse.equals("true")) {
            redirectToLogin();
        }
    }

    /**
     * Method to set a TextView
     *
     * @param msg String to put into the TextView
     */
    public void handleServerMessage(String msg) {
        System.out.println("Server responded: " + msg);

        yourTurn = msg.equals("YourTurn");

        if (msg.equals("You Won")) {
            playerWon = true;
            displayEndingDialog();
        } else if (msg.equals("You Lost")) {
            playerLost = false;
            displayEndingDialog();
        }

        lastServerResponse = msg;

        if (cardhandler.generateHand(player, msg)) {
            //Get the main thread so i can call main functions
            new Handler(Looper.getMainLooper()).postDelayed(this::prepareVisualCards, 1000);
        }

        //(((TextView) findViewById(R.id.textView))).setText(msg);
    }

    /**
     * Method to send data string to server on button click
     */
    public void sendMessage(String msg) {
        new Thread(new ServerOutput(socket, msg)).start();
    }

    /**
     * Method for handling user login
     */
    public void login_OnClick(View view) throws InterruptedException {
        String username = String.valueOf(((EditText) findViewById(R.id.loginPlayernameText)).getText());
        String password = String.valueOf(((EditText) findViewById(R.id.loginPasswordText)).getText());
        String msg = "login;" + username + ";" + password;

        player = new Player(username, "");

        sendMessage(msg);
        checkLogin();
    }

    /**
     * Method for handling user registration
     */
    public void register_OnClick(View view) throws InterruptedException {
        String username = String.valueOf(((EditText) findViewById(R.id.RegUsernameText)).getText());
        String playerName = String.valueOf(((EditText) findViewById(R.id.RegPasswordText)).getText());
        String password = String.valueOf(((EditText) findViewById(R.id.RegPlayerNameText)).getText());

        String msg = "register;" + username + ";" + playerName + ";" + password;
        sendMessage(msg);
        checkRegisterOrUpdate();
    }

    /**
     * method for handling updating of user information
     */
    public void updateUser_OnClick(View view) {
        String playerName = String.valueOf(((EditText) findViewById(R.id.loginPasswordText)).getText());
        String password = String.valueOf(((EditText) findViewById(R.id.loginPlayernameText)).getText());
        String msg;
        if (!password.equals("New Password")) {
            msg = "updateuser;" + "psw;" + playerName + ";" + player.getUserName() + ";" + password;
        } else {
            msg = "updateuser;" + "Playername;" + playerName + ";" + player.getUserName() + ";" + "";
        }

        sendMessage(msg);

        try {
            checkRegisterOrUpdate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for handling a player playing a card
     *
     * @param imageTag
     */
    public void playCard_OnDrop(String imageTag) {
        if (yourTurn) {
            String[] arrOfString = imageTag.split("_");
            List<Card> tempList = player.getCards();
            String cardToPlay = "";

            for (Card card : tempList
            ) {
                if (card.getSuit().equals(arrOfString[0])) {
                    if (card.getValue() == Integer.parseInt(arrOfString[1])) {
                        cardToPlay = card.getValue() + ";" + card.getSuit();
                        tempList.remove(card);
                        break;
                    }
                }
            }

            if (!cardToPlay.equals("")) {
                sendMessage("playcard;" + cardToPlay);
            }
            player.setCards(tempList);
            System.out.println("Player played: " + cardToPlay);
        }
    }

    /**
     * Method for handling room creation
     *
     * @param view
     */
    public void createRoom_OnClick(View view) {
        sendMessage("createroom");

        try {
            checkJoinOrCreateRoom();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for checking if a register or update call went through
     *
     * @throws InterruptedException
     */
    public void checkRegisterOrUpdate() throws InterruptedException {
        Thread.sleep(1000);
        if (lastServerResponse.equals("true")) {
            redirectToMenu();
        }
    }

    /**
     * Method for checking if a login all went through
     *
     * @throws InterruptedException
     */
    public void checkLogin() throws InterruptedException {
        Thread.sleep(1000);
        if (!lastServerResponse.equals("false")) {
            redirectToMenu();
        }
    }

    /**
     * Method for checking if a join or createroom call went through
     */
    public void checkJoinOrCreateRoom() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (lastServerResponse.equals("true")) {
            redirectToGame();
        }
    }

    /**
     * Method for redirecting a user to the Register page
     */
    public void redirectToRegister(View view) {
        setContentView(R.layout.register);
    }

    /**
     * Method for redirecting a user to the login page
     */
    public void redirectToLogin() {
        setContentView(R.layout.login);
    }

    /**
     * Method for redirecting a user to the menu page
     */
    public void redirectToMenu() {
        setContentView(R.layout.menu);
    }

    /**
     * Method for redirecting a user to the game page
     */
    public void redirectToGame() {
        setContentView(R.layout.game);
    }

    /**
     * Method for redirecting a user to the updateProfile page
     *
     * @param view
     */
    public void redirectToUpdate(View view) {
        setContentView(R.layout.updateprofile);
    }

    /**
     * Method for generating and showing a players cards
     */
    public void prepareVisualCards() {
        cardHolder = findViewById(R.id.CardHolder);

        for (Card card : player.getCards()) {
            ImageView image = new ImageView(this);
            image.setTag(card.getSuit() + "_" + card.getValue());
            Context context = image.getContext();
            int id = this.getResources().getIdentifier(card.getSuit().toLowerCase(Locale.ROOT) + "_" + card.getValue(), "drawable", context.getPackageName());
            System.out.println(card.getSuit() + "_" + card.getValue() + ",drawable," + context.getPackageName());
            image.setBackgroundResource(id);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            image.setLayoutParams(param);
            setupDragEvent(image);
            cardHolder.addView(image);
        }

        setupDropZone();
    }

    /**
     * Method for adding Dragging events to an imageview
     *
     * @param imageView
     */
    private void setupDragEvent(ImageView imageView) {
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


    }

    /**
     * Method for adding Drop events to an imageview
     */
    private void setupDropZone() {
        ImageView imageView = findViewById(R.id.dropZone);

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

                    // Resets the color tint to blue.
                    ((ImageView) v).setColorFilter(Color.BLUE);

                    // Invalidates the view to force a redraw in the new tint.
                    v.invalidate();

                    // Returns true; the value is ignored.
                    return true;

                case DragEvent.ACTION_DROP:

                    // Gets the item containing the dragged data.
                    ClipData.Item item = e.getClipData().getItemAt(0);

                    // Gets the text data from the item.
                    CharSequence dragData = item.getText();

                    // Displays a message containing the dragged data.
                    Toast.makeText(this, "Dragged data is " + dragData, Toast.LENGTH_LONG).show();

                    //Removes played card from players hands
                    final int childCount = cardHolder.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = cardHolder.getChildAt(i);
                        if (child != null) {
                            if (child.getTag().equals(dragData)) {
                                Thread thread = new Thread(() -> playCard_OnDrop((String) dragData));
                                thread.start();
                                Context context = child.getContext();
                                imageView.setBackgroundResource(this.getResources().getIdentifier(String.valueOf(child.getTag()), "drawable", context.getPackageName()));
                                ((ViewGroup) child.getParent()).removeView(child);
                            }
                        }
                    }

                    // Turns off any color tints.
                    ((ImageView) v).clearColorFilter();

                    // Invalidates the view to force a redraw.
                    v.invalidate();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    // Turns off any color tinting.
                    ((ImageView) v).clearColorFilter();

                    // Invalidates the view to force a redraw.
                    v.invalidate();


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

    /***
     * determines which final dialog box to display
     */
    private void displayEndingDialog(){
        if(playerWon){
            winningDialog();
        }

        if(playerLost){
            loosingDialog();
        }
    }

    /**
     * Dialog box to tell the user they won
     */
    private void winningDialog(){
        new AlertDialog.Builder(this)
                .setTitle("YOU WON!").show();
    }

    /**
     * Dialog box to tell the user they lost
     */
    private void loosingDialog(){
        new AlertDialog.Builder(this)
                .setTitle("You lost")
                .setMessage("Better luck next time.").show();
    }
}