package com.example.talathiapp;

import static easypay.appinvoke.manager.PaytmAssist.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PDFPrint;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tejpratapsingh.pdfcreator.utils.FileManager;
import com.tejpratapsingh.pdfcreator.utils.PDFUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;

public class LoadPdf extends AppCompatActivity {
    WebView loadPdf;
    ProgressDialog pdi;
    String TAG="Loadpdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_pdf);
        getSupportActionBar().hide();
        loadPdf=findViewById(R.id.loadPdf);

        // Check Is Valid Token Exists
       /* CheckToken ct=new CheckToken();
        ct.isExitToken(this);*/


        loadPdf.loadUrl(getIntent().getStringExtra("URL"));
        String filename=getIntent().getStringExtra("FILENAME");

        pdi= new ProgressDialog(LoadPdf.this);
        pdi.setTitle("Creating PDF");
        pdi.setMessage("Please Wait");
        pdi.setCancelable(false);
        pdi.show();

        loadPdf.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                createPdf(filename);
            }
        });

    }



    //************** PDF ********************
    public void createPdf(String filename){
        final File savedPDFFile = FileManager.getInstance().createTempFile(getApplicationContext(), "pdf", false);
// Generate Pdf From Html
        PDFUtil.generatePDFFromWebView(savedPDFFile, loadPdf, new PDFPrint.OnPDFPrintListener() {
            @Override
            public void onSuccess(File file) {
                // Open Pdf Viewer
                Uri pdfUri = Uri.fromFile(savedPDFFile);

                Log.d("PDFViewer", "onSuccess: "+pdfUri);

                // Download & Open the PDF
                    createDownloadPDF(pdfUri,filename);

                // Only Open the PDF
              /*  Intent i1=new Intent(LoadPdf.this,ViewPdf.class);
                i1.putExtra(ViewPdf.PDF_FILE_URI,pdfUri);
                startActivity(i1);
                finish();*/

            }

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }
        });

    }

    void createDownloadPDF(Uri pdfUri,String filename){

        File[] file_path = this.getExternalFilesDirs(null);
        String newFilePath=file_path[0].toString()+"/TalathiApp/";

        File dir = new File(newFilePath);
        if(!dir.exists())
        {
            dir.mkdirs();
        }

        try {
            File from = new File(pdfUri.getPath());
            File to = new File(newFilePath+filename+".pdf");
            from.renameTo(to);

            openFile(to);

//            Toast.makeText(this, "Download Success", Toast.LENGTH_SHORT).show();


        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("ERROR",""+e);
        }

    }


    void  openFile(File fileToShare){
        pdi.dismiss();

        Intent intentShareFile = new Intent(Intent.ACTION_VIEW);
        Uri apkURI = FileProvider.getUriForFile(
                getApplicationContext(),
                getApplicationContext()
                        .getPackageName() + ".provider", fileToShare);
        intentShareFile.setDataAndType(apkURI, URLConnection.guessContentTypeFromName(fileToShare.getName()));
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                apkURI);
        startActivity(Intent.createChooser(intentShareFile, "View File"));
        finish();

    }



}