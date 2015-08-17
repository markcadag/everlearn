package com.everlearning.everlearning;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.facebook.android.crypto.keychain.SecureRandomFix;
import com.facebook.crypto.cipher.NativeGCMCipher;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;
import com.facebook.crypto.mac.NativeMac;

import java.security.SecureRandom;
import java.util.Arrays;


/**
 * Created by TheKing on 2014-12-29.
 */
public class CustomSharedPrefsBackedKeyChain implements KeyChain {
    // Visible for testing.
    /* package */ static final String SHARED_PREF_NAME = "crypto";
    /* package */ static final String CIPHER_KEY_PREF = "cipher_key";
    /* package */ static final String MAC_KEY_PREF = "mac_key";

    private final SharedPreferences mSharedPreferences;
    private final SecureRandom mSecureRandom;
    private String key = null;

    protected byte[] mCipherKey;
    protected boolean mSetCipherKey;

    protected byte[] mMacKey;
    protected boolean mSetMacKey;

    private static final SecureRandomFix sSecureRandomFix = new SecureRandomFix();
    private String log = "KeyChain";

    public CustomSharedPrefsBackedKeyChain(Context context, String key) {
        Log.d(log, "CustomSharedPrefsBackedKeyChain");
        mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mSecureRandom = new SecureRandom();
        this.key = key;
    }

    @Override
    public synchronized byte[] getCipherKey() throws KeyChainException {
        if (!mSetCipherKey) {
            mCipherKey = maybeGenerateKey(CIPHER_KEY_PREF, NativeGCMCipher.KEY_LENGTH);
        }
        mSetCipherKey = true;
        return mCipherKey;
    }

    @Override
    public byte[] getMacKey() throws KeyChainException {
        if (!mSetMacKey) {
            mMacKey = maybeGenerateKey(MAC_KEY_PREF, NativeMac.KEY_LENGTH);
        }
        mSetMacKey = true;
        return mMacKey;
    }

    @Override
    public byte[] getNewIV() throws KeyChainException {
        sSecureRandomFix.tryApplyFixes();
        byte[] iv = new byte[NativeGCMCipher.IV_LENGTH];
        mSecureRandom.nextBytes(iv);
        return iv;
    }

    @Override
    public synchronized void destroyKeys() {
        mSetCipherKey = false;
        mSetMacKey = false;
        Arrays.fill(mCipherKey, (byte) 0);
        Arrays.fill(mMacKey, (byte) 0);
        mCipherKey = null;
        mMacKey = null;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(CIPHER_KEY_PREF);
        editor.remove(MAC_KEY_PREF);
        //editor.commit();
    }

    /**
     * Generates a key associated with a preference.
     */
    private byte[] maybeGenerateKey(String pref, int length) throws KeyChainException {
        String base64Key = mSharedPreferences.getString(pref, null);

        String key;
        if(length == NativeGCMCipher.KEY_LENGTH)
            key = this.key;
        else if(length == NativeMac.KEY_LENGTH)
            key = (this.key + this.key + this.key +this.key);
        else
            key = this.key;

        base64Key = key.toString();

        if (base64Key == null) {
            // Generate key if it doesn't exist.
            return generateAndSaveKey(pref, length);
        } else {
            return base64Key.getBytes();
        }
    }

    private byte[] generateAndSaveKey(String pref, int length) throws KeyChainException {
        sSecureRandomFix.tryApplyFixes();
        byte[] key = new byte[length];
        if(length == NativeGCMCipher.KEY_LENGTH)
            key = this.key.getBytes();
        else if(length == NativeMac.KEY_LENGTH)
            key = (this.key + this.key + this.key +this.key).getBytes();
        else
            key = this.key.getBytes();

        mSecureRandom.nextBytes(key);
        // Store the session key.
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(
                pref,
                encodeForPrefs(key));
        //editor.commit();

        Log.d(log, key.toString());

        return key;
    }

    /**
     * Visible for testing.
     */
    byte[] decodeFromPrefs(String keyString) {
        if (keyString == null) {
            return null;
        }
        Log.d(log, keyString.toString());
        return Base64.decode(keyString, Base64.DEFAULT);
    }

    /**
     * Visible for testing.
     */
    String encodeForPrefs(byte[] key) {
        if (key == null ) {
            return null;
        }
        return Base64.encodeToString(key, Base64.DEFAULT);
    }

}