package intel.oneman.aassist;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;



public class AuthenticatorService extends Service {

    public static final String ACCOUNT = "AAssist";

    public static Account GetAccount() {
        // Note: Normally the account name is set to the user's identity (username or email
        // address). However, since we aren't actually using any user accounts, it makes more sense
        // to use a generic string in this case.
        //
        // This string should *not* be localized. If the user switches locale, we would not be
        // able to locate the old account, and may erroneously register multiple accounts.
        final String accountName = ACCOUNT;
        return new Account(accountName, "intel.oneman");
    }
    // Instance field that stores the authenticator object
    private Authenticator mAuthenticator;
    @Override
    public void onCreate() {
        Log.i("APP", "Service created");
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }

    /** When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onDestroy() {
        Log.i("APP", "Service destroyed");
    }

}