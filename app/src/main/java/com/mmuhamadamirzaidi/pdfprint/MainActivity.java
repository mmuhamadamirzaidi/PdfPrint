package com.mmuhamadamirzaidi.pdfprint;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button button_generate_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_generate_pdf = findViewById(R.id.button_generate_pdf);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        button_generate_pdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                createPDFFile(Common.getAppPath(MainActivity.this)+"pdf_name.pdf");
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    private void createPDFFile(String path) {
        if (new File(path).exists())
            new File(path).delete();
        try{
            Document document = new Document();

            //Save PDF
            PdfWriter.getInstance(document, new FileOutputStream(path));

            //Open to write
            document.open();

            //Setting
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addCreator("Black Developer");
            document.addAuthor("Muhamad Amir Bin Zaidi");

            //Font & color (CMYK)
            BaseColor baseColor = new BaseColor(0, 69, 69, 5);
            float fontSize = 20.0f;
            float valueFontSize = 26.0f;

            BaseFont baseFont = BaseFont.createFont("assets/fonts/mr.ttf", "UTF-8", BaseFont.EMBEDDED);

            //Create header of document
            Font headerFont = new Font(baseFont, 36.0f, Font.BOLD, BaseColor.DARK_GRAY);

            addItem(document, "Parking Receipt", Element.ALIGN_CENTER, headerFont);

            //Add order id
            Font idFont = new Font(baseFont, fontSize, Font.BOLD, BaseColor.DARK_GRAY);

            addItem(document,"Receipt Id:", Element.ALIGN_LEFT, idFont);

            Font idFontValue = new Font(baseFont, valueFontSize, Font.NORMAL, baseColor);
            addItem(document,"#123456789", Element.ALIGN_LEFT, idFont);

            addLineSeparator(document);

            //Create account details
            addItem(document, "Account Details", Element.ALIGN_CENTER, headerFont);

            addItemLeftRight(document, "Name:", "Vehicle Plat:", idFont, idFont);
            addItemLeftRight(document, "Muhamad Amir Bin Zaidi", "AMR14", idFont, idFont);

            addLineSeparator(document);

            //Create parking details
            addItem(document, "Parking Details", Element.ALIGN_CENTER, headerFont);

            //Add parking date
            addItem(document,"Parking Date:", Element.ALIGN_LEFT, idFont);
            addItem(document,"08 October 2019", Element.ALIGN_LEFT, idFont);

            addItem(document,"Parking Time:", Element.ALIGN_LEFT, idFont);
            addItem(document,"11:20:41 PM - 02:20:41 PM", Element.ALIGN_LEFT, idFont);

            addItem(document,"Parking Place:", Element.ALIGN_LEFT, idFont);
            addItem(document,"Jalan Mati, Tak Jumpa-Jumpa, 00000 Sampai Ke, Sudah.", Element.ALIGN_LEFT, idFont);

            addLineSeparator(document);

            addItem(document, "Total", Element.ALIGN_CENTER, headerFont);

            addItemLeftRight(document, "Hour:", "Rate:", idFont, idFont);
            addItemLeftRight(document, "Peak Hour", "RM5.00/Hour", idFont, idFont);

            addItemLeftRight(document, "Duration:", "Rate:", idFont, idFont);
            addItemLeftRight(document, "3 Hours", "RM5.00/Hour", idFont, idFont);

            addItemLeftRight(document, "Total Price:", "Payment Status:", idFont, idFont);
            addItemLeftRight(document, "RM15.00", "Paid", idFont, idFont);

            addLineSeparator(document);

            addItem(document, "THANK YOU AND DRIVE SAFELY!", Element.ALIGN_CENTER, headerFont);

            document.close();

            Toast.makeText(this, "Successful generate Parking Receipt!", Toast.LENGTH_SHORT).show();

            printPdf();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printPdf() {
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(MainActivity.this, Common.getAppPath(MainActivity.this)+"pdf_name.pdf");
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        }
        catch (Exception exception) {
            Log.e("MMUHAMADAMIRZAIDI", ""+exception.getMessage());
        }
    }

    private void addItemLeftRight(Document document, String left, String right, Font leftFont, Font rightFont) throws DocumentException {
        Chunk chunkTextLeft = new Chunk(left, leftFont);
        Chunk chunkTextRight = new Chunk(right, rightFont);
        Paragraph p = new Paragraph(chunkTextLeft);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chunkTextRight);
        document.add(p);
    }

    private void addLineSeparator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSeparator(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    private void addItem(Document document, String text, int center, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(center);
        document.add(paragraph);
    }
}
