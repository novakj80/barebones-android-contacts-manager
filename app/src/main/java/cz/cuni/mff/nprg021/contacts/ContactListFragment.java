package cz.cuni.mff.nprg021.contacts;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * A fragment displaying a list of contacts stored on a device
 *
 */
public class ContactListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_CONTACT_ID = "cz.cuni.mff.nprg021.contacts.CONTACT_ID";

    SimpleCursorAdapter mAdapter;

    static final String[] SELECTION_ARGS = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.RawContacts._ID
    };

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getResources().getString(R.string.empty_list_message));

        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null,
                new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                new int[] { android.R.id.text1 }, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                ContactsContract.RawContacts.CONTENT_URI,
                SELECTION_ARGS,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    // When contact is clicked, launch new activity which display its details
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(getActivity(), ContactDetailActivity.class);
        // pass id of the contact to new activity
        i.putExtra(EXTRA_CONTACT_ID, Long.valueOf(getListView().getItemIdAtPosition(position)).toString());

        startActivity(i);
    }
}
