package com.spit.lms.System.Base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.spit.lms.MainActivity;
import com.spit.lms.System.Event.FileDoneEvent;
import com.spit.lms.System.Event.InsertEvent;
import com.spit.lms.System.Model.StockTakeListBook;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

public class BaseUtils {

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) MainActivity.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
    public static void parseLargeJson(String path, String stocktakeno) {
        try (JsonParser jParser = new JsonFactory().createParser(new File(path));) {
            com.fasterxml.jackson.core.JsonToken current;
            current = jParser.nextToken();

            if (!JsonToken.START_ARRAY.equals(current)) {
                System.out.println("Expected an array");
            }
            StockTakeListBook stockTakeListBook = new StockTakeListBook();

            int count = 0;

            while (!JsonToken.END_ARRAY.equals(jParser.nextToken())) {
                Log.i("parse", "parse " + jParser.getText());
                Realm.getDefaultInstance().beginTransaction();

                while (jParser.nextToken() != JsonToken.END_OBJECT) {

                    String fn = jParser.getCurrentName();
                    jParser.nextToken();


                    if(fn.equals("bookNo")) {
                        stockTakeListBook = new StockTakeListBook();
                        Log.i("bookNo", "bookNo " + jParser.getText());
                        stockTakeListBook.setBookNo(utf(jParser.getText()));
                    } else if(fn.equals("callNo")) {
                        Log.i("callNo", "callNo " + jParser.getText());
                        stockTakeListBook.setCallNo(utf(jParser.getText()));
                    } else if(fn.equals("name")) {
                        Log.i("name", "name " + jParser.getText());
                        stockTakeListBook.setName(utf(jParser.getText()));
                    } else if(fn.equals("author")) {
                        Log.i("author", "author " + jParser.getText());
                        stockTakeListBook.setAuthor(utf(jParser.getText()));
                    } else if(fn.equals("isbn")) {
                        Log.i("isbn", "isbn " + jParser.getText());
                        stockTakeListBook.setIsbn(utf(jParser.getText()));
                    } else if(fn.equals("publisher")) {
                        Log.i("publisher", "publisher " + jParser.getText());
                        stockTakeListBook.setPublisher(utf(jParser.getText()));
                    } else if(fn.equals("category")) {
                        Log.i("category", "category " + jParser.getText());
                        stockTakeListBook.setCategory(utf(jParser.getText()));
                    } else if(fn.equals("location")) {
                        Log.i("location", "location " + jParser.getText());
                        stockTakeListBook.setLocation(utf(jParser.getText()));
                    } else if(fn.equals("status")) {
                        try {
                            Log.i("status", "status " + jParser.getText());
                            stockTakeListBook.setStatus((Integer.parseInt(jParser.getText())));
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    } else if(fn.equals("description")) {
                        Log.i("description", "description " + jParser.getText());
                        stockTakeListBook.setDescription(utf(jParser.getText()));
                    } else if(fn.equals("epc")) {
                        Log.i("epc", "epc " + jParser.getText());
                        stockTakeListBook.setEpc(utf(jParser.getText()));
                    } else if(fn.equals("image")) {
                        Log.i("image", "image " + jParser.getText());
                        stockTakeListBook.setImage(jParser.getText());
                    } else if(fn.equals("foundStatus")) {
                        Log.i("foundStatus", "foundStatus " + jParser.getText());
                        try {
                            stockTakeListBook.setFoundStatus(Integer.parseInt(jParser.getText()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(fn.equals("publishingDate")) {
                        stockTakeListBook.setPublishingDate(utf(jParser.getText()));
                    }
                }

                Log.i("stocktakeno", "stocktakeno " + stocktakeno + " " + stockTakeListBook.getBookNo());

                stockTakeListBook.setStocktakeno(stocktakeno);
                stockTakeListBook.setUserid(SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID"));
                stockTakeListBook.setPk(stockTakeListBook.getUserid() + "_" + stocktakeno + "_" + stockTakeListBook.getBookNo());

                try {
                    Realm.getDefaultInstance().insert(stockTakeListBook);
                    count++;
                    EventBus.getDefault().post(new InsertEvent(count));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Realm.getDefaultInstance().commitTransaction();
                //System.out.println(jParser.readValueAsTree().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new FileDoneEvent(stocktakeno));

    }

    public static String utf(String s) {
        try {
            //Pattern p = Pattern.compile("(&apos;)|(&#39;)|(%27)");
            //Matcher m = p.matcher(s);
            //String result = m.replaceAll("'");

            Log.i("hihi", "hihi " + s.replace("&aposE;", "'") );

            return s.replace("&aposE;", "'");//StringEscapeUtils.unescapeXml(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
