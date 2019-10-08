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
                                createPDFFile(Common.getAppPath(MainActivity.this) + "pdf_name.pdf");
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
        try {
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

            //Font size
            float headerItemFontSize = 13.0f;
            float valueItemFontSize = 15.0f;

            BaseFont baseFont = BaseFont.createFont("assets/fonts/mr.ttf", "UTF-8", BaseFont.EMBEDDED);

            //Create header of document
            Font headerFont = new Font(baseFont, 20.0f, Font.BOLD, BaseColor.DARK_GRAY);

            //Create footer of document
            Font footerFont = new Font(baseFont, 18.0f, Font.BOLD, BaseColor.GRAY);

            addItem(document, "Parking Receipt", Element.ALIGN_CENTER, headerFont);

            //Add order id
            Font headerItem = new Font(baseFont, headerItemFontSize, Font.NORMAL, BaseColor.GRAY);
            Font valueItem = new Font(baseFont, valueItemFontSize, Font.BOLD, BaseColor.GRAY);

            addItemLeftRight(document, "Receipt Id:", "Company Name:", headerItem, headerItem);
            addItemLeftRight(document, "#123456789", "Black Developer Sdn. Bhd", valueItem, valueItem);

            addLineSeparator(document);

            //Create account details
            addItem(document, "Account Details", Element.ALIGN_CENTER, headerFont);

            addItemLeftRight(document, "Name:", "Vehicle Plat:", headerItem, headerItem);
            addItemLeftRight(document, "Muhamad Amir Bin Zaidi", "AMR14", valueItem, valueItem);

            addLineSeparator(document);

            //Create parking details
            addItem(document, "Parking Details", Element.ALIGN_CENTER, headerFont);

            //Add parking date
            addItemLeftRight(document, "Parking Date:", "Parking Time:", headerItem, headerItem);
            addItemLeftRight(document, "08 October 2019", "11:20:41 PM - 02:20:41 PM", valueItem, valueItem);

            addItem(document, "Parking Place:", Element.ALIGN_LEFT, headerItem);
            addItem(document, "Jalan Mati, Tak Jumpa-Jumpa, 00000 Sampai Ke, Sudah.", Element.ALIGN_LEFT, valueItem);

            addLineSeparator(document);

            addItem(document, "Total", Element.ALIGN_CENTER, headerFont);

            addItemLeftRight(document, "Hour:", "Rate:", headerItem, headerItem);
            addItemLeftRight(document, "Peak Hour", "RM5.00/Hour", valueItem, valueItem);

            addItemLeftRight(document, "Duration:", "Rate:", headerItem, headerItem);
            addItemLeftRight(document, "3 Hours", "RM5.00/Hour", valueItem, valueItem);

            addItemLeftRight(document, "Total Price:", "Payment Status:", headerItem, headerItem);
            addItemLeftRight(document, "RM15.00", "Paid", valueItem, valueItem);

            addLineSeparator(document);

            addItem(document, "THANK YOU AND DRIVE SAFELY!", Element.ALIGN_CENTER, footerFont);

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
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(MainActivity.this, Common.getAppPath(MainActivity.this) + "pdf_name.pdf");
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception exception) {
            Log.e("MMUHAMADAMIRZAIDI", "" + exception.getMessage());
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
        addLineSpace(document);
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
