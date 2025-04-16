package cz.cuni.mff.nprg021.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import static android.os.Build.VERSION_CODES.M;

/**
 * Entry point of the application
 *
 * Displays contacts stored on the device
 *
 * If running on android Marshmallow or higher, it first asks user to grant permission
 * to access contacts
 */
public class MainActivity extends Activity
{
    boolean menuHidden = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Request runtime permission if necessary
        if (Build.VERSION.SDK_INT >= M && !hasPermission()) {
            requestContactPermission();
        } else {
            displayContactList();
        }
    }

    @TargetApi(M)
    boolean hasPermission() {
        return     checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    void displayContactList() {
        setContentView(R.layout.main_activity);
    }

    void displayPermissionDeniedMessage() {
        setContentView(R.layout.main_permission_denied);
        menuHidden = true;
        invalidateOptionsMenu();
    }

    /**
     * Add button to the action bar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!menuHidden) {
            getMenuInflater().inflate(R.menu.main_activity_menu, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_contact) {
            Intent i = new Intent(this, AddContactActivity.class);
            startActivity(i);
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    @TargetApi(M)
    protected void requestContactPermission() {
        requestPermissions(new String[] {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        }, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults.length > 0) {
            // Iterate over results to check if any permission was denied
            for(int result : grantResults) {
                if(result == PackageManager.PERMISSION_DENIED) {
                    displayPermissionDeniedMessage();
                    return;
                }
            }
            // All requested permissions were granted
            displayContactList();
        } else {
            // No permission was granted because grantResults is empty
            displayPermissionDeniedMessage();
        }
    }
}
