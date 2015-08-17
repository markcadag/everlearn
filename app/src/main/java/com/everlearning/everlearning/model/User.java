package com.everlearning.everlearning.model;

/**
 * Created by mark on 8/12/15.
 */
public class User {

    private long id;
    private String name;
    private String auth_key;
    private String img_url;
    private String email;

    public boolean is_current() {
        return is_current;
    }

    public void setIs_current(boolean is_current) {
        this.is_current = is_current;
    }

    private boolean is_current;

    public User(String name,String email){
        this.name = name;
        this.email = email;
    }

    public User(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuth_key() {
        return auth_key;
    }

    public void setAuth_key(String auth_key) {
        this.auth_key = auth_key;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public void saveToPreference(Activity activity,String authKey){
//        SharedPreferences sharedPreferences =  activity.getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(activity.getString(R.string.user_name),this.getName());
//        editor.putString(activity.getString(R.string.user_email),this.getEmail());
//        editor.putString(activity.getString(R.string.user_photo),this.getImg_url());
//        editor.putString(activity.getString(R.string.user_auth),authKey);
//        editor.commit();
//    }
//
//    private void clearUserPreference(Activity activity){
//        SharedPreferences sharedPreferences =  activity.getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(activity.getString(R.string.user_auth), null);
//        editor.commit();
//    }
//
//    public static User getUserFromPreference(Activity activity){
//        User user = new User();
//        SharedPreferences pref = activity.getPreferences(Context.MODE_PRIVATE);
//        user.setName(pref.getString(activity.getString(R.string.user_name), null));
//        user.setEmail(pref.getString(activity.getString(R.string.user_email), null));
//        user.setImg_url(pref.getString(activity.getString(R.string.user_photo), null));
//        user.setAuth_key(pref.getString(activity.getString(R.string.user_auth), null));
//        return user;
//    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", auth_key='" + auth_key + '\'' +
                ", img_url='" + img_url + '\'' +
                ", email='" + email + '\'' +
                ", is_current=" + is_current +
                '}';
    }
}
