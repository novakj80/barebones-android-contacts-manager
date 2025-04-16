package cz.cuni.mff.nprg021.contacts;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddContactActivity extends Activity implements ConfirmDialogFragment.ConfirmDialogListener {
    boolean dirty = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact);
        getActionBar().setTitle(R.string.new_contact);
        bindWatchers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Overrides default action so filled data are not lost
     * if user accidentaly navigates up.
     *
     * Instead, ask user if filled data should be saved or not,
     * or if he want to continue editing.
     */
    @Override
    public boolean onNavigateUp() {
        if(!dirty) {
            return super.onNavigateUp();
        }
        else {
            ConfirmDialogFragment confirmDialog = new ConfirmDialogFragment();
            confirmDialog.show(getFragmentManager(), "confirm_contact_save");
            return false;
        }
    }

    // Back action is same as navigate up action
    @Override
    public void onBackPressed() {
        onNavigateUp();
    }

    /**
     * Read input form {@link EditText} widget
     * @param id ID of the widget
     * @return text in text field
     */
    protected String readTextInput(int id) {
        return ((EditText)findViewById(id)).getText().toString();
    }

    protected void save() {
        // read input
        String contactName = readTextInput(R.id.contact_name);
        String contactSurname = readTextInput(R.id.contact_surname);
        String contactPhone = readTextInput(R.id.contact_phone);
        String contactEmail = readTextInput(R.id.contact_email);

        ArrayList<ContentProviderOperation> addContactOperations = new ArrayList<>();
        // Create new contact
        ContentProviderOperation.Builder addContactOp = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        addContactOperations.add(addContactOp.build());
        // Add contact name and surname
        ContentProviderOperation.Builder addName = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contactName)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contactSurname);
        addContactOperations.add(addName.build());
        // add phone number
        ContentProviderOperation.Builder addPhone = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactPhone);
        addContactOperations.add(addPhone.build());
        // add email
        ContentProviderOperation.Builder addEmail = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail);
        addContactOperations.add(addEmail.build());
        try {
            // Commit data to contacts provider
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, addContactOperations);
            finish();
        } catch (Exception e) {
            CharSequence txt = e.getMessage();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, txt, duration);
            toast.show();
        }

    }

    protected void bindWatchers() {
        ((EditText)findViewById(R.id.contact_name)).addTextChangedListener(new DirtyWatcher());
        ((EditText)findViewById(R.id.contact_surname)).addTextChangedListener(new DirtyWatcher());
        ((EditText)findViewById(R.id.contact_phone)).addTextChangedListener(new DirtyWatcher());
        ((EditText)findViewById(R.id.contact_email)).addTextChangedListener(new DirtyWatcher());
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        save();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        finish();
    }

    protected class DirtyWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            dirty = true;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
