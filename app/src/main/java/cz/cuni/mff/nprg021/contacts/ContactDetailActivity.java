package cz.cuni.mff.nprg021.contacts;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ContactDetailActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_CONTACT_STRUCTURED_NAME_ID = "cz.cuni.mff.nprg021.contacts.CONTACT_STRUCTURED_NAME_ID";
    public static final String EXTRA_CONTACT_NAME = "cz.cuni.mff.nprg021.contacts.CONTACT_NAME";
    public static final String EXTRA_CONTACT_SURNAME = "cz.cuni.mff.nprg021.contacts.CONTACT_SURNAME";
    public static final String EXTRA_CONTACT_PHONE_ID = "cz.cuni.mff.nprg021.contacts.CONTACT_PHONE_ID";
    public static final String EXTRA_CONTACT_PHONE = "cz.cuni.mff.nprg021.contacts.CONTACT_PHONE";
    public static final String EXTRA_CONTACT_EMAIL_ID = "cz.cuni.mff.nprg021.contacts.CONTACT_EMAIL_ID";
    public static final String EXTRA_CONTACT_EMAIL = "cz.cuni.mff.nprg021.contacts.CONTACT_EMAIL";

    String contact_id;
    Bundle contactData;
    static final String[] COLUMNS =
            {
                    ContactsContract.Data._ID,
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.Data.DATA1,
                    ContactsContract.Data.DATA2,
                    ContactsContract.Data.DATA3,
                    ContactsContract.Data.DATA4,
                    ContactsContract.Data.DATA5,
                    ContactsContract.Data.DATA6,
                    ContactsContract.Data.DATA7,
                    ContactsContract.Data.DATA8,
                    ContactsContract.Data.DATA9,
                    ContactsContract.Data.DATA10,
                    ContactsContract.Data.DATA11,
                    ContactsContract.Data.DATA12,
                    ContactsContract.Data.DATA13,
                    ContactsContract.Data.DATA14,
                    ContactsContract.Data.DATA15
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail);
        getActionBar().setTitle(R.string.contact_detail);

        Intent intent = getIntent();
        contact_id = intent.getStringExtra(ContactListFragment.EXTRA_CONTACT_ID);
        contactData = new Bundle();


        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                ContactsContract.Data.CONTENT_URI,
                COLUMNS,
                ContactsContract.Data.RAW_CONTACT_ID + " = ?",
                new String[] {contact_id},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        contactData.clear();
        contactData.putString(ContactListFragment.EXTRA_CONTACT_ID, contact_id);
        String dataId;
        while(data.moveToNext()) {
            switch(data.getString(1)) {
                case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                    dataId = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName._ID));
                    String name = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                    String surname = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                    ((TextView) findViewById(R.id.contact_name)).setText(name);
                    ((TextView) findViewById(R.id.contact_surname)).setText(surname);
                    contactData.putString(EXTRA_CONTACT_STRUCTURED_NAME_ID, dataId);
                    contactData.putString(EXTRA_CONTACT_NAME, name);
                    contactData.putString(EXTRA_CONTACT_SURNAME, surname);
                    break;
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                    dataId = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                    String phone = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ((TextView) findViewById(R.id.contact_phone)).setText(phone);
                    contactData.putString(EXTRA_CONTACT_PHONE_ID, dataId);
                    contactData.putString(EXTRA_CONTACT_PHONE, phone);
                    break;
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                    dataId = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Email._ID));
                    String email = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    ((TextView) findViewById(R.id.contact_email)).setText(email);
                    contactData.putString(EXTRA_CONTACT_EMAIL_ID, dataId);
                    contactData.putString(EXTRA_CONTACT_EMAIL, email);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_edit) {
            Intent i = new Intent(this, EditContactActivity.class);
            // Send current contact data to Edit activity
            // so data don't have to be retrieved again by edit activity
            i.putExtras(contactData);
            startActivity(i);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
