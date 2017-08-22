package hr.ferit.kslovic.petsmissingorfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class Welcome extends Activity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.iSignOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                return true;
            case R.id.iReport:
                Intent intentReport = new Intent(getApplicationContext(), ReportPet.class);
                startActivity(intentReport);
                return true;
            case R.id.iMyAdds:
                Intent intentMyAdds = new Intent(getApplicationContext(), MyPetsList.class);
                startActivity(intentMyAdds);
                return true;
            case R.id.iSearchP:
                Intent intentAllAdds = new Intent(getApplicationContext(), AllPetsList.class);
                startActivity(intentAllAdds);
                return true;
            case R.id.iShow:
                Intent intentLocations = new Intent(getApplicationContext(), PetMap.class);
                startActivity(intentLocations);
                return true;
            case R.id.iInbox:
                Intent intentInbox = new Intent(getApplicationContext(), InboxActivity.class);
                startActivity(intentInbox);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
