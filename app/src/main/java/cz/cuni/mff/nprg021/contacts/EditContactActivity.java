package cz.cuni.mff.nprg021.contacts;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class EditContactActivity extends AddContactActivity {
    static final String DATA_SELECTION = ContactsContract.Data._ID + " = ?";
    Bundle contactData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.edit_contact);
        Intent parent = getIntent();
        contactData = parent.getExtras();
        fillData();
        dirty = false;
    }

    protected void fillData() {
        if(contactData.getString(ContactDetailActivity.EXTRA_CONTACT_STRUCTURED_NAME_ID) != null) {
            ((EditText) findViewById(R.id.contact_name)).setText(contactData.getString(ContactDetailActivity.EXTRA_CONTACT_NAME));
            ((EditText) findViewById(R.id.contact_surname)).setText(contactData.getString(ContactDetailActivity.EXTRA_CONTACT_SURNAME));
        }
        if(contactData.getString(ContactDetailActivity.EXTRA_CONTACT_PHONE_ID) != null) {
            ((EditText) findViewById(R.id.contact_phone)).setText(contactData.getString(ContactDetailActivity.EXTRA_CONTACT_PHONE));
        }
        if(contactData.getString(ContactDetailActivity.EXTRA_CONTACT_EMAIL_ID) != null) {
            ((EditText) findViewById(R.id.contact_email)).setText(contactData.getString(ContactDetailActivity.EXTRA_CONTACT_EMAIL));
        }
    }

    @Override
    protected void save() {
        String contactName = readTextInput(R.id.contact_name);
        String contactSurname = readTextInput(R.id.contact_surname);
        String contactPhone = readTextInput(R.id.contact_phone);
        String contactEmail = readTextInput(R.id.contact_email);
        String contactId = contactData.getString(ContactListFragment.EXTRA_CONTACT_ID);

        ContentProviderOperation.Builder builder;
        ArrayList<ContentProviderOperation> editContactOperations = new ArrayList<>();
        String nameId = contactData.getString(ContactDetailActivity.EXTRA_CONTACT_STRUCTURED_NAME_ID);
        if(nameId == null) {
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contactName)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contactSurname);
        } else {
            builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(DATA_SELECTION, new String[] {nameId})
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contactName)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contactSurname);
        }
        editContactOperations.add(builder.build());
        String phoneId = contactData.getString(ContactDetailActivity.EXTRA_CONTACT_PHONE_ID);
        if(phoneId == null) {
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactPhone);
        }
        else {
            builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(DATA_SELECTION, new String[] {phoneId})
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactPhone);
        }
        editContactOperations.add(builder.build());
        String emailId = contactData.getString(ContactDetailActivity.EXTRA_CONTACT_EMAIL_ID);
        if(emailId == null) {
            builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail);
        }
        else {
            builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(DATA_SELECTION, new String[] {emailId})
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmail);
        }
        editContactOperations.add(builder.build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, editContactOperations);
            finish();
        } catch (Exception e) {

            CharSequence txt = e.getMessage();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, txt, duration);
            toast.show();
        }
    }
}
