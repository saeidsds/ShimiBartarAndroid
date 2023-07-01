package com.multiplatform.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

;import com.multiplatform.shimibartar.R;

public class SearchHandler {

  SharedPreferences sp;
  SharedPreferences.Editor spe;
  Context ctx;
  String webservice = "";
  public EditText search_et;

  public SearchHandler(Context c) {


    this.ctx = c;

    sp = ctx.getSharedPreferences("init", AppCompatActivity.MODE_PRIVATE);
    spe = sp.edit();
    webservice = Constant.get_webservice();
    search_et = (EditText) ((AppCompatActivity) ctx).findViewById(R.id.search_et);
    search_et.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        if(search_et.getText().toString().trim().equals(""))
        {

          ((Activity) ctx).findViewById(R.id.cancel_btn).setVisibility(View.GONE);
        }
        else
        {
          ((Activity) ctx).findViewById(R.id.cancel_btn).setVisibility(View.VISIBLE);
        }
      }
    });

    ((AppCompatActivity) ctx).findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {



        cancel_search();
      }
    });
    ((AppCompatActivity) ctx).findViewById(R.id.quick_search_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        quick_search();
      }
    });
    search_et.setInputType(InputType.TYPE_CLASS_TEXT);
    search_et.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
    search_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          quick_search();
          handled = true;
        }
        return handled;
      }
    });


    search_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean b) {
        if(b)
        {
          search_et.setCursorVisible(true);
        }
        else
        {
          search_et.setCursorVisible(false);
        }
      }
    });

    search_et.setCursorVisible(false);
    search_et.clearFocus();


  }



  public void quick_search() {

    if(search_et.getText().toString().trim().equals(""))
    {
      return;
    }
    ((Activity) ctx).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    search_et.clearFocus();
    if(listener!=null)
      listener.callback(search_et.getText().toString());

  }




public void cancel_search()
{

  search_et.setText("");
}


  private SearchListener listener  =null ;

  public interface SearchListener {
    // you can define any parameter as per your requirement
    public void callback(String search);
  }

  public void setSearchListener(SearchListener listener)
  {
    this.listener=listener;
  }
  //===================================================================================================
  //========================================  ASYNCTASK ===============================================
  //===================================================================================================


}