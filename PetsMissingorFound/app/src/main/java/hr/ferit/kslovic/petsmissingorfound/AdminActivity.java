package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends Activity {

    private Button bUsers;
    private  Button bPets;
    private Button bSignOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setUI();
    }

    private void setUI() {
        bUsers = (Button) findViewById(R.id.bUsers);
        bPets = (Button) findViewById(R.id.bPets);
        bSignOut = (Button) findViewById(R.id.bSignOut);
        bUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAllUsers = new Intent(getApplicationContext(), UserList.class);
                intentAllUsers.putExtra("intent","admininterface");
                startActivity(intentAllUsers);
            }
        });
        bPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAllAdds = new Intent(getApplicationContext(), AllPetsList.class);
                intentAllAdds.putExtra("intent","admininterface");
                startActivity(intentAllAdds);
            }
        });
        bSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

    }
}
